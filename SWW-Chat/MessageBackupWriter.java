/*Backup the messages and user details for the system*/

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.ArrayList;


public class MessageBackupWriter{
	
	private MessageTable messages;
	private ConcurrentMap<String,ArrayList<Message>> messageTable;
	private BufferedWriter buffer;
	private FileWriter file;
	
	//constructor for the MessageBackupWriter class
	public MessageBackupWriter(MessageTable messages){
		
		this.messages = messages;
		messageTable = messages.getMap();
		
		//creates the file writer and buffered writer
		try{
			
			if(messageTable != null){
				file = new FileWriter("ServerMessageBackup.txt");
				buffer = new BufferedWriter(file);
				
				//calls the method
				backupMessages();
			}
			
			else{
				buffer.write("No messages");
			}
			
			buffer.close();
			
		//catches any errors when creating the writers
		} catch(IOException e){
			
			System.out.println("Error occurred when backing up messages to file");
			
		}	
	}
	
	//method to write the data to the backup
	private void backupMessages(){
		
		try{
			buffer.write(messageTableToString());
		}catch(IOException e){
			System.out.println("Error occurred when backing up messages to file");
		}
		
	}
	
	//method to get the data from the messageTable
	private String messageTableToString(){
		
		//string that will be the output into the file
		String output = "";
		
		//puts the message table into a set then puts each entry into the output string
		for(Map.Entry e : messageTable.entrySet()){
			
			output += e.getKey() + " " + (e.getValue()).toString() + "\n";
			
		}
		
		return output;
		
	}
	
}