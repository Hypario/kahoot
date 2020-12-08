package client;

import javax.swing.JLabel;

public class CoolDown extends Thread {
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
}
