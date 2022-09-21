package cs2030.simulator;

import java.util.List;
import cs2030.util.Pair;
import cs2030.util.ImList;

class Shop {

    private final ImList<Server> list;

    Shop(List<Server> list) {
        this.list = ImList.of(list);
    }

    Shop(ImList<Server> list) {
        this.list = ImList.of(list);
    }

    ImList<Server> getServers() {
        return this.list;
    }

    @Override
    public String toString() {
        return this.list.toString();
    }
}

