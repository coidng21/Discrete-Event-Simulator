package cs2030.simulator;

import java.util.Optional;
import cs2030.util.*;

class EventStub implements Event {

    private final Customer customer;
    private final double time;
    private static final String TYPE = "Event";

    EventStub(Customer customer, double time) {
        this.customer = customer;
        this.time = time;
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
        return Pair.<Optional<Event>, Shop>of(Optional.empty(), shop);
    }

    @Override
    public String toString() {
        return String.format("%,.3f", time);
    }
}