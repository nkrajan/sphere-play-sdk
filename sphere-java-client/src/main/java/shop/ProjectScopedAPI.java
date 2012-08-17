package de.commercetools.sphere.client.shop;

import de.commercetools.sphere.client.ProjectEndpoints;
import de.commercetools.sphere.client.util.RequestBuilder;
import de.commercetools.sphere.client.util.RequestBuilderImpl;
import com.ning.http.client.AsyncHttpClient;
import org.codehaus.jackson.type.TypeReference;

/** Package private helper for working with Sphere HTTP APIs scoped to a project. */
abstract class ProjectScopedAPI {

    protected ProjectEndpoints endpoints;
    protected ClientCredentials credentials;
    /** Cached AsyncHttpClient instance. */
    private AsyncHttpClient httpClient = new AsyncHttpClient();

    protected ProjectScopedAPI(ClientCredentials credentials, ProjectEndpoints endpoints) {
        this.endpoints = endpoints;
        this.credentials = credentials;
    }
    
    /** Factory method for concrete implementation of a {@link RequestBuilder}.
     *  Allows for overriding in tests. */
    protected <T> RequestBuilder<T> requestBuilder(String url, TypeReference<T> jsonParserTypeRef) {
        return new RequestBuilderImpl<T>(
                httpClient.prepareGet(url).setHeader("Authorization", "Bearer " + credentials.accessToken()),
                jsonParserTypeRef);
    } 
}