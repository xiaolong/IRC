/* Xiaolong Cheng, cs594, 2011 fall */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import java.net.*;
import java.io.*;

/*
 * main class for chat server
 */
public class ChatServer extends JFrame implements ActionListener{

	public static int port = 8888;//listening port of server

	ServerSocket serverSocket;//server socket
	Image icon;//prog icon
	JComboBox combobox;//to choose msg receiver
	JTextArea messageShow;//msg on server side
	JScrollPane messageScrollPane;//scroll pane
	JTextField showStatus;//show connection status
	JLabel sendToLabel,messageLabel;
	JTextField sysMessage;//
	JButton sysMessageButton;//send msg button
	UserLinkList userLinkList;// user list

	//make menue bar
	JMenuBar jMenuBar = new JMenuBar(); 
	//make menu
	JMenu serviceMenu = new JMenu ("Service(V)"); 
	//make menu item
	JMenuItem portItem = new JMenuItem ("Port config(P)");
	JMenuItem startItem = new JMenuItem ("Start service(S)");
	JMenuItem stopItem=new JMenuItem ("Terminate service(T)");
	JMenuItem exitItem=new JMenuItem ("Exit(X)");
	
	JMenu helpMenu=new JMenu ("Help(H)");
	JMenuItem helpItem=new JMenuItem ("Help(H)");

	//tool bar
	JToolBar toolBar = new JToolBar();

	//button controls
	JButton portSet;//start server listing
	JButton startServer;//start server listing
	JButton stopServer;//stop server listening
	JButton exitButton;//exit
	
	//set frame size
	Dimension faceSize = new Dimension(500, 600);
	
	ServerListen listenThread;

	JPanel downPanel ;
	GridBagLayout gridBag;
	GridBagConstraints gridBagCon;


