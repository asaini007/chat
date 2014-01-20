import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.Iterator;

public class Server {
	ServerSocket receiveServerSocket;
	HashSet<User> users;
	
	public Server(int portNumber) throws IOException, SocketException {
		receiveServerSocket = new ServerSocket(portNumber);
		receiveServerSocket.setSoTimeout(100);
		users = new HashSet<User>();
	}
	
	public void receiveNewConnection() throws IOException {
		try {
			Socket socket = receiveServerSocket.accept();
			User newUser = new User(socket);
			users.add(newUser);
		} catch (SocketTimeoutException e) {};
	}
	
	public void cycleThroughUsers() throws IndexOutOfBoundsException, IOException {
		Iterator<User> it = users.iterator();
		while(it.hasNext()) {
			User currentUser = it.next();
			if(currentUser.connected) {
				String message = currentUser.receiveMessage();
				if(message!=null) {
					String type = message.split("/")[0];
					if(type.equals("message"))
						sendMessage(message, currentUser);
					else if(type.equals("closing"))
						exitAction(message, currentUser);
					else if(type.equals("existing"))
						addExisting(message, currentUser);
					else if(type.equals("new"))
						addNew(message, currentUser);
				}
			}
		}
	}

	public void sendMessage(String message, User fromUser) {
		String[] tokens = message.split("/");
		String toUserName = tokens[1];
		String toSend = tokens[2];
		User match = null;
		for(User aUser : users)
			if(aUser.username.equals(toUserName))
				match = aUser;
		if(match!=null && match.connected) {
			match.sendMessage("message/"+fromUser.username+"/"+toSend);
			fromUser.sendMessage("ownmessage/"+match.username+"/"+toSend);
		}
	}
	
	public void exitAction(String s, User u) throws IOException {
		if(u.username!=null) {
			for(User aUser : users) {
				if(!aUser.equals(u))
					aUser.sendMessage("removed/"+u.username);
			}
		} else
			users.remove(u);
		u.connected = false;
		u.socket.close();
	}
	
	public void addExisting(String s, User u) throws IOException {
		String[] tokens = s.split("/");
		String user = tokens[1];
		String pass = tokens[2];
		User match = null;
		Iterator<User> tempIt = users.iterator();
		while(tempIt.hasNext()) {
			User tempUser = tempIt.next();
			if(tempUser.hasUserInfo() && user.equals(tempUser.username)) {
				match = tempUser;
				break;
			}
		}
		if(match!=null && pass.equals(match.password) && !match.connected) {
			users.remove(u);
			u.connected = false;
			match.socket = u.socket;
			match.connected = true;
			addAction(match);
		} else
			u.sendMessage("failure");
	}
	
	public void addNew(String s, User u) throws IOException {
		String[] tokens = s.split("/");
		String userName = tokens[1];
		String passWord = tokens[2];
		boolean exists = false;
		for(User aUser : users) 
			if(aUser.hasUserInfo() && aUser.username.equals(userName)) {
				exists = true;
				break;
			}
		if(!exists) {
			u.username = userName;
			u.password = passWord;
			addAction(u);
		} else
			u.sendMessage("failure");
	}
	
	public void addAction(User u) {
		u.sendMessage("success");
		for(User aUser : users) {
			if(!aUser.equals(u)) {
				aUser.sendMessage("added/"+u.username);
			}
		}
		String listOfUsers = "";
		for(User aUser : users)
			if(aUser.hasUserInfo() && aUser.connected && !aUser.equals(u))
				listOfUsers += "/" + aUser.username;
		u.sendMessage("added"+listOfUsers);
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
