Messenger System - Assignment 1
=======
## GitLab Link
https://git.cs.bham.ac.uk/cxf717/SWW-Chat.git

## Solution - Basic

**Structure**
Firstly, I decided I would need a way to login, register, and quit without allowing any other commands until login had happened. To do this I decided to have another infinite while loop once logged in. This meant that, once logged in, the logged in commands could be called but not before. This means that login is now essential to use the system.

The next structural decision was how the messages would now be stored in the server rather than being removed as they were in the original chat system. To do this I decided I would take the create a copy of the client class and edit it to create a message table class. This was similar to the original but had an ArrayList<Message> instead of the BlockingQueue<Message> which the messages are added in to as the new method would keep the messages stored in the server.

At first I attempted to implement all of the commands in the server receiver once the commands were sent to the server. However, I found that this became difficult to talk to the server sender when performing the commands. For instance, different things needed sending to the client based on the commands but the server sender was unaware what the command was as it was not being told by the server receiver. This prompted me to bring back the original clientQueue from the original messaging system and use that as a second connection between the server receiver and server sender (as well as the message table). This was done by using the client table as a way to pass the commands that have been called into the server sender by putting the commands into the queue for the currentUser (this is explained in the next, previous, and delete section); this did require some modifications to the original class, such as changing from Messages in the blocking queue to Strings. Then the commands were implemented in the server sender where it had received all the information and could send back what was needed. This now gave me two links: the message table for storing and reading the messages, and the client table for reading the commands.

Finally, in terms of the messages, I decided to have the most recently received message as the current message with each next message getting older and the final message being the oldest. This is because this is how most messaging systems would show their messages.

**Register**
For registration I started by considering a simple system of reading the register command into the client and through to the server where the command was picked up and the new username "registered". Being "registered" meant the user was added to the client table and the message table thus setting up the connection for when the user logged in. Once registered I decided to tell the client so that the user was aware that the registration happened. 

However, I then had to modify this approach to check the username did not contain anything it shouldn't. These checks were for a username that was blanl, all whitepace, had spaces, or contained "[],:". This is because having spaces in the username or having any of the characters "[], :" would cause issues for the backup system I created to allow the server to be restarted without losing the messages and users.

After this, I then also had to add in some checks to the registration to ensure the username being registered had not already been registered. To do this, when the client passed the register command to the server the server needed to check the message table for that username and then either register the username and tell the client that happened, or not re-register the username and tell the client the name is already registered. To check if the username had already been registered I decided to check if the return for getting the blocking queue and message array list from the client table and message table were not null for that username as this would mean that the username had been added. If this check was not true then the registration code would run, adding the username to the relevant tables etc. However, now there was the issue of telling the client whether or not the registration was successful. To do this I created a class called userExists which would be shared by both the threads in the server so that they can both check if a user had been registered or not. This value would be set to yes or no depending on whether or not the username check was successful (in the server receiver). This then meant in the server sender, the value of the string could be checked and if the user was registered or not could be sent to the client. After this has happened the serverSender then "restarts" the registration check by resetting the variable in order to allow for another registration check if needed.

In terms of the user exists class and the variable registerExists being a string and not a boolean, this was done because otherwise the value would sometimes be null until set to true or false. Unless, it started off as true or false but then the checks would not work as it would tell the client the registration was unsuccessful even when it hadn't happened. Thus, I opted for using yes or no in a string. 

**Login**
For the login function, a check needed to be done to ensure that there was a username registered in order to not get any errors where the server was trying to access messages etc. that did not exist. 

To do this, when the command and the username were sent to the server, the server would check that a username had been registered much the same as it did for checking if the username had already been registered. Then, the userExists class was used again but this time for a userExists variable. This works the same way as the registration checks but were used when logins happen instead of registrations; so it is set to yes if the username exists and no if it does not in the server threads. I did not use the same variable as a registration check could happen before or after a login check due to the backup system. Once the user existing was set to yes or no, the server sender would check this and tell the client if the login exists or not. These are all the server side checks.

On the client side, further checks were then made. These involved the client threads also sharing a version of userExists where the userExists variable was set to yes or no depending on the string sent back from the server after server side checks. This was needed so that the login loop knows whether it should start the while(true) loop that allows logged in commands to happen. Without it the client would not know if login should be allowed. Once the variable was set in the client receiver, the sender checks the variable and if it is yes starts the while loop. 

There were a few issues with this surrounding the client sender thread needing to wait for the checks to happen and receive the result before continuing; thus, I added .sleep() for the thread to ensure it waited for the result. 

Also, the client receiver was printing out the result of the check as it was sent from the server sender as a string. To combat this, I added some conditions so if the string sent back from the server was the result then it was not printed. This would not be an issue as the only time these strings could be received from the server is as the result of the login checks.

Having completed the system, I noticed that I had not dealt with multiple logins of the same username on different clients. I decided that I would modify the system to disallow this in order to prevent issues where messages were only picked up by one client that was logged in as that user and not the other. To do this I decided to add in further checks that would look to see if a user was logged in. For this, I created a new class called Login Check which held a table of the usernames and true if the user was logged in. The checks for this were all server side and if a user was logged in this was told to the client and the userExists was not set to yes thus not allowing a log in to happen client side. In terms of how these checks worked server side: in the receiver, if the username has been registered, the logged in value for the username is checked to see if it is null and if it isn’t then the user has already logged in. If the user had not logged in then the login on the server side continued as before but with the addition of adding them to the loginCheck table. 

