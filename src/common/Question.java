package common;

import java.util.ArrayList;

public class Question {
	
	private String text;
	private Proposition good_answer;
	private ArrayList<Proposition> answers;
	private String response_text;
	private boolean answered = false;
	
	
	
	public Question(String q, ArrayList<Proposition> answers, Proposition correct_answer, String correct_text) {
		text = q;
		this.answers = answers;
		good_answer = correct_answer;
		response_text = correct_text;
	}
	
	public String getText() {
		return text;
	}
	
	public ArrayList<Proposition> getPropositionList() {
		return answers;
	}
	
	public Proposition getAnswer() {
		return good_answer;
	}
	
	public String getAnnec() {
		return response_text;
	}
	
	public boolean validate(Proposition p) {
		if (p.equals(good_answer)) {
			answered = true;
			return true;
		}
			return false;
	}
	
	public String getAnswerText() {
		if (answered) {
			return response_text;
		}
		return null;
	}

	public String toString() {
		StringBuilder str = new StringBuilder(text+"\n");
		for (Proposition p : answers) {
			str.append(p.toString()+"\n");
		}
		
		str.append("good answer : "+good_answer.toString()+"\nannecdote : "+response_text);
		
		return str.toString();
	}
	
	
}
