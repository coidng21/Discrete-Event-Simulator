package cs2030.simulator;
import java.util.Comparator;

class CustomerComparator implements Comparator<Customer> {
    public int compare(Customer c1, Customer c2) {
        return c1.getID() - c2.getID();
    }
}