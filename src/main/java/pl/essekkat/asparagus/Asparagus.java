package pl.essekkat.asparagus;

import java.util.Optional;
import java.util.Set;

/**
 * <p>A set with the notion of quiet period.</p>
 * This quiet period will be defined as:
 * Given: <ol>
 * <li>there is an element E and Asparagus A</li>
 * <li>at time T1 A.add(E)</li>
 * <li>at time T2 E is retrieved, e.g. by use of iterator()</li>
 * </ol>
 * Then: T2 - T1 is greater than or equal to quiet period.
 * <p>
 * Quiet period must be positive.
 * If E is inserted more than once, the latest insertion time is used.
 * Subclassed may modify the strength of quiet period enforcement, but with a clear warning.
 * </p>
 * <p>
 * The process of making element available for retrieval will be called promotions.
 * The reverse - degradation.
 * </p>
 * Created by Kamil Essekkat on 13.12.15.
 */
public interface Asparagus<T> {
    /**
     * Adds element.
     * Initially it will be unavailable.
     * If the {@literal elem} can be {@literal pop}ped it won't be added.
     *
     * @param elem Element to add.
     */
    void add(T elem);

    /**
     * Deletes the given element. If the element is subsequently added it's quiet period
     * is counted anew.
     *
     * @param elem Element to be removed.
     */
    void remove(T elem);

    /**
     * Returns and removes an element, can (but doesn't have to) be an oldest element.
     *
     * @return An element eligible for retrieval.
     */
    Optional<T> pop();

    /**
     * @param i Number of elements to {@literal pop} from the collection.
     * @return At most {@literal i} T objects.
     */
    Set<T> pop(int i);

    /**
     * @return Quiet period current value in milliseconds.
     */
    int getQuietPeriod();

    /**
     * Make all elements retrievable. Effect is the same as when all elems were older than quiet period.
     */
    void promoteAll();

    /**
     * Make no elements retrievable; as in all were added now.
     */
    void degradeAll();

    /**
     * @return The number of available objects.
     */
    int size();
}
