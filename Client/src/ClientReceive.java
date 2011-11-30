/* Xiaolong Cheng, cs594, 2011 fall */
import javax.swing.*;
import java.io.*;
import java.net.*;

/*
 * This class is an embedded Thread of the client program 
 * it takes care of receiving message from the server and update
 * the interface
 */
public class ClientReceive extends Thread {
	private JComboBox combobox;
	private JTextArea textarea;
	
	Socket socket;
	ObjectOutputStream output;
	ObjectInputStream  input;
	JTextField showStatus;

	public ClientReceive(Socket socket,ObjectOutputStream output,
		ObjectInputStream  input,JComboBox combobox,JTextArea textarea,JTextField showStatus){

		this.socket = socket;
		this.output = output;
		this.input = input;
		this.combobox = combobox;
		this.textarea = textarea;
		this.showStatus = showStatus;
	}
	
	public void run(){
		while(!socket.isClosed()){
			try{
				String type = (String)input.readObject();
				
				if(type.equalsIgnoreCase("systemMsg")){
					//if it's a msg, next object should be the message contained
					String sysmsg = (String)input.readObject();
					textarea.append("system msg: "+sysmsg);
				}
				else if(type.equalsIgnoreCase("serviceClose")){
					output.close();
					input.close();
					socket.close();					
					textarea.append("server is down!\n");				
					break;
				}
				else if(type.equalsIgnoreCase("chatMsg")){
					//chat message in next object
					String message = (String)input.readObject();
					textarea.append(message);
				}
				else if(type.equalsIgnoreCase("userList")){
					//updated user list
					String userlist = (String)input.readObject();
					String usernames[] = userlist.split("\n");
					combobox.removeAllItems();
					
					int i =0;
					combobox.addItem("all");
					while(i < usernames.length){
						combobox.addItem(usernames[i]);
						i ++;
					}
					combobox.setSelectedIndex(0);
					showStatus.setText("active users: " + usernames.length + " users");
				}
			}
			catch (Exception e ){
				System.out.println(e);
			}
		}
	}
}