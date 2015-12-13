package pl.essekkat.asparagus.persistence;

import java.io.Serializable;
import java.util.Collection;

/**
 * Implementation of this interface should persist {@link Entry}s.
 * Query methods should use some locking.
 * Since hashCode is used to determine uniqueness - queue has limited size.
 * <p/>
 * Created by Kamil Essekkat on 13.12.15.
 */
public interface EntryStore<T extends Serializable> {
    Entry<T> save(Entry<T> entry);

    void delete(Entry<T> entry);

    Collection<Entry<T>> findByCollection(String collectionName);

    Collection<Entry<T>> findByCollectionAndGeneration(String collectionName, Generation generation);

    boolean exists(String collectionName, int valueHash);
}
