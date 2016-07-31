package list.nice.ajax;

import list.nice.bll.UserBLL;
import list.nice.dal.dto.Token;
import list.nice.dal.dto.User;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataMultiPart;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.*;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;

/**
 * Created by Jeremy on 12/30/2015.
 */
@Path("/users")
public class UserInfoService extends AjaxService{

	@POST
	@Path("/getUserFromToken")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response getUserFromToken(JAXBElement<Token> token) throws GeneralSecurityException, UnsupportedEncodingException, JAXBException {
		Token tokenReal = token.getValue();

		User user = UserBLL.getUser(tokenReal.selector, tokenReal.validator);
		httpRequest.getSession().setAttribute("user", user);

		User copiedUser = super.serializeDeepCopy(user, User.class);

		NewCookie cook = new NewCookie("nicelist", user.getTokenSelector() + ":" + user.getTokenValidator(), "/", null, null, 3600, false );
		NewCookie sessionID = new NewCookie("JSESSIONID", httpRequest.getSession().getId(), "/", null, null, 3600, false ); //used because setting other cookie seems to overwrite Tomcat generated cookie.

		return Response.status(Response.Status.OK)
					   .entity(UserBLL.wipeSensitiveFields(copiedUser)).build();
	}

	@POST
	@Path("/getUserFromLogin")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getUserFromLogin(JAXBElement<User> user){
		try {
			User userReal = user.getValue();
			userReal = UserBLL.getUserLogin(userReal.getEmailAddress(), userReal.getPassword());

			//quick deep copy so we can keep sensitive data in memory but wipe for sending over the wire.
			httpRequest.getSession().setAttribute("user", userReal);
			User copiedUser = super.serializeDeepCopy(userReal, User.class);

			NewCookie cook = new NewCookie("nicelist", userReal.getTokenSelector() + ":" + userReal.getTokenValidator(), "/", null, null, 3600, false );
			NewCookie sessionID = new NewCookie("JSESSIONID", httpRequest.getSession().getId(), "/", null, null, 3600, false ); //used because setting other cookie seems to overwrite Tomcat generated cookie.

			return Response.status(Response.Status.OK).cookie(cook, sessionID)
						   .entity(UserBLL.wipeSensitiveFields(copiedUser)).build();
		} catch(Exception e ){
			return null;
		}
	}

	@POST
	@Path("/createUser")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response putUser(JAXBElement<User> user) throws GeneralSecurityException, URISyntaxException, UnsupportedEncodingException, JAXBException {
		User userReal = user.getValue();
		String validator = UserBLL.createUserReturnUnHashedValidator(userReal);

		httpRequest.getSession().setAttribute("user", userReal);
		User copiedUser = super.serializeDeepCopy(userReal, User.class);

		NewCookie cook = new NewCookie("nicelist", userReal.getTokenSelector() + ":" + validator, "/", null, null, 3600, false );
		NewCookie sessionID = new NewCookie("JSESSIONID", httpRequest.getSession().getId(), "/", null, null, 3600, false ); //used because setting other cookie seems to overwrite Tomcat generated cookie.
		return Response.status(Response.Status.OK)
					   .cookie(cook, sessionID)
					   .entity(UserBLL.wipeSensitiveFields(copiedUser)).cookie(cook).build();
	}

	@POST
	@Path("/changeUserInformation")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response changeUserInformation(FormDataMultiPart form, @Context HttpHeaders header) throws JAXBException, GeneralSecurityException, IOException {
		User loggedInUser = (User) httpRequest.getSession().getAttribute("user");

		if(loggedInUser == null) {
			return Response.status(Response.Status.UNAUTHORIZED).entity("You need to log in first!").build();
		}

		//get user
		String userString = form.getField("user").getValue();
		JAXBContext jc = JAXBContext.newInstance(User.class);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		unmarshaller.setProperty("eclipselink.media-type", "application/json");
		unmarshaller.setProperty("eclipselink.json.include-root", false);
		User user = unmarshaller.unmarshal(new StreamSource(new StringReader(userString)), User.class).getValue();

		try {
			FormDataBodyPart filePart = form.getField("file");
			BufferedImage img = filePart.getValueAs(BufferedImage.class);

			user.setPictureURL(UserBLL.saveUserImage(img));
		} catch(Exception e) { //swallow exception, merely indicates bad file or no file, either way, no action required.
		}

		user = UserBLL.updateUser(user, loggedInUser);
		return Response.status(Response.Status.OK).entity(UserBLL.wipeSensitiveFields(user)).build();
	}




}