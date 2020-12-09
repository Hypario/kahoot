/**  
* ClientRunner.java - Master Running file
* @author  Virgile DASSONNEVILLE
* @version 1.0 
*/ 
package client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import common.CreateChannel;
import common.Message;
import common.MessageType;
import common.Proposition;
import common.Question;
import common.Quiz;


public class ClientRunner {

	ClientFrame frame;
	static ClientRunner instance;
	boolean connected = false;

	private Socket socket;
	private ObjectOutputStream oos;
	private ClientListener listener;
	private String username;

	private boolean channelAdmin = false;
	private String channelName = null;
	
	private Question currentQuestion;
	private int nbq = 0;

	private final int time_to_answer = 10;

	private ClientRunner() {
		StartUI();
	}

	public static ClientRunner getInstance() {
		if (instance == null) {
			instance = new ClientRunner();
		}
		return instance;
	}

	private void StartUI() {
		frame = ClientFrame.getInstance();
	}

	public void ReceivePanel(PanelToRunner state, Object element) {
		/*
		 * Reception des données venant de la Frame
		 */
		switch (state) {
		case ANSWER:
			frame.getPanel().question_lockbtns((JButton) element);
			quiz_answer( ((JButton) element).getText());
			break;
		case CREATE_SRV:
			create_srv();
			break;
		case JOIN:
			join_game(((JButton) element).getText() );
			break;
		case START_GAME:
			start_game();
			break;
		default:
			break;


		}

	}

	private void quiz_answer(String chosen_answer) {
		/*
		 * Reception de l'event de réponse à une question
		 */
		Proposition chosen = null;
		for (Proposition p : currentQuestion.getPropositionList()) {
			if (p.getText().equals(chosen_answer)) {
				chosen = p;
			}
		}

		Message tosend = new Message(MessageType.Proposition, chosen);
		sendToServer(tosend);

	}

	private void create_srv() {
		/*
		 * Creation d'un serveur
		 */
		channelAdmin = true;
		Message tosend = new Message(MessageType.CreateChannel, null);
		sendToServer(tosend);
	}

	private void start_game() {
		/*
		 * Demarrage d'une partie
		 */
		if (channelAdmin) {
			Message tosend = new Message(MessageType.QuizzStart,this.channelName); // = new Message(MessageType.StartChannel, this.channelName);
			sendToServer(tosend);
		}
	}

	private void join_game(String name) {
		/*
		 * Connexion à une partie
		 */
		Message tosend = new Message(MessageType.ChannelChoice, name);
		sendToServer(tosend);
		frame.getPanel().waiting_room(channelAdmin);
	}

	public void ReceiveServer(Message msg) {
		/*
		 * Reception de données venant du serveur (après le thread)
		 */
		switch (msg.getType()) {
		case Channels:
			channelChoicesRX(msg);
			break;
		case QuizzList:
			rxCreateChannel(msg);
			break;
		case Question:
			rxQuestion(msg);
			break;
		case Answer:
			rxAnswer(msg);
			break;
		case Score:
			rxScore(msg);
			break;
		default:	
			frame.showError("Le Serveur a envoyé un message qui n'a pas été reconnu par le client", "erreur client/serveur");
			break;
		}
		frame.getPanel().repaint();
	}
	
	public void rxCreateChannel(Message msg) {
		/*
		 * Cration du cannal 
		 */
		// On reçoit les propositions de quizz
		@SuppressWarnings("unchecked")
		ArrayList<Quiz> quizes = (ArrayList<Quiz>) msg.getObject();
		String[] quiz_string = new String[quizes.size()];
		int current = 0;
		for (Quiz q : quizes) {
			quiz_string[current] = q.getTheme();
			current++;
		}

		String answer_subject = null;
		while (answer_subject == null) {
			answer_subject = (String) JOptionPane.showInputDialog(frame, "Sélectionnez le thème des questions", "Thème des Questions", JOptionPane.QUESTION_MESSAGE, null, quiz_string, null);
		}
		
		Quiz selected = null;
		for (Quiz q : quizes) {
			if (q.getTheme().equals(answer_subject)) {
				selected = q;
			}
		}
		
		// Envoi vers le serveur de quiz choisi
		Message choice = new Message(MessageType.QuizzChoice, new CreateChannel(username, username, selected));
		sendToServer(choice);
		this.channelName = this.username;
		
		
		@SuppressWarnings("unused")
		JLabel label = frame.getPanel().waiting_room(channelAdmin);
		//label.setText("En attente d'autres joueurs. La partie va commencer dans 10 secondes.");
	}

	public void rxJoin() {
		// On envoie l'utilisateur dans la salle d'attente
		frame.getPanel().waiting_room(channelAdmin);
	}

	public void rxQuestion(Message m) {
		/*
		 * Réception de la question et affichage dans le pannel
		 */
		Question q = (Question) m.getObject();
		nbq++;
		this.currentQuestion = q;
		JLabel secs = frame.getPanel().question(nbq, q.getText(), q.getPropositionList());
		CoolDown cd = new CoolDown(time_to_answer, secs);
		Thread th = new Thread(cd);
		th.start();
	}

	public void rxAnswer(Message msg) {
		/*
		 * Réception de la bonne réponse
		 */
		Proposition p = (Proposition) msg.getObject();
		frame.getPanel().reponse(p, currentQuestion.getAnnec());	
		
	}

	public void rxScore(Message msg) {
		/*
		 * Réception du tableau de scores
		 */
		@SuppressWarnings("unchecked")
		HashMap<String, Integer> scores = (HashMap<String, Integer>) msg.getObject();
		frame.getPanel().scores(scores);
	}

	public void channelChoicesRX(Message msg) {
		/*
		 * Réception des choix de cannaux
		 */
		@SuppressWarnings("unchecked")
		ArrayList<String> ch = (ArrayList<String>) msg.getObject();
		
		frame.getPanel().server_welcome(ch);
	}

	public void close() {
		/*
		 * Fermeture de l'application
		 */
		try {
			closeConnection();
		} catch(Exception e) {
			frame.showError("Échec lors de la fermeture de connexion : \n"+e.getMessage(), "Erreur Fermeture");
			e.printStackTrace();
			ClientRunner.getInstance().close();
		}
		System.exit(0);
	}

	private void closeConnection() throws IOException, NullPointerException {
		/*
		 * Fermeture des connexions
		 */
		if (socket != null) {
			oos.writeObject(null);
			oos.close();
			listener.close_listen();
			socket.close();
		}
	}

	public void connectToServer(String host, int port) {
		/*
		 * Connexion au serveur
		 */
		while (username == null || username.equals("")) {
			username = frame.getUsername();
		}
		frame.getPanel().connecting_screen();
		try {
			socket = new Socket(host, port);
			oos = new ObjectOutputStream(socket.getOutputStream());
			listener = new ClientListener(socket);
			Thread l = new Thread(listener);
			l.start();
			sendToServer(new Message(MessageType.SetUsername, this.username));
		} catch (UnknownHostException e) {
			frame.showError(e.getMessage(), "Erreur Host");
			frame.getPanel().welcome_screen();
		} catch (IOException e) {
			frame.showError(e.getMessage(), "Erreur IO");
			frame.getPanel().welcome_screen();
		}

	}

	private void sendToServer(Message msg) {
		/*
		 * Envoi de données au serveur
		 */
		try {
			oos.writeObject(msg);
			oos.flush();
		} catch (IOException e) {
			frame.showError("Erreur d'envoi : \n"+e.getMessage(), "Erreur IO");
			e.printStackTrace();
			ClientRunner.getInstance().close();
		}

	}

}
