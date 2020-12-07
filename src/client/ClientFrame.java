package client;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;


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
		
		this.addWindowListener(new WindowAdapter() {
	        @Override
	        public void windowClosing(WindowEvent e) {
	            // Action when window is closed
	            System.exit(0);
	        }
	    });
		
	}
	
	
	public static ClientFrame getInstance() {
		if (instance == null) {
			instance = new ClientFrame();
		}
		return instance;
	}
}
