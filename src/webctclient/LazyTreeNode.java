/**
 * 
 */
package webctclient;

import javax.swing.event.TreeExpansionEvent;

/**
 * @author cmg
 *
 */
public class LazyTreeNode extends javax.swing.tree.DefaultMutableTreeNode {

	public LazyTreeNode(Object userObject, boolean allowsChildren) {
		super(userObject, allowsChildren);
		// TODO Auto-generated constructor stub
	}
	
	public final void willExpand(TreeExpansionEvent tev) {
		makeChildren();
	}

	/** override */
	protected void makeChildren() {
	}
}
