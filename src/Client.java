import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class Client implements ActionListener {
	String history = "";
	DataOutputStream output;
	
    JButton sendButton;
    JTextField sendField;
    JTextArea chatHistory;
    JScrollPane areaScrollPane;

    public JPanel createContentPane (){
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
    	String message = sendField.getText();
    	if(!message.equals("")) {
    		try {
    			output.writeInt(message.getBytes().length);
				output.write(message.getBytes(), 0, message.getBytes().length);
			} catch (IOException e1) {}
    	}
    	chatHistory.setText(history);
    	sendField.setText("");
    }

    private static void createAndShowGUI(Client box) {
        JFrame frame = new JFrame("Chat");
        frame.setContentPane(box.createContentPane());
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setSize(new Dimension(300,300));
        frame.setVisible(true);
    }
    
	public static void main(String[] args) throws IOException, InterruptedException {
        final Client object = new Client();
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI(object);
            }
        });
		Socket socket;
		while(true) {
			try {
				socket = new Socket(InetAddress.getLocalHost(), 12543);
				break;
			} catch (ConnectException e) {}
			Thread.sleep(100);
		}
		object.output = new DataOutputStream(socket.getOutputStream());
        DataInputStream is = new DataInputStream(socket.getInputStream());
		byte[] bytes;
		while(true) {
			if(is.available() > 0) {
				bytes = new byte[is.readInt()];
				is.read(bytes,0,bytes.length);
				object.history += object.history.equals("") ? new String(bytes,0,bytes.length,"UTF-8") : "\n" + new String(bytes,0,bytes.length,"UTF-8");
				while(object.chatHistory==null) {
					Thread.sleep(1000);
				}
				object.chatHistory.setText(object.history);
				bytes = null;
			}
			Thread.sleep(100);
		}
	}
}