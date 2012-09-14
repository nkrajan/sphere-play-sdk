package de.commercetools.sphere.client;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import de.commercetools.sphere.client.util.RequestHolder;
import com.ning.http.client.ListenableFuture;
import com.ning.http.client.AsyncCompletionHandler;

/** Request builder that does no requests to the server and just returns a prepared response. */
public class MockRequestHolder<T> implements RequestHolder<T> {
    private int statusCode;
    private String responseBody;
    private Multimap<String, String> queryParams = HashMultimap.create();

    public MockRequestHolder(int statusCode, String responseBody) {
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public MockRequestHolder(String responseBody) {
        this(200, responseBody);
    }

    /** Retuns query parameters for assertion purposes. */
    public Multimap<String, String> getQueryParams() {
        return queryParams;
    }

    /** Simulate a request to a server - just return prepared response. */
    public ListenableFuture<T> executeRequest(AsyncCompletionHandler<T> onResponse) throws Exception {
        return MockListenableFuture.completed(onResponse.onCompleted(new MockHttpResponse(statusCode, responseBody)));
    }

    /** Returns a dummy string as there is no real request. */
    public String getRawUrl() {
        return "No URL (MockRequestHolder used in tests)";
    }

    /** Adds the parameters to an map for assertion purposes. */
    public void addQueryParameter(String name, String value) {
        queryParams.put(name, value);
    }
}