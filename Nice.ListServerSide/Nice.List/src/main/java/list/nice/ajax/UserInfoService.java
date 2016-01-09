package list.nice.ajax;

import list.nice.bll.UserBLL;
import list.nice.dal.dto.Token;
import list.nice.dal.dto.User;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import javax.xml.bind.JAXBElement;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

/**
 * Created by Jeremy on 12/30/2015.
 */
@Path("/users")
public class UserInfoService {


	@POST
	@Path("/getUser")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getUser(){
		UserBLL.getUser();
		return Response.status(Response.Status.OK).header("Access-Control-Allow-Origin", "*").build();
	}






	@POST
	@Path("/getUserFromToken")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getUserFromToken(JAXBElement<Token> token) throws GeneralSecurityException, UnsupportedEncodingException {
		Token tokenReal = token.getValue();
		return Response.status(Response.Status.OK).header("Access-Control-Allow-Origin", "*").entity(UserBLL.getUser(tokenReal.selector, tokenReal.validator)).build();
	}

	@POST
	@Path("/getUserFromLogin")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserFromLogin(JAXBElement<User> user){
		try {
			User rUser = user.getValue();
			rUser = UserBLL.getUserLogin(rUser.getEmailAddress(), rUser.getPassword());

			NewCookie cook = new NewCookie("nicelist", rUser.getTokenSelector() + ":" + rUser.getTokenValidator(), "/", null, null, 3600, false );
			return Response.status(Response.Status.OK).header("Access-Control-Allow-Origin", "*").cookie(cook).entity(rUser).build();
		} catch(Exception e ){
			return null;
		}
	}

	@POST
	@Path("/changeUserInformation")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response changeUserInformation(JAXBElement<User> user, @Context HttpHeaders header){
		try {
			String cookie = header.getCookies().get("nicelist").getValue();
			String[] entry = cookie.split(":");
			User rUser = user.getValue();
			return Response.status(Response.Status.OK).header("Access-Control-Allow-Origin", "*").entity(UserBLL.updateUser(rUser, entry[0], entry[1])).build();
		} catch(Exception e ){
			return null;
		}
	}

	@POST
	@Path("/createUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response putUser(JAXBElement<User> user) throws GeneralSecurityException, URISyntaxException, UnsupportedEncodingException {
		User userReal = user.getValue();
		String validator = UserBLL.createUser(userReal);

		NewCookie cook = new NewCookie("nicelist", userReal.getTokenSelector() + ":" + validator, "/", null, null, 3600, false );
		return Response.status(Response.Status.OK).header("Access-Control-Allow-Origin", "*").entity(userReal).cookie(cook).build();
	}
}