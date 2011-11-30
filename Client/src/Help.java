/* Xiaolong Cheng, cs594, 2011 fall */
import java.awt.*;
import javax.swing.border.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;


/**
 * class for window configuration
 */
public class Help extends JDialog {

	JPanel titlePanel = new JPanel();
	JPanel contentPanel = new JPanel();
	JPanel closePanel = new JPanel();

	JButton close = new JButton();
	JLabel title = new JLabel("Help for chat client");
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
		//make dialogue box centered
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation( (int) (screenSize.width - 400) / 2 + 25,
						(int) (screenSize.height - 320) / 2);
		this.setResizable(false);
	}

	private void jbInit() throws Exception {
		this.setSize(new Dimension(350, 270));
		this.setTitle("help");
		
		titlePanel.setBackground(bg);;
		contentPanel.setBackground(bg);
		closePanel.setBackground(bg);
		
		help.setText("1 specify server address and port"+
			"(default config is    127.0.0.1:8888)\n"+
			"2 specify your username (default: xiaolong)\n"+
			"3 click login to connect \n"+
			"     click logoff to unconnect \n"+
			"4 choose the msg receiver \n"+
			"      and choose your mode and send\n");
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

		close.setText("close ");
		
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