/*ClientTable being used to send the commands through for each individual client whilst user logged in*/

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

public class ClientTable {

  private ConcurrentMap<String,BlockingQueue<String>> queueTable
      = new ConcurrentHashMap<>();

  //adds a new user to the table
  //this is used when a user is registered
  public void add(String nickname) {
    queueTable.put(nickname, new LinkedBlockingQueue<String>());
  }

  // Returns null if the nickname is not in the table:
  public BlockingQueue<String> getQueue(String nickname) {
    return queueTable.get(nickname);
  }
  
  //returns the map
  public ConcurrentMap<String,BlockingQueue<String>> getMap(){
	  return queueTable;
  }

  // Removes a user from table:
  /*This is no longer implemented in the system as the user must
	receive messages during off line activity.
	However, it is kept here as it could be implemented if
	a user "deleted their account"*/
  public void remove(String nickname) {
    queueTable.remove(nickname);
  }
}
