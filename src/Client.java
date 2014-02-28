import java.awt.*;
import java.awt.event.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.*;

public class Client {
	Socket socket;
	DataOutputStream output;
	DataInputStream input;
	boolean signedIn;
	int panelwidth = 200;
	
	JFrame loginFrame, listFrame;
	ArrayList<JPanel> chatPanels;
	JLabel loginLabel, usernameLabel, passwordLabel, onlinefriendsLabel, offlinefriendsLabel;
	JTextField nameField, search;
	JPasswordField passField;
	JButton newUserButton, loginButton, add;
    JList<String> onlineFriendsList;
    JList<String> offlineFriendsList;
    DefaultListModel<String> onlineListModel;
    DefaultListModel<String> offlineListModel;
    JScrollPane areaScrollPane, onlineListScrollPane, offlineListScrollPane;
	
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
        		loginClose();
        	}
        });
    	loginFrame.setSize(new Dimension(300,150));
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

	public void updateAndShowListGUI() {
        if(listFrame == null) {
        	listFrame = new JFrame(nameField.getText());
            listFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
            listFrame.addWindowListener(new WindowAdapter() {
            	public void windowClosing(WindowEvent e) {
            		listClose();
            	}
            });
            listFrame.setResizable(true);
            listFrame.setSize(new Dimension(200,200));
        } else {
        	int numPanels = 0;
        	for(JPanel chatPanel : chatPanels)
        		if(chatPanel.isOpaque())
        			numPanels++;
        	listFrame.setSize(new Dimension(panelwidth*(numPanels+1),listFrame.getHeight()));
        }
    	listFrame.setContentPane(getListContentPane());
        listFrame.setVisible(true);
    }
	
	public JPanel getListContentPane() {
		if(chatPanels.size()==0 && onlineListModel==null) { 
	        onlineListModel = new DefaultListModel<String>();
	        onlineFriendsList = new JList<String>(onlineListModel);
	        onlineFriendsList.addMouseListener(new MouseAdapter() {
	        	public void mouseClicked(MouseEvent event) {
	                if(event.getClickCount() == 2) {
	                	Rectangle r = onlineFriendsList.getCellBounds(0, onlineFriendsList.getLastVisibleIndex());
						if(r != null && r.contains(event.getPoint())) {
	                		int index = onlineFriendsList.locationToIndex(event.getPoint());
	                		createNewWindow((String)onlineListModel.get(index));
						}
	                }
	            }
	        });
	        onlineListScrollPane = new JScrollPane(onlineFriendsList);
	        onlineListScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	        
	        offlineListModel = new DefaultListModel<String>();
	        offlineFriendsList = new JList<String>(offlineListModel);
	        offlineFriendsList.addMouseListener(new MouseAdapter() {
	        	public void mouseClicked(MouseEvent event) {
	                if(event.getClickCount() == 2) {
	                	Rectangle r = offlineFriendsList.getCellBounds(0, offlineFriendsList.getLastVisibleIndex());
						if(r != null && r.contains(event.getPoint())) {
	                		// int index = offlineFriendsList.locationToIndex(event.getPoint());
	                		// createNewWindow((String)offlineListModel.get(index));
						}
	                }
	            }
	        });
	        offlineListScrollPane = new JScrollPane(offlineFriendsList);
	        offlineListScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
	        
	        onlinefriendsLabel = new JLabel("Online Friends:", JLabel.HORIZONTAL);
	        offlinefriendsLabel = new JLabel("Offline Friends:", JLabel.HORIZONTAL);
	        
	        search = new JTextField("Add Friend...");
	        search.addFocusListener(new FocusListener() {
				public void focusGained(FocusEvent e) {
					search.setText("");
				}
				public void focusLost(FocusEvent e) {
					if(search.getText().equals(""))
						search.setText("Add Friend...");
				}
	        });
	        search.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String name = search.getText();
					search.setText("");
					if(!name.equals("") && !name.equals("Add Friend...") && name != null)
						addFriend(name);
				}
	        });
	        
	        add = new JButton("Add");
	        add.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String name = search.getText();
					search.setText("Add Friend...");
					if(!name.equals("") && !name.equals("Add Friend...") && name != null)
						addFriend(name);
				}
	        });
		}
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new GridLayout(1,0));
		
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 2;
		c.gridheight = 1;
		c.weightx = 1;
		c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
        listPanel.add(onlinefriendsLabel, c);
        
        c.gridy = 1;
        c.weighty = 1;
        listPanel.add(onlineListScrollPane,c);
        
		c.gridy = 2;
        c.weighty = 0;
        listPanel.add(offlinefriendsLabel,c);       
        
        c.gridy = 3;
        c.weighty = 1;
        listPanel.add(offlineListScrollPane,c);
        
        c.gridy = 4;
        c.gridwidth = 1;
        c.weighty = 0;
        listPanel.add(search,c);
        
        c.gridx = 1;
        c.weightx = 0;
        listPanel.add(add,c);
        
        mainPane.add(listPanel);
        for(JPanel chatPanel : chatPanels)
        	if(chatPanel.isOpaque())
        		mainPane.add(chatPanel);
        return mainPane;
	}
	
	public void createNewWindow(String userName) {
		JPanel cPanel = null;
		for(JPanel chatBox : chatPanels)
			if(((JLabel) chatBox.getComponent(0)).getText().equals(userName)) {
				cPanel = chatBox;
				break;
			}
		int numPanels = 0;
    	for(JPanel panel : chatPanels)
    		if(panel.isOpaque())
    			numPanels++;
        panelwidth = listFrame.getWidth()/(1+numPanels);
		if(cPanel==null) {
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
	        chatHistory.setWrapStyleWord(true);
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
	        
	        final JTextField sendField = new JTextField();
	        sendField.setName(userName);
	        sendField.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(sendField.getText() != null && !sendField.getText().equals(""))
						sendMessage(sendField);
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
	        
	        final JButton sendButton = new JButton("Send");
	        c.gridx = 1;
	        c.gridy = 2;
	        c.gridwidth = 1;
	        c.gridheight = 1;
	        c.weightx = 0;
	        c.weighty = 0;
	        c.fill = GridBagConstraints.NONE;
	        sendButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(sendField.getText() != null && !sendField.getText().equals(""))
						sendMessage(sendField);
				}
	        });
	        chatPanel.add(sendButton, c);
	        
	        chatPanel.setOpaque(true);
	        chatPanels.add(chatPanel);
		} else
			cPanel.setOpaque(!cPanel.isOpaque());
		updateAndShowListGUI();
	}
	
	public void sendMessage(JTextField field) {
		String message = "message/"+field.getName()+"/"+field.getText();
		field.setText("");
		try {
			output.writeInt(message.getBytes().length);
			output.write(message.getBytes(), 0, message.getBytes().length);
		} catch (IOException e) {}
	}
	
	public void addFriend(String name) {
		String message = "add/"+name;
		try {
			output.writeInt(message.getBytes().length);
			output.write(message.getBytes(), 0, message.getBytes().length);
		} catch (IOException e) {}
	}
	
	public void getMessage() {
		try {
			if(input.available() > 0) {
				byte[] bytes = new byte[input.readInt()];
				input.read(bytes,0,bytes.length);
				String message = new String(bytes,0,bytes.length,"UTF-8");
				String[] messageContents = message.split("/");
				String type = messageContents[0];
				if(type.equals("message"))
					newMessage(messageContents);
				else if(type.equals("ownmessage"))
					ownMessage(messageContents);
				else if(type.equals("offline"))
					removeUser(messageContents[1]);
				else if(type.equals("added"))
					addUser(messageContents);
				else if(type.equals("success")) {
					signedIn = true;
					chatPanels = new ArrayList<JPanel>();
					loginFrame.dispose();
					updateAndShowListGUI();
				} else if(type.equals("failure"))
					loginLabel.setText("Login failed");
			}
		} catch (IOException e) {}
	}
	
	public void addUser(String[] messageContents) {
		for(int i=1;i<messageContents.length;i++) {
			String friend = messageContents[i];
			while(onlineListModel == null || offlineListModel == null) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {}
			}
			onlineListModel.addElement(messageContents[i]);
			for(int j = 0; j < offlineListModel.size(); j++){
				if(friend.equals(offlineListModel.get(j)))
					offlineListModel.remove(j);
			}
			for(final JPanel chatPanel : chatPanels)
				if(((JLabel) chatPanel.getComponent(0)).getText().equals(friend)) {
					JTextArea textArea = (JTextArea) ((JScrollPane) chatPanel.getComponent(1)).getViewport().getView();
					textArea.append((textArea.getText() == null ||  textArea.getText().equals("")) ? friend+" has reconnected" : "\n"+friend+" has reconnected");
					textArea.setCaretPosition(textArea.getDocument().getLength());
					final JTextField text = ((JTextField) chatPanel.getComponent(2));
					text.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if(text.getText() != null && !text.getText().equals(""))
								sendMessage(text);
						}
			        });
					text.setEditable(true);
					text.setText("");
					JButton button = ((JButton) chatPanel.getComponent(3));
					button.setText("Send");
					button.removeActionListener(button.getActionListeners()[0]);
					button.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							if(text.getText() != null && !text.getText().equals(""))
								sendMessage(text);
						}
			        });
				}
		}
	}

	public void removeUser(String user) {
		onlineListModel.removeElement(user);
		offlineListModel.addElement(user);
		for(final JPanel chatPanel : chatPanels)
			if(((JLabel) chatPanel.getComponent(0)).getText().equals(user)) {
				JTextArea textArea = (JTextArea) ((JScrollPane) chatPanel.getComponent(1)).getViewport().getView();
				textArea.append((textArea.getText() == null ||  textArea.getText().equals("")) ? user+" has disconnected" : "\n"+user+" has disconnected");
				textArea.setCaretPosition(textArea.getDocument().getLength());
				JTextField text = ((JTextField) chatPanel.getComponent(2));
				text.removeActionListener(text.getActionListeners()[0]);
				text.setEditable(false);
				text.setText("");
				JButton button = ((JButton) chatPanel.getComponent(3));
				button.setText("Close");
				button.removeActionListener(button.getActionListeners()[0]);
				button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						int numPanels = 0;
						for(JPanel panel : chatPanels)
				    		if(panel.isOpaque())
				    			numPanels++;
				        panelwidth = listFrame.getWidth()/(1+numPanels);
				        chatPanels.remove(chatPanel);
				        updateAndShowListGUI();
					}
				});
			}
	}
	
	public void newMessage(String[] messageContents) {
		String fromUser = messageContents[1];
		String message = messageContents[2];
		boolean exists = false;
		for(JPanel chatPanel : chatPanels)
			if(((JLabel) chatPanel.getComponent(0)).getText().equals(fromUser)) {
				chatPanel.setOpaque(true);
				updateAndShowListGUI();
				JTextArea textArea = (JTextArea) ((JScrollPane) chatPanel.getComponent(1)).getViewport().getView();
				textArea.append((textArea.getText() == null ||  textArea.getText().equals("")) ? fromUser+": "+message : "\n"+fromUser+": "+message);
				textArea.setCaretPosition(textArea.getDocument().getLength());
				exists = true;
				break;
			}
		if(!exists) {
			createNewWindow(fromUser);
			for(JPanel chatPanel : chatPanels)
				if(((JLabel) chatPanel.getComponent(0)).getText().equals(fromUser)) {
					JTextArea textArea = (JTextArea) ((JScrollPane) chatPanel.getComponent(1)).getViewport().getView();
					textArea.append((textArea.getText() == null ||  textArea.getText().equals("")) ? fromUser+": "+message : "\n"+fromUser+": "+message);
					textArea.setCaretPosition(textArea.getDocument().getLength());
				}
		}
	}
	
	public void ownMessage(String[] messageContents) {
		String user = messageContents[1];
		String message = messageContents[2];
		for(JPanel chatPanel : chatPanels)
			if(((JLabel) chatPanel.getComponent(0)).getText().equals(user)) {
				JTextArea textArea = (JTextArea) ((JScrollPane) chatPanel.getComponent(1)).getViewport().getView();
				textArea.append((textArea.getText() == null ||  textArea.getText().equals("")) ? "me: "+message : "\nme: "+message);
				textArea.setCaretPosition(textArea.getDocument().getLength());
				break;
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
		disconnectFromServer();
		try {
			loginFrame.dispose();
			System.exit(0);
		} catch (NullPointerException e) {};
	}
	
	public void listClose() {
		disconnectFromServer();
		try {
			listFrame.dispose();
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
			aClient.getMessage();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {}
		}
	}
}