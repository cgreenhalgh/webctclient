/**
 * 
 */
package webctclient;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import webctclient.tree.WebctTreeModel;
import webctclient.tree.WebctTreeModel.ContextTreeNode;
import webctclient.tree.WebctTreeModel.FolderTreeNode;

import com.webct.platform.sdk.context.gen.LearningCtxtVO;
import com.webct.platform.sdk.context.gen.SessionVO;
import com.webct.platform.sdk.filemanager.FileManagerFolder;
import com.webct.platform.sdk.filemanager.FileManagerService;
import com.webct.platform.sdk.filemanager.client.FileManagerSDKFactory;

/**
 * @author cmg
 *
 */
public class Main {
	static Logger logger = Logger.getLogger(Main.class.getName());

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		final Main instance = new Main();
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				instance.createGui();
			}
		});
	}
	private JTree tree;
	private WebctTreeModel treem;
	private JTextArea detailsta;
	private AbstractAction download;
	private TreeNode detailsnode;
	/** in swing thread */
	protected void createGui() {
		final JFrame frame = new JFrame("WebCT Client");
		final LoginDialog login = new LoginDialog(frame);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		
		DefaultMutableTreeNode rootn = new DefaultMutableTreeNode("Root", true);
		
		treem = new WebctTreeModel(login.getSession());
		tree = new JTree(treem);
		tree.setShowsRootHandles(false);
		tree.addTreeWillExpandListener(new TreeWillExpandListener() {
			
			@Override
			public void treeWillExpand(TreeExpansionEvent e)
					throws ExpandVetoException {
				// TODO Auto-generated method stub
				TreePath p = e.getPath();
				Object tno = p.getLastPathComponent();
				if (tno instanceof LazyTreeNode)
					((LazyTreeNode)tno).willExpand(e);
			}
			@Override
			public void treeWillCollapse(TreeExpansionEvent arg0)
					throws ExpandVetoException {
			}
		});
		
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				updateDetails(null);
				TreePath ps[] = e.getPaths();
				for (int pi=0; pi<ps.length; pi++) {
					if (!e.isAddedPath(pi)) {
						continue;
					}
					Object node = ps[pi].getLastPathComponent();
					updateDetails(node);
				}
			}
		});
		
		JScrollPane treesp = new JScrollPane(tree);
		treesp.setPreferredSize(new Dimension(400,600));		
		p.add(treesp, BorderLayout.WEST);
		
		JPanel details = new JPanel();
		details.setLayout(new BorderLayout());
		details.add(new JLabel("Details:"), BorderLayout.NORTH);
		detailsta = new JTextArea();
		detailsta.setEditable(false);
		JScrollPane dsp = new JScrollPane(detailsta);
		dsp.setPreferredSize(new Dimension(400,600));
		details.add(dsp, BorderLayout.CENTER);
		JPanel buttons = new JPanel();
		buttons.setLayout(new FlowLayout());
		download = new AbstractAction("Download") {

			@Override
			public void actionPerformed(ActionEvent arg0) {

				if (detailsnode instanceof WebctTreeModel.FolderTreeNode) {
					FolderTreeNode ftn = (FolderTreeNode)detailsnode;
					FileManagerFolder folder = ftn.getFolder();
					// choose output directory
					JFileChooser fc = new JFileChooser();
					fc.setDialogTitle("Directory to save files into");
					fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
					int res = fc.showSaveDialog(frame);
					if (res!=JFileChooser.APPROVE_OPTION)
						return;
					File dir = fc.getSelectedFile();
					if (!dir.isDirectory() || !dir.canWrite()) {
						JOptionPane.showMessageDialog(frame, "Cannot save to "+dir, "Error", JOptionPane.ERROR_MESSAGE);
						return;
					}
					// show progress dialog
					// download...
					new DownloadFolderDialog(frame, login.getSession(), folder, dir);
				}
			}
			
		};
		buttons.add(new JButton(download));
		download.setEnabled(false);
		details.add(buttons, BorderLayout.SOUTH);
		p.add(details, BorderLayout.CENTER);
		
		frame.setContentPane(p);
		frame.pack();
		frame.setVisible(true);
		login.setVisible(true);
		treem.setSession(login.getSession());
	}
	protected void updateDetails(Object node) {
		detailsta.setText("");
		download.setEnabled(false);
		
		if (node instanceof WebctTreeModel.FolderTreeNode) {
			FolderTreeNode ftn = (FolderTreeNode)node;
			detailsnode = ftn;
			FileManagerFolder folder = ftn.getFolder();
			detailsta.append("Name: "+folder.getName()+"\n");
			detailsta.append("ID: "+folder.getId()+"\n");
			detailsta.append("Path: "+folder.getPath()+"\n");
			detailsta.append("Type: "+folder.getType()+"\n");
			detailsta.append("Created: "+(folder.getCreated().getTime())+"\n");
			detailsta.append("Last Modified: "+folder.getLastModified().getTime()+"\n");

			download.setEnabled(true);
			return;
		}
		if (node instanceof WebctTreeModel.ContextTreeNode) {
			ContextTreeNode ctn = (ContextTreeNode)node;
			detailsnode = ctn;
			LearningCtxtVO context = ctn.getContext();
			detailsta.append("ID: "+ctn.getContextid()+"\n");
			detailsta.append("Name: "+context.getName()+"\n");
			detailsta.append("Description: "+context.getDesc()+"\n");
			detailsta.append("Label: "+context.getLabel()+"\n");

//			download.setEnabled(true);
			return;
		}
	}
	private static FileManagerService fileManager = null;
	public static synchronized FileManagerService getFileManager() throws MalformedURLException, RemoteException {
		if (fileManager!=null)
			return fileManager;
		
        URL fmURL = WebctConstants.getWsUrl("FileManager");
        fileManager = FileManagerSDKFactory.getSOAPInstance(fmURL);

//        logger.log(Level.INFO, "Created filemanager " + fileManager);
        return fileManager;
	}
}
