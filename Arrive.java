package cs2030.simulator;

import java.util.Optional;
import cs2030.util.*;

class Arrive implements Event {

    private final Customer customer;
    private final double time;
    private final double serviceTime;
    private static final String TYPE = "Arrive";
    private final Server server;

    Arrive(Customer customer, double time) {
        this.customer = customer;
        this.time = customer.getTime();
        this.serviceTime = 1.0;
        this.server = new Server(0);
    }

    Arrive(Customer customer, double time, double serviceTime) {
        this.customer = customer;
        this.time = customer.getTime();
        this.serviceTime = serviceTime;
        this.server = new Server(0);
    }

    Arrive(Customer customer, double time, double serviceTime, Server server) {
        this.customer = customer;
        this.time = customer.getTime();
        this.serviceTime = serviceTime;
        this.server = server;
    }

    public double getTime() {
        return this.time;
    }

    public String getType() {
        return TYPE;
    }

    boolean serverIsFree(Shop shop) {
        ImList<Server> server = shop.getServers();
        for (int i = 0; i < server.size(); i++) {
            if (server.get(i).isFree()) {
                return true;
            }
        }
        return false;
    }

    public Server getServer() {
        return new Server(0);
    }

    public Customer getCustomer() {
        return this.customer;
    }

    @Override
    public Pair<Optional<Event>, Shop> execute(Shop shop) {
        ImList<Server> server = shop.getServers();
        Optional<Event> event = Optional.empty();
        for (int i = 0; i < server.size(); i++) {
            if (server.get(i).isFree()) {
                double doneTime = getTime() + this.serviceTime;
                int maxPQ = server.get(i).getMaxPQ();
                ImList<Customer> custs = server.get(i).getServed();
                ImList<Customer> pq = server.get(i).getPQ();
                int id = server.get(i).getID();
                boolean self = server.get(i).isSelf();
                server = server.set(i, new Server(id, false, doneTime, pq, maxPQ, false, custs, self));
                event = Optional.of(new Serve(customer, time, this.server)); // changed!
                break;
            }
        }
        Shop updated = new Shop(server);
        return Pair.<Optional<Event>, Shop>of(event, updated);
    }

    @Override
    public String toString() {
        return String.format("%,.3f %d arrives", time, customer.getID());
    }

}