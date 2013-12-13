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
			if(users.size()>0) {
				String listOfUsers = "";
				for(User u : users)
					if(u.hasUserName())
						listOfUsers += "/" + u.getUserName();
				newUser.sendMessage("//added"+listOfUsers);
			}
			users.add(newUser);
		} catch (SocketTimeoutException e) {
		} catch (IOException e) {}
	}
	
	public void cycleThroughUsers() {
		for(int i=0;i<users.size();i++) {
			User currentUser = users.get(i);
			String message = currentUser.receiveMessage();
			if(message!=null && !specialMessage(message, currentUser)) {
				if(!currentUser.hasUserName()) {
					currentUser.setUserName(message);
					for(User someUser : users)
						if(!someUser.equals(currentUser))
							someUser.sendMessage("//added/"+currentUser.getUserName());
				} else {
					for(User aUser : users)
						if(aUser.hasUserName())
							aUser.sendMessageFrom(currentUser, message);
				}
			}
		}
	}
	
	public boolean specialMessage(String s, User u) {
		if(s.equals(disconnectedMessage)) {
			if(u.hasUserName()) {
				for(User aUser : users) {
					if(!aUser.equals(u))
					aUser.sendMessage("//removed/"+u.getUserName());
				}
			}
			users.remove(u);
			return true;
		}
		return false;
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
