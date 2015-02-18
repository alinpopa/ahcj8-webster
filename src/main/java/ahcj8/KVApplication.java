package ahcj8;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import webster.netty.Server;
import webster.requestresponse.Request;
import webster.requestresponse.Response;
import webster.routing.Decorators;
import webster.routing.RoutingTable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static webster.routing.RoutingBuilder.from;
import static webster.routing.RoutingBuilder.routingTable;

public class KVApplication {
    private final static Logger logger = LoggerFactory.getLogger(Decorators.class);

    public static void main(String[] args) throws Exception {
        final UnaryOperator<Function<Request, CompletableFuture<Response>>> loggerDecorator =
                handler ->
                        request -> {
                            System.out.println("REQUEST LOG: " + request.toString());
                            return handler.apply(request)
                                    .whenComplete((resp, throwable) -> {
                                        if (resp != null)
                                            System.out.println("RESPONSE LOG: " + resp.toString());
                                        else if (throwable != null)
                                            throwable.printStackTrace();
                                    });
                        };

        final UnaryOperator<Function<Request, CompletableFuture<Response>>> nopDecorator =
                handler ->
                        request -> {
                            System.out.println("NOOP");
                            return handler.apply(request);
                        };

        RoutingTable routingTable = routingTable()
                .withRoute(from("/hello1").toResource(new HelloWorld())
                        .decoratedWith(loggerDecorator))
                .withRoute(from("/hello2").toResource(new HelloWorld())
                        .decoratedWith(nopDecorator))
                .build();

        new Server.Builder().withPort(8080).build().run(routingTable);
    }
}
