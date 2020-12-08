package client;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import common.Message;
import common.MessageType;
import common.Proposition;
import common.Question;
import common.Quiz;
import server.Channel;


public class ClientRunner {

	ClientFrame frame;
	static ClientRunner instance;
	boolean connected = false;

	private Socket socket;
	private ObjectOutputStream oos;
	private ClientListener listener;
	private String username;

	private boolean channelAdmin = false;

	private Question currentQuestion;
	private int nbq = 0;

	private final int time_to_answer = 10;

	private ClientRunner() {
		// TODO Auto-generated constructor stub
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
		switch (state) {
		case ANSWER:
			quiz_answer( ((JButton) element).getText());
			break;
		case CREATE_SRV:
			create_srv();
			break;
		case JOIN:
			join_game(((JButton) element).getText() );
			break;
		case START_GAME:
			break;
		default:
			break;


		}

	}

	private void quiz_answer(String chosen_answer) {
		Proposition chosen = null;
		for (Proposition p : currentQuestion.getPropositionList()) {
			if (p.getText().equals(chosen_answer)) {
				chosen = p;
			}
		}

		Message tosend = new Message(MessageType.Proposition, chosen);
		sendToServer(tosend);

	}

	// ICI FABIEN

	private void create_srv() {
		channelAdmin = true;
		Message tosend = new Message(MessageType.CreateChannel, null);
		sendToServer(tosend);
	}

	private void start_game() {
		if (channelAdmin) {
			// On attend fabien
		}
	}

	private void join_game(String name) {
		Message tosend = new Message(MessageType.ChannelChoice, name);
		sendToServer(tosend);
	}

	public void ReceiveServer(Message msg) {
		switch (msg.getType()) {
		case Channels:
			channelChoicesRX(msg);
			break;
		case CreateChannel:
			rxCreateChannel(msg);
			break;
		default:
			frame.showError("Le Serveur a envoyé un message qui n'a pas été reconnu par le client", "erreur client/serveur");
			break;
		}
	}
	
	public void rxCreateChannel(Message msg) {
		// On reçoit les propositions de quizz
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
		// Select difficulté
		String[] difficulties = new String[selected.getDifficulties().size()];
		current =0;
		for (String d : selected.getDifficulties()) {
			difficulties[current] = d;
			current++;
		}
				
		String answer_difficulties = null;
		while(answer_difficulties == null) {
			answer_difficulties = (String) JOptionPane.showInputDialog(frame, "Sélectionnez la difficulté", "Sélectionner la difficulté", JOptionPane.QUESTION_MESSAGE, null, difficulties, null);
		}

		System.out.println(answer_subject+" "+answer_difficulties);
	}

	public void rxJoin() {
		frame.getPanel().waiting_room(channelAdmin);
	}

	public void rxQuestion(Question q) {
		nbq++;
		this.currentQuestion = q;
		JLabel secs = frame.getPanel().question(nbq, q.getText(), q.getPropositionList());
		CoolDown cd = new CoolDown(time_to_answer, secs);
		Thread th = new Thread(cd);
		th.start();
	}

	public void rxAnswer(Proposition p) {
		frame.getPanel().reponse(p, currentQuestion.getAnnec());
	}

	public void rxScore(HashMap<String, Integer> scores) {
		frame.getPanel().scores(scores);
	}

	public void channelChoicesRX(Message msg) {
		HashMap<String, Channel> ch = (HashMap<String, Channel>) msg.getObject();
		ArrayList<String> srvs = new ArrayList<>();
		for (Map.Entry<String, Channel> srv : ch.entrySet()) {
			srvs.add(srv.getKey());
		}
		frame.getPanel().server_welcome(srvs);
	}

	public void close() {
		try {
			closeConnection();
		} catch(Exception e) {
			frame.showError("Échec lors de la fermeture de connexion : \n"+e.getMessage(), "Erreur Fermeture");
		}
		System.exit(0);
	}

	private void closeConnection() throws IOException, NullPointerException {
		if (socket != null) {
			oos.writeObject(null);
			oos.close();
			listener.close_listen();
			socket.close();
		}
	}

	public void connectToServer(String host, int port) {
		if (username == null) {
			username = frame.getUsername();
		}
		frame.getPanel().connecting_screen();
		try {
			socket = new Socket(host, port);
			oos = new ObjectOutputStream(socket.getOutputStream());
			listener = new ClientListener(socket);
			Thread l = new Thread(listener);
			l.start();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			frame.showError(e.getMessage(), "Erreur Host");
			frame.getPanel().welcome_screen();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			frame.showError(e.getMessage(), "Erreur IO");
			frame.getPanel().welcome_screen();
		}

	}

	private void sendToServer(Message msg) {
		try {
			oos.writeObject(msg);
			oos.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			frame.showError("Erreur d'envoi : \n"+e.getMessage(), "Erreur IO");
		}

	}

}
