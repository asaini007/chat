import java.io.*;
import java.net.*;

public class Client {
	Socket socket;
	DataOutputStream output;
	DataInputStream input;
	ClientWindow aWindow;
	
	public Client() {
		aWindow  = new ClientWindow();
	}
  
	public void sendMessage() {
		if(aWindow.messageToSend.length()>0) {
			try {
    			String message = aWindow.getMessage();
				output.writeInt(message.getBytes().length);
				output.write(message.getBytes(), 0, message.getBytes().length);
			} catch (IOException e1) {}
		}
	}
	
	public void getMessage() {
		try {
			if(input.available() > 0) {
				byte[] bytes = new byte[input.readInt()];
				input.read(bytes,0,bytes.length);
				String message = new String(bytes,0,bytes.length,"UTF-8");
				if(isSpecialMessage(message)) {
					specialMessage(message);
				} else
					aWindow.displayMessage(message);
			}
		} catch (UnsupportedEncodingException e) {
		} catch (IOException e) {}
	}
	
	public void specialMessage(String s) {
		String[] messageContents = s.substring(2).split("/");
		if(messageContents[0].equals("removed")) {
			for(int i=1;i<messageContents.length;i++)
				aWindow.remove(messageContents[i]);
		}
		if(messageContents[0].equals("added")) {
			for(int i=1;i<messageContents.length;i++)
				aWindow.add(messageContents[i]);
		}
	}
	
	public boolean isSpecialMessage(String s) {
		try{
			if(s.substring(0, 2).equals("//"))
				return true;
		} catch (NullPointerException e1) {
		} catch (IndexOutOfBoundsException e2) {}
		return false;
	}

	public void connectToServer() {
		while(true) {
			try {
				socket = new Socket(InetAddress.getLocalHost(), 12543);
				output = new DataOutputStream(socket.getOutputStream());
				input = new DataInputStream(socket.getInputStream());
				String message = aWindow.userName;
				output.writeInt(message.getBytes().length);
				output.write(message.getBytes(), 0, message.getBytes().length);
				break;
			} catch (ConnectException e) {
			} catch (IOException e) {}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
	}
	
	public boolean hasSignedIn() {
		return aWindow.userName.length()>0;
	}
	
	public void disconnectFromServer() {
		if(hasSignedIn()) {
			try {
				String message = "//closing";
				output.writeInt(message.getBytes().length);
				output.write(message.getBytes(), 0, message.getBytes().length);
				output.close();
				input.close();
				socket.close();
			} catch (IOException e) {}
		}
	}
		
	public static void main(String[] args) {
        Client aClient = new Client();
        while(!aClient.hasSignedIn() && aClient.aWindow.open) {
        	try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
        }
        aClient.connectToServer();
    	while(aClient.aWindow.open) {
			aClient.sendMessage();
			aClient.getMessage();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
		aClient.disconnectFromServer();
		aClient.aWindow.close();
	}
}