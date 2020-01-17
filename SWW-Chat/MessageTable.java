import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.ArrayList;
import java.util.*;

public class MessageTable {
	
	private static final int currentMessage = 0;

	private ConcurrentMap<String,ArrayList<Message>> messageTable = new ConcurrentHashMap<>();
  
  //returns the map
  public ConcurrentMap<String,ArrayList<Message>> getMap(){
	  return messageTable;
  }

  //Gives the message array list for the user
  //returns null if the user does not exist in the messageTable
  public ArrayList<Message> getMessages(String username) {
    return messageTable.get(username);
  }
  
    //Returns the message at index 0 as current is most recently sent
  public String getCurrentMessage(String user){
	
	ArrayList<Message> messages = getMessages(user);
	
	//if there are no messages
	if(messages.isEmpty()){
		return "No current messages";
	}
	
	//if there are messages then returns the most recent, i.e. at index 0.
	else{
		return (messages.get(currentMessage)).toString();
	}
	
  }
  
   //Adds a user into the table and creates an arraylist to store user's messages
	public void add(String username) {
		messageTable.put(username, new ArrayList<Message>());
	}
	
	
  // Removes user from table:
  /*This is no longer implemented in the system as the user must
	receive messages during off line activity.
	However, it is kept here as it could be implemented if
	a user "deleted their account"*/
  public void remove(String username) {
    messageTable.remove(username);
  }
  
  
  //Adds a message to the array list for that user
  public void addMessage(String username, Message message){
	
	ArrayList<Message> messages = getMessages(username);
	messages.add(currentMessage, message);
  
  }
  
  //provides the next message for user which will be an older message
  public String nextMessage(String user, User currentUser){
	
	/*creates a temporary user for the one that called the command
	  and gets the message array list for the user */
	User tempUser = currentUser;
	ArrayList<Message> messages = getMessages(user);
	
	//if there are no messages in the array list
	if(messages.isEmpty()){
		return "No current messages";
	}
	
	//if there are messages in the array list
	else{
		try{
			
			//the index is increased to get the wanted message
			currentUser.increaseMessageIndex();
			//the message  is then provided
			return (messages.get(currentUser.getMessageIndex())).toString();
			
		//if there is no next message, i.e. reached the end of the array
		}catch(IndexOutOfBoundsException e){
			
			//the index is decreased back to before the try was run as there is no next message
			currentUser.decreaseMessageIndex();
			return "No more messages to show";
		}
	}
	
  }
  
  //provides the previous message for user which will be a newer message
  public String previousMessage(String user, User currentUser){
	
	/*creates a temporary user for the one that called the command
	  and gets the message array list for the user */
	User tempUser = currentUser;
	ArrayList<Message> messages = getMessages(user);
	
	//if there are no messages in the array list
	if(messages.isEmpty()){
		return "No current messages";
	}
	
	//if there are messages in the array list
	else{
		try{
			
			//the index is decreased to get the wanted message
			currentUser.decreaseMessageIndex();
			//this message is then returned
			return (messages.get(currentUser.getMessageIndex())).toString();
			
		//if there is no previous message, i.e. the end of the array list is reached
		}catch(IndexOutOfBoundsException e){
			
			//the index is increased back to before the try was run as there is no previous message
			currentUser.increaseMessageIndex();
			return "Current Message reached," + " " + getCurrentMessage(user);
		}
	}
  }
  
  //removes the message at the 0 index as it removes the current message for the user
  public String deleteMessage(String user){

	ArrayList<Message> messages = getMessages(user);
	
	//if the message array list is empty
	if(messages.isEmpty()){
		return "No message to delete";
	}
	
	//if there is a message to remove
	else{
		messages.remove(messages.get(currentMessage));
		return "Message deleted";
	}	
  }
}