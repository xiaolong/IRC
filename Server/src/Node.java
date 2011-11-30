/* Xiaolong Cheng, cs594, 2011 fall */
import java.net.*;
import java.io.*;

/**
 * every node represents a user
 */
public class Node {
	String username = null;
	
	//every node(client) has its own socket and IO streams
	Socket socket = null;
	ObjectOutputStream output = null;
	ObjectInputStream input = null;
		
	Node next = null;
}