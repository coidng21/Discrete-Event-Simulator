package cs2030.util;

import java.util.Comparator;
import java.util.PriorityQueue;

public class PQ<T> {

    private final PriorityQueue<T> pq;

    public PQ() {
        this.pq = new PriorityQueue<T>();
    }

    public PQ(Comparator<? super T> cmp) {
        this.pq = new PriorityQueue<T>(cmp);
    }

    public PQ(PriorityQueue<T> pq) {
        this.pq = new PriorityQueue<T>(pq);
    }

    public PQ<T> add(T t) {
        PQ<T> newPQ = new PQ<T>(this.pq);
        newPQ.pq.add(t);
        return newPQ;
    }

    public Pair<T, PQ<T>> poll() {
        PQ<T> newPQ = new PQ<T>(this.pq);
        return Pair.<T, PQ<T>>of(newPQ.pq.poll(), newPQ);
    }

    public boolean isEmpty() {
        return this.pq.isEmpty();
    }

    public int size() {
        return this.pq.size();
    }

    @Override
    public String toString() {
        return this.pq.toString();
    }
}

