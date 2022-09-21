package cs2030.simulator;

import java.util.List;
import cs2030.util.*;

public class Simulate4 {

    private final Statistic stat;
    private final String sim3;

    public Simulate4(int numOfServers, List<Double> arrivalTimes) {
        Statistic tempStat = new Statistic(numOfServers, arrivalTimes);
        this.stat = tempStat;
        Simulate3 sim = new Simulate3(numOfServers, arrivalTimes);
        this.sim3 = sim.run();
    }

    public String run() {
        String output = new String();
        String newLine = System.lineSeparator();
        output += this.sim3;
        output = output.replace("\n-- End of Simulation --", "");
        output += newLine;
        output += stat.toString();
        return output;
    }
}

