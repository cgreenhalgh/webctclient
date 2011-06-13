package webctclient.webscraper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.log4j.Logger;

public class Login {
	/** logger */
	static Logger logger = Logger.getLogger(Login.class);

	// for some reason this doesn't work but it should...
	public static Cookies loginBad(String surl, String body) throws IOException {
		/*
POST / HTTP/1.1
Content-Type: application/x-www-form-urlencoded
User-Agent: curl/7.20.1 (i686-pc-cygwin) libcurl/7.20.1 OpenSSL/0.9.8o zlib/1.2.3 libidn/1.18 libssh2/1.2.5
Accept: * /*
Connection: close
Host: localhost:8080
Content-Length: 202

glcid=URN%3AX%2DWEBCT%2DVISTA%2DV1%3A156bc1b3%2D80f3%2D290b%2D010c%2Db13c10f8e699&insId=4130001&gotoid=null&insName=UNIVERSITY%20OF%20NOTTINGHAM%20&timeZoneOffset=0&webctid=pszcmg&password=%7ByinkY%7D11

*/
		URL url = new URL(surl);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setDoOutput(true);
		conn.setDoInput(true);
		conn.setRequestMethod("POST");
		//String body = marshallParameters(formValues)+"\n";
		logger.info("URL: "+url);
		byte [] bytes = body.getBytes();
		int len = bytes.length;
		logger.info("post body: "+body+" ("+len+" bytes)");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
//		conn.setRequestProperty("Content-Length", ""+len);
		conn.setRequestProperty("User-Agent", "curl/7.20.1 (i686-pc-cygwin) libcurl/7.20.1 OpenSSL/0.9.8o zlib/1.2.3 libidn/1.18 libssh2/1.2.5");
		conn.setRequestProperty("Accept", "*/*");
		conn.setRequestProperty("Connection", "close");

		OutputStream os = conn.getOutputStream();
		os.write(body.getBytes(CHARSET));
		os.close();
		int status = conn.getResponseCode();
		logger.info("Login status "+status+": "+conn.getResponseMessage());
		WebPage page = WebPage.read(conn, false);
		logger.info("Response: "+page.getText());
		return new Cookies(conn);
	}

	
	public static Cookies login(String surl, String body) throws IOException {
		// this login code is hand-crafted over raw TCP
		/*
POST / HTTP/1.1
Content-Type: application/x-www-form-urlencoded
User-Agent: curl/7.20.1 (i686-pc-cygwin) libcurl/7.20.1 OpenSSL/0.9.8o zlib/1.2.3 libidn/1.18 libssh2/1.2.5
Accept: * /*
Connection: close
Host: localhost:8080
Content-Length: 202

glcid=URN%3AX%2DWEBCT%2DVISTA%2DV1%3A156bc1b3%2D80f3%2D290b%2D010c%2Db13c10f8e699&insId=4130001&gotoid=null&insName=UNIVERSITY%20OF%20NOTTINGHAM%20&timeZoneOffset=0&webctid=pszcmg&password=%7ByinkY%7D11

*/
		URL url = new URL(surl);
		int port = url.getPort();
		if (port<0)
			port = 80; // default
		logger.info("connect to "+url.getHost()+" port "+port);
		Socket s = new Socket(url.getHost(), port);
		OutputStream os = s.getOutputStream();
		//OutputStreamWriter osw = new OutputStreamWriter(os, CHARSET);
		byte [] bytes = body.getBytes();
		int len = bytes.length;
		String req = "POST "+url.getPath()+" HTTP/1.1\n"+
			"Content-Type: application/x-www-form-urlencoded\n"+
			"User-Agent: curl/7.20.1 (i686-pc-cygwin) libcurl/7.20.1 OpenSSL/0.9.8o zlib/1.2.3 libidn/1.18 libssh2/1.2.5\n"+
			"Accept: */*\n"+
			"Host: "+url.getHost()+":"+port+"\n"+
			"Connection: close\n"+
			"Content-Length: "+len+"\n"+
			"\n";
//		logger.info("Request:\n"+req+body);
		os.write(req.getBytes(CHARSET));
		os.write(bytes);
		os.flush();
		InputStream is = s.getInputStream();

		BufferedReader br = new BufferedReader(new InputStreamReader(is, CHARSET));
		String resp = br.readLine();
		System.out.println("Read: "+resp);
		
		LinkedList<String> headers = new LinkedList<String>();
		while (true) {
			String line = br.readLine();
			if (line==null || line.length()==0) 
				break;
			//System.out.println("Read: "+line);
			headers.add(line);
		}
		os.close();
		return new Cookies(headers);
	}

	private static String marshallParameters(Map<String, String> formValues) throws UnsupportedEncodingException {
		StringBuffer b = new StringBuffer();
		boolean first = true;
		for (String key : formValues.keySet()) {
			String value  = formValues.get(key);
			if (first) 
				first = false;
			else
				b.append("&");
			formValue(b, key, value);
		}
		return b.toString();
	}

	
	public static Cookies webctLogin(String username, String password) throws IOException {
		StringBuffer b = new StringBuffer();
		//HashMap<String,String> formValues = new HashMap<String,String>();
		// glcid=URN:X-WEBCT-VISTA-V1:156bc1b3-80f3-290b-010c-b13c10f8e699 
		formValue(b, "glcid", "URN:X-WEBCT-VISTA-V1:156bc1b3-80f3-290b-010c-b13c10f8e699");
		//--data-urlencode insId=4130001 
		b.append("&");
		formValue(b, "insId", "4130001");
		//--data-urlencode gotoid=null
		b.append("&");
		formValue(b, "gotoid", "null");
		// --data-urlencode "insName=UNIVERSITY OF NOTTINGHAM " 
		b.append("&");
		formValue(b, "insName", "UNIVERSITY OF NOTTINGHAM ");
		//--data-urlencode timeZoneOffset=0 
		b.append("&");
		formValue(b, "timeZoneOffset", "0");
		b.append("&");
		formValue(b, "webctid", username);
		b.append("&");
		formValue(b, "password", password);
		
		return login ("http://webct.nottingham.ac.uk/webct/authenticateUser.dowebct", b.toString());
//		return login ("http://localhost:8080/", b.toString());
			
	}
	
	private static void formValue(StringBuffer b, String string, String string2) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		formEscape(b, string);
		//b.append(URLEncoder.encode(string));
		b.append("=");
		formEscape(b, string2);
		//b.append(URLEncoder.encode(string2));
	}

	private static void formEscape(StringBuffer b, String string) throws UnsupportedEncodingException {
		String enc = URLEncoder.encode(string, "UTF-8");
		// curl does more escaping...
		b.append(enc.replace("-", "%2D").replace("+", "%20"));
	}
	static String CHARSET = "ISO-8859-1";

	public static void main(String args[]) {
		if (args.length!=2) {
			System.err.println("Usage: username password");
			System.exit(-1);
		}
		try {
			Cookies cookies = webctLogin(args[0], args[1]);
			logger.info("Login with cookies "+cookies.getRequestValue());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
}
