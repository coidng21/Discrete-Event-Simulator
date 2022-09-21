
package cs2030.simulator;

import java.util.Comparator;

class TimeComparator implements Comparator<Double> {
    public int compare(Double t1, Double t2) {
        double diff = t1 - t2;
        if (diff < 0) {
            return 1;
        } else if (diff > 0) {
            return -1;
        } else {
            return 0;
        }
    }
}