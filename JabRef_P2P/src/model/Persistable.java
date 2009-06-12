package model;

/**
 * Just to keep track of saveable object, in case future need mass save
 * @author Thien Rong
 */
public interface Persistable<T> {

    void delete();

    T load();

    void save();
}
