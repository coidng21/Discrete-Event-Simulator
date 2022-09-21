package cs2030.simulator;

import java.util.List;
import java.util.Comparator;
import cs2030.util.Pair;
import cs2030.util.ImList;
import cs2030.util.PQ;

public class Simulate2 {

    private final PQ<Event> pq;
    private final Shop shop;

    public Simulate2(int numOfServer, List<Double> list) {
        Comparator<Event> cmp = new EventComparator();
        PQ<Event> tempPQ = new PQ<Event>(cmp);
        for (int i = 0; i < list.size(); i++) {
            Customer c = new Customer(i + 1, list.get(i));
            tempPQ = tempPQ.add(new EventStub(c, list.get(i)));
        }
        this.pq = tempPQ;
        ImList<Server> ls = ImList.of();
        for (int i = 0; i < numOfServer; i++) {
            Server s = new Server(i + 1);
            ls = ls.add(s);
        }
        this.shop = new Shop(ls);
    }

    public String run() {
        String output = new String();
        String newLine = System.lineSeparator();
        PQ<Event> tempPQ = this.pq;
        for (int i = 0; i < pq.size(); i++) {
            output += tempPQ.poll().first();
            output += newLine;
            tempPQ = tempPQ.poll().second();
        }
        output += "-- End of Simulation --";
        return output;
    }

    @Override
    public String toString() {
        return "Queue: " + this.pq.toString() + "; Shop: " + this.shop.toString();
    }
}

