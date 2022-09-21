package cs2030.simulator;

import cs2030.util.*;

class Server {

    private final int id;
    private final boolean free;
    private final double nextFreeTime;
    private final ImList<Customer> customerPQ;
    private final int maxPQ;
    private final boolean rest;
    private final ImList<Customer> customerServed;
    private final boolean self;

    Server(int id) {
        this.id = id;
        this.free = true;
        this.nextFreeTime = 0;
        this.customerPQ = ImList.<Customer>of();
        this.maxPQ = 1;
        this.rest = false;
        this.customerServed = ImList.<Customer>of();
        this.self = false;
    }

    Server(int id, int maxPQ) {
        this.id = id;
        this.free = true;
        this.nextFreeTime = 0;
        this.customerPQ = ImList.<Customer>of();
        this.maxPQ = maxPQ;
        this.rest = false;
        this.customerServed = ImList.<Customer>of();
        this.self = false;
    }

    Server(int id, int maxPQ, boolean self) {
        this.id = id;
        this.free = true;
        this.nextFreeTime = 0;
        this.customerPQ = ImList.<Customer>of();
        this.maxPQ = maxPQ;
        this.rest = false;
        this.customerServed = ImList.<Customer>of();
        this.self = self;
    }

    Server(int id, boolean free, double nextFreeTime, int maxPQ) {
        this.id = id;
        this.free = free;
        this.nextFreeTime = nextFreeTime;
        this.customerPQ = ImList.<Customer>of();
        this.maxPQ = maxPQ;
        this.rest = false;
        this.customerServed = ImList.<Customer>of();
        this.self = false;
    }

    Server(int id, boolean free, double ft, ImList<Customer> pq, int maxPQ, boolean rest, ImList<Customer> served) {
        this.id = id;
        this.free = free;
        this.nextFreeTime = ft;
        this.customerPQ = pq;
        this.maxPQ = maxPQ;
        this.rest = rest;
        this.customerServed = served;
        this.self = false;
    }

    Server(int id, boolean free, double ft, ImList<Customer> pq, int maxPQ, boolean rest, ImList<Customer> served,
            boolean self) {
        this.id = id;
        this.free = free;
        this.nextFreeTime = ft;
        this.customerPQ = pq;
        this.maxPQ = maxPQ;
        this.rest = rest;
        this.customerServed = served;
        this.self = self;
    }

    int getID() {
        return this.id;
    }

    boolean isFree() {
        return this.free;
    }

    double getNextFreeTime() {
        return this.nextFreeTime;
    }

    boolean isEmpty() {
        return customerPQ.size() < this.maxPQ;
    }

    int getMaxPQ() {
        return this.maxPQ;
    }

    boolean isRest() {
        return this.rest;
    }

    boolean isSelf() {
        return this.self;
    }

    ImList<Customer> getPQ() {
        return this.customerPQ;
    }

    ImList<Customer> getServed() {
        return this.customerServed;
    }

    Server queue(Customer c) {
        ImList<Customer> pq = this.customerPQ;
        pq = pq.add(c);
        return new Server(id, free, nextFreeTime, pq, this.maxPQ, this.rest, this.customerServed, this.self);
    }

    Server serve(Customer c, double t) {
        ImList<Customer> pq = this.customerPQ;
        pq = pq.sort(new CustomerComparator());
        if (pq.size() > 0) {
            pq = pq.remove(0).second();
        }
        ImList<Customer> served = this.customerServed;
        served = served.add(c);
        double startTime = c.getServiceStartTime();
        double serviceTime = c.getServiceTime();
        double doneTime = startTime + serviceTime;
        boolean self = this.isSelf();
        return new Server(this.id, false, doneTime, pq, this.maxPQ, this.rest, served, self); // 1.0 ->
        // customer.serviceEndTime
    }

    Server serve(double time, Customer c) {
        return new Server(this.id, false, time + 1.0, this.maxPQ);
    }

    Server removeCustomer(Customer c) {
        ImList<Customer> pq = this.customerPQ;
        pq = pq.sort(new CustomerComparator());
        if (pq.size() > 0) {
            pq = pq.remove(0).second();
        }
        ImList<Customer> served = this.customerServed;
        boolean self = this.isSelf();
        return new Server(this.id, false, this.getNextFreeTime(), pq, this.maxPQ, this.rest, served, self);
    }

    @Override
    public String toString() {
        if (isSelf()) {
            return String.format("self-check %d", id);
        }
        return String.format("%d", id);
    }
}