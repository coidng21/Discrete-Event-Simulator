package cs2030.simulator;

import cs2030.util.*;
import java.util.Optional;

class Leave implements Event {

    private final Customer customer;
    private final double time;
    private static final String TYPE = "Leave";

    Leave(Customer customer, double time) {
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
        Optional<Event> event = Optional.of(this);
        return Pair.<Optional<Event>, Shop>of(event, shop);
    }

    @Override
    public String toString() {
        return String.format("%,.3f %d leaves", time, customer.getID());
    }
}