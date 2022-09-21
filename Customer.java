package cs2030.simulator;

class Customer {

    private final int id;
    private final double time;
    private final double serviceStartTime;
    private final double serviceTime;
    private final boolean waitServed;

    Customer(int id, double time) {
        this.id = id;
        this.time = time;
        this.serviceStartTime = 0;
        this.serviceTime = 0;
        this.waitServed = false;
    }

    Customer(int id, double time, double serviceStartTime) {
        this.id = id;
        this.time = time;
        this.serviceStartTime = serviceStartTime;
        this.serviceTime = 1.0;
        this.waitServed = false;
    }

    Customer(int id, double time, double serviceStartTime, double serviceTime) {
        this.id = id;
        this.time = time;
        this.serviceStartTime = serviceStartTime;
        this.serviceTime = serviceTime;
        this.waitServed = false;
    }

    Customer(int id, double time, double serviceStartTime, boolean waitServed) {
        this.id = id;
        this.time = time;
        this.serviceStartTime = serviceStartTime;
        this.serviceTime = 1.0;
        this.waitServed = waitServed;
    }

    int getID() {
        return this.id;
    }

    double getTime() {
        return this.time;
    }

    double getServiceStartTime() {
        return this.serviceStartTime;
    }

    double getServiceTime() {
        return this.serviceTime;
    }

    boolean getWaitServed() {
        return this.waitServed;
    }

    @Override
    public String toString() {
        return String.format("%d", id);
    }
}



