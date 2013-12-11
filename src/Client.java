import java.io.*;
import java.net.*;

public class Client {
	Socket socket;
	DataOutputStream output;
	DataInputStream input;
  
	public void sendMessage(ClientWindow aWindow) {
		if(aWindow.hasMessageToSend()) {
			try {
    			String message = aWindow.getMessage();
				output.writeInt(message.getBytes().length);
				output.write(message.getBytes(), 0, message.getBytes().length);
				aWindow.clearSendField();
			} catch (IOException e1) {}
		}
	}
	
	public String getMessage() {
		try {
			if(input.available() > 0) {
				byte[] bytes = new byte[input.readInt()];
				input.read(bytes,0,bytes.length);
				String message = new String(bytes,0,bytes.length,"UTF-8");
				return message;
			}
		} catch (UnsupportedEncodingException e) {
		} catch (IOException e) {}
		return null;
	}

	public void connectToServer() {
		while(true) {
			try {
				socket = new Socket(InetAddress.getLocalHost(), 12543);
				output = new DataOutputStream(socket.getOutputStream());
				input = new DataInputStream(socket.getInputStream());
				break;
			} catch (ConnectException e) {
			} catch (IOException e) {}
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
	}
		
	public static void main(String[] args) {
        final Client aClient = new Client();
        final ClientWindow aWindow  = new ClientWindow(aClient);
		aClient.connectToServer();
		while(true) {
			aClient.sendMessage(aWindow);
			aWindow.displayMessage(aClient.getMessage());
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
	}
}