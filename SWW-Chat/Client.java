/*Client where threads for client are run*/

// Usage:
//        java Client user-nickname server-hostname
//
// After initializing and opening appropriate sockets, we start two
// client threads, one to send messages, and another one to get
// messages.

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;

class Client {

  public static void main(String[] args) {

    // Check correct usage:
	//This is now 1 as it does not need a username
    if (args.length != 1) {
      Report.errorAndGiveUp("Usage: java Client server-hostname");
    }

    // Initialize information:
    String hostname = args[0];

    // Open sockets:
    PrintStream toServer = null;
    BufferedReader fromServer = null;
    Socket server = null;

    try {
      server = new Socket(hostname, Port.number); // Matches AAAAA in Server.java
      toServer = new PrintStream(server.getOutputStream());
      fromServer = new BufferedReader(new InputStreamReader(server.getInputStream()));
    } catch (UnknownHostException e) {
      Report.errorAndGiveUp("Unknown host: " + hostname);
    } catch (IOException e) {
      Report.errorAndGiveUp("The server doesn't seem to be running " + e.getMessage());
    }

    // Tell the server client is the name
	String clientName = "client";
    toServer.println(clientName); // Matches BBBBB in Server.java
	
	//creates a userExists checking class to be used in the threads
	UserExists userExists = new UserExists();
	
    // Create two client threads of a different nature:
    ClientSender sender = new ClientSender(clientName,toServer,userExists);
    ClientReceiver receiver = new ClientReceiver(fromServer,userExists);

    // Run them in parallel:
    sender.start();
    receiver.start();

    // Wait for them to end and close sockets.
    try {
      sender.join();         // Waits for ClientSender.java to end. Matches GGGGG.
      Report.behaviour("Client sender ended");
      toServer.close();      // Will trigger SocketException
      fromServer.close();    // (matches HHHHH in ClientServer.java).
      server.close();        // https://docs.oracle.com/javase/7/docs/api/java/net/Socket.html#close()
      receiver.join();
      Report.behaviour("Client receiver ended");
    } catch (IOException e) {
      Report.errorAndGiveUp("Something wrong " + e.getMessage());
    } catch (InterruptedException e) {
      Report.errorAndGiveUp("Unexpected interruption " + e.getMessage());
    }
    Report.behaviour("Client ended. Goodbye.");
  }
}

