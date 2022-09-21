package cs2030.simulator;

import java.util.List;
import cs2030.util.*;

import java.util.function.Supplier;
import java.util.Optional;
import java.util.List;
import java.util.Comparator;

public class Simulate8 {
    private final PQ<Event> pq;
    private final Shop shop;
    private final ImList<Customer> customer;
    private final ImList<Double> arrivalTime;
    private final ImList<Supplier<Double>> servingTime;
    private final ImList<Supplier<Double>> restTimes;
    private final ImList<Server> serverList;
    private final ImList<Server> selfList;

    public Simulate8(int numOfServers, int numOfSelfChecks, List<Pair<Double, Supplier<Double>>> list, int qmax,
            Supplier<Double> restTimes) {
        Comparator<Event> cmp = new EventComparator();
        PQ<Event> tempPQ = new PQ<Event>(cmp);
        ImList<Customer> tempCustomer = ImList.<Customer>of();
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
        ImList<Server> ls = ImList.<Server>of();
        ImList<Server> tempServerList = ImList.<Server>of();
        ImList<Server> tempSelfList = ImList.<Server>of();
        for (int i = 0; i < numOfServers; i++) {
            Server s = new Server(i + 1, qmax);
            ls = ls.add(s);
            tempServerList = tempServerList.add(s);
        }
        for (int i = 1; i <= numOfSelfChecks; i++) {
            int id = i + numOfServers;
            Server s = new Server(id, qmax, true);
            ls = ls.add(s);
            tempSelfList = tempSelfList.add(s);
        }
        this.shop = new Shop(ls);
        ImList<Supplier<Double>> rest = ImList.<Supplier<Double>>of();
        for (int i = 0; i < tempArrivalTime.size(); i++) {
            rest = rest.add(restTimes);
        }
        this.restTimes = rest;
        this.serverList = tempServerList;
        this.selfList = tempSelfList;
    }

