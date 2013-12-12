import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class User {
	private String name;
	private Socket socket;
	
	public User(Socket sock) {
		socket = sock;
		name = null;
	}
	
	public String getUserName() {
		return name;
	}
	
	public void setUserName(String userName) {
		name = userName;
	}
	
	public boolean hasUserName() {
		return !(name==null);
	}
	
	public boolean equals(User otherUser) {
		return (otherUser.getUserName()  == getUserName()) ? true : false;
	}
	
	public void sendMessage(String message) {
		DataOutputStream output;
		try {
			output = new DataOutputStream(socket.getOutputStream());
			output.writeInt(message.getBytes().length);
			output.write(message.getBytes(), 0, message.getBytes().length);
		} catch (IOException e) {}
	}
	
	public void sendMessageFrom(User otherUser, String message) {
		DataOutputStream output;
		try {
			output = new DataOutputStream(socket.getOutputStream());
			message=otherUser.getUserName()+": "+message;
			output.writeInt(message.getBytes().length);
			output.write(message.getBytes(), 0, message.getBytes().length);
		} catch (IOException e) {}
	}
	
	public String receiveMessage() {
		String message = null;
		try {
			DataInputStream input = new DataInputStream(socket.getInputStream());
			if(input.available() > 0) {
				byte[] bytes = new byte[input.readInt()];
				input.read(bytes,0,bytes.length);
				message = new String(bytes,0,bytes.length,"UTF-8");
			}
		} catch (IOException e) {}
		return message;
	}
}
