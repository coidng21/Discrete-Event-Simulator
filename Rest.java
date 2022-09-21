package cs2030.simulator;

import java.util.Optional;
import cs2030.util.*;

class Rest implements Event {

    private final Customer customer;
    private final double time;
    private final Server server;
    private static final String TYPE = "Rest";

    Rest(Customer customer, double time, Server server) {
        this.customer = customer;
        this.time = time;
        this.server = server;
    }

    public double getTime() {
        return this.time;
    }

    public String getType() {
        return TYPE;
    }

    public Server getServer() {
        return new Server(0);
    }

    public Customer getCustomer() {
        return this.customer;
    }

    @Override
    public Pair<Optional<Event>, Shop> execute(Shop shop) {
        ImList<Server> servers = shop.getServers();
        Optional<Event> event = Optional.empty();
        for (int i = 0; i < servers.size(); i++) {
            Server s = servers.get(i);
            if (this.server.getID() == s.getID()) {
                double doneTime = getTime();
                int maxPQ = s.getMaxPQ();
                ImList<Customer> custs = s.getServed();
                ImList<Customer> pq = s.getPQ();
                int id = s.getID();
                servers = servers.set(i, new Server(id, true, doneTime, pq, maxPQ, false, custs));
                event = Optional.of(new Serve(customer, time, servers.get(i)));
                break;
            }
        }
        Shop updated = new Shop(servers);
        return Pair.<Optional<Event>, Shop>of(event, updated);
    }

    @Override
    public String toString() {
        return String.format("%,.3f %d rests", getTime(), server.getID());
    }

}