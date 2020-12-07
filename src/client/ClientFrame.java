package client;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;


@SuppressWarnings("serial")
public class ClientFrame extends JFrame {
	
	private static ClientFrame instance;
	private ClientPanel panel;
	
	
	private ClientFrame() {
		initWindow();
	}
	
	private void initWindow() {
		this.setTitle("Kahoot Java");
		this.setSize(1280,720);
		this.setLocationRelativeTo(null);
		this.setResizable(false);
		this.setVisible(true);
		
		this.setContentPane(panel = new ClientPanel());
		validate();
		
		this.addWindowListener(new WindowAdapter() {
	        @Override
	        public void windowClosing(WindowEvent e) {
	            // Action when window is closed
	            System.exit(0);
	        }
	    });
		
	}
	
	public void showError(String message, String title) {
		JOptionPane.showMessageDialog(panel, message, title, JOptionPane.ERROR_MESSAGE);
		System.err.println(message);
	}
	
	
	public ClientPanel getPanel() {
		return panel;
	}
	
	
	public static ClientFrame getInstance() {
		if (instance == null) {
			instance = new ClientFrame();
		}
		return instance;
	}

	public String getUsername() {
		return (String) JOptionPane.showInputDialog(panel, "Entrez votre nom d'utilisateur", "Entrez votre pseudo", JOptionPane.PLAIN_MESSAGE, null, null, null);
	}
}
