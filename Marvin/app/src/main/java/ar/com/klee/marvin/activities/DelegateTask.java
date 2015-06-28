package ar.com.klee.marvin.activities;

/**
 * @author msalerno
 */
public interface DelegateTask<T> {

    void executeCallback(T result);
}
