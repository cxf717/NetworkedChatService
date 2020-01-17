

public class Message {

  private final String sender;
  private final String text;

  //constructor for the class
  Message(String sender, String text) {
    this.sender = sender;
    this.text = text;
  }
  
  public String getSender() {
    return sender;
  }

  public String getText() {
    return text;
  }
	
  //creates the message to be sent
  public String toString() {
    return "From " + sender + " : " + text;
  }
}
