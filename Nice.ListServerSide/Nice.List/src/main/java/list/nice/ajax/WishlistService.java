package list.nice.ajax;

import list.nice.bll.WishListBLL;
import list.nice.dal.dto.WishListItem;

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
@Path("/wishlist")
public class WishlistService {

	@POST
	@Path("/addListItem")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response addListItem(JAXBElement<WishListItem> item, @Context HttpHeaders header) throws GeneralSecurityException, UnsupportedEncodingException {
		String cookie = header.getCookies().get("nicelist").getValue();
		String[] entry = cookie.split(":");
		WishListItem wish = item.getValue();
		WishListBLL.addWishListItem(wish, entry[0], entry[1]);

		return Response.status(Response.Status.OK).header("Access-Control-Allow-Origin", "*").build();
	}
}
