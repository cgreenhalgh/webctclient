/**
 * 
 */
package webctclient;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.BadLocationException;

/**
 * @author cmg
 *
 */
public class ProgressDialog extends JDialog {
	private JTextArea outputta;
	protected AbstractAction ok;
	protected boolean cancelled = false;
	
	public ProgressDialog(Frame frame, String title) {
		super(frame, title, true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		JPanel p = new JPanel();
		p.setLayout(new BorderLayout());
		outputta = new JTextArea(10,40);
		outputta.setEditable(false);
		ok = new AbstractAction("OK") {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setVisible(false);
			}			
		};
		ok.setEnabled(false);
		JPanel buttons = new JPanel();
		buttons.setLayout(new FlowLayout());
		buttons.add(new JButton(ok));
		p.add(buttons, BorderLayout.SOUTH);
		
		p.add(new JScrollPane(outputta), BorderLayout.CENTER);
		p.setPreferredSize(new Dimension(500, 600));
		setContentPane(p);
		pack();
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				cancel();
			}
			
		});
	}
	protected void cancel() {
		synchronized(this) {
			cancelled = true;
		}		
		append("Cancelled");
	}
	public void backgroundOutput(final String text) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				append(text);
			}
		});
	}
	public void append(String text) {
		outputta.append(text);
		
		try {
			outputta.scrollRectToVisible(outputta.modelToView(outputta.getText().length()-1));
		} catch (BadLocationException e) {
			//e.printStackTrace();
		}
	}
}
