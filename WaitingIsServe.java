package cs2030.simulator;

import cs2030.util.*;

import java.util.Optional;

class WaitingIsServe implements Event {

    private final Server server;
    private final Customer customer;
    private final double time;
    private static final String TYPE = "WaitingIsServe";

    WaitingIsServe(Customer customer, double time) {
        this.server = new Server(0);
        this.customer = customer;
        this.time = time;
    }

    WaitingIsServe(Customer customer, double time, Server server) {
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
        Optional<Event> Done = Optional.empty();
        for (int i = 0; i < servers.size(); i++) {
            Server s = servers.get(i);
            if (s.getID() == this.server.getID()) {
                Server newServer = s.serve(customer, time); // need to be changed
                servers = servers.set(i, newServer);
                int maxPQ = newServer.getMaxPQ();
                ImList<Customer> served = newServer.getServed();
                ImList<Customer> pq = newServer.getPQ();
                double nextFree = newServer.getNextFreeTime();
                boolean self = newServer.isSelf();
                Server doneServer = new Server(this.server.getID(), true, nextFree, pq, maxPQ, false, served, self);
                Done = Optional.of(new Done(customer, newServer.getNextFreeTime(), doneServer));
                break;
            }
        }
        Shop updated = new Shop(servers);
        return Pair.<Optional<Event>, Shop>of(Done, updated);
    }

    @Override
    public String toString() {
        String serverID = this.server.toString();
        return String.format("%,.3f %d serves by %s", time, customer.getID(), serverID);
    }
}