package list.nice.ajax;

import list.nice.bll.WishListBLL;
import list.nice.dal.dto.Friendship;
import list.nice.dal.dto.User;
import list.nice.dal.dto.WishListItem;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import javax.xml.bind.JAXBElement;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.List;

/**
 * Created by Jeremy on 1/20/2016.
 */
@Path("/wishlist")
public class WishlistService extends AjaxService {

	@POST
	@Path("/addListItem")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addListItem(JAXBElement<WishListItem> item, @Context HttpHeaders header) throws GeneralSecurityException, UnsupportedEncodingException {
		User loggedInUser = (User) httpRequest.getSession().getAttribute("user");
		if(loggedInUser == null) {
			return Response.status(Response.Status.UNAUTHORIZED).entity("You need to log in first!").build();
		}

		WishListItem wish = item.getValue();
		return Response.status(Response.Status.OK).entity(WishListBLL.addWishListItem(wish, loggedInUser)).build();
	}

	@POST
	@Path("/editListItem")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response editListItem(JAXBElement<WishListItem> item, @Context HttpHeaders header) throws GeneralSecurityException, UnsupportedEncodingException {
		User loggedInUser = (User) httpRequest.getSession().getAttribute("user");
		if(loggedInUser == null) {
			return Response.status(Response.Status.UNAUTHORIZED).entity("You need to log in first!").build();
		}

		WishListItem wish = item.getValue();

		return Response.status(Response.Status.OK).entity(WishListBLL.updateWishListItem(wish, loggedInUser)).build();
	}

	@POST
	@Path("/getUserWishList")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserWishList(JAXBElement<Friendship> relationship, @Context HttpHeaders header) throws GeneralSecurityException, UnsupportedEncodingException {
		User loggedInUser = (User) httpRequest.getSession().getAttribute("user");
		if(loggedInUser == null) {
			return Response.status(Response.Status.UNAUTHORIZED).entity("You need to log in first!").build();
		}

		Friendship friendship = relationship.getValue();

		return Response.status(Response.Status.OK)
					   .entity(new GenericEntity<List<WishListItem>>(
								   WishListBLL.getFriendsWishList(friendship.getRequesterUserID(), friendship.getRequestedUserID(), loggedInUser)
					   ) {}).build();
	}
}
