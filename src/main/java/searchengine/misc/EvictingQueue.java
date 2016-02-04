package searchengine.misc;

import java.util.LinkedList;

public class EvictingQueue<E> extends LinkedList<E> {
    private int limit;

    public EvictingQueue(int limit) {
        this.limit = limit;
    }

    @Override
    public boolean add(E o) {
        boolean added = super.add(o);
        while (added && size() > limit) {
            super.remove();
        }
        return added;
    }

    public boolean isFull()
    {
        if(this.size()>=limit) return true;
        return false;
    }
}
