import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class Client {
	Socket socket;
	DataOutputStream output;
	DataInputStream input;
	boolean signedIn;
	int panelwidth = 200;
	
	JFrame loginFrame, listFrame;
	ArrayList<JPanel> chatPanels;
	JLabel loginLabel, usernameLabel, passwordLabel, friendsLabel;
	JTextField nameField;
	JPasswordField passField;
	JButton newUserButton, loginButton;
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
		if(chatPanels.size()==0 && listModel==null) {
	        friendsLabel = new JLabel("Online Users:");
	        friendsLabel.setAlignmentX(JComponent.CENTER_ALIGNMENT);
	        
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
		}
		JPanel mainPane = new JPanel();
		mainPane.setLayout(new GridLayout(1,0));
		JPanel listPanel = new JPanel();
		listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.add(friendsLabel);        
        listPanel.add(listScrollPane);
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
				else if(type.equals("removed"))
					removeUser(messageContents[1]);
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
					chatPanels = new ArrayList<JPanel>();
					loginFrame.dispose();
					updateAndShowListGUI();
				} else if(type.equals("failure"))
					loginLabel.setText("Login failed");
			}
		} catch (IOException e) {}
	}
	
	public void removeUser(String user) {
		listModel.removeElement(user);
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