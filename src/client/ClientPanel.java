/**  
* ClientPanel.java - JPanel
* @author  Virgile DASSONNEVILLE
* @version 1.0 
*/ 
package client;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import common.Proposition;

@SuppressWarnings("serial")
public class ClientPanel extends JPanel implements ActionListener {

	private JTextField ip, port;
	private JButton login_btn;

	private ArrayList<JButton> srv_bts;
	private ArrayList<JButton> answers;
	private JButton createsrv;
	private JButton start_game;
	private JButton close;


	public ClientPanel() {
		this.setLayout(null);
		welcome_screen();
	}


	public void welcome_screen() {
		/*
		 * Ecran d'accueil demandant l'adresse ip et le port
		 */
		clear_screen();
		JLabel kahoot_msg = new JLabel("Kahoot Java", SwingConstants.CENTER);
		kahoot_msg.setFont(new Font("Calibri", Font.BOLD, 50));
		kahoot_msg.setBounds(0,156, 1280,60 );
		this.add(kahoot_msg);
		JLabel please_enter = new JLabel("Merci d'entrer les informations de connexion du serveur", SwingConstants.CENTER);
		please_enter.setFont(new Font("Calibri", Font.PLAIN, 30));
		please_enter.setBounds(0,204,1280,50);
		this.add(please_enter);

		JLabel enter_ip = new JLabel("Entrez l'adresse IP du serveur : ");
		enter_ip.setBounds(320, 499, 320, 13);
		this.add(enter_ip);

		JLabel enter_port = new JLabel("Entrez le port du serveur : ");
		enter_port.setBounds(320, 530, 320, 13);
		this.add(enter_port);

		// JTextArea pour entrer les donn�es
		ip = new JTextField("127.0.0.1");
		ip.setBounds(640, 497, 320, 16);
		this.add(ip);

		port = new JTextField("50000");
		port.setBounds(640, 530, 320, 16);
		this.add(port);

		login_btn = new JButton("Se Connecter");
		login_btn.setBounds(515,640,250,30);
		login_btn.addActionListener(this);
		this.add(login_btn);

	}

	private void clear_screen() {
		/*
		 * Vider l'affichage de l'écran
		 */
		this.removeAll();
		try {
			this.getGraphics().clearRect(0, 0, this.getWidth(), this.getHeight());
		} catch (NullPointerException e) {
			System.out.println("init");
		}

		this.repaint();

	}

	public void connecting_screen() {
		/*
		 * Ecran de transition lors de la connexion au serveur
		 */
		clear_screen();
		JLabel connect = new JLabel("Connexion au serveur en cours, merci de patienter...", SwingConstants.CENTER);
		connect.setFont(new Font("Calibri", Font.BOLD, 50));
		connect.setBounds(0, 325, 1280, 60);
		this.add(connect);
	}

	public void server_welcome(ArrayList<String> listeserv) {
		/*
		 * Ecran de bienvenue du serveur (Cannaux disponibles / Création de canal)
		 */
		clear_screen();
		JLabel connected = new JLabel("Vous êtes connectés au serveur.");
		connected.setBounds(0, 0, 1280, 15);
		this.add(connected);

		JLabel choice = new JLabel("Choisissez un serveur sur lequel vous voulez vous connecter", SwingConstants.CENTER);
		choice.setBounds(0,15,1280,15);
		this.add(choice);

		srv_bts = new ArrayList<>();
		int x = 320;
		for (String srv : listeserv) {
			JButton join = new JButton(srv);
			join.addActionListener(this);
			join.setBounds(x, 231, 100, 20);
			x+=110;
			this.add(join);
			srv_bts.add(join);
		}

		createsrv = new JButton("Créer un serveur");
		createsrv.setBounds(515,640,250,30);
		createsrv.addActionListener(this);
		this.add(createsrv);
	}

	public JLabel waiting_room(boolean administrator) {
		/*
		 * Affichage du salon d'attente avant le début de partie
		 */
		clear_screen();
		JLabel status_text = new JLabel("En attente d'autres joueurs ...", SwingConstants.CENTER);
		status_text.setFont(new Font("Calibri", Font.BOLD, 50));
		status_text.setBounds(0, 325, 1280, 60);
		this.add(status_text);

		if (administrator) {
			start_game = new JButton("Commencer la partie");
			start_game.setBounds(515,640,250,30);
			start_game.addActionListener(this);
			this.add(start_game);
		}

		return status_text;
	}

