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
import java.io.*;
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
		return Response.status(Response.Status.OK).header("Access-Control-Allow-Origin", "*").entity(UserBLL.wipeSensitiveFields(UserBLL.getUser(tokenReal.selector, tokenReal.validator))).build();
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
			return Response.status(Response.Status.OK).header("Access-Control-Allow-Origin", "*").cookie(cook).entity(UserBLL.wipeSensitiveFields(rUser)).build();
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
		return Response.status(Response.Status.OK).header("Access-Control-Allow-Origin", "*").entity(UserBLL.wipeSensitiveFields(userReal)).cookie(cook).build();
	}

	@POST
	@Path("/changeUserInformation")
	@Consumes(MediaType.MULTIPART_FORM_DATA)
	@Produces(MediaType.APPLICATION_JSON)
	public Response changeUserInformation(FormDataMultiPart form, @Context HttpHeaders header) throws JAXBException, GeneralSecurityException, IOException {

		//get cookie
		String cookie = header.getCookies().get("nicelist").getValue();
		String[] entry = cookie.split(":");

		//get user
		String userString = form.getField("user").getValue();
		JAXBContext jc = JAXBContext.newInstance(User.class);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		unmarshaller.setProperty("eclipselink.media-type", "application/json");
		unmarshaller.setProperty("eclipselink.json.include-root", false);
		User user = unmarshaller.unmarshal(new StreamSource(new StringReader(userString)), User.class).getValue();

		user = UserBLL.updateUser(user, entry[0], entry[1]);

		//get file TODO add to user
		FormDataBodyPart filePart = form.getField("file");
		InputStream fileInputStream = filePart.getValueAs(InputStream.class);
		OutputStream out = new FileOutputStream(new File("C:\\Users\\Jeremy\\Desktop\\Test.jpg")); // TODO not on my desktop also correct file ext
		int read = 0;
		byte[] bytes = new byte[1024];

		while ((read = fileInputStream.read(bytes)) != -1) {
			out.write(bytes, 0, read);
		}
		out.flush();
		out.close();

		return Response.status(Response.Status.OK).header("Access-Control-Allow-Origin", "*").entity(user).build();
	}




}