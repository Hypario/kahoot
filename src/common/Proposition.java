package common;

import java.io.Serializable;

public class Proposition implements Serializable {

	private static final long serialVersionUID = -4857096751890469156L;

	private String text;
	
	public Proposition(String val) {
		text = val;
	}

	public String getText() {
		return text;
	}
	
	public String toString() {
		return text;
	}
}
