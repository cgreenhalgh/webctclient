/**
 * 
 */
package webctclient;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;

import org.json.JSONException;
import org.json.JSONObject;

import webctclient.webscraper.ContentItem;
import webctclient.webscraper.ContentPage;
import webctclient.webscraper.Cookies;
import webctclient.webscraper.Login;
import webctclient.webscraper.WebPage;

import com.webct.platform.sdk.context.client.ContextSDK;
import com.webct.platform.sdk.context.gen.LearningCtxtVO;
import com.webct.platform.sdk.context.gen.SessionVO;
import com.webct.platform.sdk.filemanager.FileManagerFolder;

/**
 * @author cmg
 *
 */
public class DownloadContextDialog extends ProgressDialog {
	static Logger logger = Logger.getLogger(DownloadContextDialog.class.getName());

	private BackgroundTask task;
	private SessionVO session;
	private long contextid;
	private LearningCtxtVO context;

	private Cookies cookies;
	
	public DownloadContextDialog(JFrame frame, SessionVO session,
			Cookies cookies, final long contextid, final LearningCtxtVO context, final File dir) {
		super(frame, "Download Context");
		this.session = session;
		this.cookies = cookies;
		this.contextid = contextid;
		this.context = context;
		task = new BackgroundTask() {

			@Override
			protected void onFailure() {
				append("\nFailed");
			}

			@Override
			protected void onSuccess() {
				ok.setEnabled(true);
				append("Done");
			}

			@Override
			protected boolean task() {
				try {
					dumpContext(context, contextid, dir);
				} catch (Exception e) {
					logger.log(Level.WARNING, "Error downloading", e);
					return false;
				}
				return true;
			}
			
		};
		task.start();
		setVisible(true);

	}
	private void dumpContext(LearningCtxtVO context, long contextid, File courseDir) throws IOException, JSONException {
		backgroundOutput("Download context content...\n");
		String url = "http://webct.nottingham.ac.uk/webct/urw/lc"+contextid+".tp"+context.getTemplID()+"/CourseContentDispatch.dowebct?tab=view";
		WebPage page = WebPage.download(url, cookies);
		
		File debugDir = new File(courseDir, "debug");
		debugDir.mkdir();
		page.write(new File(debugDir, "course.html"));
		
		ContentPage cp = new ContentPage(page, context.getName());

		backgroundOutput("Read: "+cp+"\n");
		for (ContentItem item : cp.getItems()) {
			// shallow
			item.followItem(cookies, courseDir, debugDir, false);
		}

		backgroundOutput("Write course page index\n");
		cp.writeIndex(new File(courseDir, "index.html"));
//			backgroundOutput("Item: "+item+"\n");

		// recurse
		for (ContentItem item : cp.getItems()) {
			logger.info("Item: "+item);
			item.followItem(cookies, courseDir, debugDir, true);
		}
		
		JSONObject jo = cp.toJson();
		File json = new File(debugDir, "course"+contextid+".json");
		backgroundOutput("Write to "+json+"\n");
		FileWriter fw = new FileWriter(json);
		jo.write(fw);
		fw.close();
		
		cp.dump();

	}

}
