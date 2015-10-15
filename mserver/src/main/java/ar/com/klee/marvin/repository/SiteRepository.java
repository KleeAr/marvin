package ar.com.klee.marvin.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import ar.com.klee.marvin.model.Site;
import ar.com.klee.marvin.model.TripKey;
import java.lang.Long;
import java.util.List;

@Repository
public interface SiteRepository extends CrudRepository<Site, TripKey> {

	List<Site> findByUserId(Long userid);
}
