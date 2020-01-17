/*Login check class*/

import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.ArrayList;
import java.util.*;

public class LoginCheck{
	
	private ConcurrentMap<String,Boolean> loginTable = new ConcurrentHashMap<>();

	
	//gets the value attached to key, either null or true
	public Boolean getLoggedIn(String username){
		
		return loginTable.get(username);
		
	}
	
	//adds the user to the table and sets logged in as true
	public void login(String username){

		loginTable.put(username, true);
	
	}
	
	//logout removes the user and resets the string
	public void logout(String username){
		
		loginTable.remove(username);
		
	}
	
	//checks if the user has logged in already
	public boolean checkLoggedIn(String username){
		
		if(loginTable.get(username) == true){
			return true;
		}
		
		return false;
		
	}
	
	
}