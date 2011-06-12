package webctclient.webscraper;

import java.net.URLConnection;
import java.util.HashMap;
import java.util.LinkedList;

public class Cookies {
	/** cookie values */
	private LinkedList<String> cookies = new LinkedList<String>();
	/** cons */
	public Cookies(URLConnection conn) {
		for (int hi=0; ; hi++) {
			String val = conn.getHeaderField(hi);
			if (val==null)
				break;
			checkHeader(val);
		}
	}
	private void checkHeader(String val) {
		int ix = val.indexOf(':');
		if (ix<0)
			return;
		String name = val.substring(0, ix);
		if ("Set-Cookie".equals(name)) {
			int ix2 = val.indexOf(';', ix);
			String cookie = val.substring(ix+1).trim();
			if (ix2>0)
				cookie = val.substring(ix+1, ix2).trim();
			cookies.add(cookie);
		}
	}
	public Cookies(LinkedList<String> headers) {
		for (int hi=0; hi<headers.size(); hi++) {
			String val = headers.get(hi);
			checkHeader(val);
		}
	}
	/** get cookie header line */
	public String getRequestValue() {
		StringBuffer b = new StringBuffer();
		//b.append("Cookie: ");
		for (int i=0; i<cookies.size(); i++) {
			if (i>0)
				b.append("; ");
			b.append(cookies.get(i));
		}
		return b.toString();
	}
	public String getRequestProperty() {
		return "Cookie";
	}
	@Override
	public String toString() {
		return "Cookies [cookies=" + cookies + "]";
	}

}
