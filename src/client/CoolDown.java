package client;

import javax.swing.JLabel;

public class CoolDown extends Thread {
	/*
	 * Compte à rebourd pour les question
	 */
	int secs;
	JLabel label_sec;
	
	public CoolDown(int seconds, JLabel label) {
		secs = seconds;
		label_sec = label;
	}
	
	public void run() {
		while (secs > 0) {
			
			try {
				label_sec.setText(secs+" secondes restantes.");
				Thread.sleep(1000);
				secs--;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
	}
	
}
