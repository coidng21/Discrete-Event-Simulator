package cs2030.simulator;

import java.util.Comparator;

class ServerCompartor implements Comparator<Server> {
    public int compare(Server s1, Server s2) {
        double diff = s1.getNextFreeTime() - s2.getNextFreeTime();
        if (diff < 0) {
            return -1;
        } else if (diff > 0) {
            return 1;
        } else {
            return 0;
        }
    }
}
