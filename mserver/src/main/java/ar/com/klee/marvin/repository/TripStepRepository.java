package ar.com.klee.marvin.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ar.com.klee.marvin.model.TripStep;

@Repository
public interface TripStepRepository extends CrudRepository<TripStep, Long> {

}
