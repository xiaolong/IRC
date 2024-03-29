/* Xiaolong Cheng, cs594, 2011 fall */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.io.*;
import java.net.*;

/*
 * Client Class
 */
public class ChatClient extends JFrame implements ActionListener{

	String ip = "127.0.0.1";//address of server side
	int port = 8888;//port number
	String userName = "Xiaolong";//user name
	int type = 0;//1 stands for connected, 0 unconnected

	Image icon;//icon for the program
	JComboBox combobox;//to choose the receiver for message
	JTextArea messageShow;//message display for client
	JScrollPane messageScrollPane;//scroll message window

	JLabel express,sendToLabel,messageLabel ;

	JTextField clientMessage;//client message
	JCheckBox checkbox;//choose private or not?
	JComboBox actionlist;//action choices
	JButton clientMessageButton;
	JTextField showStatus;//connection status
	
	Socket socket;
	ObjectOutputStream output;//socket IO
	ObjectInputStream input;
	
	//thread to handle the received messages..
	ClientReceive recvThread;

	//construct the menu bar
	JMenuBar jMenuBar = new JMenuBar(); 
	//construct menu
	JMenu operateMenu = new JMenu ("Operation(O)"); 
	//menu items
	JMenuItem loginItem = new JMenuItem ("Log In(I)");
	JMenuItem logoffItem = new JMenuItem ("Log Off(L)");
	JMenuItem exitItem=new JMenuItem ("Exit(X)");
	

	JMenu conMenu=new JMenu ("Configuration(C)");
	JMenuItem userItem=new JMenuItem ("User Config(U)");
	JMenuItem connectItem=new JMenuItem ("Connection Config(C)");
	
	
	JMenu helpMenu=new JMenu ("Help(H)");
	JMenuItem helpItem=new JMenuItem ("Help(H)");

	//construct toolbar
	JToolBar toolBar = new JToolBar();
	//button controls
	JButton loginButton;//login
	JButton logoffButton;//logoff
	JButton userButton;//user informaton
	JButton connectButton;//connection button
	JButton exitButton;//exit prog

	//decide the frame size
	Dimension faceSize = new Dimension(500, 600);

	JPanel downPanel ;
	GridBagLayout gridBag;
	GridBagConstraints gridBagCon;
	
