import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ClientWindow extends WindowAdapter implements ActionListener {
	String messageToSend;
	String history;
	boolean open;
	
	JFrame frame;
    JButton sendButton;
    JTextField sendField;
    JLabel friendsLabel;
    JList<String> friendsList;
    DefaultListModel<String> listModel;
    JTextArea chatHistory;
    JScrollPane areaScrollPane;
    JScrollPane listScrollPane;
    
    public ClientWindow() {
    	messageToSend = "";
    	history = "";
    	open = true;
    	SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	createAndShowGUI();
            }
        });
    }
    
	public void createAndShowGUI() {
        frame = new JFrame("Chat");
        frame.setContentPane(getContentPane());
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(this);
        frame.setSize(new Dimension(300,300));
        frame.setVisible(true);
    }
    
	public JPanel getContentPane (){
        JPanel totalGUI = new JPanel();
        totalGUI.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        chatHistory = new JTextArea();
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
        c.gridwidth = 1;
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
        c.gridwidth = 1;
        c.gridheight = 1;
        c.weightx = 0.1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        totalGUI.add(listScrollPane, c);
        
        sendField = new JTextField();
        sendField.addActionListener(this);
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
        sendButton.addActionListener(this);
        totalGUI.add(sendButton, c);
        
        totalGUI.setOpaque(true);
        return totalGUI;
    }
	
	public void actionPerformed(ActionEvent e) {
    	if(e.getSource().equals(sendButton) || e.getSource().equals(sendField)) {
    		messageToSend += (messageToSend.equals("")) ? sendField.getText() : "\n"+sendField.getText();
    		sendField.setText("");
    	}
    }
	
	public boolean hasMessageToSend() {
		return !messageToSend.equals("");
	}
	
	public String getMessage() {
		String toReturn = messageToSend;
		messageToSend = "";
		return toReturn;
	}
	
	public void displayMessage(String line) {
		history += history.equals("") ? line : "\n" + line;
		while(chatHistory==null) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {}
		}
		chatHistory.setText(history);
	}
	
	public void remove(String s) {
		listModel.removeElement(s);
	}
	
	public void add(String s) {
		listModel.addElement(s);
	}
	
	public boolean isOpen() {
		return open;
	}
	
	public void windowClosing(WindowEvent e) {
		open = false;
	}
	
	public void close() {
		frame.dispose();
	}
}
