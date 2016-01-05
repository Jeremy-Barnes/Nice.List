package list.nice.ajax;

import list.nice.bll.UserBLL;
import list.nice.dal.dto.Token;
import list.nice.dal.dto.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

/**
 * Created by Jeremy on 12/30/2015.
 */
@Path("/users")
public class UserInfoService {

	@POST
	@Path("/getUserFromToken")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getUserFromToken(JAXBElement<Token> token) throws GeneralSecurityException, UnsupportedEncodingException {
		Token tokenReal = token.getValue();
		return Response.status(Response.Status.OK).header("Access-Control-Allow-Origin", "*").entity(UserBLL.getUser(tokenReal.selector, tokenReal.validator)).build();
	}

	@POST
	@Path("/getUserFromLogin")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserFromLogin(@FormParam("emailAddress") String emailAddress, @FormParam("password") String password){
		try {
			return Response.status(Response.Status.OK).header("Access-Control-Allow-Origin", "*").entity(UserBLL.getUserLogin(emailAddress, password)).build();
		} catch(Exception e ){
			return null;
		}
	}

	@POST
	@Path("/createUser")
	@Consumes(MediaType.APPLICATION_JSON)
	public void putUser(JAXBElement<User> user) throws GeneralSecurityException, UnsupportedEncodingException {
		UserBLL.createUser(user.getValue());
	}

	@POST
	@Path("/createUser")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response putUser(@FormParam("userID") int userID, String userName, @FormParam("firstName") String firstName, @FormParam("lastName") String lastName,
						@FormParam("emailAddress") String emailAddress, @FormParam("password") String password, @FormParam("city") String city, @FormParam("state") String state,
						@FormParam("country") String country, @FormParam("postcode") String postcode) throws GeneralSecurityException, URISyntaxException, UnsupportedEncodingException {
		User user = new User(userID, firstName, lastName, emailAddress, password, null, city, state, country, postcode, null, null);
		String validator = UserBLL.createUser(user);


		NewCookie cook = new NewCookie("nicelist", user.getTokenSelector() + ":" + validator, "/", null, null, 3600, false );
		return Response.seeOther(new URI("/")).status(Response.Status.OK).header("Access-Control-Allow-Origin", "*").cookie(cook).build();

	}
}