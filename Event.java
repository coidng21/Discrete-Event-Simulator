package cs2030.simulator;

import java.util.Optional;
import cs2030.util.Pair;

interface Event {
    double getTime();

    Pair<Optional<Event>, Shop> execute(Shop shop);

    Server getServer();

    Customer getCustomer();

    String getType();
}