	public ChatClient(){//CTOR..
		init();//initialization, construct the window interface

		//close operation for frame
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		//set the frame size
		this.setSize(faceSize);

		//window location
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation( (int) (screenSize.width - faceSize.getWidth()) / 2,
						 (int) (screenSize.height - faceSize.getHeight()) / 2);
		this.setResizable(false);
		this.setTitle("IRC Client"); //prog title

		//icon
		/*icon = getImage("icon.gif");
		this.setIconImage(icon); //set icon
		*/
		this.setVisible(true);//bring the window up!!

		//hot key'V'
		operateMenu.setMnemonic('O');

		//hot key for login :ctrl+i
		loginItem.setMnemonic ('I'); 
		loginItem.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_I,InputEvent.CTRL_MASK));

		//hot key for log off: ctrl+l
		logoffItem.setMnemonic ('L'); 
		logoffItem.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_L,InputEvent.CTRL_MASK));

		//hot key for exit :ctrl+x
		exitItem.setMnemonic ('X'); 
		exitItem.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_X,InputEvent.CTRL_MASK));

		//hot key for menu 'C'
		conMenu.setMnemonic('C');

		//hot key for user config :ctrl+u
		userItem.setMnemonic ('U'); 
		userItem.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_U,InputEvent.CTRL_MASK));

		//ctrl+c
		connectItem.setMnemonic ('C'); 
		connectItem.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_C,InputEvent.CTRL_MASK));

		//hot key for help menu :'H'
		helpMenu.setMnemonic('H');

		//hot key for help items: ctrl+h
		helpItem.setMnemonic ('H'); 
		helpItem.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_H,InputEvent.CTRL_MASK));
	}

	/**
	 * initialization procedure
	 */
	public void init(){

		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//menu bar construction
		operateMenu.add (loginItem);
		operateMenu.add (logoffItem);
		operateMenu.add (exitItem);
		//operateMenu.add(sss);
		jMenuBar.add (operateMenu); 
		conMenu.add (userItem);
		conMenu.add (connectItem);
		jMenuBar.add (conMenu);
		helpMenu.add (helpItem);
		jMenuBar.add (helpMenu); 
		setJMenuBar (jMenuBar);

		//buttons
		loginButton = new JButton("Log In");
		logoffButton = new JButton("Log Off");
		userButton  = new JButton("User config" );
		connectButton  = new JButton("Conn config" );
		exitButton = new JButton("Exit" );
		//message when cursor move on
		loginButton.setToolTipText("Connect to server specified");
		logoffButton.setToolTipText("End connection");
		userButton.setToolTipText("Set user info");
		connectButton.setToolTipText("Set server info");
		//add buttons to tool bar
		toolBar.add(userButton);
		toolBar.add(connectButton);
		toolBar.addSeparator();//sepatator :fen ge fu~
		toolBar.add(loginButton);
		toolBar.add(logoffButton);
		toolBar.addSeparator();// fen ge fu
		toolBar.add(exitButton);
		contentPane.add(toolBar,BorderLayout.NORTH);

		checkbox = new JCheckBox("private");
		checkbox.setSelected(false);

		actionlist = new JComboBox();
		actionlist.addItem("smily");
		actionlist.addItem("happily");
		actionlist.addItem("gently");
		actionlist.addItem("angrily");
		actionlist.addItem("cautiously");
		actionlist.addItem("quietly");
		actionlist.setSelectedIndex(0);

		//initialization
		loginButton.setEnabled(true);
		logoffButton.setEnabled(false);

		//action listener for menu bar
		loginItem.addActionListener(this);
		logoffItem.addActionListener(this);
		exitItem.addActionListener(this);
		userItem.addActionListener(this);
		connectItem.addActionListener(this);
		helpItem.addActionListener(this);
		
		//action listener for buttons
		loginButton.addActionListener(this);
		logoffButton.addActionListener(this);
		userButton.addActionListener(this);
		connectButton.addActionListener(this);
		exitButton.addActionListener(this);
		
		combobox = new JComboBox();
		combobox.insertItemAt("all",0);
		combobox.setSelectedIndex(0);
		
		messageShow = new JTextArea();
		messageShow.setEditable(false);
		
		//add scroll pan
		messageScrollPane = new JScrollPane(messageShow,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		messageScrollPane.setPreferredSize(new Dimension(400,400));
		messageScrollPane.revalidate();
		
		clientMessage = new JTextField(23);
		clientMessage.setEnabled(false);
		clientMessageButton = new JButton();
		clientMessageButton.setText("Send");

		//add listener for system message
		clientMessage.addActionListener(this);
		clientMessageButton.addActionListener(this);

		sendToLabel = new JLabel("Send to:");
		express = new JLabel("         face:   ");
		messageLabel = new JLabel("Send MSG:");
		gridBag = new GridBagLayout();
		downPanel = new JPanel(gridBag);		

		gridBagCon = new GridBagConstraints();
		gridBagCon.gridx = 0;
		gridBagCon.gridy = 0;
		gridBagCon.gridwidth = 5;
		gridBagCon.gridheight = 2;
		gridBagCon.ipadx = 5;
		gridBagCon.ipady = 5;
		JLabel none = new JLabel("    ");
		gridBag.setConstraints(none,gridBagCon);
		downPanel.add(none);

		gridBagCon = new GridBagConstraints();
		gridBagCon.gridx = 0;
		gridBagCon.gridy = 2;
		gridBagCon.insets = new Insets(1,0,0,0);
		//gridBagCon.ipadx = 5;
		//gridBagCon.ipady = 5;
		gridBag.setConstraints(sendToLabel,gridBagCon);
		downPanel.add(sendToLabel);

		gridBagCon = new GridBagConstraints();
		gridBagCon.gridx =1;
		gridBagCon.gridy = 2;
		gridBagCon.anchor = GridBagConstraints.LINE_START;
		gridBag.setConstraints(combobox,gridBagCon);
		downPanel.add(combobox);

		gridBagCon = new GridBagConstraints();
		gridBagCon.gridx =2;
		gridBagCon.gridy = 2;
		gridBagCon.anchor = GridBagConstraints.LINE_END;
		gridBag.setConstraints(express,gridBagCon);
		downPanel.add(express);

		gridBagCon = new GridBagConstraints();
		gridBagCon.gridx = 3;
		gridBagCon.gridy = 2;
		gridBagCon.anchor = GridBagConstraints.LINE_START;
		//gridBagCon.insets = new Insets(1,0,0,0);
		//gridBagCon.ipadx = 5;
		//gridBagCon.ipady = 5;
		gridBag.setConstraints(actionlist,gridBagCon);
		downPanel.add(actionlist);

		gridBagCon = new GridBagConstraints();
		gridBagCon.gridx = 4;
		gridBagCon.gridy = 2;
		gridBagCon.insets = new Insets(1,0,0,0);
		//gridBagCon.ipadx = 5;
		//gridBagCon.ipady = 5;
		gridBag.setConstraints(checkbox,gridBagCon);
		downPanel.add(checkbox);

		gridBagCon = new GridBagConstraints();
		gridBagCon.gridx = 0;
		gridBagCon.gridy = 3;
		gridBag.setConstraints(messageLabel,gridBagCon);
		downPanel.add(messageLabel);

		gridBagCon = new GridBagConstraints();
		gridBagCon.gridx = 1;
		gridBagCon.gridy = 3;
		gridBagCon.gridwidth = 3;
		gridBagCon.gridheight = 1;
		gridBag.setConstraints(clientMessage,gridBagCon);
		downPanel.add(clientMessage);

		gridBagCon = new GridBagConstraints();
		gridBagCon.gridx = 4;
		gridBagCon.gridy = 3;
		gridBag.setConstraints(clientMessageButton,gridBagCon);
		downPanel.add(clientMessageButton);

		showStatus = new JTextField(35);
		showStatus.setEditable(false);
		gridBagCon = new GridBagConstraints();
		gridBagCon.gridx = 0;
		gridBagCon.gridy = 5;//0 will make it just below textArea
		gridBagCon.gridwidth = 5;
		gridBag.setConstraints(showStatus,gridBagCon);
		downPanel.add(showStatus);

		contentPane.add(messageScrollPane,BorderLayout.CENTER);
		contentPane.add(downPanel,BorderLayout.SOUTH);
		
		//actions when program exit
		this.addWindowListener(
			new WindowAdapter(){
				public void windowClosing(WindowEvent e){
					if(type == 1){
						//if still connected, disconnect it
						DisConnect();
					}
					System.exit(0);
				}
			}
		);
	}

	/**
	 * Events handling
	 */
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		
		if (obj == userItem || obj == userButton) { //user config
			//pop out the userConf window
			UserConf userConf = new UserConf(this,userName);
			userConf.setVisible(true);
			userName = userConf.userInputName;
		}
		else if (obj == connectItem || obj == connectButton) { //connection config
			//pop up the window
			ConnectConf conConf = new ConnectConf(this,ip,port);
			conConf.setVisible(true);
			//change the new ip & port number to user specified
			ip = conConf.userInputIp;
			port = conConf.userInputPort;
			
		}
		else if (obj == loginItem || obj == loginButton) { //login
			Connect();
		}
		else if (obj == logoffItem || obj == logoffButton) { //log off
			DisConnect();
			showStatus.setText("");
		}
		else if (obj == clientMessage || obj == clientMessageButton) { //send msg
			SendMessage();
			clientMessage.setText("");
		}
		else if (obj == exitButton || obj == exitItem) { //exit
			int j=JOptionPane.showConfirmDialog(
				this,"Are your sure?","Exit",
				JOptionPane.YES_OPTION,JOptionPane.QUESTION_MESSAGE);
			
			if (j == JOptionPane.YES_OPTION){
				if(type == 1){
					DisConnect();
				}
				System.exit(0);
			}
		}
		else if (obj == helpItem) { //help for menu bar
			//pop up help window
			Help helpDialog = new Help(this);;
			helpDialog.setVisible(true);
		}
	}

	
	//method called when you hit the login button
	public void Connect(){
		try{
			socket = new Socket(ip,port);
		}
		catch (Exception e){
			JOptionPane.showConfirmDialog(
				this,"Unable to connect to server specified, please check config","Tip",
				JOptionPane.DEFAULT_OPTION,JOptionPane.WARNING_MESSAGE);
			return;
		}

		try{
			output = new ObjectOutputStream(socket.getOutputStream());
			output.flush();
			input  = new ObjectInputStream(socket.getInputStream() );
			//let the server know my username, this is important
			output.writeObject(userName);
			output.flush();
			
			//use another thread to handle IO
			recvThread = new ClientReceive(socket,output,input,combobox,messageShow,showStatus);
			recvThread.start();
			
			loginButton.setEnabled(false);
			loginItem.setEnabled(false);
			userButton.setEnabled(false);
			userItem.setEnabled(false);
			connectButton.setEnabled(false);
			connectItem.setEnabled(false);
			logoffButton.setEnabled(true);
			logoffItem.setEnabled(true);
			clientMessage.setEnabled(true);
			//show some message on the panel
			messageShow.append("Connecting to server"+ip+":"+port+" success!...\n");
			type = 1;//set flag as connected
		}
		catch (Exception e){
			System.out.println(e);
			return;
		}
	}//end of Connect()
	
	public void DisConnect(){
		loginButton.setEnabled(true);
		loginItem.setEnabled(true);
		userButton.setEnabled(true);
		userItem.setEnabled(true);
		connectButton.setEnabled(true);
		connectItem.setEnabled(true);
		logoffButton.setEnabled(false);
		logoffItem.setEnabled(false);
		clientMessage.setEnabled(false);
		
		if(socket.isClosed()){
			return ;
		}
		
		try{ //tell the server that I am leaving..
			output.writeObject("userLeft");
			output.flush();
		
			input.close();
			output.close();
			socket.close();
			messageShow.append("connection ended...\n");
			type = 0;//set flag as unconnected
		}
		catch (Exception e){
			//
		}
	}
	
	public void SendMessage(){
		String toSomebody = combobox.getSelectedItem().toString();
		String status  = "";
		if(checkbox.isSelected()){
			status = "private";
		}
		
		String action = actionlist.getSelectedItem().toString();
		String message = clientMessage.getText();
		
		if(socket.isClosed()){
			return ;
		}
		
		try{
			output.writeObject("chatMsg");
			output.flush();
			output.writeObject(toSomebody);
			output.flush();
			output.writeObject(status);
			output.flush();
			output.writeObject(action);
			output.flush();
			output.writeObject(message);
			output.flush();
		}
		catch (Exception e){
			//
		}
	}

	/**
	 * get image for specified filename
	 */
	/*
	Image getImage(String filename) {
		URLClassLoader urlLoader = (URLClassLoader)this.getClass().
			getClassLoader();
		URL url = null;
		Image image = null;
		url = urlLoader.findResource(filename);
		image = Toolkit.getDefaultToolkit().getImage(url);
		MediaTracker mediatracker = new MediaTracker(this);
		try {
			mediatracker.addImage(image, 0);
			mediatracker.waitForID(0);
		}
		catch (InterruptedException _ex) {
			image = null;
		}
		if (mediatracker.isErrorID(0)) {
			image = null;
		}

		return image;
	}

	*/
	public static void main(String[] args) {
		
		ChatClient app = new ChatClient();
	}
}