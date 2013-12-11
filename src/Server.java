import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class Server {
	ServerSocket receiveServerSocket;
	ArrayList<User> users;
	
	public Server(int number) {
		try {
			receiveServerSocket = new ServerSocket(number);
		} catch (IOException e) {}
		try {
			receiveServerSocket.setSoTimeout(100);
		} catch (SocketException e) {}
		users = new ArrayList<User>();
	}
	
	public void receiveNewConnection() {
		try {
			Socket socket = receiveServerSocket.accept();
			User newUser = new User(socket);
			newUser.sendMessage("Enter username: ");
			users.add(newUser);
		} catch (SocketTimeoutException e) {
		} catch (IOException e) {}
	}
	
	public void cycleThroughUsers() {
		for(int i=0;i<users.size();i++) {
			User currentUser = users.get(i);
			if(!disconnected(currentUser))
				sendMessage(currentUser);
		}
	}
	
	public boolean disconnected(User u) {
		if(u.hasDisconnected() && u.hasUserName()) {
			String goodbye = u.getuserName()+" has left the group.";
			for(User aUser : users)
				if(aUser.hasUserName())
					aUser.sendMessage(goodbye);
			users.remove(u);
			return true;
		}
		return false;
	}
	
	public void sendMessage(User u) {
		String message = u.receiveMessage();
		if(message!=null) {
			if(!u.hasUserName()) {
				u.setuserName(message);
				String welcome = message + " has joined the group.";
				for(User aUser : users)
					if(aUser.hasUserName())
						aUser.sendMessage(welcome);
			} else {
				for(User aUser : users)
					if(aUser.hasUserName())
						aUser.sendMessageFrom(u, message);
			}
		}
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
		Server theServer = new Server(12543);
		while(true) {
			theServer.receiveNewConnection();
			theServer.cycleThroughUsers();
			Thread.sleep(100);
		}
	}
}
