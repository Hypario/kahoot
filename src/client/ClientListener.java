package client;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.Socket;

import common.Message;

public class ClientListener extends Thread{

	Socket socket;
	ObjectInputStream ois;
	InputStream is;


	public ClientListener(Socket soc)  {
		socket = soc;
	}

	public void run() {
		System.out.println("Client Listener Thread Started");
		while(!Thread.currentThread().isInterrupted()) {
			Message msg;
			try {
				ois = new ObjectInputStream(socket.getInputStream());
				while((msg = (Message) ois.readObject()) != null) {
				ClientRunner.getInstance().ReceiveServer(msg);
				}
			} catch (IOException e) {
				ClientFrame.getInstance().showError(e.getMessage(), "Erreur IO Thread Reader");
				e.printStackTrace();
				ClientRunner.getInstance().close();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				ClientFrame.getInstance().showError(e.getMessage(), "Erreur Class Not Found");
				e.printStackTrace();
				ClientRunner.getInstance().close();
			}

		}
	}

	public void close_listen() throws IOException {
		ois.close();
	}

}