	/**
	 * constructor for server
	 */
	public ChatServer(){
		init();//initialization

		//add event handler for frame closing
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		//set frame size
		this.setSize(faceSize);

		//set window location
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation( (int) (screenSize.width - faceSize.getWidth()) / 2,
						 (int) (screenSize.height - faceSize.getHeight()) / 2);
		this.setResizable(false);

		this.setTitle("IRC Server"); //

		//prog icon
		icon = getImage("icon.gif");
		this.setIconImage(icon); //set icon
		this.setVisible(true);

		//hot key'V'
		serviceMenu.setMnemonic('V');

		//hot key ctrl+p
		portItem.setMnemonic ('P'); 
		portItem.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_P,InputEvent.CTRL_MASK));

		//hot key ctrl+s
		startItem.setMnemonic ('S'); 
		startItem.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_S,InputEvent.CTRL_MASK));

		//hot key ctrl+T
		stopItem.setMnemonic ('T'); 
		stopItem.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_T,InputEvent.CTRL_MASK));

		//hot key ctrl+x
		exitItem.setMnemonic ('X'); 
		exitItem.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_X,InputEvent.CTRL_MASK));

		//hot key 'H'
		helpMenu.setMnemonic('H');

		//hot key ctrl+p
		helpItem.setMnemonic ('H'); 
		helpItem.setAccelerator (KeyStroke.getKeyStroke (KeyEvent.VK_H,InputEvent.CTRL_MASK));

	}
	
	/**
	 * initialization method
	 */
	public void init(){

		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//add menu bar
		serviceMenu.add (portItem);
		serviceMenu.add (startItem);
		serviceMenu.add (stopItem);
		serviceMenu.add (exitItem);
		jMenuBar.add (serviceMenu); 
		helpMenu.add (helpItem);
		jMenuBar.add (helpMenu); 
		setJMenuBar (jMenuBar);

		//buttons
		portSet = new JButton("Port Setting");
		startServer = new JButton("start service");
		stopServer = new JButton("stop service" );
		exitButton = new JButton("Exit" );
		//add buttons to menu bar
		toolBar.add(portSet);
		toolBar.addSeparator();//add separator
		toolBar.add(startServer);
		toolBar.add(stopServer);
		toolBar.addSeparator();//fen ge fu
		toolBar.add(exitButton);
		contentPane.add(toolBar,BorderLayout.NORTH);

		//disable stop button on initialization
		stopServer.setEnabled(false);
		stopItem .setEnabled(false);

		//set listener for menu bar
		portItem.addActionListener(this);
		startItem.addActionListener(this);
		stopItem.addActionListener(this);
		exitItem.addActionListener(this);
		helpItem.addActionListener(this);
		
		//set listener for button events
		portSet.addActionListener(this);
		startServer.addActionListener(this);
		stopServer.addActionListener(this);
		exitButton.addActionListener(this);
		
		combobox = new JComboBox();
		combobox.insertItemAt("all",0);
		combobox.setSelectedIndex(0);
		
		messageShow = new JTextArea();
		messageShow.setEditable(false);
		//add scroll bar
		messageScrollPane = new JScrollPane(messageShow,
			JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
			JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		messageScrollPane.setPreferredSize(new Dimension(400,400));
		messageScrollPane.revalidate();
		
		showStatus = new JTextField(35);
		showStatus.setEditable(false);
		
		sysMessage = new JTextField(24);
		sysMessage.setEnabled(false);
		sysMessageButton = new JButton();
		sysMessageButton.setText("Send");

		//add listener for system message
		sysMessage.addActionListener(this);
		sysMessageButton.addActionListener(this);

		sendToLabel = new JLabel("Send to:");
		messageLabel = new JLabel("Send msg:");
		downPanel = new JPanel();
		gridBag = new GridBagLayout();
		downPanel.setLayout(gridBag);

		gridBagCon = new GridBagConstraints();
		gridBagCon.gridx = 0;
		gridBagCon.gridy = 0;
		gridBagCon.gridwidth = 3;
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
		gridBagCon.ipadx = 5;
		gridBagCon.ipady = 5;
		gridBag.setConstraints(sendToLabel,gridBagCon);
		downPanel.add(sendToLabel);

		gridBagCon = new GridBagConstraints();
		gridBagCon.gridx =1;
		gridBagCon.gridy = 2;
		gridBagCon.anchor = GridBagConstraints.LINE_START;
		gridBag.setConstraints(combobox,gridBagCon);
		downPanel.add(combobox);

		gridBagCon = new GridBagConstraints();
		gridBagCon.gridx = 0;
		gridBagCon.gridy = 3;
		gridBag.setConstraints(messageLabel,gridBagCon);
		downPanel.add(messageLabel);

		gridBagCon = new GridBagConstraints();
		gridBagCon.gridx = 1;
		gridBagCon.gridy = 3;
		gridBag.setConstraints(sysMessage,gridBagCon);
		downPanel.add(sysMessage);

		gridBagCon = new GridBagConstraints();
		gridBagCon.gridx = 2;
		gridBagCon.gridy = 3;
		gridBag.setConstraints(sysMessageButton,gridBagCon);
		downPanel.add(sysMessageButton);

		gridBagCon = new GridBagConstraints();
		gridBagCon.gridx = 0;
		gridBagCon.gridy = 4;
		gridBagCon.gridwidth = 3;
		gridBag.setConstraints(showStatus,gridBagCon);
		downPanel.add(showStatus);

		contentPane.add(messageScrollPane,BorderLayout.CENTER);
		contentPane.add(downPanel,BorderLayout.SOUTH);
		
		//operations on program exit
		this.addWindowListener(
			new WindowAdapter(){
				public void windowClosing(WindowEvent e){
					stopService();
					System.exit(0);
				}
			}
		);
	}

	/**
	 * events handling
	 */
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		if (obj == startServer || obj == startItem) { //start server
			startService();
		}
		else if (obj == stopServer || obj == stopItem) { //stop server
			int j=JOptionPane.showConfirmDialog(
				this,"Are you sure to stop?","Stop",
				JOptionPane.YES_OPTION,JOptionPane.QUESTION_MESSAGE);
			
			if (j == JOptionPane.YES_OPTION){
				stopService();
			}
		}
		else if (obj == portSet || obj == portItem) { //port setting
			//pop up port setting window
			PortConf portConf = new PortConf(this);
			portConf.setVisible(true);
		}
		else if (obj == exitButton || obj == exitItem) { //exit prog
			int j=JOptionPane.showConfirmDialog(
				this,"Are you sure to exit?","Exit",
				JOptionPane.YES_OPTION,JOptionPane.QUESTION_MESSAGE);
			
			if (j == JOptionPane.YES_OPTION){
				stopService();
				System.exit(0);
			}
		}
		else if (obj == helpItem) { //help on menu bar
			//pop up help dialogue
			Help helpDialog = new Help(this);
			helpDialog.setVisible(true);
		}
		else if (obj == sysMessage || obj == sysMessageButton) { 
			sendSystemMessage();
		}
	}
	
	/**
	 * start server
	 */
	public void startService(){
		try{
			serverSocket = new ServerSocket(port,10);
			messageShow.append("server started, listening to port"+port+"...\n");
			
			startServer.setEnabled(false);
			startItem.setEnabled(false);
			portSet.setEnabled(false);
			portItem.setEnabled(false);

			stopServer .setEnabled(true);
			stopItem .setEnabled(true);
			sysMessage.setEnabled(true);
		}
		catch (Exception e){
			//System.out.println(e);
		}
		userLinkList = new UserLinkList();
		
		listenThread = new ServerListen(serverSocket,combobox,
			messageShow,showStatus,userLinkList);
		listenThread.start();
	}
	
	/**
	 * close server
	 */
	public void stopService(){
		try{
			//tell everyone the server is closing
			sendStopToAll();
			listenThread.isStop = true;
			serverSocket.close();
			
			//stop one by one....
			Node node = userLinkList.root;
			while(node.next!=null){
				node.next.input.close();
				node.next.output.close();
				node.next.socket.close();
				node = node.next;
			}

			stopServer.setEnabled(false);
			stopItem.setEnabled(false);
			startServer.setEnabled(true);
			startItem.setEnabled(true);
			portSet.setEnabled(true);
			portItem.setEnabled(true);
			sysMessage.setEnabled(false);

			messageShow.append("server is closed\n");

			combobox.removeAllItems();
			combobox.addItem("all");
		}
		catch(Exception e){
			//System.out.println(e);
		}
	}
	
	/**
	 * tell every body the server is closing
	 */
	public void sendStopToAll(){

		Node node = userLinkList.root;
		while(node.next!=null){
			try{
				node.next.output.writeObject("serviceClose");
				node.next.output.flush();
			}
			catch (Exception e){
				//System.out.println("$$$"+e);
			}
			node=node.next;
		}//endwhile
	}
	
	/**
	 * send message to all
	 */
	public void sendMsgToAll(String msg){

		Node node = userLinkList.root;
		while(node.next!= null){
			try{
				node.next.output.writeObject("systemMsg");
				node.next.output.flush();
				node.next.output.writeObject(msg);
				node.next.output.flush();
			}
			catch(Exception e){
				//System.out.println("@@@"+e);
			}
			node= node.next;
		}

		sysMessage.setText("");//clear the input box
	}

	/**
	 * send msg to client
	 */
	public void sendSystemMessage(){
		String toSomebody = combobox.getSelectedItem().toString();
		String message = sysMessage.getText() + "\n";
		
		messageShow.append(message);
		
		//send to all
		if(toSomebody.equalsIgnoreCase("all")){
			sendMsgToAll(message);
		}
		else{
			//send to some particular user
			Node node = userLinkList.findUser(toSomebody);
			
			try{
				node.output.writeObject("systemMsg");
				node.output.flush();
				node.output.writeObject(message);
				node.output.flush();
			}
			catch(Exception e){
				//System.out.println("!!!"+e);
			}
			sysMessage.setText("");//clear the input area
		}
	}

	/**
	 * get image from specified filename
	 */
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

	public static void main(String[] args) {
		ChatServer app = new ChatServer();
	}
}