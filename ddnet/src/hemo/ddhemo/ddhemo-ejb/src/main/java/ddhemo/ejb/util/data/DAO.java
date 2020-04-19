package ddhemo.ejb.util.data;

public interface DAO<K, E> {
    void persist(E entity);
    void remove(E entity);
    E findById(K id);
}