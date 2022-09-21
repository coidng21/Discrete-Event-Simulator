package cs2030.simulator;

import java.util.Optional;
import cs2030.util.*;

class WaitServe implements Event {
    private final Server server;
    private final Customer customer;
    private final double time;
    private static final String TYPE = "WaitServe";

    WaitServe(Customer customer, double time) {
        this.server = new Server(0);
        this.customer = customer;
        this.time = time;
    }

    WaitServe(Customer customer, double time, Server server) {
        this.customer = customer;
        this.time = time;
        this.server = server;
    }

    public String getType() {
        return TYPE;
    }


    public double getTime() {
        return this.time;
    }

    public Server getServer() {
        return this.server;
    }

    public Customer getCustomer() {
        return this.customer;
    }


    @Override
    public Pair<Optional<Event>, Shop> execute(Shop shop) {
        ImList<Server> servers = shop.getServers();
        Optional<Event> serve = Optional.empty();
        for (int i = 0; i < servers.size(); i++) {
            Server s = servers.get(i);
            if (this.server.getID() == s.getID()) {
                servers = servers.set(i, this.server);
            }
        }
        Shop newShop = new Shop(servers);
        return Pair.<Optional<Event>, Shop>of(serve, newShop);
    }

    @Override
    public String toString() {
        String serverID = this.server.toString();
        return String.format("%,.3f %d waits at %s", time, customer.getID(), serverID);
    }
}
