/* Xiaolong Cheng, cs594, 2011 fall */
import java.awt.*;
import javax.swing.border.*;
import java.net.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * class for the port setting window
 */
public class PortConf extends JDialog {
	JPanel panelPort = new JPanel();
	JButton save = new JButton();
	JButton cancel = new JButton();
	public static JLabel DLGINFO=new JLabel(
		"                            default port:8888");

	JPanel panelSave = new JPanel();
	JLabel message = new JLabel();

	public static JTextField portNumber ;

	public PortConf(JFrame frame) {
		super(frame, true);
		try {
			jbInit();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		//make dialogue box centered
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation( (int) (screenSize.width - 400) / 2 + 50,
						(int) (screenSize.height - 600) / 2 + 150);
		this.setResizable(false);
	}

	private void jbInit() throws Exception {
		this.setSize(new Dimension(300, 200));
		this.setTitle("port configure");
		message.setText("port to listen to:");
		portNumber = new JTextField(10);
		portNumber.setText(""+ChatServer.port);
		save.setText("save");
		cancel.setText("cancel");

		panelPort.setLayout(new FlowLayout());
		panelPort.add(message);
		panelPort.add(portNumber);

		panelSave.add(new Label("              "));
		panelSave.add(save);
		panelSave.add(cancel);
		panelSave.add(new Label("              "));

		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());
		contentPane.add(panelPort, BorderLayout.NORTH);
		contentPane.add(DLGINFO, BorderLayout.CENTER);
		contentPane.add(panelSave, BorderLayout.SOUTH);

		//event handlers for save button
		save.addActionListener(
			new ActionListener() {
				public void actionPerformed (ActionEvent a) {
					int savePort;
					try{
						
						savePort=Integer.parseInt(PortConf.portNumber.getText());

						if(savePort<1 || savePort>65535){
							PortConf.DLGINFO.setText(" port number must be integer between 0 and 65535!");
							PortConf.portNumber.setText("");
							return;
						}
						ChatServer.port = savePort;
						dispose();
					}
					catch(NumberFormatException e){
						PortConf.DLGINFO.setText(" wrong port number, please enter an integer!");
						PortConf.portNumber.setText("");
						return;
					}
				}
			}
		);

		//
		this.addWindowListener(
			new WindowAdapter(){
				public void windowClosing(WindowEvent e){
					DLGINFO.setText("  default port:8888");
				}
			}
		);

		//
		cancel.addActionListener(
			new ActionListener(){
				public void actionPerformed(ActionEvent e){
					DLGINFO.setText("  default port:8888");
					dispose();
				}
			}
		);
	}
}