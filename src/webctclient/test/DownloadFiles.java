package webctclient.test;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;

import javax.activation.DataHandler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import webscraper.PasswordUtils;

import com.webct.platform.sdk.context.client.ContextSDK;
import com.webct.platform.sdk.context.gen.ContextException;
import com.webct.platform.sdk.context.gen.SessionVO;
import com.webct.platform.sdk.filemanager.FileManagerFolder;
import com.webct.platform.sdk.filemanager.FileManagerItem;
import com.webct.platform.sdk.filemanager.FileManagerService;
import com.webct.platform.sdk.filemanager.client.FileManagerSDKFactory;
import com.webct.platform.sdk.filemanager.exceptions.FileManagerException;
import com.webct.platform.sdk.filemanager.FileManagerFile;
import com.webct.platform.sdk.test.TestFileManagerSDK;

/** download files using File API */

public class DownloadFiles {
    private static final Log LOG = LogFactory.getLog(DownloadFiles.class);

	private static final Object FOLDER = "FOLDER";

	private static final String INDEX_FILENAME = "index.log";

	/**
	 * @param args
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		_url = TestConstants.PROTOCOL+"://"+TestConstants.HOST+":"+TestConstants.PORT+TestConstants.CTX_WS_URI+"/";
		// TODO Auto-generated method stub
		if (args.length<1) {
			System.err.println("Usage: user [localpath path...]");
			System.exit(-1);
		}
		_user = args[0];
        try {
    		_password = PasswordUtils.getPassword(_user);
        	obtainSession();

        	if (args.length<=1)
        		listRootFolders();
        	else {
        		File localPath = new File(args[1]);
        		localPath.mkdirs();
        		FileManagerService fms = getFileManager();
        		for (int i=2; i<args.length; i++) {
        			String path = args[i];
        			getFolder(fms, localPath, path);
        		}
        	}
        } catch (Exception e1) {
        	// TODO Auto-generated catch block
        	e1.printStackTrace();
        	fail(e1.toString());
        }



	}
	@SuppressWarnings("deprecation")
	private static void getFolder(FileManagerService fms, File localPath, String path) throws FileManagerException, RemoteException {
		// TODO Auto-generated method stub
		System.out.println("Path "+path+":");
		FileManagerFolder file = fms.readFolder(_session, path);
//		file.get
		String type = file.getType();
		if (FOLDER.equals(type)) {
			FileManagerFolder folder = fms.readFolder(_session, path);
			FileManagerItem items [] = folder.getContents();
			for (int i=0; items!=null && i<items.length; i++) {
				File localFile = new File(localPath, items[i].getName());
				if (FOLDER.equals(items[i].getType())) {
					localFile.mkdirs();
					getFolder(fms, localFile, items[i].getPath());
				}
				else {
					System.out.println("Download "+localFile+"...");
					// TODO
					DataHandler dh = fms.getFileContent(_session, items[i].getPath());
			        try {
						localFile.createNewFile();
			    	
						OutputStream out = new FileOutputStream(localFile);

						dh.writeTo(out);

						out.close();
						status("Downloaded "+items[i].getId()+" to: " + localFile);
						addToIndex(items[i].getId(), localFile);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			
				}
			}
		}
	}
	private static void addToIndex(long id, File localFile) {
		// TODO Auto-generated method stub
		File index = new File(localFile.getParentFile(), INDEX_FILENAME);
		try {
			FileWriter fw = new FileWriter(index, true);
			fw.append(id+" "+localFile.getName()+"\n");
			fw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private static void listRootFolders() throws FileManagerException, RemoteException {
		// TODO Auto-generated method stub
    	try {

    		FileManagerFolder[] folders = getFileManager().getRootFolders(_session);

    		if (folders == null)
    		{
    			status("No root folder.");
    			return;
    		}

    		for (int i = 0; i < folders.length; i++)
    		{
    			System.out.println(folders[i].getPath()+" ("+folders[i].getType()+")");
    		}
    	}
    	finally {
    		releaseSession() ;
    	}

	}
	/** from TestFileManagerSDK */
    private static FileManagerService getFileManager() throws RemoteException
    //, CreateException
    {
        if (_fileManager == null)
        {
            try
            {
                URL finalURL = new URL(_url + "FileManager");

                _fileManager = FileManagerSDKFactory.getSOAPInstance(finalURL);

                LOG.debug("Created: " + _fileManager);
            }
            catch (MalformedURLException ex)
            {
                fail("Invalid URL: " + ex.getMessage());
            }
        }

        return _fileManager;
    }
    /**
     *  The user name used to connect to the service.
     * 
     **/

    private static String _user;

    /**
     *  The password for the user.
     * 
     **/

    private static String _password;
   /**
     *  The base URL used to connect to the service, as input by the
     *  user.
     * 
     **/

    private static String _url;
    /**
     *  The SDK session, which encapsulates authentication.
     * 
     *  @see #obtainSession()
     * 
     **/

    private static SessionVO _session;

    /**
     *  A reference to the SDKFileManager service.
     * 
     **/

    private static FileManagerService _fileManager;
    /**
     *  Displays an error message and command usage and exits.
     * 
     **/

    private static void fail(String message)
    {
        System.err.println(message);

        //showUsage();

        System.exit(-1);
    }
    /**
     *  Displays a status message.
     * 
     **/

    private static void status(String message)
    {
        System.out.println(message);
    }
    /**
     *  Invoked by each command to obtain an {@link com.webct.platform.sdk.context.gen.SessionVO}
     *  based on the user, password and GLCID.
     * 
     **/

    private static void obtainSession() throws ContextException, RemoteException
    {
        LOG.info("Obtaining SessionVO for user " + _user + " ...");

        ContextSDK context = new ContextSDK(buildURL("Context"));

        _session = context.login(_user, _password, TestConstants.INSTITUTION_GCLID);

        LOG.debug("Obtained: " + _session);

    }
    private static URL buildURL(String serviceName)
    {
        try
        {
            return new URL(_url + serviceName);
        }
        catch (MalformedURLException ex)
        {
            System.err.println("URL (" + _url + ") is not valid.");
            System.exit(-1);

            return null;
        }
    }
    /**
     *  Invoked by each command to release {@link com.webct.platform.sdk.context.gen.SessionVO}
     *  resources.
     * 
     */

    private static void releaseSession() throws ContextException, RemoteException
    {
        LOG.info("Releasing session resources");

        ContextSDK context = new ContextSDK(buildURL("Context"));

        context.logout(_session);

        LOG.debug("Session resources have been released");

    }


}
