package webctclient.test;
import java.net.URL;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.impl.SimpleLog;

//import webscraper.PasswordUtils;

import com.webct.platform.sdk.context.client.ContextSDK;
import com.webct.platform.sdk.context.gen.LearningCtxtVO;
import com.webct.platform.sdk.context.gen.SessionVO;

/**
 * 
 */

/**
 * @author cmg
 *
 */
public class ContextTest {

	private static Log log = new SimpleLog( "ContextTest" );

	public static final String USAGE = "username";
	
	private static final String CTX_WS_URI = "/webct/axis/Context";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length!=1) {
			log.error("Usage: "+USAGE);
			System.exit(-1);
		}
		ContextSDK ctxt = null;
		SessionVO session = null;
		try {
			String username = args[0];
			String password = PasswordUtils.getPassword(username);
			URL serviceUrl = null;
			serviceUrl = new URL( TestConstants.PROTOCOL, TestConstants.HOST, TestConstants.PORT, CTX_WS_URI );
			ctxt = new ContextSDK( serviceUrl );
			String version = ctxt.getReleaseVersion();
			log.info( "Checking Server Compatibility w. version " + version + "..." );
			boolean comp = ctxt.isCompatibleWith( version );
			if ( !comp ) {
				String msg = "Server reported incompatibility with version " + version ;
				throw new Exception( msg );
			}
			session = ctxt.login( username, password, TestConstants.INSTITUTION_GCLID);
			log.info("logged in");
			long[] lcArray = ctxt.getLearningContextIDs( session );
			ArrayList lcList = new ArrayList();
			for ( int i = 0; lcArray != null && i < lcArray.length; i++ ) {
				log.trace( "Getting data for LC " + lcArray[ i ] + "..." );
				LearningCtxtVO lc = ctxt.getLearningContext( session, lcArray[ i ] );
				lcList.add( lc );
				log.info( "Learning Context "+lcArray[i]+" '" + lc.getName() + "' ("+lc.getLabel()+", sourcedID="+lc.getSourcedID().getSource()+"/"+lc.getSourcedID().getMyID()+", template="+lc.getTemplID()+"):" );
//				log.info( lc );
/*				try {
					long parentlcid = lcArray[ i ];
					while(true) {
						parentlcid = ctxt.getParent( session, parentlcid );
						log.info("- parent "+parentlcid);
						if (parentlcid==0)
							break;
						LearningCtxtVO plc = ctxt.getLearningContext( session, parentlcid );
						log.info( plc );
					}
					// children
					findChildren(ctxt, session, lcArray[i]);
				}
				catch (Exception e) {
					log.error("Getting parent", e);
				}
*/				
			}
			
		} catch (Exception e) {
			log.error("Error", e);
		}
		if (session!=null) {
			try {
				ctxt.logout( session );
				log.info("logged out");
			}
			catch (Exception e) {
				log.error("Logging out",e);
			}
		}
		log.info("done");
	}
	static void findChildren(ContextSDK ctxt, SessionVO session, long lcid) {
		try {
			log.info("children of "+lcid+":");
			long childLcids[] = ctxt.getChildren(session, lcid);
			if (childLcids==null)
				return;
			for (int i=0; i<childLcids.length;i++) {
				LearningCtxtVO childContext = ctxt.getLearningContext(session, childLcids[i]);
				log.info("- "+childLcids[i]+": "+childContext);
			}
			for (int i=0; i<childLcids.length;i++) {
				findChildren(ctxt, session, childLcids[i]);
			}
		}
		catch (Exception e) {
			log.error("findChildren("+lcid+")", e);
		}
	}
}
