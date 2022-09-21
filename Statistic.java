package cs2030.simulator;

import java.util.List;
import cs2030.util.*;

class Statistic {
    
    private final PQ<Event> pq;

    Statistic(int numOfServer, List<Double> arrivalTimes) {
        Simulate3 sim = new Simulate3(numOfServer, arrivalTimes);
        ImList<Server> ls = ImList.of();
        for (int i = 0; i < numOfServer; i++) {
            Server s = new Server(i + 1);
            ls = ls.add(s);
        }
        Shop tempShop = new Shop(ls);
        PQ<Event> tempPQ = sim.helper(tempShop);
        this.pq = tempPQ;
    }

    double getAvgWaitingTime() {
        PQ<Event> tempPQ = this.pq;
        ImList<Double> totalWaitTime = ImList.of();
        double avgWaitingTime = 0.0;
        int numServed = 0;
        int size = tempPQ.size();
        for (int i = 0; i < size; i++) {
            Event e = tempPQ.poll().first();
            tempPQ = tempPQ.poll().second();
            double arrivalTime = 0.0;
            double servedTime = 0.0;
            if (e.getType().equals("Wait")) {
                Customer customer = e.getCustomer();
                arrivalTime = customer.getTime();
                servedTime = customer.getServiceStartTime();
                totalWaitTime = totalWaitTime.add(servedTime - arrivalTime);
            } else if (e.getType().equals("Serve")) {
                numServed += 1;
            }
        }
        for (int j = 0; j < totalWaitTime.size(); j++) {
            double time = totalWaitTime.get(j);
            avgWaitingTime += time;
        }
        avgWaitingTime = avgWaitingTime / numServed;

        return avgWaitingTime;
    }

    int getNumServed() {
        int numServed = 0;
        PQ<Event> tempPQ = this.pq;
        int size = tempPQ.size();
        for (int i = 0; i < size; i++) {
            Event e = tempPQ.poll().first();
            tempPQ = tempPQ.poll().second();
            if (e.getType().equals("Serve")) {
                numServed += 1;
            }
        }
        return numServed;
    }

    int getNumLeft() {
        int numLeft = 0;
        PQ<Event> tempPQ = this.pq;
        int size = tempPQ.size();
        for (int i = 0; i < size; i++) {
            Event e = tempPQ.poll().first();
            tempPQ = tempPQ.poll().second();
            if (e.getType().equals("Leave")) {
                numLeft += 1;
            }
        }
        return numLeft;
    }

    @Override
    public String toString() {
        return String.format("[%,.3f %d %d]", getAvgWaitingTime(), getNumServed(), getNumLeft());
    }


}
