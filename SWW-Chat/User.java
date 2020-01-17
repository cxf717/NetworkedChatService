/*user class for use in server*/

public class User{
	
	//username of the user
	private String username = null;
	
	//Which message is currently being viewed by user
	private int messageIndex = 0;
	
	//sets the username
	public void setUsername(String username){
		this.username = username;
	}
	
	//gets the username
	public String getUsername(){
		return username;
	}
	
	//sets the messageIndex
	public void setMessageIndex(int i){
		messageIndex = i;
	}
	
	//returns the message index for use in next and previous (see message table)
	public int getMessageIndex(){
		return messageIndex;
	}
	
	//increases the message index
	public void increaseMessageIndex(){
		messageIndex++;
	}
	
	//decreases the message index 
	public void decreaseMessageIndex(){
		messageIndex--;
	}
	
	//resets the user so that can login as someone else
	public void resetUser(){
		username = null;
		messageIndex = 0;
	}
	
}