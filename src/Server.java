import java.io.IOException;
import java.net.ServerSocket;
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
			users.add(new User(receiveServerSocket.accept()));
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
					else if(type.equals("add"))
						addUser(message, currentUser);
					else if(type.equals("closing")) {
						if(exitAction(message, currentUser))
							it.remove();
					}
					else if(type.equals("existing")) {
						if(addExisting(message, currentUser))
							it.remove();
					}
					else if(type.equals("new"))
						addNew(message, currentUser);
				}
			}
		}
	}

	public void addUser(String message, User currentUser) {
		String[] tokens = message.split("/");
		String name = tokens[1];
		for(User u : users) {
			if(u.signedIn()) {
				if(u.username.equals(name) && !u.equals(currentUser)) {
					if(currentUser.friends.add(u))
						currentUser.sendMessage("added/" + u.username);
					if(u.friends.add(currentUser))
						u.sendMessage("added/" + currentUser.username);
					return;
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
			if(aUser.signedIn() && aUser.username.equals(toUserName))
				match = aUser;
		if(match!=null && match.connected) {
			match.sendMessage("message/"+fromUser.username+"/"+toSend);
			fromUser.sendMessage("ownmessage/"+match.username+"/"+toSend);
		}
	}
	
	public boolean exitAction(String s, User u) throws IOException {
		boolean remove = false;
		if(u.signedIn()) {
			for(User aUser : users) {
				for(User aUsersFriend : aUser.friends)
					if(aUsersFriend.equals(u))
						aUser.sendMessage("offline/"+u.username);
			}
		} else
			remove =true;
		u.connected = false;
		u.socket.close();
		return remove;
	}
	
	public boolean addExisting(String s, User u) throws IOException {
		boolean remove = false;
		String[] tokens = s.split("/");
		String user = tokens[1];
		String pass = tokens[2];
		User match = null;
		Iterator<User> tempIt = users.iterator();
		while(tempIt.hasNext()) {
			User tempUser = tempIt.next();
			if(tempUser.signedIn() && user.equals(tempUser.username)) {
				match = tempUser;
				break;
			}
		}
		if(match!=null && pass.equals(match.password) && !match.connected) {
			u.sendMessage("success");
			remove = true;
			u.connected = false;
			match.socket = u.socket;
			match.connected = true;
			for(User aFriend : match.friends) {
				if(aFriend.connected) {
					aFriend.sendMessage("added/"+match.username);
					match.sendMessage("added/"+aFriend.username);
				} else
					match.sendMessage("offline/"+aFriend.username);
			}
		} else
			u.sendMessage("failure");
		return remove;
	}
	
	public void addNew(String s, User u) throws IOException {
		String[] tokens = s.split("/");
		String userName = tokens[1];
		String passWord = tokens[2];
		boolean exists = false;
		for(User aUser : users) {
			if(aUser.signedIn() && aUser.username.equals(userName)) {
				exists = true;
				break;
			}
		}
		if(!exists) {
			u.username = userName;
			u.password = passWord;
			u.sendMessage("success");
		} else
			u.sendMessage("failure");
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
