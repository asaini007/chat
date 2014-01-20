import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.util.HashSet;
import javax.swing.*;

public class Client {
	Socket socket;
	DataOutputStream output;
	DataInputStream input;
	boolean signedIn;
	
	JFrame loginFrame, listFrame;
	HashSet<JPanel> chatPanels;
	JLabel loginLabel, usernameLabel, passwordLabel, friendsLabel;
	JTextField nameField, sendField;
	JPasswordField passField;
	JButton newUserButton, loginButton, nameButton, sendButton, selectButton;
    JList<String> friendsList;
    DefaultListModel<String> listModel;
    JScrollPane areaScrollPane, listScrollPane;
	
	public Client() {
		try {
			socket = new Socket(InetAddress.getLocalHost(), 12543);
			output = new DataOutputStream(socket.getOutputStream());
			input = new DataInputStream(socket.getInputStream());
		} catch (IOException e) {}
		signedIn = false;
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	createAndShowLoginGUI();
            }
        });
	}
  
	public void createAndShowLoginGUI() {
    	loginFrame = new JFrame("Sign in");
    	loginFrame.setContentPane(getLoginContentPane());
    	loginFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
    	loginFrame.addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e) {
        		disconnectFromServer();
        		loginClose();
        	}
        });
    	loginFrame.setSize(new Dimension(350,150));
    	loginFrame.setVisible(true);
    	loginFrame.setResizable(false);
    }
    
	public JPanel getLoginContentPane() {
		JPanel loginGUI = new JPanel();
		loginGUI.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        loginLabel = new JLabel("Sign in");
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 0.25;
        c.fill = GridBagConstraints.NONE;
        loginGUI.add(loginLabel, c);
        
        usernameLabel = new JLabel("Username");
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0;
        c.weighty = 0.25;
        c.fill = GridBagConstraints.NONE;
        loginGUI.add(usernameLabel, c);
        
        nameField = new JTextField();
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 0.25;
        c.fill = GridBagConstraints.HORIZONTAL;
        loginGUI.add(nameField, c);
        
        passwordLabel = new JLabel("Password");
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0;
        c.weighty = 0.25;
        c.fill = GridBagConstraints.NONE;
        loginGUI.add(passwordLabel, c);
        
        passField = new JPasswordField();
        c.gridx = 1;
        c.gridy = 2;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 0.25;
        c.fill = GridBagConstraints.HORIZONTAL;
        loginGUI.add(passField, c);
        
        loginButton = new JButton("Sign in");
        c.gridx = 1;
        c.gridy = 3;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 0.25;
        c.fill = GridBagConstraints.NONE;
        loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = nameField.getText();
				String pass = new String(passField.getPassword());
				passField.setText("");
	    		if(!name.equals("") && !pass.equals(""))
	    			connectToServer(name, pass, true);
			}
        });
        loginGUI.add(loginButton, c);
        
        newUserButton = new JButton("New User");
        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0;
        c.weighty = 0.25;
        c.fill = GridBagConstraints.NONE;
        newUserButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = nameField.getText();
				String pass = new String(passField.getPassword());
				passField.setText("");
				if(!name.equals("") && !pass.equals(""))
	    			connectToServer(name, pass, false);
			}
        });
        loginGUI.add(newUserButton, c);
        
        loginGUI.setOpaque(true);
        return loginGUI;
	}

	public void createAndShowListGUI() {
		chatPanels = new HashSet<JPanel>();
        listFrame = new JFrame("Online Users");
        listFrame.setContentPane(getListContentPane());
        listFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        listFrame.addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e) {
        		disconnectFromServer();
        		listClose();
        	}
        });
        listFrame.setSize(new Dimension(200,300));
        listFrame.setResizable(false);
        listFrame.setVisible(true);
    }
	
	public JPanel getListContentPane (){
        JPanel listGUI = new JPanel();
        listGUI.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        friendsLabel = new JLabel("Online Users:");
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        listGUI.add(friendsLabel, c);
        
        listModel = new DefaultListModel<String>();
        friendsList = new JList<String>(listModel);
        friendsList.addMouseListener(new MouseAdapter() {
        	public void mouseClicked(MouseEvent event) {
                if(event.getClickCount() == 2) {
                	Rectangle r = friendsList.getCellBounds(0, friendsList.getLastVisibleIndex());
					if(r != null && r.contains(event.getPoint())) {
                		int index = friendsList.locationToIndex(event.getPoint());
                		createNewWindow((String)listModel.get(index));
					}
                }
            }
        });
        listScrollPane = new JScrollPane(friendsList);
        listScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        listGUI.add(listScrollPane, c);
        
        listGUI.setOpaque(true);
        return listGUI;
    }
	
	public void updateAndShowListGUI() {
        listFrame.setSize(new Dimension(200*(1+chatPanels.size()),300));
		listFrame.setContentPane(updateListFrameContentPane());
	}
	
	public JPanel updateListFrameContentPane() {
		JPanel listGUI = new JPanel();
        listGUI.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1.0/(1.0/(double)chatPanels.size());
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        listGUI.add(friendsLabel, c);
        
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1.0/(1.0/(double)chatPanels.size());
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        listGUI.add(listScrollPane, c);
        
        for(JPanel chatPanel : chatPanels) {
        	c.gridx++;
        	c.gridy = 0;
        	c.gridwidth = 1;
            c.gridheight = 2;
            c.weightx = 1.0/(1.0/(double)chatPanels.size());
            c.weighty = 1;
            c.fill = GridBagConstraints.BOTH;
            listGUI.add(chatPanel, c);
        }
        
        listGUI.setOpaque(true);
        return listGUI;
	}
	
	public void createNewWindow(String userName) {
		boolean exists = false;
		for(JPanel chatBox : chatPanels)
			if(((JLabel) chatBox.getComponent(0)).getText().equals(userName)) {
				exists = true;
				break;
			}
		if(!exists) {
			JPanel chatPanel = new JPanel();
			chatPanel.setLayout(new GridBagLayout());
	        GridBagConstraints c = new GridBagConstraints();
	        
	        JLabel name = new JLabel(userName);
	        c.gridx = 0;
	        c.gridy = 0;
	        c.gridwidth = 2;
	        c.gridheight = 1;
	        c.weightx = 1;
	        c.weighty = 0;
	        c.fill = GridBagConstraints.NONE;
	        chatPanel.add(name, c);
	        
	        
	        JTextArea chatHistory = new JTextArea();
	        chatHistory.setLineWrap(true);
	        chatHistory.setEditable(false);
	        areaScrollPane = new JScrollPane(chatHistory);
	        areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	        c.gridx = 0;
	        c.gridy = 1;
	        c.gridwidth = 2;
	        c.gridheight = 1;
	        c.weightx = 1;
	        c.weighty = 1;
	        c.fill = GridBagConstraints.BOTH;
	        chatPanel.add(areaScrollPane, c);
	        
	        sendField = new JTextField();
	        sendField.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// sendMessage
				}
	        });
	        c.gridx = 0;
	        c.gridy = 2;
	        c.gridwidth = 1;
	        c.gridheight = 1;
	        c.weightx = 1;
	        c.weighty = 0;
	        c.fill = GridBagConstraints.HORIZONTAL;
	        chatPanel.add(sendField, c);
	        
	        sendButton = new JButton("Send");
	        c.gridx = 1;
	        c.gridy = 2;
	        c.gridwidth = 1;
	        c.gridheight = 1;
	        c.weightx = 0;
	        c.weighty = 0;
	        c.fill = GridBagConstraints.NONE;
	        sendButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					// sendMessage
				}
	        });
	        chatPanel.add(sendButton, c);
	        
	        chatPanel.setOpaque(true);
	        chatPanels.add(chatPanel);
	        updateAndShowListGUI();
		}
	}
	
