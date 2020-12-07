package client;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class ClientPanel extends JPanel implements ActionListener {
	
	private JTextField ip, port;
	private JButton login_btn;
	
	
	public ClientPanel() {
		this.setLayout(null);
		welcome_screen();
	}
	
	
	private void welcome_screen() {
		JLabel kahoot_msg = new JLabel("Kahoot Java", SwingConstants.CENTER);
		kahoot_msg.setFont(new Font("Calibri", Font.BOLD, 50));
		kahoot_msg.setBounds(0,156, 1280,60 );
		this.add(kahoot_msg);
		JLabel please_enter = new JLabel("Merci d’entrer les informations de connexion du serveur", SwingConstants.CENTER);
		please_enter.setFont(new Font("Calibri", Font.PLAIN, 30));
		please_enter.setBounds(0,204,1280,50);
		this.add(please_enter);
		
		JLabel enter_ip = new JLabel("Entrez l'adresse IP du serveur : ");
		enter_ip.setBounds(320, 499, 320, 13);
		this.add(enter_ip);
		
		JLabel enter_port = new JLabel("Entrez le port du serveur : ");
		enter_port.setBounds(320, 530, 320, 13);
		this.add(enter_port);
		
		// JTextArea pour entrer les données
		ip = new JTextField("127.0.0.1");
		ip.setBounds(640, 497, 320, 16);
		this.add(ip);
		
		port = new JTextField("1234");
		port.setBounds(640, 530, 320, 16);
		this.add(port);
		
		login_btn = new JButton("Se Connecter");
		login_btn.setBounds(515,640,250,30);
		login_btn.addActionListener(this);
		this.add(login_btn);
		
	}
	
	private void clear_screen() {
		this.removeAll();
		this.getGraphics().clearRect(0, 0, this.getWidth(), this.getHeight());
		this.repaint();

	}
	
	private void connecting_screen() {
		JLabel connect = new JLabel("Connexion au serveur en cours, merci de patienter...", SwingConstants.CENTER);
		connect.setFont(new Font("Calibri", Font.BOLD, 50));
		connect.setBounds(0, 325, 1280, 60);
		this.add(connect);
	}
	
	public void server_welcome() {
		JLabel connected = new JLabel("Vous êtes connectés au serveur : 0.0.0.0:1234");
		connected.setBounds(0, 0, 1280, 15);
		this.add(connected);
	}
	
	
	
	
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == login_btn) {
			login_to_server();
		}
	}
	
	private void login_to_server() {
		int port_srv;
		String ip_srv = ip.getText();
		try {
			port_srv = Integer.parseInt(port.getText());
		} catch(NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Vous ne pouvez pas entrer un port vide.\nErreur : "+e.getMessage(),"Erreur", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		System.out.println("Tentative de connexion sur : "+ip_srv+":"+port_srv);
		clear_screen();
		connecting_screen();
	}

}
