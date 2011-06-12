/**
 * 
 */
package webctclient;

import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

import webctclient.test.TestConstants;
import webctclient.webscraper.Cookies;
import webctclient.webscraper.Login;

import com.webct.platform.sdk.context.client.ContextSDK;
import com.webct.platform.sdk.context.gen.SessionVO;
import com.webct.platform.sdk.filemanager.client.FileManagerSDKFactory;
import com.webct.platform.sdk.filemanager.FileManagerService;

/**
 * @author cmg
 *
 */
public class LoginDialog extends JDialog {
	static Logger logger = Logger.getLogger(LoginDialog.class.getName());
	
	private JTextField usernametf;
	private JTextField passwordtf;
	private JTextArea statusta;
	private AbstractAction ok;
	private BackgroundTask loginTask;
	private SessionVO session;
	private String username;
	private Cookies cookies;
	
	public SessionVO getSession() {
		return session;
	}
	public String getUsername() {
		return username;
	}

	public Cookies getCookies() {
		return cookies;
	}
	public static void addLabel(JPanel p, String text) {
		JLabel l = new JLabel(text);
		//l.setHorizontalAlignment(SwingConstants.LEFT);
		//l.setAlignmentY(0);
		l.setAlignmentX(0);
		//l.setHorizontalTextPosition(SwingConstants.LEFT);
		p.add(l);
	}
	/**
	 * @param arg0
	 * 
	 */
	public LoginDialog(Frame arg0) {
		super(arg0, true);
		JPanel p = new JPanel();
		// TODO Auto-generated constructor stub
		BoxLayout pl = new BoxLayout(p, BoxLayout.PAGE_AXIS);
		p.setLayout(pl);
		addLabel(p, "WebCT username:");
		usernametf = new JTextField(40);
		usernametf.setAlignmentX(0);
		p.add(usernametf);
		addLabel(p, "Password:");
		passwordtf = new JPasswordField(40);
		passwordtf.setAlignmentX(0);
		p.add(passwordtf);
		JPanel buttons = new JPanel();
		buttons.setLayout(new FlowLayout());
		buttons.setAlignmentX(0);
		p.add(buttons);
		ok = new AbstractAction("OK") {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				startLogin();
			}			
		};
		buttons.add(new JButton(ok));
		usernametf.addActionListener(ok);
		passwordtf.addActionListener(ok);
		addLabel(p, "Status:");		
		statusta = new JTextArea(4, 40);
		statusta.setAlignmentX(0);
		JScrollPane sp = new JScrollPane(statusta);
		sp.setAlignmentX(0);
		p.add(sp);
		statusta.setEditable(false);
		this.getContentPane().add(p);
		this.pack();
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				if (loginTask!=null)
					loginTask.cancel();			
				System.exit(0);
			}			
		});
	}

	protected void startLogin() {
		username = usernametf.getText();
		if (username.length()==0) {
			status("Please specify a username");
			return;
		}
		final String password = passwordtf.getText();
		if (password.length()==0) {
			status("Please specify a password for "+username);
			return;
		}
		ok.setEnabled(false);
		status("login as "+username);
		loginTask = new BackgroundTask() {
			@Override
			protected void onFailure() {
				status("Error logging in");
				ok.setEnabled(true);
				JOptionPane.showMessageDialog(LoginDialog.this, "Could not log in with this username and password", "Error logging in", JOptionPane.ERROR_MESSAGE);
			}

			@Override
			protected void onSuccess() {
				setVisible(false);
			}

			@Override
			protected boolean task() {
				return backgroundLogin(username, password);
			}
			
		};
		loginTask.start();
		ok.setEnabled(true);
	}
	protected boolean backgroundLogin(String username, String password) {
		try {
	        ContextSDK context = new ContextSDK(WebctConstants.getWsUrl("Context"));
	
	        session = context.login(username, password, WebctConstants.INSTITUTION_GCLID);
	        logger.log(Level.INFO, "got session "+session);

	        cookies = Login.webctLogin(username, password);
	        logger.log(Level.INFO, "got cookies "+cookies);
	        
	        return true;
		}
		catch (Exception e) {
			logger.log(Level.WARNING, "error getting session", e);
			//status("Error logging in: "+e);			
		}
		return false;
	}
	private void status(String string) {
		statusta.append(string+"\n");
		try {
			statusta.scrollRectToVisible(statusta.modelToView(statusta.getText().length()-1));
		} catch (BadLocationException e) {
			logger.log(Level.WARNING, "scrolling to status text end", e);
		}
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createTestGui();
			}
		});
	}
	private static void createTestGui() {
		JFrame testf = new JFrame("Test login");
		testf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		testf.pack();
		testf.setVisible(true);
		
		LoginDialog logind = new LoginDialog(testf);
		logind.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		logind.setVisible(true);
		// modal!
	}

}
