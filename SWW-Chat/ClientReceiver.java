/*Client Receiver class*/

import java.io.BufferedReader;
import java.io.IOException;
import java.net.SocketException;

// Gets messages from other clients via the server (by the
// ServerSender thread).

public class ClientReceiver extends Thread {

  private BufferedReader server;
  private UserExists userExists;

  //constructor for the class reads in a reader to listen to the server
  //Also reads in a UserExists for the login/registration checks
  ClientReceiver(BufferedReader server, UserExists userExists) {
    this.server = server;
	this.userExists = userExists;
  }

  /**
   * Run the client receiver thread.
   */
  public void run() {
    // Print to the user whatever we get from the server:
    try {
      while (true) {
        
		//reads from the server
		String s = server.readLine(); // Matches FFFFF in ServerSender.java

        if (s == null) {
          throw new NullPointerException();
        }
		
		//Receives the result of the user exists server side check
		if (s.equals("User does not exist")){
			
			//if the user does not exist check should answer no
			userExists.setUserExists("no");
			//continue so that message isn't sent to the user
			continue;
			
		}
		
		//Receives the result of the user exists server side check
		if (s.equals("User does exist")){
			
			//if the user does exist check should answer yes
			userExists.setUserExists("yes");
			//continue so that message isn't sent to the user
			continue;
		}

        System.out.println(s);
      }
    } catch (SocketException e) { // Matches HHHHH in Client.java
      Report.behaviour("Client receiver ending");
    } catch (NullPointerException | IOException e) {
      Report.errorAndGiveUp("Server seems to have died "
              + (e.getMessage() == null ? "" : e.getMessage()));
    }
  }
}


/*

 * The method readLine returns null at the end of the stream

 * It may throw IoException if an I/O error occurs

 * See https://docs.oracle.com/javase/8/docs/api/java/io/BufferedReader.html#readLine--


 */
