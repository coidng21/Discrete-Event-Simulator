package cs2030.simulator;

import java.util.Comparator;

class EventComparator implements Comparator<Event> {
    public int compare(Event e1, Event e2) {
        double diff = e1.getTime() - e2.getTime();
        if (diff < 0) {
            return -1;
        } else if (diff > 0) {
            return 1;
        } else {
            if (e1.getCustomer().getID() < e2.getCustomer().getID()) {
                return -1;
            } else if (e1.getCustomer().getID() > e2.getCustomer().getID()) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
