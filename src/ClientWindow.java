import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ClientWindow {
	String messageToSend, userName;
	boolean open;
	
	JFrame frame;
	JLabel userNameLabel, friendsLabel;
	JTextField nameField, sendField;
	JButton nameButton, sendButton, selectButton;
    JList<String> friendsList;
    DefaultListModel<String> listModel;
    JTextArea chatHistory;
    JScrollPane areaScrollPane, listScrollPane;
    
    public ClientWindow() {
    	messageToSend = "";
    	userName = "";
    	open = true;
    	SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	createAndShowUserNameGUI();
            }
        });
    }
    
    public void createAndShowUserNameGUI() {
    	frame = new JFrame("Log in");
    	frame.setContentPane(getUserNameContentPane());
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new WindowAdapter() {
        	public void windowClosing(WindowEvent e) {
        		open = false;
        	}
        });
        frame.setSize(new Dimension(300,300));
        frame.setVisible(true);
        frame.setResizable(false);
    }
    
	public JPanel getUserNameContentPane() {
		JPanel userGUI = new JPanel();
		userGUI.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        userNameLabel = new JLabel("Enter User Name:");
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 0.33;
        c.fill = GridBagConstraints.NONE;
        userGUI.add(userNameLabel, c);
        
        nameField = new JTextField();
        nameField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = nameField.getText();
	    		if(!name.equals("")) {
	    			userName = name;
	    			createAndShowGUI();
	    		}
			}
        });
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 0.33;
        c.fill = GridBagConstraints.HORIZONTAL;
        userGUI.add(nameField, c);
        
        nameButton = new JButton("Enter");
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 0.33;
        c.fill = GridBagConstraints.NONE;
        nameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = nameField.getText();
	    		if(!name.equals("")) {
	    			userName = name;
	    			createAndShowGUI();
	    		}
			}
        });
        userGUI.add(nameButton, c);
        
        userGUI.setOpaque(true);
        return userGUI;
	}

	public void createAndShowGUI() {
		frame.dispose();
        frame = new JFrame(userName);
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
		chatHistory.append(line+"\n");
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
