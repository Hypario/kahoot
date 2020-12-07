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
	
	public void connectToServer(String host, int port) {
		if (username == null) {
			username = frame.getUsername();
		}
		try {
			socket = new Socket(host, port);
			is = socket.getInputStream();
			os = socket.getOutputStream();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			frame.showError(e.getMessage(), "Erreur Host");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			frame.showError(e.getMessage(), "Erreur IO");
		}
		
	}

}
