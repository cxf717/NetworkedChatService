/*Class to check if user already exists or not*/

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

public class UserExists {
	
	//variables that will be used in the class
	//One for checking a user exists and one to check a registration happened
	private String userExists = "";
	private String registerExists = "";
	
	//set the userExists variable
	public void setUserExists(String t){
		userExists = t;
	}
	
	//get the userExists variable
	public String getUserExists(){
		return userExists;
	}
	
	//set the registerExists variable
	public void setRegisterExists(String t){
		registerExists = t;
	}
	
	//get the registerExists variable
	public String getRegisterExists(){
		return registerExists;
	}
	
	//Allows for a reset if the checks need to be done again
	public void userStartAgain(){
		userExists = "";
	}
	
	//allows for a reset if the checks need to be done again
	public void registerStartAgain(){
		registerExists = "";
	}
	
}