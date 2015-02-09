package ahcj8;

import ahcj8.client.AhcCompletableClient;
import ahcj8.client.CompletableClient;
import com.ning.http.client.Response;
import webster.requestresponse.Request;
import webster.resource.ContentNegotiationResource;
import webster.resource.Resource;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public class HelloWorld extends ContentNegotiationResource implements Resource{
    private final CompletableClient client = new AhcCompletableClient("http://0.0.0.0:9991");

    @Override
    public ContentNegotiator contentNegotation() {
        return supportFor()
                .mediaType("application/json", this::jsonEntity);
    }

    @Override
    public CompletableFuture<Boolean> doesRequestedResourceExist(Request request) {
        return CompletableFuture.completedFuture(true);
    }

    private CompletableFuture<Object> jsonEntity(Request request){
        final Function<Response, String> responseParser = response -> {
            try {
                return response.getResponseBody();
            } catch (Exception e) {
                return e.getMessage();
            }
        };
        final CompletableFuture<String> key = client.get("/", responseParser);
        final CompletableFuture<String> setValue = key.thenCompose(k ->
                        client.put("/kv/" + k, "SOME DATA", r -> k)
        );
        final CompletableFuture<String> value = setValue.thenCompose(k ->
                        client.get("/kv/" + k, responseParser)
        );
        return value.thenApply(v -> v);
    }

    @Override
    public CompletableFuture<Optional<String>> etag(Request request) {
        return CompletableFuture.completedFuture(Optional.of("1"));
    }
}
