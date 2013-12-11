import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class Server {
	public static void main(String[] args) throws IOException, InterruptedException {
		final int portNumber = 12543;
		ServerSocket receiveServerSocket = new ServerSocket(portNumber);
		receiveServerSocket.setSoTimeout(100);
		ArrayList<User> users = new ArrayList<User>();
		while(true) {
			try {
				Socket socket = receiveServerSocket.accept();
				User newUser = new User(socket);
				newUser.sendMessage("Enter username: ");
				users.add(newUser);
			} catch (SocketTimeoutException e) {}
			for(int i=0;i<users.size();i++) {
				User currentUser = users.get(i);
				if(currentUser.hasDisconnected() && currentUser.hasUserName()) {
					String goodbye = currentUser.getuserName()+" has left the group.";
					for(User aUser : users)
						if(aUser.hasUserName())
							aUser.sendMessage(goodbye);
					users.remove(currentUser);
				} else {
					String message = currentUser.receiveMessage();
					if(message!=null) {
						if(!currentUser.hasUserName()) {
							currentUser.setuserName(message);
							String welcome = message + " has joined the group.";
							for(User aUser : users)
								if(aUser.hasUserName())
									aUser.sendMessage(welcome);
						} else {
							for(User aUser : users)
								if(aUser.hasUserName())
									aUser.sendMessageFrom(currentUser, message);
						}
					}
				}
			}
			Thread.sleep(100);
		}
	}
}
