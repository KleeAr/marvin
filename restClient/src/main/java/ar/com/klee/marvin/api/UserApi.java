package ar.com.klee.marvin.api;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import ar.com.klee.marvin.model.Trip;
import ar.com.klee.marvin.model.User;
import ar.com.klee.marvin.model.UserSetting;

public interface UserApi {

	@POST
	@Path("/users/register")
	@Consumes("application/json")
	@Produces("application/json")
	User register(User user);
	
	@GET
	@Path("/users")
	@Consumes("*/*")
	@Produces("application/json")
	List<User> getAll();
	
	@GET
	@Path("/users/{id}")
	@Consumes("*/*")
	@Produces("application/json")
	List<User> getOne(@PathParam("id")Long id);
	
	@PUT
	@Path("/users")
	@Consumes("application/json")
	@Produces("application/json")
	User update(User user);

	@DELETE
	@Path("/users/{id}")
	@Consumes("*/*")
	@Produces("*/*")
	void delete(@PathParam("id")Long id);
	
	@GET
	@Path("/users/{id}/settings")
	@Consumes("*/*")
	@Produces("application/json")
	void getSettings(@PathParam("id")Long id);
	
	@POST
	@Path("/users/{id}/settings")
	@Consumes("application/json")
	@Produces("application/json")
	UserSetting createSetting(UserSetting setting);
	
	@PUT
	@Path("/users/{id}/settings")
	@Consumes("application/json")
	@Produces("application/json")
	UserSetting updateSetting(UserSetting setting);
	
	@DELETE
	@Path("/users/{id}/settings/{key}")
	@Consumes("*/*")
	@Produces("*/*")
	void deleteSetting(@PathParam("id")Long id, @PathParam("key")String key);
	
	@GET
	@Path("/users/{id}/trips")
	@Consumes("*/*")
	@Produces("application/json")
	void getTrips(@PathParam("id")Long id);
	
	@POST
	@Path("/users/{id}/trips")
	@Consumes("application/json")
	@Produces("application/json")
	Trip createTrip(Trip trip);

	@DELETE
	@Path("/users/{id}/trips/{name}")
	@Consumes("*/*")
	@Produces("*/*")
	void deleteTrip(@PathParam("id")Long id, @PathParam("name")String name);
}