/*	public void sendMessage() throws IOException {
		if(aWindow.messageToSend.length()>0) {
			String message = aWindow.getMessage();
			output.writeInt(message.getBytes().length);
			output.write(message.getBytes(), 0, message.getBytes().length);
		}
	} */
	
	public void getMessage() throws UnsupportedEncodingException, IOException {
		if(input.available() > 0) {
			byte[] bytes = new byte[input.readInt()];
			input.read(bytes,0,bytes.length);
			String message = new String(bytes,0,bytes.length,"UTF-8");
			String[] messageContents = message.split("/");
			String type = messageContents[0];
			if(type.equals("message"))
				;
			else if(type.equals("ownmessage"))
				;
			else if(type.equals("removed")) {
				listModel.removeElement(messageContents[1]);
				// if chatting with user, display "[user] has disconnected"
			}
			else if(type.equals("added"))
				for(int i=1;i<messageContents.length;i++) {
					while(listModel == null) {
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {}
					}
					listModel.addElement(messageContents[i]);
				}
			else if(type.equals("success")) {
				signedIn = true;
				loginFrame.dispose();
				createAndShowListGUI();
			} else if(type.equals("failure"))
				loginLabel.setText("The username or password you enterred is incorrect");
		}
	}
	
	public void connectToServer(String username, String password, boolean existing) {
		try {
			String message = existing ? "existing/"+username+"/"+password : "new/"+username+"/"+password;
			output.writeInt(message.getBytes().length);
			output.write(message.getBytes(), 0, message.getBytes().length);
			while(input.available() == 0)
				Thread.sleep(100);
			getMessage();
		} catch (IOException | InterruptedException e) {}
	}
	
	public void disconnectFromServer() {
		try {
			String message = "closing";
			output.writeInt(message.getBytes().length);
			output.write(message.getBytes(), 0, message.getBytes().length);
			try {
				output.close();
				input.close();
				socket.close();
			} catch (NullPointerException e) {}
		} catch (IOException e) {}
	}
	
	public void loginClose() {
		try {
			loginFrame.dispose();
			// other miscellaneous closing actions
			System.exit(0);
		} catch (NullPointerException e) {};
	}
	
	public void listClose() {
		try {
			listFrame.dispose();
			// dispose all other frames
			// other miscellaneous closing actions
			System.exit(0);
		} catch (NullPointerException e) {};
	}
	
	public static void main(String[] args) throws IOException, InterruptedException {
        Client aClient = new Client();
    	while(!aClient.signedIn) {
    		try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
    	}
    	while(true) {
//			aClient.sendMessage();
			aClient.getMessage();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
	}
}