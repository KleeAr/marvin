package ar.com.klee.marvin.api;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Cookie;

import ar.com.klee.marvin.model.LoginRequest;
import ar.com.klee.marvin.model.LoginResponse;
import ar.com.klee.marvin.model.TripRepresentation;
import ar.com.klee.marvin.model.User;
import ar.com.klee.marvin.model.UserSetting;

public interface UserApi {

	@POST
	@Path("/users/register")
	@Consumes("application/json")
	@Produces("application/json")
	User register(User user);
	
	@POST
	@Path("/users/auth")
	@Consumes("application/json")
	@Produces("application/json")
	LoginResponse authenticate(LoginRequest loginRequest);
	
	@GET
	@Path("/users")
	@Consumes("*/*")
	@Produces("application/json")
	List<User> getAll(@CookieParam("JSESSIONID")List<Cookie> cookie);
	
	@GET
	@Path("/users/me")
	@Consumes("*/*")
	@Produces("application/json")
	List<User> getOne(@CookieParam("JSESSIONID")List<Cookie> cookie);
	
	@PUT
	@Path("/users")
	@Consumes("application/json")
	@Produces("application/json")
	User update(User user, @CookieParam("JSESSIONID")List<Cookie> cookie);
	
	@GET
	@Path("/users/me/settings")
	@Consumes("*/*")
	@Produces("application/json")
	void getSettings(@CookieParam("JSESSIONID")List<Cookie> cookie);
	
	@POST
	@Path("/users/me/settings")
	@Consumes("application/json")
	@Produces("application/json")
	UserSetting createSetting(UserSetting setting, @CookieParam("JSESSIONID")List<Cookie> cookie);
	
	@PUT
	@Path("/users/me/settings")
	@Consumes("application/json")
	@Produces("application/json")
	UserSetting updateSetting(UserSetting setting, @CookieParam("JSESSIONID")List<Cookie> cookie);
	
	@DELETE
	@Path("/users/me/settings/{key}")
	@Consumes("*/*")
	@Produces("*/*")
	void deleteSetting(@PathParam("key")String key, @CookieParam("JSESSIONID")List<Cookie> cookie);
	
	@GET
	@Path("/users/me/trips")
	@Consumes("*/*")
	@Produces("application/json")
	void getTrips(@CookieParam("JSESSIONID")List<Cookie> cookie);
	
	@POST
	@Path("/users/me/trips")
	@Consumes("application/json")
	@Produces("application/json")
	TripRepresentation createTrip(TripRepresentation trip, @CookieParam("JSESSIONID")List<Cookie> cookie);

	@DELETE
	@Path("/users/me/trips/{name}")
	@Consumes("*/*")
	@Produces("*/*")
	void deleteTrip(@PathParam("name")String name, @CookieParam("JSESSIONID")List<Cookie> cookie);
}
