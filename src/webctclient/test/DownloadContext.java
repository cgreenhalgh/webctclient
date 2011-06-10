package webctclient.test;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.SimpleLog;
import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import webctclient.webscraper.ContentItem;
import webctclient.webscraper.ContentPage;
import webctclient.webscraper.Cookies;
import webctclient.webscraper.Login;
//import PasswordUtils;
import webctclient.webscraper.WebPage;

import com.webct.platform.sdk.context.client.ContextSDK;
import com.webct.platform.sdk.context.gen.ContextException;
import com.webct.platform.sdk.context.gen.LearningCtxtVO;
import com.webct.platform.sdk.context.gen.SessionVO;

/**
 * 
 */

/**
 * @author cmg
 *
 */
public class DownloadContext {
	/** logger */
	static Logger logger = Logger.getLogger(DownloadContext.class);

	public static final String USAGE = "username [contextid] ...";
	
	private static final String CTX_WS_URI = "/webct/axis/Context";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length<1) {
			logger.error("Usage: "+USAGE);
			System.exit(-1);
		}
		String username = args[0];
		URL serviceUrl = null;
		ContextSDK ctxt = null;
		SessionVO session = null;
		try {
			String password = PasswordUtils.getPassword(username);
			serviceUrl = new URL( TestConstants.PROTOCOL, TestConstants.HOST, TestConstants.PORT, CTX_WS_URI );
			ctxt = new ContextSDK( serviceUrl );
			String version = ctxt.getReleaseVersion();
			//logger.info( "Checking Server Compatibility w. version " + version + "..." );
			boolean comp = ctxt.isCompatibleWith( version );
			if ( !comp ) {
				String msg = "Server reported incompatibility with version " + version ;
				throw new Exception( msg );
			}
			session = ctxt.login( username, password, TestConstants.INSTITUTION_GCLID);
			logger.info("logged in");
			
			if (args.length==1)
				printContexts(ctxt, session);
			else {
				for (int ai=1; ai<args.length; ai++) {
					dumpContext(ctxt, session, username, password, Long.parseLong(args[ai]));
				}
			}
			
			
		} catch (Exception e) {
			logger.error("Error", e);
		}
		if (session!=null) {
			try {
				ctxt.logout( session );
				logger.info("logged out");
			}
			catch (Exception e) {
				logger.error("Logging out",e);
			}
		}
		logger.info("done");
	}
	private static void printContexts(ContextSDK ctxt, SessionVO session) throws ContextException, RemoteException {
		// TODO Auto-generated method stub
		long[] lcArray = ctxt.getLearningContextIDs( session );
		ArrayList lcList = new ArrayList();
		for ( int i = 0; lcArray != null && i < lcArray.length; i++ ) {
			//log.trace( "Getting data for LC " + lcArray[ i ] + "..." );
			LearningCtxtVO lc = ctxt.getLearningContext( session, lcArray[ i ] );
			lcList.add( lc );
			System.out.println(lcArray[i]+" '" + lc.getName() + "' ("+lc.getLabel()+", sourcedID="+lc.getSourcedID().getSource()+"/"+lc.getSourcedID().getMyID()+", template="+lc.getTemplID()+"):" );
		}

	}
	private static void dumpContext(ContextSDK ctxt, SessionVO session, String username, String password, long id) throws IOException, JSONException {
		// TODO Auto-generated method stub
		LearningCtxtVO lc = ctxt.getLearningContext( session, id );

		File userdir = new File(username);
		userdir.mkdir();

		Cookies cookies = Login.webctLogin(username, password);
		logger.info("Login with cookies "+cookies.getRequestValue());

		logger.info("Download context content...");
		String url = "http://webct.nottingham.ac.uk/webct/urw/lc"+id+".tp"+lc.getTemplID()+"/CourseContentDispatch.dowebct?tab=view";
		WebPage page = WebPage.download(url, cookies);
		
		File courseDir = new File(userdir, ""+id);
		courseDir.mkdirs();
		page.write(new File(courseDir, "course.html"));
		
		ContentPage cp = new ContentPage(page);

		logger.info("Read: "+cp);
		for (ContentItem item : cp.getItems()) {
			logger.info("Item: "+item);
			item.followItem(cookies, courseDir);
		}
		
		JSONObject jo = cp.toJson();
		File json = new File(userdir, "course"+id+".json");
		logger.info("Write to "+json);
		FileWriter fw = new FileWriter(json);
		jo.write(fw);
		fw.close();
		
		cp.dump();

	}
}
