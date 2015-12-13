package pl.essekkat.asparagus.persistence;

import java.io.Serializable;
import java.util.Objects;

/**
 * Immutable object representing {@link pl.essekkat.asparagus.Asparagus} insides.
 * All parameters contribute to uniqueness.
 * <p/>
 * Created by Kamil Essekkat on 13.12.15.
 */
public class Entry<T extends Serializable> {
    private final String collectionName;
    private final Generation generation;
    private final T value;
    private final int mtime;

    public Entry(String collectionName, Generation generation, T value, int mtime) {
        Objects.requireNonNull(collectionName, "Collection name is needed to correlate with Asparagus");
        Objects.requireNonNull(generation);
        Objects.requireNonNull(value, "Nulls are not supported by Asparagus");
        this.collectionName = collectionName;
        this.generation = generation;
        this.value = value;
        this.mtime = mtime;
    }

    public Generation getGeneration() {
        return generation;
    }

    public T getValue() {
        return value;
    }

    public int getMtime() {
        return mtime;
    }

    public String getCollectionName() {

        return collectionName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entry<?> entry = (Entry<?>) o;

        if (getMtime() != entry.getMtime()) return false;
        if (!getCollectionName().equals(entry.getCollectionName())) return false;
        if (getGeneration() != entry.getGeneration()) return false;
        return getValue().equals(entry.getValue());

    }

    @Override
    public int hashCode() {
        int result = getCollectionName().hashCode();
        result = 31 * result + getGeneration().hashCode();
        result = 31 * result + getValue().hashCode();
        result = 31 * result + getMtime();
        return result;
    }
}
