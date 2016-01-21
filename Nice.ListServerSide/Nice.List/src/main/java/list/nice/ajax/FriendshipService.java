package list.nice.ajax;

import list.nice.bll.FriendshipBLL;
import list.nice.dal.dto.User;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBElement;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;

/**
 * Created by Jeremy on 1/20/2016.
 */
@Path("/friends")
public class FriendshipService {

	@POST
	@Path("/createFriendship")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createFriendship(JAXBElement<User> friendRequester, JAXBElement<String> requestedEmailAddress,@Context HttpHeaders header) throws GeneralSecurityException, UnsupportedEncodingException {
		String cookie = header.getCookies().get("nicelist").getValue();
		String[] entry = cookie.split(":");

		FriendshipBLL.createFriendship(friendRequester.getValue(), requestedEmailAddress.getValue(), entry[0], entry[1]);

		return Response.status(Response.Status.OK).header("Access-Control-Allow-Origin", "*").build();
	}

}
