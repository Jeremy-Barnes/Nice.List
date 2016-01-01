package list.nice.ajax;

import list.nice.bll.UserBLL;
import list.nice.dal.dto.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBElement;

/**
 * Created by Jeremy on 12/30/2015.
 */
@Path("/users")
public class UserInfoService {
//
//	@GET
//	@Path("/{param}")
//	public Response getTest(@PathParam("param") String message) {
//		String output = "Hello World";
//		return Response.status(200).entity(output).build();
//	}

	@GET
	@Path("/getAll")
	@Produces(MediaType.APPLICATION_JSON)
	public User[] getUsers() {
		return UserBLL.getAllUsers();
	}

	@POST
	@Path("/createUser")
	@Consumes(MediaType.APPLICATION_JSON)
	public void putUser(JAXBElement<User> user) {
		UserBLL.createUser(user.getValue());
	}


	@POST
	@Path("/createUser")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public void putUser(@FormParam("userID") int userID, @FormParam("userName") String userName, @FormParam("firstName") String firstName, @FormParam("lastName") String lastName,
						@FormParam("emailAddress") String emailAddress, @FormParam("password") String password, @FormParam("city") String city, @FormParam("state") String state,
						@FormParam("country") String country, @FormParam("postcode") String postcode) {
		UserBLL.createUser(new User(userID, userName, firstName, lastName, emailAddress, password, null, city, state, country, postcode));
	}
}