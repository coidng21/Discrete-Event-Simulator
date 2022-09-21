package cs2030.simulator;

import cs2030.util.*;
import java.util.Optional;

class Wait implements Event {

    private final Customer customer;
    private final Server server;
    private final double time;
    private static final String TYPE = "Wait";

    Wait(Customer customer, double time) {
        this.customer = customer;
        this.time = time;
        this.server = new Server(0);
    }

    Wait(Customer customer, double time, Server server) {
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


    boolean serverIsFree(Shop shop) {
        ImList<Server> server = shop.getServers();
        for (int i = 0; i < server.size(); i++) {
            if (server.get(i).isFree()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Pair<Optional<Event>, Shop> execute(Shop shop) {
        ImList<Server> servers = shop.getServers();
        Optional<Event> serve = Optional.empty();
            for (int i = 0; i < servers.size(); i++) {
                Server server = servers.get(i);
                if (servers.get(i).getID() == this.server.getID()) {
                    Server newServer = this.server.queue(customer); //need change 
                    servers = servers.set(i, newServer);
                    double time = this.server.getNextFreeTime();
                    serve = Optional.of(new Serve(customer, time, server));
                    break;
                }
            } 
        Shop updated = new Shop(servers);
        return Pair.<Optional<Event>, Shop>of(serve, updated);
    }

    @Override
    public String toString() {
        String serverID = this.server.toString();
        return String.format("%,.3f %d waits at %s", time, customer.getID(), serverID);
    }
}