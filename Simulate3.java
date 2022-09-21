package cs2030.simulator;

import cs2030.util.*;

import java.util.Optional;
import java.util.List;
import java.util.Comparator;

public class Simulate3 {

    private final PQ<Event> pq;
    private final Shop shop;
    private final ImList<Customer> customer;

    public Simulate3(int numOfServer, List<Double> list) {
        Comparator<Event> cmp = new EventComparator();
        PQ<Event> tempPQ = new PQ<Event>(cmp);
        ImList<Customer> tempCustomer = ImList.of();
        for (int i = 0; i < list.size(); i++) {
            Customer c = new Customer(i + 1, list.get(i));
            tempCustomer = tempCustomer.add(c);
        }
        this.customer = tempCustomer;
        this.pq = tempPQ;
        ImList<Server> ls = ImList.of();
        for (int i = 0; i < numOfServer; i++) {
            Server s = new Server(i + 1);
            ls = ls.add(s);
        }
        this.shop = new Shop(ls);
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

    Server fastest(Shop shop) {
        ImList<Server> server = shop.getServers();
        return server.get(0);
    }

    PQ<Event> helper(Shop shop) {;
        PQ<Event> tempPQ = this.pq;
        PQ<Event> result = this.pq;
        ImList<Server> server = shop.getServers();
        ImList<Event> servingList = ImList.<Event>of();
        ImList<Customer> waitingList = ImList.<Customer>of();
        Shop curShop = this.shop;
        for (Customer c : this.customer) {
            tempPQ = tempPQ.add(new Arrive(c, c.getTime()));
        }
        while (!tempPQ.isEmpty()) {
            Event e = tempPQ.poll().first();
            result = result.add(e);
            tempPQ = tempPQ.poll().second();
            Customer c = e.getCustomer();
            Server s = e.getServer();
            if (e.getType().equals("Arrive")) {
                boolean served = false;
                if (served == false) {
                    ImList<Server> servers = curShop.getServers();
                    for (int i = 0; i < servers.size(); i++) {
                        if (servers.get(i).isFree()) {
                            Customer updated = new Customer(c.getID(), c.getTime(), c.getTime(), 1.0);
                            Event arrive = new Arrive(updated, updated.getTime(), 1.0, servers.get(i));
                            Event event = arrive.execute(curShop).first().orElse(new EventStub(c, c.getTime()));
                            curShop = arrive.execute(curShop).second();
                            tempPQ = tempPQ.add(event);
                            served = true;
                            break;
                        }
                    }
                }
                if (served == false) {
                    for (int i = 0; i < curShop.getServers().size(); i++) {
                        Server ws = curShop.getServers().get(i);
                        if (ws.isEmpty()) {
                            Customer cus = new Customer(c.getID(), c.getTime(), ws.getNextFreeTime(), 1.0);
                            Event event = new Wait(cus, cus.getTime(), ws);
                            tempPQ = tempPQ.add(event);
                            served = true;
                            break;
                        }
                    }
                }
                if (served == false) {
                    Event event = new Leave(c, c.getTime());
                    tempPQ = tempPQ.add(event);
                }
            } else if (e.getType().equals("Wait")) {
                Event event = e.execute(curShop).first().orElse(new EventStub(c, c.getTime()));
                curShop = e.execute(curShop).second();
                tempPQ = tempPQ.add(event);
            } else if (e.getType().equals("Serve")) {
                Event event = e.execute(curShop).first().orElse(new EventStub(c, c.getTime()));
                curShop = e.execute(curShop).second();
                tempPQ = tempPQ.add(event);
            } else if (e.getType().equals("Done")) {
                Event event = e.execute(curShop).first().orElse(new EventStub(c, c.getTime()));
                Server se = event.getServer();
                curShop = e.execute(curShop).second();
            }
        }
        return result;
    }

    public String run() {
        String output = new String();
        String newLine = System.lineSeparator();
        output = helper(this.shop).toString();
        output = output.replace("[", "");
        output = output.replace("]", "");
        output = output.replace(",", newLine);
        output = output.replace("\n ", "\n");
        output += newLine;
        output += "-- End of Simulation --";
        return output;
    }

    @Override
    public String toString() {
        return "Queue: " + helper(this.shop).toString() + "; Shop: " + this.shop.toString();
    }
}
