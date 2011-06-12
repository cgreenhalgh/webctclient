/**
 * 
 */
package webctclient;

import java.awt.Frame;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.activation.DataHandler;

import webctclient.tree.WebctTreeModel;

import com.webct.platform.sdk.context.gen.SessionVO;
import com.webct.platform.sdk.filemanager.FileManagerFolder;
import com.webct.platform.sdk.filemanager.FileManagerItem;
import com.webct.platform.sdk.filemanager.FileManagerService;
import com.webct.platform.sdk.filemanager.exceptions.FileManagerException;

/**
 * @author cmg
 *
 */
public class DownloadFolderDialog extends ProgressDialog {
	static Logger logger = Logger.getLogger(DownloadFolderDialog.class.getName());
	
	private static final String FOLDER = "FOLDER";
	private BackgroundTask task;
	private FileManagerFolder folder;
	private SessionVO session;
	/**
	 * @param frame
	 * @param title
	 */
	public DownloadFolderDialog(Frame frame, SessionVO session, FileManagerFolder folder, final File dir) {
		super(frame, "Download Folder");
		this.session = session;
		this.folder = folder;
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
					getFolder(Main.getFileManager(), dir, DownloadFolderDialog.this.folder.getPath());
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
	@SuppressWarnings("deprecation")
	private void getFolder(FileManagerService fms, File localPath, String path) throws FileManagerException, RemoteException {
		backgroundOutput("Path "+path+":\n");
		FileManagerFolder file = fms.readFolder(session, path);
//		file.get
		String type = file.getType();
		if (FOLDER.equals(type)) {
			FileManagerFolder folder = fms.readFolder(session, path);
			FileManagerItem items [] = folder.getContents();
			for (int i=0; items!=null && i<items.length; i++) {
				if (cancelled)
					return;
				File localFile = new File(localPath, items[i].getName());
				if (FOLDER.equals(items[i].getType())) {
					localFile.mkdirs();
					getFolder(fms, localFile, items[i].getPath());
				}
				else {
					backgroundOutput("Download "+localFile+"...\n");
					// TODO
					DataHandler dh = fms.getFileContent(session, items[i].getPath());
			        try {
						localFile.createNewFile();
			    	
						OutputStream out = new FileOutputStream(localFile);

						dh.writeTo(out);

						out.close();
						backgroundOutput("Downloaded "+items[i].getId()+" to: " + localFile+"\n");
//						addToIndex(items[i].getId(), localFile);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						backgroundOutput("Error downloading "+localFile+": "+e);
					}
			
				}
			}
		}
	}
}
