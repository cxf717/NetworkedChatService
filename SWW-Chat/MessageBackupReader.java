/*Restore Backup the messages and user details for the system*/

import java.util.ArrayList;
import java.io.*;
import java.util.*;

public class MessageBackupReader{
	
	private static final int keyIndex = 0;
	private static final int userIndex = 1;
	private static final int messageIndex = 3;
	
	private MessageTable messageTable;
	private ArrayList<Character> fileIndividual;
	private ClientTable clientTable;
	
	public MessageBackupReader(MessageTable messageTable, ClientTable clientTable){
		
		this.messageTable = messageTable;
		this.clientTable = clientTable;
		
		//attempts to read a file
		try{
			
			FileReader file = new FileReader("ServerMessageBackup.txt");
			BufferedReader buffer = new BufferedReader(file);
			
			//array list for the reading
			fileIndividual = new ArrayList<Character>();
			
			//reading the file in as characters into an array list
			int c;
			while ((c = file.read()) != -1 ){
				char ch = (char)c;
				fileIndividual.add(ch);
			}
			
			//restores the data read from the file
			restoreBackup(fileToString(fileIndividual));
			
			
		//catches any errors when creating the writers
		} catch(IOException e){
			
			System.out.println("Error occurred when backing up messages to file");
			
		}	
		
	}
	
	//method to restore the data from the backup of the server
	public void restoreBackup(String fileS){
		
		String fileReadIn = fileS;
		
		if(fileS.isEmpty() != true){
			
			String[] backupData = fileS.split("\n");
			
			//goes through the file and splits it into the sets of key/values
			//these are then added to the message table
			for(String s: backupData){
				
				//split each set of data into the name and the messages
				String[] keySplit = s.split("\\[");
				//take the name from array and put into a string
				String keyFinal = (keySplit[keyIndex]).trim();
				
				//adds the user to the table 
				messageTable.add(keyFinal);
				clientTable.add(keyFinal);
				
				//goes through each set of information, user and messages, and splits them
				for(String t: keySplit){
					
					//only applies method to the messages and not the user which is also in the array
					if( t.contains(":") ){
						
						//splits the message string into each individual message
						String[] tempMessages = t.split(",");
						
						//goes through each message in the tempMessages variable and adds it to the message array list of key
						for(String r: tempMessages){
							
							//splits into the user and the message into array
							//because it is split by spaces, any messages that have spaces will be split thus will need adding back together
							String[] tempSplit = r.split(" ");
							String fromUser = "";
							String message = "";
							
							//split the message into the user it was sent from and the message
							for(int i=messageIndex; i<tempSplit.length; i++){
								
								//removes all the white space from the username that sent the message
								fromUser = (tempSplit[userIndex]).replaceAll("\\s","");
								
								//adds all the words to the message that was sent and readd the spaces
								message += tempSplit[i] + " ";
								
							}
							
							//now create a message using that
							Message msg = new Message(fromUser, message);
							
							//now add that message to the array list of the key
							//this array list was created when the key was found earlier 
							messageTable.addMessage(keyFinal, msg);
							
						}
							
						
					}
					
					else{
						//do nothing as that will be a key and not a value
						//this is due to the formatting of messages
					}
					
				}
				
			} 
		}
		
		//no data in the file
		else{
			
			System.out.println("No data to restore");
			
		}
		
	}
	
	//method to take the file that has been read in and put it into a string
	public String fileToString(ArrayList<Character> fileIndividual) {
		
		String fileS = "";
		
		//adds the character to the string if it is not ]
		for(Character ch: fileIndividual){
		if(ch != ']'){
				fileS += ch;
			}
		}
		
		//returns the file as a string without ]
		return fileS;
	}
	
}