    public String run() {
        PQ<Event> tempPQ = this.pq;
        PQ<Event> result = this.pq;
        ImList<Server> server = shop.getServers();
        ImList<Server> selfList = this.selfList;
        ImList<Supplier<Double>> serveTime = this.servingTime;
        ImList<Double> totalWaitTime = ImList.<Double>of();
        ImList<Double> prevCustomerTime = ImList.<Double>of();
        ImList<Supplier<Double>> resting = this.restTimes;
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
            if (!e.getType().equals("WaitServe") && !e.getType().equals("Rest")) {
                result = result.add(e);
            }
            tempPQ = tempPQ.poll().second();
            Customer c = e.getCustomer();
            Server s = e.getServer();
            if (e.getType().equals("Rest")) {
                if (s.getServed().size() > 0) {
                    Event event = e.execute(curShop).first().orElse(new EventStub(c, c.getTime()));
                    tempPQ = tempPQ.add(event);
                }
                curShop = e.execute(curShop).second();
            } else if (e.getType().equals("WaitingIsServe")) {
                Event event = e.execute(curShop).first().orElse(new EventStub(c, c.getTime()));
                curShop = e.execute(curShop).second();
                numServed += 1;
                if (selfList.size() > 0) {
                    selfList = selfList.set(0, selfList.get(0).removeCustomer(c));
                }
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
                // 나중에 더 수정!
                if (served == false) {
                    for (int i = 0; i < curShop.getServers().size(); i++) {
                        Server ws = curShop.getServers().get(i);
                        if (!ws.isSelf()) {
                            if (ws.isEmpty()) {
                                double startTime = ws.getNextFreeTime();
                                Customer cus = new Customer(c.getID(), c.getTime(), startTime);
                                Event event = new Waiting(cus, cus.getTime(), ws);
                                curShop = event.execute(curShop).second();
                                tempPQ = tempPQ.add(event);
                                served = true;
                                break;
                            }
                        } else {
                            ImList<Server> servers = curShop.getServers();
                            if (selfList.get(0).isEmpty()) {
                                for (int j = 0; j < servers.size(); j++) {
                                    if (selfList.get(0).getID() == servers.get(j).getID()) {
                                        double startTime = servers.get(j).getNextFreeTime();
                                        Customer cus = new Customer(c.getID(), c.getTime(), startTime);
                                        Event event = new Waiting(cus, cus.getTime(), servers.get(j));
                                        Server newSelf = selfList.get(0).queue(c);
                                        selfList = selfList.set(0, newSelf);
                                        curShop = event.execute(curShop).second();
                                        tempPQ = tempPQ.add(event);
                                        served = true;
                                        break;
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
                if (served == false) {
                    Event event = new Leave(c, c.getTime());
                    numLeft += 1;
                    tempPQ = tempPQ.add(event);
                }
            } else if (e.getType().equals("Waiting")) {
                if (!s.isSelf()) {
                    for (int i = 0; i < serverList.size(); i++) {
                        if (serverList.get(i).getID() == s.getID()) {
                            Event event = e.execute(curShop).first().orElse(new EventStub(c, c.getTime()));
                            tempPQ = tempPQ.add(event);
                            break;
                        }
                    }
                } else {
                    boolean waitServe = false;
                    ImList<Server> tempSelfList = ImList.<Server>of();
                    ImList<Server> servers = curShop.getServers();
                    for (int i = 0; i < servers.size(); i++) {
                        if (servers.get(i).isSelf()) {
                            tempSelfList = tempSelfList.add(servers.get(i));
                        }
                    }
                    for (int j = 0; j < tempSelfList.size(); j++) {
                        if (tempSelfList.get(j).isFree()) {
                            Event event = e.execute(curShop).first().orElse(new EventStub(c, c.getTime()));
                            tempPQ = tempPQ.add(event);
                            break;
                        }
                    }
                    if (waitServe == false) {
                        double optimal = tempSelfList.get(0).getNextFreeTime();
                        int index = 0;
                        for (int k = 0; k < tempSelfList.size(); k++) {
                            if (tempSelfList.get(k).getNextFreeTime() < optimal) {
                                optimal = tempSelfList.get(k).getNextFreeTime();
                                index = k;
                            }
                        }
                        Event eve = new WaitServe(c, optimal, tempSelfList.get(index));
                        tempPQ = tempPQ.add(eve);
                    }
                }
            } else if (e.getType().equals("WaitServe")) {
                ImList<Server> servers = curShop.getServers();
                for (int i = 0; i < servers.size(); i++) {
                    if (servers.get(i).getID() == s.getID()) {
                        Server curServer = servers.get(i);
                        if (curServer.isFree() && !curServer.isRest()) {
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
                            break;
                        } else {
                            if (!s.isSelf()) {
                                Event event = new WaitServe(c, curServer.getNextFreeTime(), curServer);
                                tempPQ = tempPQ.add(event);
                            } else {
                                ImList<Server> tempSelfList = ImList.<Server>of();
                                ImList<Server> serverss = curShop.getServers();
                                for (int j = 0; j < serverss.size(); j++) {
                                    if (serverss.get(j).isSelf()) {
                                        tempSelfList = tempSelfList.add(serverss.get(j));
                                    }
                                }
                                double optimal = tempSelfList.get(0).getNextFreeTime();
                                int index = 0;
                                for (int k = 0; k < tempSelfList.size(); k++) {
                                    if (tempSelfList.get(k).getNextFreeTime() < optimal) {
                                        optimal = tempSelfList.get(k).getNextFreeTime();
                                        index = k;
                                    }
                                }
                                Event eve = new WaitServe(c, optimal, tempSelfList.get(index));
                                tempPQ = tempPQ.add(eve);
                            }
                        }
                    }
                }
            } else if (e.getType().equals("Serve")) {
                Event event = e.execute(curShop).first().orElse(new EventStub(c, c.getTime()));
                curShop = e.execute(curShop).second();
                numServed += 1;
                ImList<Server> servers = curShop.getServers();
                /*
                 * if (s.isSelf()) {
                 * for (int i = 0; i < curShop.getServers().size(); i++) {
                 * if (curShop.getServers().get(i).getID() == selfList.get(0).getID()) {
                 * int id = s.getID();
                 * boolean free = s.isFree();
                 * double ft = s.getNextFreeTime();
                 * ImList<Customer> pq = s.getPQ();
                 * int maxPQ = s.getMaxPQ();
                 * boolean rest = s.isRest();
                 * ImList<Customer> served = s.getServed();
                 * boolean self = s.isSelf();
                 * servers = servers.set(i, new Server(id, free, ft, pq, maxPQ, rest, served,
                 * self));
                 * }
                 * }
                 * }
                 */
                if (selfList.size() > 0) {
                    selfList = selfList.set(0, selfList.get(0).removeCustomer(c));
                }
                tempPQ = tempPQ.add(event);
            } else if (e.getType().equals("Done")) {
                Event event = e.execute(curShop).first().orElse(new EventStub(c, c.getTime()));
                Server se = event.getServer();
                double eveTime = e.getTime();
                curShop = e.execute(curShop).second();
                if (!s.isSelf()) {
                    ImList<Customer> served = se.getServed();
                    double restTime = 0.0;
                    int size = served.size();
                    restTime = resting.get(size - 1).get();
                    ImList<Server> servers = curShop.getServers();
                    if (restTime != 0.0) {
                        Event eve = new Rest(c, eveTime + restTime, se);
                        for (int i = 0; i < servers.size(); i++) {
                            Server ser = servers.get(i);
                            if (ser.getID() == s.getID()) {
                                double nextTime = eveTime + restTime;
                                int maxPQ = s.getMaxPQ();
                                ImList<Customer> custs = s.getServed();
                                ImList<Customer> pq = s.getPQ();
                                int id = s.getID();
                                servers = servers.set(i, new Server(id, false, nextTime, pq, maxPQ, true, custs));
                            }
                        }
                        tempPQ = tempPQ.add(eve);
                    }
                    curShop = new Shop(servers);
                }
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
}
