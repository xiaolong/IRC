/* Xiaolong Cheng, cs594, 2011 fall */
import java.awt.*;
import javax.swing.border.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * this class generates the configuration window
 * user can specify server IP and port number
 */
public class ConnectConf extends JDialog {
	JPanel panelUserConf = new JPanel();
	JButton save = new JButton();
	JButton cancel = new JButton();
	JLabel DLGINFO=new JLabel(
		"                 default configure is:  127.0.0.1:8888");

	JPanel panelSave = new JPanel();
	JLabel message = new JLabel();

	String userInputIp;
	int userInputPort;

	JTextField inputIp;
	JTextField inputPort;

	public ConnectConf(JFrame frame,String ip,int port) {
		super(frame, true);
		this.userInputIp = ip;
		this.userInputPort = port;
		try {
			jbInit();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//make the box centered..
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation( (int) (screenSize.width - 400) / 2 + 50,
						(int) (screenSize.height - 600) / 2 + 150);
		this.setResizable(false);
	}

	private void jbInit() throws Exception {
		this.setSize(new Dimension(300, 130));
		this.setTitle("network configuration");
		message.setText("Please enter the server address:");
		inputIp = new JTextField(10);
		inputIp.setText(userInputIp);
		inputPort = new JTextField(4);
		inputPort.setText(""+userInputPort);
		save.setText("Save");
		cancel.setText("Cancel");

		panelUserConf.setLayout(new GridLayout(2,2,1,1));
		panelUserConf.add(message);
		panelUserConf.add(inputIp);
		panelUserConf.add(new JLabel("Please specify the port number:"));
		panelUserConf.add(inputPort);

		panelSave.add(new Label("              "));
		panelSave.add(save);
		panelSave.add(cancel);
		panelSave.add(new Label("              "));

		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(panelUserConf, BorderLayout.NORTH);
		contentPane.add(DLGINFO, BorderLayout.CENTER);
		contentPane.add(panelSave, BorderLayout.SOUTH);

		//event handling for the 'save' button
		save.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent a) {
					int savePort;
					String inputIP;
					//check if port valid or not
					try{
						userInputIp = "" + InetAddress.getByName(inputIp.getText());
						userInputIp = userInputIp.substring(1);
					}
					catch(UnknownHostException e){
						DLGINFO.setText(
							"                              Wrong IP address!");

						return;
					}
					//userInputIp = inputIP;

					//check port number
					try{
						savePort = Integer.parseInt(inputPort.getText());

						if(savePort<1 || savePort>65535){
							DLGINFO.setText("               must be number between 0 and 65535!");
							inputPort.setText("");
							return;
						}
						userInputPort = savePort;
						dispose();
					}
					catch(NumberFormatException e){
						DLGINFO.setText("                port number must be an integer!");
						inputPort.setText("");
						return;
					}
				}
			}
		);

		//event handler for closing window
		this.addWindowListener(
			new WindowAdapter(){
				public void windowClosing(WindowEvent e){
					DLGINFO.setText("                  default config is  127.0.0.1:8888");
				}
			}
		);

		//event handler for 'cancel' button
		cancel.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
					DLGINFO.setText("                  default config is 127.0.0.1:8888");
					dispose();
				}
			}
		);
	}
}