package io.sphere.internal.oauth;

import io.sphere.client.SphereClientException;
import io.sphere.client.oauth.ClientCredentials;
import io.sphere.client.shop.SphereClientConfig;
import io.sphere.internal.Defaults;
import io.sphere.internal.util.Concurrent;
import io.sphere.client.oauth.OAuthClient;
import io.sphere.client.oauth.Tokens;
import io.sphere.client.Endpoints;
import io.sphere.internal.util.Log;
import com.google.common.base.Optional;
import io.sphere.internal.util.Util;
import io.sphere.internal.util.ValidationE;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

/** Holds OAuth access tokens for accessing protected Sphere HTTP API endpoints.
 *  Refreshes the access token as needed automatically. */
@ThreadSafe
public final class SphereClientCredentials implements ClientCredentials {
    private final String tokenEndpoint;
    private final String projectKey;
    private final String clientId;
    private final String clientSecret;
    private final OAuthClient oauthClient;

    private final Object accessTokenLock = new Object();

    @GuardedBy("accessTokenLock")
    private Optional<ValidationE<AccessToken>> accessTokenResult = Optional.absent();

    /** Allows at most one refresh operation running in the background. */
    private final ThreadPoolExecutor refreshExecutor = Concurrent.singleTaskExecutor("Sphere-ClientCredentials-refresh");
    private final Timer refreshTimer = new Timer("Sphere-ClientCredentials-refreshTimer", /*isDaemon*/true);

    /** Creates an instance of ClientCredentials based on config. */
    public static SphereClientCredentials createAndBeginRefreshInBackground(SphereClientConfig config, OAuthClient oauthClient) {
        String tokenEndpoint = Endpoints.tokenEndpoint(config.getAuthHttpServiceUrl());
        SphereClientCredentials credentials = new SphereClientCredentials(
                oauthClient, tokenEndpoint, config.getProjectKey(), config.getClientId(), config.getClientSecret());
        credentials.beginRefresh();
        return credentials;
    }

    private SphereClientCredentials(OAuthClient oauthClient, String tokenEndpoint, String projectKey, String clientId, String clientSecret) {
        this.oauthClient  = oauthClient;
        this.tokenEndpoint = tokenEndpoint;
        this.projectKey = projectKey;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    public String getAccessToken() {
        synchronized (accessTokenLock) {
            Optional<ValidationE<AccessToken>> tokenResult = waitForToken();
            if (!tokenResult.isPresent()) {
                // Shouldn't happen as the timer should refresh the token soon enough.
                Log.warn("[oauth] Access token expired, blocking until a new one is available.");
                beginRefresh();
                tokenResult = waitForToken();
                if (!tokenResult.isPresent()) {
                    throw new SphereClientException("Access token expired immediately after refresh.");
                }
            }
            if (tokenResult.get().isError()) {
                beginRefresh();   // retry on error (essential to recover from backend errors)
                throw tokenResult.get().getError();
            }
            return tokenResult.get().getValue().getAccessToken();
        }
    }

    /** If there is an access token present, checks whether it's not expired yet and returns it.
     *  If the token already expired, clears the token.
     *  Called only from {@link #getAccessToken()} so {@link #accessTokenLock} is already acquired. */
    private Optional<ValidationE<AccessToken>> waitForToken() {
        while (!accessTokenResult.isPresent()) {
            try {
                accessTokenLock.wait();
            } catch (InterruptedException e) { }
        }
        if (accessTokenResult.get().isError()) {
            return accessTokenResult;
        }
        Optional<Long> remainingMs = accessTokenResult.get().getValue().getRemaniningMs();
        if (remainingMs.isPresent()) {
            // Have some tolerance here so that we don't send tokens with 100ms validity to the server,
            // expiring "on the way".
            if (remainingMs.get() <= 2000) {
                // if the token expired, clear it
                accessTokenResult = Optional.absent();
            }
        }
        return accessTokenResult;
    }

    /** Asynchronously refreshes the tokens contained in this instance. */
    private void beginRefresh() {
        try {
            refreshExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    Log.debug("[oauth] Refreshing access token.");
                    Tokens tokens;
                    try {
                        tokens = oauthClient.getTokensForClient(tokenEndpoint, clientId, clientSecret, "manage_project:" + projectKey).get();
                    } catch (Exception e) {
                        update(null, e);
                        return;
                    }
                    update(tokens, null);
                }
            });
        } catch (RejectedExecutionException e) {
            // another refresh is already in progress, ignore this one
        }
    }

    private void update(Tokens tokens, Exception e) {
        synchronized (accessTokenLock) {
            try {
                if (e == null) {
                    AccessToken newToken = new AccessToken(tokens.getAccessToken(), tokens.getExpiresIn(), System.currentTimeMillis());
                    this.accessTokenResult = Optional.of(ValidationE.<AccessToken>success(newToken));
                    Log.debug("[oauth] Refreshed access token.");
                    scheduleNextRefresh(tokens);
                } else {
                    this.accessTokenResult = Optional.of(ValidationE.<AccessToken>error(Util.toSphereException(e)));
                    Log.error("[oauth] Failed to refresh access token.", e);
                }
            } finally {
                accessTokenLock.notifyAll();
            }
        }
    }

    private void scheduleNextRefresh(Tokens tokens) {
        if (!tokens.getExpiresIn().isPresent()) {
            Log.warn("[oauth] Authorization server did not provide expires_in for the access token.");
            return;
        }
        if (tokens.getExpiresIn().get() * 1000 < Defaults.tokenAboutToExpireMs) {
            Log.warn("[oauth] Authorization server returned an access token with a very short validity of " +
                    tokens.getExpiresIn().get() + "s!");
            return;
        }
        long refreshTimeout = tokens.getExpiresIn().get() * 1000 - Defaults.tokenAboutToExpireMs;
        Log.debug("[oauth] Scheduling next token refresh " + refreshTimeout / 1000 + "s from now.");
        refreshTimer.schedule(new TimerTask() {
            public void run() {
                beginRefresh();
            }
        }, refreshTimeout);
    }

    /** Shuts down internal thread pools. */
    public void shutdown() {
        refreshExecutor.shutdownNow();
        refreshTimer.cancel();
    }
}
