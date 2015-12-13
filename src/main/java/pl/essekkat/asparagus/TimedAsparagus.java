package pl.essekkat.asparagus;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * Basic implementation. Only time constraint. Also promote method has to be called manually.
 * <p/>
 * Created by Kamil Essekkat on 13.12.15.
 */
public class TimedAsparagus<T> implements Asparagus<T> {
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
    public void add(T elem) {
        this.lock.lock();
        if (!survivor.contains(elem))
            this.incoming.put(elem, getMillis());
        this.lock.unlock();
    }

    @Override
    public void remove(T elem) {
        this.lock.lock();
        this.incoming.remove(elem);
        this.survivor.remove(elem);
        this.lock.unlock();
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
        this.lock.unlock();
    }

    @Override
    public int size() {
        return survivor.size();
    }
}
