package list.nice.ajax;

import list.nice.bll.FriendshipBLL;
import list.nice.dal.dto.Friendship;
import list.nice.dal.dto.User;
import list.nice.dal.dto.UserFriendAddContainer;

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
public class FriendshipService extends AjaxService {

	@POST
	@Path("/createFriendship")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response createFriendship(JAXBElement<UserFriendAddContainer> request,@Context HttpHeaders header) throws GeneralSecurityException, UnsupportedEncodingException {
		User loggedInUser = (User) httpRequest.getSession().getAttribute("user");
		if(loggedInUser == null) {
			return Response.status(Response.Status.UNAUTHORIZED).entity("You need to log in first!").build();
		}

		UserFriendAddContainer req = request.getValue();
		FriendshipBLL.createFriendship(req.getUser(), req.getRequestedEmailAddress(), loggedInUser);

		return Response.status(Response.Status.OK).build();
	}

	@POST
	@Path("/respondToFriendRequest")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response updateFriendship(JAXBElement<Friendship> request,@Context HttpHeaders header) throws GeneralSecurityException, UnsupportedEncodingException {
		User loggedInUser = (User) httpRequest.getSession().getAttribute("user");
		if(loggedInUser == null) {
			return Response.status(Response.Status.UNAUTHORIZED).entity("You need to log in first!").build();
		}

		Friendship req = request.getValue();
		if(req.isAccepted()){
			FriendshipBLL.acceptFriendRequest(req, loggedInUser);
		} else {
			FriendshipBLL.deleteFriendRequest(req, loggedInUser);
		}

		return Response.status(Response.Status.OK).build();
	}
}
