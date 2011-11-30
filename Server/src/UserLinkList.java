/* Xiaolong Cheng, cs594, 2011 fall */
/**
 * list of (active) users
 */
public class UserLinkList {
	Node root;
	int count;
	
	/**
	 * the root only function as a dummy head of the list,
	 * no real data in it.
	 */
	public UserLinkList(){
		root = new Node();
		root.username = "dummy_head";
		root.next = null;
		count = 0;
	}
	
	/**
	 * add user
	 */
	public void addUser(Node n){
		Node pointer = root;
		
		while(pointer.next != null){
			pointer = pointer.next;
		}
		
		pointer.next = n;
		n.next = null;
		count++;	
	}
	
	
	public void delUser(String username){
		delUser(findUser(username));
	}
	
	/**
	 * remove user
	 */
	public void delUser(Node n){
		//if n is root
		if(n==this.root){//just in case...
			this.root=this.root.next;
			count--;
		} 
		else{//not the root
			Node pointer = this.root;		
			while(pointer.next != null){
				if(pointer.next == n){
					pointer.next = n.next;
					count --;			
					break;
				}//end-if				
				pointer = pointer.next;
			}//end-while
		}
	}
	
	/**
	 * get user count
	 */
	public int getCount(){
		return count;
	}
	
	/**
	 * find user given a username
	 */
	public Node findUser(String username){
		if(count == 0) return null;
		
		Node pointer = this.root;
		
		while(pointer.next != null){
			pointer = pointer.next;
			
			if(pointer.username.equalsIgnoreCase(username)){
				return pointer;
			}
		}
		
		return null;
	}//endfunc
	
}