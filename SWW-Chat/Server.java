// Usage:
//        java Server
//
// There is no provision for ending the server gracefully.  It will
// end if (and only if) something exceptional happens.

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.File;
import java.util.*;

public class Server {

  /**
   * Start the server listening for connections.
   */
  public static void main(String [] args) {

    // These tables will be shared by the server threads:
    ClientTable clientTable = new ClientTable();
	MessageTable messageTable = new MessageTable();
	LoginCheck loginCheck = new LoginCheck();
    
    ServerSocket serverSocket = null;
    
    try {
      serverSocket = new ServerSocket(Port.number);
    } catch (IOException e) {
      Report.errorAndGiveUp("Couldn't listen on port " + Port.number);
    }
	
	//when the server is started, the backup should be restored if possible
	//Implements a check that a backup file does exist
	File fileCheck = new File("ServerMessageBackup.txt");
	if(fileCheck.exists() && fileCheck.isDirectory() != true) { 
		//restores the backup
		MessageBackupReader backupRestore = new MessageBackupReader(messageTable, clientTable);
	}
    
    try { 
      // We loop for ever, as servers usually do.
      while (true) {
        // Listen to the socket, accepting connections from new clients:
        Socket socket = serverSocket.accept(); // Matches AAAAA in Client

        // This is so that we can use readLine():
        BufferedReader fromClient = new BufferedReader(
            new InputStreamReader(socket.getInputStream()));

        // We ask the client what its name is:
        String clientName = fromClient.readLine(); // Matches BBBBB in Client
        Report.behaviour(clientName + " connected");
        
		//Creates a UserExists for checking 
		UserExists userExists = new UserExists();
		//Creates a User which will hold the username and which message is being shown for that client
		User currentUser = new User();

        // We create and start a new thread to write to the client:
        PrintStream toClient = new PrintStream(socket.getOutputStream());
        ServerSender serverSender = new ServerSender(clientTable, toClient, messageTable, userExists, currentUser, loginCheck);
        serverSender.start();

        // We create and start a new thread to read from the client:
        (new ServerReceiver(clientName, fromClient, clientTable, serverSender, messageTable, userExists, currentUser, loginCheck)).start();
		
		
      }
    } catch (IOException e) {
      // Lazy approach:
      Report.error("IO error " + e.getMessage());
	  
      // A more sophisticated approach could try to establish a new
      // connection. But this is beyond the scope of this simple exercise.
    }
  }
}
