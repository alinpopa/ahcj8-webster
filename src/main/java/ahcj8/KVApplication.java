package ahcj8;

import webster.netty.Server;
import webster.routing.RoutingTable;
import static webster.routing.RoutingBuilder.from;
import static webster.routing.RoutingBuilder.routingTable;

public class KVApplication {
    public static void main(String[] args) throws Exception {
        RoutingTable routingTable = routingTable()
                .withRoute(from("/hello").toResource(new HelloWorld()))
                .build();

        new Server.Builder().withPort(8080).build().run(routingTable);
    }
}
