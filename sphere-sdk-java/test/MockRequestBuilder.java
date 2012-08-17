package sphere;

import de.commercetools.sphere.client.util.RequestBuilder;
import de.commercetools.sphere.client.util.AbstractRequestBuilder;
import com.ning.http.client.ListenableFuture;
import org.codehaus.jackson.type.TypeReference;
import com.ning.http.client.AsyncCompletionHandler;

/** Request builder that does no requests to the server and just returns a prepared response.
 *  Useful for tests. */
public class MockRequestBuilder<T> extends AbstractRequestBuilder<T> {

    int statusCode;
    String responseBody;

    public MockRequestBuilder(int statusCode, String responseBody, TypeReference<T> jsonParserTypeRef) {
        super(jsonParserTypeRef);
        this.statusCode = statusCode;
        this.responseBody = responseBody;
    }

    public MockRequestBuilder(String responseBody, TypeReference<T> jsonParserTypeRef) {
        this(200, responseBody, jsonParserTypeRef);
    }

    /** No request to a server, just return prepared response. */
    @Override
    protected ListenableFuture<T> executeRequest(AsyncCompletionHandler<T> onResponse) throws Exception {
        return MockListenableFuture.completed(onResponse.onCompleted(new MockHttpResponse(statusCode, responseBody)));
    }

    @Override
    protected String getRawRequestUrl() {
        return "No URL (MockRequestBuilder used in tests)";
    }

    /** Request references to be expanded. */
    @Override
    public RequestBuilder<T> expand(String... paths) {
        // do nothing
        return this;
    }
}