/* Xiaolong Cheng, cs594, 2011 fall */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;

import java.io.*;
import java.net.*;

/*
 * listening class on server side
 */
public class ServerListen extends Thread {
	ServerSocket server;
	
	JComboBox combobox;
	JTextArea textarea;
	JTextField textfield;
	UserLinkList userLinkList;
	
	//Node client;
	ServerReceive recvThread;
	
	public boolean isStop;

	/*
	 * listening to login and logoff...
	 */
	public ServerListen(ServerSocket server,JComboBox combobox,
		JTextArea textarea,JTextField textfield,UserLinkList userLinkList){

		this.server = server;
		this.combobox = combobox;
		this.textarea = textarea;
		this.textfield = textfield;
		this.userLinkList = userLinkList;
		
		isStop = false;
	}
	
	/* when there is a request, accept it, 
	 * construct a new node on the server side,
	 * add the new node to userlist,
	 * and assign a new Thread to handle coming message.
	 */
	public void run(){
		while(!isStop && !server.isClosed()){
			try{
				Node client = new Node();
				client.socket = server.accept();
				client.output = new ObjectOutputStream(client.socket.getOutputStream());
				client.output.flush();
				client.input  = new ObjectInputStream(client.socket.getInputStream());
				/*client will write its username to this stream when
				it starts a connection */
				client.username = (String)client.input.readObject();	
				
				//show information
				combobox.addItem(client.username);
				
				userLinkList.addUser(client);
				
				textarea.append("user " + client.username + " is active" + "\n");
				textfield.setText("active users:" + userLinkList.getCount() + "users\n");
				
				recvThread = new ServerReceive(textarea,textfield,
					combobox,client,userLinkList);				
				recvThread.sendToAll("user "+client.username+" joins in the room!\n");
				recvThread.start();
			}
			catch(Exception e){
			}
		}
	}
}