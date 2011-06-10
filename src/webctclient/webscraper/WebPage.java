package webctclient.webscraper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.Date;

import org.apache.log4j.Logger;

public class WebPage {
	/** logger */
	static Logger logger = Logger.getLogger(WebPage.class);
	/** content */
	private byte [] bytes;
	private String text;
	private String contentType;
	private String encoding;
	public WebPage(byte [] bytes, int length, String contentType, String encoding) {
		this.bytes = new byte[length];
		System.arraycopy(bytes, 0, this.bytes, 0, length);
		this.contentType = contentType;
		this.encoding = encoding;
		// TODO Auto-generated constructor stub
	}
	
	public String getText() throws UnsupportedEncodingException {
		if (text==null) {
			if (encoding!=null)
				text = new String(bytes, encoding);
			else 
				text = new String(bytes); // charset??
		}
		return text;
	}

	/**
	 * @return the bytes
	 */
	public byte[] getBytes() {
		return bytes;
	}

	/**
	 * @return the contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * @return the encoding
	 */
	public String getEncoding() {
		return encoding;
	}

	public static WebPage download(String surl, Cookies cookies) throws MalformedURLException, IOException {
		return download(surl, cookies, null);
	}
	public static WebPage download(String surl, Cookies cookies, String referer) throws MalformedURLException, IOException {
		URL url = new URL(surl);
		HttpURLConnection conn = (HttpURLConnection)url.openConnection();
		conn.setRequestProperty(cookies.getRequestProperty(), cookies.getRequestValue());
		if (referer!=null)
			conn.setRequestProperty("Referer", referer);
		return read(conn);
	}
	public static WebPage read(HttpURLConnection conn) throws IOException {
		String contentType = conn.getContentType();
		logger.info("Content type: "+contentType);
		String encoding = conn.getContentEncoding();
		return read(conn.getInputStream(), contentType, encoding);
	}
	public static WebPage read(InputStream is) throws IOException {
		return read(is, null, null);
	}
	static WebPage read(InputStream is, String contentType, String encoding) throws IOException {
		byte buf [] = new byte[10000];
		int count = 0;
		while(true) {
			if (count>=buf.length) {
				byte nbuf[] = new byte[buf.length*4];
				System.arraycopy(buf, 0, nbuf, 0, count);
				buf = nbuf;				
			}
			int n = is.read(buf, count, buf.length-count);
			if (n<0)
				break;
			count += n;
		}
		try {
			is.close();
		}
		catch (Exception e) {
			// ignore
		}
		WebPage page = new WebPage(buf, count, contentType, encoding);
		return page;
	}
	public static WebPage read(File file) throws IOException {
		return read(new FileInputStream(file), null, "UTF-8");// assumed encoding!
	}
	public void write(File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(bytes);
		fos.close();
		logger.debug("Wrote WebPage to "+file);
	}
	
}
