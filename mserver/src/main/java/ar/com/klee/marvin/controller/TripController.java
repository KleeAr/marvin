package ar.com.klee.marvin.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import ar.com.klee.marvin.model.Site;
import ar.com.klee.marvin.model.Trip;
import ar.com.klee.marvin.model.TripKey;
import ar.com.klee.marvin.model.User;
import ar.com.klee.marvin.repository.SiteRepository;
import ar.com.klee.marvin.repository.TripRepository;
import ar.com.klee.marvin.repository.TripStepRepository;


@Transactional
@RestController
public class TripController {

	final private TripRepository tripRepository;
	final private TripStepRepository stepRepository;
	final private SiteRepository siteRepository;
	
	@Autowired	
	public TripController(TripRepository tripRepository, SiteRepository siteRepository, TripStepRepository stepRepository) {
		super();
		this.tripRepository = tripRepository;
		this.siteRepository = siteRepository;
		this.stepRepository = stepRepository;
	}

	@RequestMapping(method = RequestMethod.GET, value = "/users/{id}/trips")
	public Iterable<Trip> getAll(@PathVariable("id")Long userId) {
		return tripRepository.findByUserId(userId);
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/users/{id}/trips")
	public Trip save(@RequestBody Trip trip) {
		return tripRepository.save(trip);
	}
	
	@RequestMapping(method = RequestMethod.PUT, value = "/users/{id}/trips")
	public Trip update(@RequestBody Trip trip) {
		return tripRepository.save(trip);
	}

	@RequestMapping(value = "/users/{id}/trips/{name}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id")Long userId, @PathVariable("name") String name) {
		tripRepository.delete(new TripKey(name, userId));
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/users/me/trips")
	public Iterable<Trip> getAllMe(HttpSession session) {
		User user = (User) session.getAttribute("user");
		return tripRepository.findByUserId(user.getId());
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/users/me/trips")
	public Iterable<Trip> saveMe(@RequestBody List<Trip> trips, HttpSession session) {
		User user = (User) session.getAttribute("user");
		trips.forEach(trip -> {
			trip.setUserId(user.getId());
			stepRepository.save(trip.getTripPath());
		});
		tripRepository.deleteByUserId(user.getId());
		return tripRepository.save(trips);
	}

	@RequestMapping(method = RequestMethod.POST, value = "/users/me/trips/one")
	public Trip saveSingle(@RequestBody Trip trip, HttpSession session) {
		User user = (User) session.getAttribute("user");
		trip.setUserId(user.getId());
		stepRepository.save(trip.getTripPath());
		return tripRepository.save(trip);
	}
	
	@RequestMapping(method = RequestMethod.PUT, value = "/users/me/trips")
	public Iterable<Trip> update(@RequestBody List<Trip> trips, HttpSession session) {
		User user = (User) session.getAttribute("user");
		trips.forEach(trip -> {
			trip.setUserId(user.getId());
			stepRepository.save(trip.getTripPath());
		});
		
		tripRepository.deleteByUserId(user.getId());
		return tripRepository.save(trips);
	}
	
	@RequestMapping(value = "/users/me/trips/{name}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("name") String name, HttpSession session) {
		User user = (User) session.getAttribute("user");
		tripRepository.delete(new TripKey(name, user.getId()));
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/users/me/trips/sites")
	public List<Site> getAllSites(HttpSession session) {
		User user = (User) session.getAttribute("user");
		return siteRepository.findByUserId(user.getId());
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/users/me/trips/sites")
	public Iterable<Site> saveSite(@RequestBody List<Site> sites, HttpSession session) {
		User user = (User) session.getAttribute("user");
		sites.forEach(site -> site.setUserId(user.getId()));
		return siteRepository.save(sites);
	}
	
	@RequestMapping(method = RequestMethod.PUT, value = "/users/me/trips/sites")
	public Iterable<Site> updateSite(@RequestBody List<Site> sites, HttpSession session) {
		User user = (User) session.getAttribute("user");
		sites.forEach(site -> site.setUserId(user.getId()));
		return siteRepository.save(sites);
	}
	
	@RequestMapping(value = "/users/me/trips/sites/{name}", method = RequestMethod.DELETE)
	public void deleteSite(@PathVariable("name") String name, HttpSession session) {
		User user = (User) session.getAttribute("user");
		siteRepository.delete(new TripKey(name, user.getId()));
	}
}
