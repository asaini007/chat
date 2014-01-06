import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ClientWindow {
	String messageToSend, username, password;
	boolean open, existingUser, signedIn;
	
	JFrame frame;
	JLabel loginLabel, usernameLabel, passwordLabel, friendsLabel;
	JTextField nameField, sendField;
	JButton newUserButton, loginButton, nameButton, sendButton, selectButton;
    JList<String> friendsList;
    DefaultListModel<String> listModel;
    JTextArea chatHistory;
    JScrollPane areaScrollPane, listScrollPane;
    JPasswordField passField;
    
    public ClientWindow() {
    	messageToSend = "";
    	username = "";
    	password = "";
    	open = true;
    	signedIn = false;
    	SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	createAndShowLoginGUI();
            }
        });
    }
    
    public void createAndShowLoginGUI() {
    	frame = new JFrame("Sign in");
    	frame.setContentPane(getLoginContentPane());
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e) {
        		open = false;
        	}
        });
        frame.setSize(new Dimension(300,200));
        frame.setVisible(true);
        frame.setResizable(false);
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
	    		if(!name.equals("") && pass!=("")) {
	    			username = name;
	    			password = pass;
	    			existingUser = true;
	    		}
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
	    		if(!name.equals("") && pass!=("")) {
	    			username = name;
	    			password = pass;
	    			existingUser = false;
	    		}
			}
        });
        loginGUI.add(newUserButton, c);
        
        loginGUI.setOpaque(true);
        return loginGUI;
	}

	public void createAndShowGUI() {
		frame.dispose();
        frame = new JFrame(username);
        frame.setContentPane(getContentPane());
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e) {
        		open = false;
        	}
        });
        frame.setSize(new Dimension(300,300));
        frame.setVisible(true);
    }
    
	public JPanel getContentPane (){
        JPanel totalGUI = new JPanel();
        totalGUI.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        chatHistory = new JTextArea("");
        chatHistory.setLineWrap(true);
        chatHistory.setEditable(false);
        areaScrollPane = new JScrollPane(chatHistory);
        areaScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 2;
        c.weightx = 0.9;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        totalGUI.add(areaScrollPane, c);
        
        friendsLabel = new JLabel("Online Users:");
        c.gridx = 1;
        c.gridy = 0;
        c.gridwidth = 2;
        c.gridheight = 1;
        c.weightx = 0.1;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        totalGUI.add(friendsLabel, c);
        
        listModel = new DefaultListModel<String>();
        friendsList = new JList<String>(listModel);
        listScrollPane = new JScrollPane(friendsList);
        listScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 2;
        c.gridheight = 1;
        c.weightx = 0.1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        totalGUI.add(listScrollPane, c);
        
        sendField = new JTextField();
        sendField.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		messageToSend += (messageToSend.equals("")) ? sendField.getText() : "\n"+sendField.getText();
        		sendField.setText("");
        	}
        });
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 0;
        c.fill = GridBagConstraints.HORIZONTAL;
        totalGUI.add(sendField, c);
        
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
        		messageToSend += (messageToSend.equals("")) ? sendField.getText() : "\n"+sendField.getText();
        		sendField.setText("");
        	}
        });
        totalGUI.add(sendButton, c);
        
        selectButton = new JButton("Choose");
        c.gridx = 2;
        c.gridy = 2;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0;
        c.weighty = 0;
        c.fill = GridBagConstraints.NONE;
        sendButton.addActionListener(new ActionListener() {
        	public void actionPerformed(ActionEvent e) {
        		messageToSend += (messageToSend.equals("")) ? sendField.getText() : "\n"+sendField.getText();
        		sendField.setText("");
        	}
        });
        totalGUI.add(selectButton, c);
        
        totalGUI.setOpaque(true);
        return totalGUI;
    }
	
	public void successfulSignIn() {
		signedIn = true;
		createAndShowGUI();
	}
	
	public void failedSignIn() {
		signedIn = false;
		username = "";
		password = "";
		loginLabel.setText("Failed");
	}
	
	public String getMessage() {
		String toReturn = messageToSend;
		messageToSend = "";
		return toReturn;
	}
	
	public void displayMessage(String line) {
		while(chatHistory==null) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {}
		}
		chatHistory.append((chatHistory.getText().equals("")) ? line : "\n"+line);
	}
	
	public void remove(String s) {
		listModel.removeElement(s);
	}
	
	public void add(String s) {
		listModel.addElement(s);
	}
	
	public void close() {
		frame.dispose();
	}
}
