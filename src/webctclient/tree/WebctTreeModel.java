/**
 * 
 */
package webctclient.tree;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import webctclient.BackgroundTask;
import webctclient.LazyTreeNode;
import webctclient.Main;
import webctclient.WebctConstants;

import com.webct.platform.sdk.context.client.ContextSDK;
import com.webct.platform.sdk.context.gen.LearningCtxtVO;
import com.webct.platform.sdk.context.gen.SessionVO;
import com.webct.platform.sdk.filemanager.FileManagerFolder;
import com.webct.platform.sdk.filemanager.FileManagerItem;
import com.webct.platform.sdk.filemanager.FileManagerService;

/**
 * @author cmg
 *
 */
public class WebctTreeModel extends DefaultTreeModel {
	static Logger logger = Logger.getLogger(WebctTreeModel.class.getName());

	private SessionVO session;
	/**
	 * 
	 */
	public WebctTreeModel(SessionVO session) {
		super(new DefaultMutableTreeNode("Roots"), true);
		this.session = session;
		DefaultMutableTreeNode filesn = new RootFoldersTreeNode();
		
		DefaultMutableTreeNode coursesn = new RootContextsTreeNode();
		DefaultMutableTreeNode rootn = (DefaultMutableTreeNode)getRoot();
		rootn.add(filesn);
		rootn.add(coursesn);

	}
	
	public SessionVO getSession() {
		return session;
	}
	public void setSession(SessionVO session) {
		this.session = session;
	}

	class RootFoldersTreeNode extends LazyTreeNode {
		RootFoldersTreeNode() {
			super("Root Folders", true);
		}

		@Override
		protected void makeChildren() {
			final DefaultMutableTreeNode node = this;
			DefaultMutableTreeNode working = new DefaultMutableTreeNode("(Loading...)", false);
			add(working);
			BackgroundTask t = new BackgroundTask() {
				private FileManagerFolder [] folders;

				@Override
				protected void onFailure() {
					DefaultMutableTreeNode tn = (DefaultMutableTreeNode)getFirstChild();
					tn.setUserObject("Error");
					nodeChanged(tn);
				}

				@Override
				protected void onSuccess() {
					removeAllChildren();
					for (int fi=0; fi<folders.length; fi++) {
						add(new FolderTreeNode(session, folders[fi]));
					}
					nodeStructureChanged(node);	
				}

				@SuppressWarnings("deprecation")
				@Override
				protected boolean task() {
					try {
						logger.log(Level.INFO, "Getting file roots...");
						FileManagerService fms = Main.getFileManager();
						folders = fms.getRootFolders(session);
						logger.log(Level.INFO, "Got "+folders.length+" file roots");
						return true;
					}
					catch (Exception e) {
						logger.log(Level.WARNING, "getting root folders", e);
					}
					return false;
				}

			};
			t.start();
		}
	}
	public class FolderTreeNode extends LazyTreeNode {
		private FileManagerFolder folder;
		/**
		 * @param userObject
		 * @param folder
		 */
		public FolderTreeNode(SessionVO session, FileManagerFolder folder) {
			super(folder.getName(), false/*true*/);
			this.folder = folder;
		}
		
		public FileManagerFolder getFolder() {
			return folder;
		}

		@Override
		protected void makeChildren() {
//			DefaultMutableTreeNode working = new DefaultMutableTreeNode("...", false);
//			add(working);
//			BackgroundTask t = new BackgroundTask() {
//				private FileManagerItem [] items;
//
//				@Override
//				protected void onFailure() {
//					DefaultMutableTreeNode tn = (DefaultMutableTreeNode)getFirstChild();
//					tn.setUserObject("Error");
//				}
//
//				@Override
//				protected void onSuccess() {
//					removeAllChildren();
//					// TODO Auto-generated method stub
//					for (int fi=0; fi<items.length; fi++) {
//						if (items[fi] instanceof FileManagerFolder) {							
//							add(new FolderTreeNode(session, (FileManagerFolder)items[fi]));
//						}
//						else
//							add(new FileTreeNode(session, items[fi]));
//					}
//				}
//
//				@SuppressWarnings("deprecation")
//				@Override
//				protected boolean task() {
//					try {
//						FileManagerService fms = Main.getFileManager();
//						items = folder.getContents();
//						return true;
//					}
//					catch (Exception e) {
//						logger.log(Level.WARNING, "getting root folders", e);
//					}
//					return false;
//				}
//				
//			};
//			t.start();
		}

	}
	class RootContextsTreeNode extends LazyTreeNode {
		RootContextsTreeNode() {
			super("Contexts", true);
		}

		@Override
		protected void makeChildren() {
			final DefaultMutableTreeNode node = this;
			DefaultMutableTreeNode working = new DefaultMutableTreeNode("(Loading...)", false);
			add(working);
			BackgroundTask t = new BackgroundTask() {
				private long [] contextids;
				private LearningCtxtVO [] contexts;

				@Override
				protected void onFailure() {
					DefaultMutableTreeNode tn = (DefaultMutableTreeNode)getFirstChild();
					tn.setUserObject("Error");
					nodeChanged(tn);
				}

				@Override
				protected void onSuccess() {
					removeAllChildren();
					for (int fi=0; fi<contexts.length; fi++) {
						add(new ContextTreeNode(session, contexts[fi], contextids[fi]));
					}
					nodeStructureChanged(node);	
				}

				@SuppressWarnings("deprecation")
				@Override
				protected boolean task() {
					try {
						logger.log(Level.INFO, "Getting contexts...");
						ContextSDK ctxt = new ContextSDK( WebctConstants.getWsUrl("Context") );
						contextids = ctxt.getLearningContextIDs( session );
						logger.log(Level.INFO, "Got "+contextids.length+" contexts");
						contexts = new LearningCtxtVO [contextids.length];
						for (int i=0; i<contextids.length; i++) {
							contexts[i]= ctxt.getLearningContext( session, contextids[ i ] );
						}
						return true;
					}
					catch (Exception e) {
						logger.log(Level.WARNING, "getting contexts", e);
					}
					return false;
				}

			};
			t.start();
		}
	}
	public class ContextTreeNode extends LazyTreeNode {
		private LearningCtxtVO context;
		private long contextid;
		/**
		 * @param userObject
		 * @param folder
		 */
		public ContextTreeNode(SessionVO session, LearningCtxtVO context, long contextid) {
			super(context.getName(), false/*true*/);
			this.context = context;
			this.contextid = contextid;
		}
		
		public LearningCtxtVO getContext() {
			return context;
		}
		public long getContextid() {
			return contextid;
		}
	}
}
