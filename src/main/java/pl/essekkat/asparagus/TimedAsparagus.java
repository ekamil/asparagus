package pl.essekkat.asparagus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * <p>Basic implementation. Only time constraint. Also promote method has to be called manually.
 * </p>
 * Created by Kamil Essekkat on 13.12.15.
 */
public class TimedAsparagus<T> implements Asparagus<T> {
    private final static Logger LOG = LoggerFactory.getLogger(TimedAsparagus.class);
    private final Lock lock;
    private final int quietPeriod;
    private final Map<T, Long> incoming;
    private final Set<T> survivor;

    public TimedAsparagus(int quietPeriod) {
        if (quietPeriod <= 0)
            throw new IllegalArgumentException("Quiet period must be positive");
        this.quietPeriod = quietPeriod;
        incoming = new HashMap<>();
        survivor = new HashSet<>();
        lock = new ReentrantLock();
    }

    public int getQuietPeriod() {
        return this.quietPeriod;
    }

    public void promoteAll() {
        this.lock.lock();
        survivor.addAll(incoming.keySet());
        incoming.clear();
        this.lock.unlock();
    }

    public void degradeAll() {
        this.lock.lock();
        for (T e : survivor) {
            if (incoming.containsKey(e))
                incoming.replace(e, getMillis());
            else
                incoming.put(e, getMillis());
        }
        survivor.clear();
        this.lock.unlock();
    }

    protected Long getMillis() {
        return System.currentTimeMillis();
    }

    @Override
    public boolean add(T elem) {
        boolean added = false;
        this.lock.lock();
        if (!survivor.contains(elem)) {
            this.incoming.put(elem, getMillis());
            added = true;
        }
        this.lock.unlock();
        return added;
    }

    @Override
    public boolean addAll(Collection<? extends T> elems) {
        return elems.stream()
                .map(this::add)
                .anyMatch(b -> b);
    }

    @Override
    public boolean remove(Object elem) {
        boolean changed = false;
        this.lock.lock();
        try {
            changed = this.incoming.remove(elem) != null;
            changed = changed || this.survivor.remove(elem);
        } finally {
            this.lock.unlock();
        }
        return changed;
    }

    public Optional<T> pop() {
        if (this.survivor.isEmpty())
            return Optional.empty();

        this.lock.lock();
        T elem = survivor.iterator().next();
        survivor.remove(elem);
        this.lock.unlock();

        return Optional.of(elem);
    }

    @Override
    public Set<T> pop(int i) {
        if (i < 1)
            throw new IllegalArgumentException("Cannot pop less than one elements.");

        this.lock.lock();
        final Set<T> result = this.survivor.stream()
                .limit(i)
                .collect(Collectors.toSet());
        this.survivor.removeAll(result);
        this.lock.unlock();
        return result;
    }

    /**
     * Promotes elements from {@literal incoming} into {@literal survivor}.
     */
    protected void promote() {
        this.lock.lock();

        final long now = getMillis();
        final Set<T> eden = incoming.entrySet()
                .stream()
                .filter(entry -> now - entry.getValue() >= quietPeriod)
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        eden.forEach(incoming::remove);
        // one lock could be released
        survivor.addAll(eden);
        if (!eden.isEmpty())
            afterPromotion(eden.size());
        this.lock.unlock();
    }

    protected void afterPromotion(int cnt) {
        LOG.debug("{} elements where promoted", cnt);
    }

    @Override
    public int size() {
        return survivor.size();
    }

    @Override
    public boolean isEmpty() {
        return this.incoming.isEmpty() && this.survivor.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return this.incoming.containsKey(o) || this.survivor.contains(o);
    }

    @Override
    public Iterator<T> iterator() {
        return this.survivor.iterator();
    }

    @Override
    public Object[] toArray() {
        return this.survivor.toArray();
    }

    /**
     * This method is unsupported.
     */
    @Override
    public <T1> T1[] toArray(T1[] a) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return c.stream()
                .map(this::contains)
                .anyMatch(b -> b);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return c.stream()
                .map(this::remove)
                .anyMatch(b -> b);
    }

    /**
     * This method is unsupported.
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        this.lock.lock();
        try {
            this.incoming.clear();
            this.survivor.clear();
        } finally {
            this.lock.unlock();
        }
    }
}
