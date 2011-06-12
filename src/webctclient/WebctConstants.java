package webctclient;

import java.net.MalformedURLException;
import java.net.URL;

public class WebctConstants {
	/** Nottingham GCLID */
	public static final String INSTITUTION_GCLID = "URN:X-WEBCT-VISTA-V1:156bc1b3-80f3-290b-010c-b13c10f8e699";
	public static final String PROTOCOL = "http";
	public static final String HOST = "webct.nottingham.ac.uk";
	public static final int PORT = 80;
	public static final String CTX_WS_URI = "/webct/axis/";
	public static URL getUrl(String path) throws MalformedURLException {
		return new URL(PROTOCOL,HOST,PORT,path);
	}
	public static URL getWsUrl(String path) throws MalformedURLException {
		return new URL(PROTOCOL,HOST,PORT,CTX_WS_URI+path);
	}
}
