package cs2030.simulator;

import cs2030.util.*;
import java.util.Optional;

class Done implements Event {

    private final Customer customer;
    private final double time;
    private final Server server;
    private static final String TYPE = "Done";

    Done(Customer customer, double time) {
        this.customer = customer;
        this.time = time;
        this.server = new Server(0);
    }

    Done(Customer customer, double time, Server server) {
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
        Optional<Event> event = Optional.of(this);
        for (int i = 0; i < servers.size(); i++) {
            Server s = servers.get(i);
            if (s.getID() == this.server.getID()) {
                int id = s.getID();
                int maxPQ = s.getMaxPQ();
                ImList<Customer> served = s.getServed();
                ImList<Customer> pq = s.getPQ();
                boolean isRest = s.isRest();
                double ft = s.getNextFreeTime();
                boolean self = s.isSelf();
                Server newServer = new Server(id, true, ft, pq, maxPQ, isRest, served, self);
                servers = servers.set(i, newServer);
                break;
            }
        }
        Shop updated = new Shop(servers);
        return Pair.<Optional<Event>, Shop>of(event, updated);
    }

    @Override
    public String toString() {
        String serverID = this.server.toString();
        return String.format("%,.3f %d done serving by %s", time, customer.getID(), serverID);
    }
}