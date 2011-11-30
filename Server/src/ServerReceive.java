/* Xiaolong Cheng, cs594, 2011 fall */
import javax.swing.*;
import java.io.*;
import java.net.*;

/*
 * message transfer class on server
 */
public class ServerReceive extends Thread {
	JTextArea textarea;
	JTextField textfield;
	JComboBox combobox;
	Node client;
	UserLinkList userLinkList;//users list
	
	public boolean isStop;
	
	public ServerReceive(JTextArea textarea,JTextField textfield,
		JComboBox combobox,Node client,UserLinkList userLinkList){

		this.textarea = textarea;
		this.textfield = textfield;
		this.client = client;
		this.userLinkList = userLinkList;
		this.combobox = combobox;
		
		isStop = false;
	}
	
	public void run(){
		//send user list to all users
		sendUserList();
		
		while(!isStop && !client.socket.isClosed()){
			try{
				String type = (String)client.input.readObject();
				
				if(type.equalsIgnoreCase("chatMsg")){
					
					//read the subsequent objects if it's a chatMsg...
					String toSomebody = (String)client.input.readObject();
					String status  = (String)client.input.readObject();
					String action  = (String)client.input.readObject();
					String message = (String)client.input.readObject();
					
					String msg = client.username 
							+" "+ action
							+ " says to "
							+ toSomebody 
							+ "  : "
							+ message
							+ "\n";
					if(status.equalsIgnoreCase("private")){
						msg = " [private] " + msg;
					}
					
					textarea.append(msg);
					
					if(toSomebody.equalsIgnoreCase("all")){
						sendToAll(msg);//send to all
					}
					else{
						try{
							client.output.writeObject("chatMsg");
							client.output.flush();
							client.output.writeObject(msg);
							client.output.flush();
						}
						catch (Exception e){
							//System.out.println("###"+e);
						}
						
						Node node = userLinkList.findUser(toSomebody);
						
						if(node != null){
							node.output.writeObject("chatMsg"); 
							node.output.flush();
							node.output.writeObject(msg);
							node.output.flush();
						}
					}
				}
				else if(type.equalsIgnoreCase("userLeft")){
					userLinkList.delUser(client.username);
					//Node node = userLinkList.findUser(client.username);
					//userLinkList.delUser(node);
					
					String msg = "user " + client.username + " has left\n";
					int count = userLinkList.getCount();

					combobox.removeAllItems();
					combobox.addItem("all");
					/*
					int i = 0;
					while(i < count){
						Node node = userLinkList.findUser(i);
						if(node == null) {
							i ++;
							continue;
						} 		
						combobox.addItem(node.username);
						i++;
					} */
					Node n= userLinkList.root;
					while(n.next != null){
						combobox.addItem(n.next.username);
						n=n.next;
					}
					
					combobox.setSelectedIndex(0);

					textarea.append(msg);
					textfield.setText("active user:" + userLinkList.getCount() + "users\n");
					
					sendToAll(msg);//send message to all
					sendUserList();//resend user list to update
					
					break;
				}
			}
			catch (Exception e){
				//System.out.println(e);
			}
		}
	}
	
	/*
	 * send message to all active users 
	 */
	public void sendToAll(String msg){
		//write to users' stream one by one...
		Node node=userLinkList.root;
		while(node.next!=null){
			try{
				node.next.output.writeObject("chatMsg");
				node.next.output.flush();
				node.next.output.writeObject(msg);
				node.next.output.flush();
			}
			catch (Exception e){
				//System.out.println(e);
			}
			node= node.next;
		}//end-while
		
	}
	
	/*
	 * send updated user list to all active users
	 */
	public void sendUserList(){
		String userlist = "";
		
		//package all the usernames into a long string
		Node iter=userLinkList.root;
		while(iter.next!=null){
			userlist+=iter.next.username +'\n';
			iter=iter.next;
		}

		//send the list to all the users one by one..
		Node node=userLinkList.root;
		while(node.next!=null){
			try{
				node.next.output.writeObject("userList");
				node.next.output.flush();
				node.next.output.writeObject(userlist);
				node.next.output.flush();
			} catch (Exception e){
				//System.out.println(e);
			}			
			node=node.next;
		}//endwhile		
		
	}//endfunc
	
}