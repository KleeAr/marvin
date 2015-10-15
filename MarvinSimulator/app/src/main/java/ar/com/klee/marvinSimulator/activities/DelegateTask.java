package ar.com.klee.marvinSimulator.activities;

/**
 * @author msalerno
 */
public interface DelegateTask<T> {

    void executeCallback(T result);
}
