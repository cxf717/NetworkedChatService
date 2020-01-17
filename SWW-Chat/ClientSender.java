
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;


// Repeatedly reads recipient's nickname and text from the user in two
// separate lines, sending them to the server (read by ServerReceiver
// thread).

public class ClientSender extends Thread {
	
  private String clientName;
  private PrintStream server;
  private UserExists userExists;

  //Constructor takes the client name, the connection to the server, and a userExists checker.
  ClientSender(String clientName, PrintStream server, UserExists userExists) {
    
	this.clientName = clientName;
    this.server = server;
	this.userExists = userExists;
	
  }

  /**
   * Start ClientSender thread.
   */
  public void run() {
    // So that we can use the method readLine:
    BufferedReader user = new BufferedReader(new InputStreamReader(System.in));

    try {
      // Then loop forever sending messages to recipients via the server:
	  
	  outerloop:
      while (true) {
        //reads the user input
		String userInput = user.readLine();
		
		//checks for quit outside of login
		if (userInput.equals("quit")) {
          server.println(userInput);
          break;
        }
		
		//checks for registration
		else if(userInput.equals("register")){
			//reads the username to be registered
			String username = user.readLine();
			
			//does not allow empty or whitespace only usernames
			if(username.isEmpty() == true || username.trim().isEmpty() == true || username.contains(" ")){
				
				//tells user username cannot be blank
				System.out.println("Username must not be blank or contain spaces");
			}
			
			else if(username.contains(":") == true || username.matches("\\[") == true || username.matches("\\]") == true){
				
				//tells user the username cannot contain these characters
				System.out.println("Username cannot contain : or []");
				
			}
			
			//Sends the information to the server
			else{
				//sends these to server
				server.println(userInput);
				server.println(username);
			}
			
		}
		
		//if the login command is used
		else if(userInput.equals("login")){
			
			//sends the command to the server
			server.println(userInput);
			
			//reads the username want to login with
			//send this to the server
			String nickname = user.readLine();
			server.println(nickname);
			
			//in server a check that the username is registered is performed
			//waits for the clientreciever to receive the response of the test.
			try{
				Thread.sleep(1000);
			}catch(InterruptedException e){
				
			}
			
			
			//uses response of the test 
			if((userExists.getUserExists()).equals("yes")){
				
				//Lets user know login was successful
				//Provides an intro to the system
				System.out.println("To switch through messages please use the next and previous commands" + "\n" + 
									"To send a message please use the send command, then enter the user you want to contact and the message" + "\n" +
									"To see the current message please use the current message command" + "\n" +
									"To delete a message use the delete command" + "\n" + "Or use the logout command");
				
				//Starts another loop for reading commands whilst logged in
				while(true){
					
					//reads the command
					String command = user.readLine();
					
					//disallows login whilst logged in
					if(command.equals("login")){
						
						System.out.println("Already logged in");
					}
					
					//disallows registration whilst logged in
					else if(command.equals("register")){
						
						System.out.println("Registration not possible when logged in");
					}
					
					//reads details for the send command
					else if(command.equals("send")){
						
						//reads the person to be sent to and the message
						String name = user.readLine();
						String text = user.readLine();
						
						//sends the details to the server
						server.println(command);
						server.println(name);
						server.println(text);
						
						
					}
					
					//sends the command to server if next command called
					else if(command.equals("next")){
						
						server.println(command);

					}
					
					//sends the command to server if previous command called
					else if(command.equals("previous")){
						
						server.println(command);
					
					}
					
					//sends the command to server if delete command called
					else if(command.equals("delete")){
						
						server.println(command);

					}
					
					else if(command.equals("current message")){
						
						server.println(command);
						
					}
					
					//if quit command is called will break this while loop and thread while loop
					//sends quit to the server
					else if (command.equals("quit")) {
					  server.println(command);
					  break outerloop;
					}
					
					//needs to make it do different stuff for logout
					else if (command.equals("logout")) {
					  server.println(command);
					  //will need to break this loop to allow registration, quit, and login again
					  userExists.userStartAgain();
					  break;
					}
					
					//prompts the required commands need to be used.
					else{
						
						System.out.println("Please use a recognised command");
						
					}
					
				}
			
			//If the username is not registered it needs registering
			}else{
				
				System.out.println("Cannot login. User may already be logged in or not be registered");
				
			}
			
		}
		
		//if no correct command received
		else{
			System.out.println("Please register, login, or quit");
		}
		
		server.flush();
				

	  }
    } catch (IOException e) {
      Report.errorAndGiveUp("Communication broke in ClientSender"
                        + e.getMessage());
    }

    Report.behaviour("Client sender thread ending"); // Matches GGGGG in Client.java
  }
}

/*

What happens if recipient is null? Then, according to the Java
documentation, println will send the string "null" (not the same as
null!). So maye we should check for that case! Paticularly in
extensions of this system.

 */
