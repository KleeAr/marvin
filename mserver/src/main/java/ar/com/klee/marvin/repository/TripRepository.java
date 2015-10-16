package ar.com.klee.marvin.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ar.com.klee.marvin.model.Trip;
import ar.com.klee.marvin.model.TripKey;

@Repository
public interface TripRepository extends CrudRepository<Trip, TripKey> {

	Iterable<Trip> findByUserId(Long userId);

	void deleteByUserId(Long userId);
}
