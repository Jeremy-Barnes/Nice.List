package list.nice.ajax;

import org.eclipse.persistence.jaxb.JAXBContextProperties;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Created by Jeremy on 7/20/2016.
 */
public class AjaxService {

	@Context
	HttpServletRequest httpRequest;


	protected String getHeaderSelectorValidatorString(HttpHeaders header){
		return header.getCookies().get("nicelist") != null ? header.getCookies().get("nicelist").getValue() : header.getHeaderString("SelectorValidator");
	}

	protected String[] getHeaderSelectorValidatorArray(HttpHeaders header){
		String cookie = getHeaderSelectorValidatorString(header);
		return cookie.split(":");
	}

	protected <T> T serializeDeepCopy(T object, Class objType) throws JAXBException {
		JAXBContext jc = JAXBContext.newInstance(objType);
		Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty("eclipselink.media-type", "application/json");
		marshaller.setProperty("eclipselink.json.include-root", false);
		StringWriter sw = new StringWriter();
		marshaller.marshal(object, sw);
		Unmarshaller unmarshaller = jc.createUnmarshaller();
		unmarshaller.setProperty("eclipselink.json.include-root", false);
		unmarshaller.setProperty(JAXBContextProperties.MEDIA_TYPE, "application/json");
		T copiedObject = (T)unmarshaller.unmarshal(new StreamSource(new StringReader(sw.toString())), objType).getValue();
		return copiedObject;
	}
}
