import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class User {
	public String username;
	public String password;
	public Set<User> friends;
	public Socket socket;
	public boolean connected;
	
	public User(Socket sock) {
		socket = sock;
		friends = new HashSet<User>();
		connected = true;
	}
	
	public boolean equals(User otherUser) {
		if(otherUser.signedIn() && signedIn())
			return otherUser.username.equals(username);
		return false;
	}
	
	public boolean signedIn() {
		return (username!=null && password!=null);
	}
	
	public void sendMessage(String message) {
		if(connected) {
			DataOutputStream output;
			try {
				output = new DataOutputStream(socket.getOutputStream());
				output.writeInt(message.getBytes().length);
				output.write(message.getBytes(), 0, message.getBytes().length);
			} catch (IOException e) {}
		}
	}
	
	public String receiveMessage() throws IOException {
		String message = null;
		DataInputStream input = new DataInputStream(socket.getInputStream());
		if(input.available() > 0) {
			byte[] bytes = new byte[input.readInt()];
			input.read(bytes,0,bytes.length);
			message = new String(bytes,0,bytes.length,"UTF-8");
		}
		return message;
	}
}
