package cs2030.simulator;

import java.util.List;
import cs2030.util.*;

import java.util.function.Supplier;
import java.util.Optional;
import java.util.List;
import java.util.Comparator;

public class Simulate6 {

    private final PQ<Event> pq;
    private final Shop shop;
    private final ImList<Customer> customer;
    private final ImList<Double> arrivalTime;
    private final ImList<Supplier<Double>> servingTime;

    public Simulate6(int numOfServer, List<Pair<Double, Supplier<Double>>> list, int qmax) {
        Comparator<Event> cmp = new EventComparator();
        PQ<Event> tempPQ = new PQ<Event>(cmp);
        ImList<Customer> tempCustomer = ImList.of();
        ImList<Double> tempArrivalTime = ImList.<Double>of();
        ImList<Supplier<Double>> tempServingTime = ImList.<Supplier<Double>>of();
        for (int i = 0; i < list.size(); i++) {
            tempArrivalTime = tempArrivalTime.add(list.get(i).first());
            tempServingTime = tempServingTime.add(list.get(i).second());
        }
        this.arrivalTime = tempArrivalTime;
        this.servingTime = tempServingTime;
        for (int i = 0; i < tempArrivalTime.size(); i++) {
            Customer c = new Customer(i + 1, tempArrivalTime.get(i));
            tempCustomer = tempCustomer.add(c);
        }
        this.customer = tempCustomer;
        this.pq = tempPQ;
        ImList<Server> ls = ImList.of();
        for (int i = 0; i < numOfServer; i++) {
            Server s = new Server(i + 1, qmax);
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

    boolean serverContains(Server s, Customer c) {
        ImList<Customer> cPQ = s.getPQ();
        for (int i = 0; i < cPQ.size(); i++) {
            if (cPQ.get(i).getID() == c.getID()) {
                return true;
            }
        }
        return false;
    }

    public String run() {
        PQ<Event> tempPQ = this.pq;
        PQ<Event> result = this.pq;
        ImList<Server> server = shop.getServers();
        ImList<Supplier<Double>> serveTime = this.servingTime;
        ImList<Double> totalWaitTime = ImList.of();
        ImList<Double> prevCustomerTime = ImList.<Double>of();
        prevCustomerTime = prevCustomerTime.add(0.0);
        double avgWaitingTime = 0.0;
        int numServed = 0;
        int numLeft = 0;
        Shop curShop = this.shop;
        for (Customer c : this.customer) {
            tempPQ = tempPQ.add(new Arrive(c, c.getTime()));
        }
        while (!tempPQ.isEmpty()) {
            Event e = tempPQ.poll().first();
            if (!e.getType().equals("WaitServe")) {
                result = result.add(e);
            }
            tempPQ = tempPQ.poll().second();
            Customer c = e.getCustomer();
            Server s = e.getServer();
            if (e.getType().equals("WaitingIsServe")) {
                Event event = e.execute(curShop).first().orElse(new EventStub(c, c.getTime()));
                curShop = e.execute(curShop).second();
                numServed += 1;
                tempPQ = tempPQ.add(event);
            } else if (e.getType().equals("Arrive")) {
                boolean served = false;
                if (served == false) {
                    ImList<Server> servers = curShop.getServers();
                    for (int i = 0; i < servers.size(); i++) {
                        if (servers.get(i).isFree()) {
                            double serviceTime = serveTime.get(0).get();
                            serveTime = serveTime.remove(0).second();
                            Customer updated = new Customer(c.getID(), c.getTime(), c.getTime(), serviceTime);
                            Event arrive = new Arrive(updated, updated.getTime(), serviceTime, servers.get(i));
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
                            double startTime = ws.getNextFreeTime();
                            Customer cus = new Customer(c.getID(), c.getTime(), startTime);
                            Event event = new Waiting(cus, cus.getTime(), ws);
                            curShop = event.execute(curShop).second();
                            tempPQ = tempPQ.add(event);
                            served = true;
                            break;
                        }
                    }
                }
                if (served == false) {
                    Event event = new Leave(c, c.getTime());
                    numLeft += 1;
                    tempPQ = tempPQ.add(event);
                }
            } else if (e.getType().equals("Waiting")) {
                Event event = e.execute(curShop).first().orElse(new EventStub(c, c.getTime()));
                curShop = e.execute(curShop).second();
                tempPQ = tempPQ.add(event);
            } else if (e.getType().equals("WaitServe")) {
                ImList<Server> servers = curShop.getServers();
                Server curServer = s;
                for (int i = 0; i < servers.size(); i++) {
                    if (servers.get(i).getID() == s.getID()) {
                        curServer = servers.get(i);
                    }
                }
                if (curServer.isFree()) {
                    double startTime = curServer.getNextFreeTime();
                    double serviceTime = serveTime.get(0).get();
                    serveTime = serveTime.remove(0).second();
                    Customer cus = new Customer(c.getID(), c.getTime(), startTime, serviceTime);
                    int size = prevCustomerTime.size();
                    double prev = prevCustomerTime.get(size - 1);
                    Event event = new WaitingIsServe(cus, curServer.getNextFreeTime(), curServer); // waitIsServe
                    prevCustomerTime = prevCustomerTime.add(s.getNextFreeTime() + cus.getServiceTime());
                    double arrivalTime = cus.getTime();
                    double servedTime = cus.getServiceStartTime();
                    totalWaitTime = totalWaitTime.add(servedTime - arrivalTime);
                    tempPQ = tempPQ.add(event);
                } else {
                    int size = prevCustomerTime.size();
                    Event event = new WaitServe(c, prevCustomerTime.get(size - 1), s);
                    tempPQ = tempPQ.add(event);
                }
            } else if (e.getType().equals("Serve")) {
                Event event = e.execute(curShop).first().orElse(new EventStub(c, c.getTime()));
                curShop = e.execute(curShop).second();
                numServed += 1;
                tempPQ = tempPQ.add(event);
            } else if (e.getType().equals("Done")) {
                Event event = e.execute(curShop).first().orElse(new EventStub(c, c.getTime()));
                curShop = e.execute(curShop).second();
            }
        }
        for (int j = 0; j < totalWaitTime.size(); j++) {
            double time = totalWaitTime.get(j);
            avgWaitingTime += time;
        }
        avgWaitingTime = avgWaitingTime / numServed;
        String output = new String();
        String newLine = System.lineSeparator();
        output = result.toString();
        output = output.replace("[", "");
        output = output.replace("]", "");
        output = output.replace(",", newLine);
        output = output.replace("\n ", "\n");
        output += newLine;
        output += String.format("[%,.3f %d %d]", avgWaitingTime, numServed, numLeft);
        return output;
    }

    @Override
    public String toString() {
        return "Simulate5";
    }
}