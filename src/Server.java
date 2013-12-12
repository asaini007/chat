import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class Server {
	ServerSocket receiveServerSocket;
	ArrayList<User> users;
	String disconnectedMessage = "//closing";
	
	public Server(int portNumber) {
		try {
			receiveServerSocket = new ServerSocket(portNumber);
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
			performAction(currentUser);
		}
	}
	
	public boolean specialMessage(String s, User u) {
		if(s.equals(disconnectedMessage) && u.hasUserName()) {
			String name = u.getUserName();
			String goodbye = name+" has left the group.";
			for(User aUser : users)
				if(aUser.hasUserName() && aUser.getUserName()!=name)
					aUser.sendMessage(goodbye);
			users.remove(u);
			return true;
		}
		return false;
	}
	
	public void performAction(User u) {
		String message = u.receiveMessage();
		if(message!=null && !specialMessage(message, u)) {
			if(!u.hasUserName()) {
				u.setUserName(message);
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
