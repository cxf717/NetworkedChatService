
import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;

public class ServerReceiver extends Thread {
  private String myClientsName;
  private String clientName;
  private BufferedReader myClient;
  private ClientTable clientTable;
  private ServerSender companion;
  private User currentUser;
  private MessageTable messageTable;
  private UserExists userExists;
  private LoginCheck loginCheck;

  /**
   * Constructs a new server receiver.
   * @param n the name of the client with which this server is communicating
   * @param c the reader with which this receiver will read data
   * @param t the table of known clients and connections
   * @param s the corresponding sender for this receiver
   * @param m the Message table for the system
   * @param u the UserExists checking class for the system
   * @param loggedIn the User class that corresponds to this receiver
   */
  public ServerReceiver(String n, BufferedReader c, ClientTable t, ServerSender s, MessageTable m, UserExists u, User loggedIn, LoginCheck l) {
    clientName = n;
    myClient = c;
    clientTable = t;
    companion = s;
	currentUser = loggedIn;
	messageTable = m;
	userExists = u;
	loginCheck = l;
  }

  /**
   * Starts this server receiver.
   */
  public void run() {
    try {
	
      while (true) {
		  
		  String userInput = myClient.readLine(); // Matches CCCCC in ClientSender.java
		
		//if the command received is empty, quit or logout then the thread is ended
        if (userInput == null || userInput.equals("quit")) {
          // Either end of stream reached, just give up, or user wants to quit
          break;
        }
		
		//logout command
		else if (userInput.equals("logout")){
			BlockingQueue<String> recipientsQueue = clientTable.getQueue(currentUser.getUsername()); 
			
			//if there is a queueoffer the command to the sender
		    if (recipientsQueue != null) {
			  recipientsQueue.offer(userInput);
		  
		    //there is no queue
		    } else {
				Report.error("No queue to send command");
		    }
		}
			
		
		//register command 
		else if(userInput.equals("register")){
			
			String username = myClient.readLine();
			
			//check that the user name is not already taken
			if(messageTable.getMessages(username) != null && clientTable.getQueue(username) != null){
				
				//if user has been registered sets the check to yes
				userExists.setRegisterExists("yes");
				
			}else{
				
				//tells server a registration happened
				System.out.println("New user registered:" + " " + username);
				
				//if the user does not exist then adds the user 
				userExists.setRegisterExists("no");
				clientTable.add(username);
				messageTable.add(username);
				
				//create the backup file for the messagetable
				MessageBackupWriter backup = new MessageBackupWriter(messageTable);
			
			}
		}
		
		
		//login command
		else if(userInput.equals("login")){
			
			String user = myClient.readLine();

			//checks that the username has been registered
			if(messageTable.getMessages(user) != null && clientTable.getQueue(user) != null){
				
				//if user is logged in elsewhere
				if((loginCheck.getLoggedIn(user)) != null){
								
					//gives the checked username to the server sender
					System.out.println("User already logged in");
					
				//if the user is not logged in elsewhere
				} else if ((loginCheck.getLoggedIn(user)) == null || (loginCheck.checkLoggedIn(user)) == false){
					
					//logs in the user
					loginCheck.login(user);
					
					//sets the username for the user and that the user exists
					currentUser.setUsername(user);
					userExists.setUserExists("yes");
					
					//gets the blocking queue for the user
					BlockingQueue<String> recipientsQueue = clientTable.getQueue(currentUser.getUsername()); 
					
					//send the command to the server sender via the blocking queue
					if (recipientsQueue != null) {
						recipientsQueue.offer(userInput);
					
					//if there are no messages
					} else {
						Report.error("No messages to show");
					  }
					
				}
			
			//if the user has not been registered so username cannot be logged in
			}else{
				userExists.setUserExists("no");
			}
			
		}
		
		//send command
		else if(userInput.equals("send")){
			
			String sendUser = myClient.readLine();
			String text = myClient.readLine();
			
			//if there is a message to send
			if (text != null) {
			  
			  //create the new message
			  Message msg = new Message(currentUser.getUsername(), text);
			  BlockingQueue<String> recipientsQueue = clientTable.getQueue(sendUser); // Matches EEEEE in ServerSender.java
			  
			  //if the user exists
			  if (recipientsQueue != null && messageTable != null) {

				//send the send command to the server sender and the message
				recipientsQueue.offer(userInput);
				messageTable.addMessage(sendUser, msg);
				
				//create the backup file of the messages
				MessageBackupWriter backup = new MessageBackupWriter(messageTable);
			  
			  //if the user does not exist
			  } else {
				Report.error("Message for nonexistent client " + sendUser + ": " + text);
			  }
			  
			}
		}
		
		//next command
		else if(userInput.equals("next")){
			
		  BlockingQueue<String> recipientsQueue = clientTable.getQueue(currentUser.getUsername()); 
		  
		  //if the queue has been created
		  if (recipientsQueue != null) {
			recipientsQueue.offer(userInput);
		  
		  //if the queue has not been created
		  } else {
			Report.error("No next messages");
		  }
			  
			
		}
		
		//previous command
		else if(userInput.equals("previous")){
			
			BlockingQueue<String> recipientsQueue = clientTable.getQueue(currentUser.getUsername()); 
		  
		  //if the queue has been created offer the command to the server sender
		  if (recipientsQueue != null) {
			recipientsQueue.offer(userInput);
		 
		 //the queue has not been created
		 } else {
			Report.error("No previous messages");
		  }

			
		}
		
		else if(userInput.equals("current message")){
			
			BlockingQueue<String> recipientsQueue = clientTable.getQueue(currentUser.getUsername()); 
		  
		  //if there are messages  offer the command to the server sender
		  if (recipientsQueue != null) {
			recipientsQueue.offer(userInput);
		 
		 //there are no messages
		 } else {
			Report.error("No current message to show");
		  }

			
		}
			
			
		
		//delete command
		else if(userInput.equals("delete")){
			
		BlockingQueue<String> recipientsQueue = clientTable.getQueue(currentUser.getUsername()); 
		  
		  //if there are messages offer the command to the server sender
		  if (recipientsQueue != null) {
			recipientsQueue.offer(userInput);
			
			//create the backup file of the messages
			MessageBackupWriter backup = new MessageBackupWriter(messageTable);
		  
		  //if there are no messages
		  } else {
			Report.error("No message to delete");
		  }
		}
		
		  
         else {
          // No point in closing socket. Just give up.
        }
      }
    } catch (IOException e) {
      Report.error("Something went wrong with the client " 
                   + myClientsName + " " + e.getMessage()); 
      // No point in trying to close sockets. Just give up.
      // We end this thread (we don't do System.exit(1)).
    }

    Report.behaviour("Server receiver ending");
    companion.interrupt();
  }
}

