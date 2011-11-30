/* Xiaolong Cheng, cs594, 2011 fall */
import java.awt.*;
import javax.swing.border.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;


/**
 * generate the window to configure settings
 */
public class Help extends JDialog {

	JPanel titlePanel = new JPanel();
	JPanel contentPanel = new JPanel();
	JPanel closePanel = new JPanel();

	JButton close = new JButton();
	JLabel title = new JLabel("IRC server help");
	JTextArea help = new JTextArea(); 

	Color bg = new Color(255,255,255);

	public Help(JFrame frame) {
		super(frame, true);
		try {
			jbInit();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//make dialogue centered
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation( (int) (screenSize.width - 400) / 2,
						(int) (screenSize.height - 320) / 2);
		this.setResizable(false);
	}

	private void jbInit() throws Exception {
		this.setSize(new Dimension(400, 200));
		this.setTitle("Help");
		
		titlePanel.setBackground(bg);;
		contentPanel.setBackground(bg);
		closePanel.setBackground(bg);
		
		help.setText("1 set server listening port(default is 8888£)\n"+
			"2 click start button to start listening\n"+
			"3 select the receivers, enter message and you can send.\n"+
			"4 status message will show the server status"+
			"sent message and\n     system message sent by server");
		help.setEditable(false);

		titlePanel.add(new Label("              "));
		titlePanel.add(title);
		titlePanel.add(new Label("              "));

		contentPanel.add(help);

		closePanel.add(new Label("              "));
		closePanel.add(close);
		closePanel.add(new Label("              "));

		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(titlePanel, BorderLayout.NORTH);
		contentPane.add(contentPanel, BorderLayout.CENTER);
		contentPane.add(closePanel, BorderLayout.SOUTH);

		close.setText("close");
		//event handling
		close.addActionListener(
			new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			}
		);
	}
}