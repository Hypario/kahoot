/**  
* ClientFrame.java - JFrame
* @author  Virgile DASSONNEVILLE
* @version 1.0 
*/ 
package client;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;


@SuppressWarnings("serial")
public class ClientFrame extends JFrame {
	// Le ClientFrame est un singleton
	private static ClientFrame instance;
	private ClientPanel panel;
	
	
	private ClientFrame() {
		initWindow();
	}
	
	private void initWindow() {
		/* On affiche défini les infos de bases de la fenêtre
		*	Titre de la fenêtre / Sa Taille / La centrer / La rendre visible et définir son contenu
		*/
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
	            ClientRunner.getInstance().close();
	        }
	    });
		
	}
	
	public void showError(String message, String title) {
		/*
		 * Affichage d'erreur de manière physique sur la fenêtre et sur le terminal
		 */
		JOptionPane.showMessageDialog(panel, message, title, JOptionPane.ERROR_MESSAGE);
		System.err.println(message);
	}
	
	
	public ClientPanel getPanel() {
		return panel;
	}
	
	
	public static ClientFrame getInstance() {
		/*
		 * C'est un singleton, donc si l'objet n'est pas créé, on le créé
		 */
		if (instance == null) {
			instance = new ClientFrame();
		}
		return instance;
	}

	public String getUsername() {
		/*
		 * Demande à l'utilisateur du nom d'utilisateur via une fenêtre graphique
		 */
		return (String) JOptionPane.showInputDialog(panel, "Entrez votre nom d'utilisateur", "Entrez votre pseudo", JOptionPane.PLAIN_MESSAGE, null, null, null);
	}
}
