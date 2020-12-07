package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientRunner {
	
	ClientFrame frame;
	static ClientRunner instance;
	boolean connected = false;
	
	private Socket socket;
	private InputStream is;
	private OutputStream os;
	private String username;
	
	

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
	
	public void RecievePanel(PanelToRunner state, Object element) {
		
	}
	
	public void close() {
		try {
			closeConnection();
		} catch(IOException e) {
			frame.showError("Échec lors de la fermeture de connexion : \n"+e.getMessage(), "Erreur Fermeture");
		}
		System.exit(0);
	}
	
	private void closeConnection() throws IOException {
		if (socket != null) {
			is.close();
			os.close();
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
			is = socket.getInputStream();
			os = socket.getOutputStream();
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

}
