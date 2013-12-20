import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class Server {
	ServerSocket receiveServerSocket;
	ArrayList<User> users;
	
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
			users.add(newUser);
		} catch (SocketTimeoutException e) {
		} catch (IOException e) {}
	}
	
	public void cycleThroughUsers() {
		for(int i=0;i<users.size();i++) {
			User currentUser = users.get(i);
			String message = currentUser.receiveMessage();
			if(message!=null && !specialMessage(message, currentUser)) {
				for(User aUser : users)
					if(aUser.hasUserName())
						aUser.sendMessageFrom(currentUser, message);
			}
		}
	}
	
	public boolean specialMessage(String s, User u) throws IndexOutOfBoundsException {
		if(s.substring(0,2).equals("//")) {
			String special = s.substring(2);
			String[] tokens = special.split("/");
				if(tokens[0].equals("closing")) {
					if(u.hasUserName()) {
						for(User aUser : users) {
							if(!aUser.equals(u) && aUser.hasUserName())
								aUser.sendMessage("//removed/"+u.getUserName());
						}
					} else
						users.remove(u);
					u.connected = false;
					try {
						u.socket.close();
					} catch (IOException e) {}
				} else if(tokens[0].equals("existing")) {
					String user = tokens[1];
					String pass = tokens[2];
					User match = null;
					for(User aUser : users)
						if(aUser.getUserName().equals(user))
							match = aUser;
					if(match!=null && pass.equals(match.getPassword()) && !match.connected) {
						users.remove(u);
						for(User someUser : users)
							someUser.sendMessage("//added/"+match.getUserName());
						match.socket = u.socket;
						match.connected = true;
						match.sendMessage("//success");
					} else {
						u.sendMessage("//failure");
					}
				} else if(tokens[0].equals("new")) {
					String user = tokens[1];
					String pass = tokens[2];
					boolean exists = false;
					for(User aUser : users) 
						if(aUser.getUserName().equals(user)) {
							exists = true;
							break;
						}
					if(!exists) {
						u.sendMessage("//success");
						u.setUserName(user);
						u.setPassword(pass);
						for(User someUser : users)
							if(!someUser.equals(u))
								someUser.sendMessage("//added/"+u.getUserName());
						if(users.size()>0) {
							String listOfUsers = "";
							for(User aUser : users)
								if(aUser.hasUserName() && !aUser.equals(u))
									listOfUsers += "/" + aUser.getUserName();
							u.sendMessage("//added"+listOfUsers);
						}
					} else {
						u.sendMessage("//failure");
					}
				}
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