**Logout**
At first I attempted logout in the same way that the quit function works. This was by breaking the login while loop and the client while loops. I had the logout command being called whilst logged in only by only reading logout when in the login while loop. However, upon completion of my extra feature, I decided to change the logout function. Now logout works by sending the command to the server where the User class and UserExists class are reset. Within the client, the while loop for the login is broken and the userexists class reset as well as the user being removed from the logged in table via .logout(). Within the server, the logout class is sent through the blocking queue for that user, as for other commands, and then logout successful is sent to the client. Now another user can be registered, log in can happen, or the client can quit.

**Next, Previous, Delete**
For the next, previous, and delete commands methods were added to the messageTable class which dealt with this. This allowed the server sender to just call on these methods and print the returned values straight to the client. For each of these commands a conditional check that there were messages was done, if the messages were empty then a string was returned stating this rather than a message.

For the delete command this was all that was needed and worked fine; however, modifications were needed for the next and previous. For next and previous there was a need to change which message was being called and know which message was currently being looked at. To do this I created a User class which stored an integer that represented the Array List index of the messages. This User class also stored the user that was currently logged in’s username so that the server Sender could access the username (.getUsername()) as well as the receiver – this was also used by in the receiver so that the thread knew who’s blocking queue to put a command into as explained in the structure section of this file. This was used for the login commands such as next and previous. This number was then incremented/decremented for the next and previous and the message at the index returned.

However, I again encountered and issue when the index became out of bounds. To combat this, I added a try catch that looked for the array index being out of bounds and if this happened returned that the final/current message was reached. The array index was then incremented (for previous) and decremented (for next) to ensure that the value of the index stayed constant and was not continuously increased larger than the ArrayList.

## Solution - Extras

**Current Message**
Usage: “current message” is typed on one line and entered. This can only happen once logged in and not when logged out.

This method should return the most recently received message for the logged in user and show it to the user. 

This was added so that there was a quicker way to reach the current message if needed. This method uses the already available getCurrentMessage in the messageTable class. It also sets the currentUser’s message index back to 0 as otherwise next and previous would not start back from the current message.

**Server Backup**
Usage: Nothing is needed for this. All backup and restoration are automatically done.

This feature should store the message table in a file which keeps all of the registered usernames and the messages that they have received. Thus, if the server ends, the message Table can be restored with the messages and the usernames added back to the client table. (This is another reason the logged in checks were done with a separate table as the users would not be logged in when the server restarts). 

To do this server backup, two extra classes were created: MessageBackupWriter and MessageBackupReader which write the file and read the file. Instances of these classes are then created within the system.

The MessageBackupReader is created in the server class where a file check is first performed to ensure that there is a file to read. This is because there may be no file to read if the server has never run before or ended before a file could be created. The reader is created here so that the backup is restored before anything else happens thus the server runs as if it didn’t end (in terms of the client table and message table).

The MessageBackupWriter is created in various places through the ServerReceiver. Firstly, an instance of it is created each time a registration is performed in order to continuously update the file with the new usernames. This is because each time it writes a new file with the current table. Then, an instance is also created each time a message is sent or deleted in order to continuously update the file with the current messages for each username. 

The Message BackupWriter works by having the message table passed into it. When the writer is constructed a try catch tries to write a file and then calls backupMessages(). This is a method within the class to write the message table as a string. This is done by implementing an iterating for loop that creates entry sets and returns a string of each value and key. This is needed as otherwise the returned messagetable would be returned incorrectly for input into a file that could be read later.

The Message BackupReader works by having the message table and client table passed into it in order to restore the tables from the file it reads. When the class is constructed there is a try catch that attempts to read the file by characters into an array list. The restoreBackup(String) method is then called within this which is a method within the class. In the class there is also a fileToString which takes the array the file was read into as characters and turns it into a string but without the “]” so that when this string is passed into restoreBackup it will work. If this didn’t happen the various splits in order to get the required data to restore the tables would not work.

The restoreBackup method is where the file is taken as a string and split back into the username and messages which are then used to restore the client table and message table. Firstly the string is checked to not be empty and if it is (as in a file was created but the server ended before any data was added) then a message that nothing to restore is sent. However, if the string is not empty then it is split by the “\n” which I included in the writing of the file between each set of username and messages. This provides an array with each element being a username and its messages. Then a series of nested loops begins to split all of this data back up. 

So, firstly each element of the array is taken and split into the username and the message (by looking for “[“). This now gives an array of the username (which is always the first index) and the messages thus the username can be added to the client and message tables as the first element is taken and put into a string then added to the tables – this is because the element at 0 is always the username. 

Next another loop begins to split the messages for that username into the user it was from and the text in order to create a Message (object) to add to the Message table. I had some issues with this part as the first element of the array is still the username thus I added a check that the element contains : which only the messages do (due to disallowing : in usernames). This now meant that the messages were split into their parts and a new Message (object0 created and added to the table. It had to be split and added as a Message object as the ArrayList in the message table is ArrayList<Message>. All these nested loops then work through the string of the file to restore the server’s client table and message table. This backupRestore message took a lot of trial and error to find how to split the string in order to get all the needed data and restore it correctly. For instance, I found that I needed to remove the spaces from the username (keyFinal) else when logging in the username was not recognised as it had spaces at the end in the tables. This was also true for the fromUser when creating the Message. 