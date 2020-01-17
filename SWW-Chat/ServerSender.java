
import java.io.PrintStream;
import java.util.concurrent.BlockingQueue;

// Continuously reads from message queue for a particular client,
// forwarding to the client.

public class ServerSender extends Thread {
  private BlockingQueue<String> clientQueue;
  private ClientTable clientTable;
  private MessageTable messageTable;
  private PrintStream client;
  private UserExists userExists;
  private User currentUser;
  private LoginCheck loginCheck;

  /**
   * Constructs a new server sender.
   * @param q messages from this queue will be sent to the client
   * @param c the stream used to send data to the client
   * @param m the message table for storing the messages
   * @param u the class to check if a user exists
   * @param loggedIn class for the currently logged in user
   * @param l login class from server 
   */
  public ServerSender(ClientTable t, PrintStream c, MessageTable m, UserExists u, User loggedIn, LoginCheck l) { 
    client = c;
	clientTable = t;
	messageTable = m;
	clientQueue = null;
	userExists = u;
	currentUser = loggedIn;
	loginCheck = l;
  }

  /**
   * Starts this server sender.
   */
  public void run() {
    try {
      while (true) {
		  
		System.out.print("");
		
		//if the result of the check is no then sends response to client
		if((userExists.getUserExists()).equals("no")){
			client.println("User does not exist");
			client.flush();
			userExists.userStartAgain();
		}
		 
		//if the result of the check is yes then sends response to client
		else if((userExists.getUserExists()).equals("yes")){
			client.println("User does exist");
			client.flush();
			userExists.userStartAgain();
		}
		
		//if the result of the check is no then sends response to client
		else if((userExists.getRegisterExists()).equals("no")){
			client.println("New username registered");
			client.flush();
			userExists.registerStartAgain();
		}
		 
		//if the result of the check is yes then sends response to client
		else if((userExists.getRegisterExists()).equals("yes")){
			client.println("Username already exists");
			client.flush();
			userExists.registerStartAgain();
		}
		
		//If there is a username then gets the queue for that user for use later
		if(currentUser.getUsername() != null){
			clientQueue = clientTable.getQueue(currentUser.getUsername());
		}
		
		//If there is a username and the client queue has been set (see above)
		if(currentUser.getUsername() != null && clientQueue != null){
			
			//reads the command that was sent into the queue and then acts
			String command = clientQueue.take();
			
			//Prints out the new current message when a login happens
			if(command.equals("login")){
				
				client.println("Login Successful");
				client.println("Current Message: " + messageTable.getCurrentMessage(currentUser.getUsername()));
			}
			
			//If there has been a send command sent by another user
			if(command.equals("send")){
				
				//sends the client the message that was received
				client.println("New Message," + "  " + messageTable.getCurrentMessage(currentUser.getUsername()));
				
			}
			
			//sends the client the next message with this being an older message
			if( command.equals("next")){
				
				client.println(messageTable.nextMessage(currentUser.getUsername(), currentUser));
			}
			
			//sends the client the previous message with this being a newer message
			if( command.equals("previous")){
				
				client.println(messageTable.previousMessage(currentUser.getUsername(), currentUser));
			}
			
			//sends the client the current message which is the most recent
			if(command.equals("current message")){
				
				//the index is set to 0 to so that next and previous works correctly
				currentUser.setMessageIndex(0);
				client.println(messageTable.getCurrentMessage(currentUser.getUsername()));
				
			}
			
			//removes the newest message that was received
			if( command.equals("delete")){

				client.println(messageTable.deleteMessage(currentUser.getUsername()));
			}
			
			//logout command
			if(command.equals("logout")){
				
				//tells the client logout worked then cleans up
				client.println("Logout Successfull" +"\n" + "Please login, register, or quit");
				//then reset all user based variables
				loginCheck.logout(currentUser.getUsername());
				currentUser.resetUser();
				userExists.userStartAgain();
				userExists.registerStartAgain();
			}
		}
		
      }
    } catch (InterruptedException e) {
      Report.behaviour("Server sender ending");
    }
  }

}

/*

 * Throws InterruptedException if interrupted while waiting

 * See https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html#take--

 */
