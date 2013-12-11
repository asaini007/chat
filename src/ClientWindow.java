import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;


public class ClientWindow implements ActionListener {
	Client myClient;
	String messageToSend;
	String history;
	
    JButton sendButton;
    JTextField sendField;
    JTextArea chatHistory;
    JScrollPane areaScrollPane;
    
    public ClientWindow(Client aClient) {
    	myClient = aClient;
    	messageToSend = "";
    	history = "";
    	SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	createAndShowGUI();
            }
        });
    }
    
	public void createAndShowGUI() {
        JFrame frame = new JFrame("Chat");
        frame.setContentPane(getContentPane());
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
        c.gridwidth = 3;
        c.weightx = 1.0;
        c.weighty = 0.99;
        c.fill = GridBagConstraints.BOTH;
        totalGUI.add(areaScrollPane, c);
        
        sendField = new JTextField();
        sendField.addActionListener(this);
        c.gridy = 1;
        c.gridwidth = 1;
        c.weightx = 0.99;
        c.weighty = 0.01;
        c.fill = GridBagConstraints.HORIZONTAL;
        totalGUI.add(sendField, c);
        
        sendButton = new JButton("Send");
        c.gridx = 1;
        c.gridy = 1;
        c.gridwidth = 1;
        c.weightx = 0.01;
        c.weighty = 0.01;
        c.fill = GridBagConstraints.NONE;
        sendButton.addActionListener(this);
        totalGUI.add(sendButton, c);
        
        totalGUI.setOpaque(true);
        return totalGUI;
    }
	
	public void actionPerformed(ActionEvent e) {
    	if(e.getSource().equals(sendButton) || e.getSource().equals(sendField)) {
    		if(messageToSend.equals(""))
    			messageToSend = sendField.getText();
    		else
    			messageToSend+="\n"+sendField.getText();
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
	
	public void clearSendField() {
		sendField.setText("");
	}
	
	public void displayMessage(String line) {
		if(line!=null)
			history += history.equals("") ? line : "\n" + line;
		while(chatHistory==null) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {}
		}
		chatHistory.setText(history);
	}
}
