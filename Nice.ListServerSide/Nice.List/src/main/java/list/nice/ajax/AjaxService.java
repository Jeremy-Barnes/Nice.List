package list.nice.ajax;

import javax.ws.rs.core.HttpHeaders;

/**
 * Created by Jeremy on 7/20/2016.
 */
public class AjaxService {

	protected String getHeaderSelectorValidatorString(HttpHeaders header){
		return header.getCookies().get("nicelist") != null ? header.getCookies().get("nicelist").getValue() : header.getHeaderString("SelectorValidator");
	}

	protected String[] getHeaderSelectorValidatorArray(HttpHeaders header){
		String cookie = getHeaderSelectorValidatorString(header);
		return cookie.split(":");
	}
}
