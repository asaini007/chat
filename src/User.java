import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class User {
	public String username;
	public String password;
	public Socket socket;
	public boolean connected;
	
	public User(Socket sock) {
		socket = sock;
		username = "";
		connected = true;
	}
	
	public boolean hasUserName() {
		return !(username==null);
	}
	
	public boolean equals(User otherUser) {
		return (otherUser.username.equals(username)) ? true : false;
	}
	
	public void sendMessage(String message) throws IOException {
		if(hasUserName() && connected) {
			DataOutputStream output;
			output = new DataOutputStream(socket.getOutputStream());
			output.writeInt(message.getBytes().length);
			output.write(message.getBytes(), 0, message.getBytes().length);
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
