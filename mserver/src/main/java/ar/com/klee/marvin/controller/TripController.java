package ar.com.klee.marvin.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ar.com.klee.marvin.model.Trip;
import ar.com.klee.marvin.model.TripKey;
import ar.com.klee.marvin.repository.TripRepository;

@RestController
@RequestMapping("/users/{id}/trips")
public class TripController {

	private TripRepository tripRepository;
	
	@Autowired	
	public TripController(TripRepository tripRepository) {
		super();
		this.tripRepository = tripRepository;
	}

	@RequestMapping(method = RequestMethod.GET)
	public Iterable<Trip> getAll(@PathVariable("id")Long userId) {
		return tripRepository.findByUserId(userId);
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public Trip save(@RequestBody Trip trip) {
		return tripRepository.save(trip);
	}
	
	@RequestMapping(method = RequestMethod.PUT)
	public Trip update(@RequestBody Trip trip) {
		return tripRepository.save(trip);
	}
	
	@RequestMapping(value = "/{name}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id")Long userId, @PathVariable("name") String name) {
		tripRepository.delete(new TripKey(name, userId));
	}
}