	public JLabel question(int numquestion, String question, ArrayList<Proposition> answers) {
		/*
		 * Ecran de question
		 */
		clear_screen();
		JLabel numq = new JLabel("Question n°"+numquestion);
		numq.setBounds(0, 0, 100, 13);
		this.add(numq);

		JLabel sec = new JLabel("x Secondes Restantes");
		sec.setBounds(100,0,200,13);
		this.add(sec);
		

		JLabel q = new JLabel(question, SwingConstants.CENTER);
		q.setBounds(0, 100, 1280, 30);
		q.setFont(new Font("Calibri", Font.BOLD, 25));
		this.add(q);

		this.answers = new ArrayList<>();

		int[][] places = {{20,156}, {640,156}, {20, 408}, {640,408}};

		int nbr = 0;
		for (Proposition p : answers) {

			JButton r = new JButton(p.getText());
			r.setBounds(places[nbr][0], places[nbr][1], 610, 242);
			r.addActionListener(this);
			this.answers.add(r);
			this.add(r);


			nbr+=1;
		}
		return sec;
	}

	public void question_lockbtns(JButton selected) {
		/*
		 * Vérouillage des boutons
		 */
		for (JButton b : answers) {
			if (b == selected) {
				b.setBackground(Color.BLUE);
			}
			b.setEnabled(false);
		}
	}

	public void reponse(Proposition reponse, String hint) {
		/*
		 * Ecran de réponse
		 */
		clear_screen();
		JLabel rep =  new JLabel("La réponse était : "+reponse.getText(), SwingConstants.CENTER);
		rep.setBounds(0, 325, 1280, 60);
		rep.setFont(new Font("Calibri", Font.BOLD, 25));

		JLabel hintlabel = new JLabel(hint, SwingConstants.CENTER);
		hintlabel.setFont(new Font(hintlabel.getFont().getName(), Font.BOLD, 15));
		hintlabel.setBounds(0,600,1280,120);
		this.add(hintlabel);
		this.add(rep);

	}

	public void scores(HashMap<String, Integer> scoreboard) {
		/*
		 * Affichage du tableau des scores
		 */
		clear_screen();
		// Le tableau de scores arrive déjà trié.
		JLabel sc = new JLabel("Partie Terminé. Tableau des scores : ");
		sc.setBounds(0, 0, 1280, 40);
		sc.setFont(new Font(sc.getFont().getName(), Font.BOLD, 30));
		this.add(sc);

		int begin = 60;

		for (Map.Entry<String, Integer> elt : scoreboard.entrySet()) {
			JLabel u_score = new JLabel(elt.getKey()+" : "+elt.getValue()+" points", SwingConstants.CENTER);
			u_score.setFont(new Font(sc.getFont().getName(), Font.PLAIN, 20));
			u_score.setBounds(0, begin, 1280, 30);
			this.add(u_score);
			begin+=40;
		}

		close = new JButton("Fermer");
		close.setBounds(5, 640,150,40);
		this.add(close);
		close.addActionListener(this);
	}


	// ACTION TO RUNNER

	private void login_to_server() {
		/*
		 * Connexion au serveur
		 */
		int port_srv;
		String ip_srv = ip.getText();
		try {
			port_srv = Integer.parseInt(port.getText());
			ClientRunner.getInstance().connectToServer(ip_srv, port_srv);
		} catch(NumberFormatException e) {
			JOptionPane.showMessageDialog(this, "Vous ne pouvez pas entrer un port vide.\nErreur : "+e.getMessage(),"Erreur", JOptionPane.ERROR_MESSAGE);
			return;
		}

	}

	@Override
	public void actionPerformed(ActionEvent e) {
		/*
		 * Recup des évenements envoyés par les boutons
		 */
		Object src = e.getSource();

		if (src == login_btn) {
			login_to_server();
		} else if (src == createsrv) {
			ClientRunner.getInstance().ReceivePanel(PanelToRunner.CREATE_SRV, null);
		} else if (src == start_game) {
			ClientRunner.getInstance().ReceivePanel(PanelToRunner.START_GAME, null);
		} else if (src == close) {
			ClientRunner.getInstance().close();
		} else {
			if (answers !=null) {
				for (JButton b : answers) {
					if (src == b) {
						ClientRunner.getInstance().ReceivePanel(PanelToRunner.ANSWER, b);
						return;
					}
				}
			} else if (srv_bts != null) {
				for (JButton b : srv_bts) {
					if (src == b) {
						ClientRunner.getInstance().ReceivePanel(PanelToRunner.JOIN, b);
					}
				}
			}
		}
	}



}
