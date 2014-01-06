import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class Server {
	ServerSocket receiveServerSocket;
	ArrayList<User> users;
	
	public Server(int portNumber) throws IOException, SocketException {
		receiveServerSocket = new ServerSocket(portNumber);
		receiveServerSocket.setSoTimeout(100);
		users = new ArrayList<User>();
	}
	
	public void receiveNewConnection() throws IOException {
		try {
			Socket socket = receiveServerSocket.accept();
			User newUser = new User(socket);
			users.add(newUser);
		} catch (SocketTimeoutException e) {};
	}
	
	public void cycleThroughUsers() throws IndexOutOfBoundsException, IOException {
		for(int i=0;i<users.size();i++) {
			User currentUser = users.get(i);
			String message = currentUser.receiveMessage();
			if(message!=null && !specialMessage(message, currentUser)) {
				String transmission = currentUser.username+": "+message;
				for(User aUser : users)
					aUser.sendMessage(transmission);
			}
		}
	}
	
	public boolean specialMessage(String s, User u) throws IOException {
		if(s.length()>2 && s.substring(0,2).equals("//")) {
			String[] tokens = s.substring(2).split("/");
				if(tokens[0].equals("closing")) {
					if(u.hasUserName()) {
						for(User aUser : users) {
							if(!aUser.equals(u))
								aUser.sendMessage("//removed/"+u.username);
						}
					} else
						users.remove(u);
					u.connected = false;
					u.socket.close();
				} else if(tokens[0].equals("existing")) {
					String user = tokens[1];
					String pass = tokens[2];
					User match = null;
					for(User aUser : users)
						if(aUser.username.equals(user))
							match = aUser;
					if(match!=null && pass.equals(match.password) && !match.connected) {
						users.remove(u);
						match.socket = u.socket;
						match.connected = true;
						match.sendMessage("//success");
						for(User someUser : users)
							someUser.sendMessage("//added/"+match.username);
						String listOfUsers = "";
						for(User aUser : users)
							if(aUser.hasUserName() && aUser.connected && !aUser.equals(match))
								listOfUsers += "/" + aUser.username;
						u.sendMessage("//added"+listOfUsers);
					} else
						u.sendMessage("//failure");
				} else if(tokens[0].equals("new")) {
					String user = tokens[1];
					String pass = tokens[2];
					boolean exists = false;
					for(User aUser : users) 
						if(aUser.username.equals(user)) {
							exists = true;
							break;
						}
					if(!exists) {
						u.sendMessage("//success");
						u.username = user;
						u.password = pass;
						for(User someUser : users)
							if(!someUser.equals(u))
								someUser.sendMessage("//added/"+u.username);
						String listOfUsers = "";
						for(User aUser : users)
							if(aUser.hasUserName() && aUser.connected && !aUser.equals(u))
								listOfUsers += "/" + aUser.username;
						u.sendMessage("//added"+listOfUsers);		
					} else
						u.sendMessage("//failure");
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
