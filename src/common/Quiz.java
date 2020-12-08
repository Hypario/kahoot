package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Quiz implements Serializable {

	private static final long serialVersionUID = 7778560071532920177L;

	private HashMap<String, ArrayList<Question>> questions;
    private String author;
    private String theme;
    private double difficulty;


    public Quiz(HashMap<String, ArrayList<Question>> questions, String author, String theme, double difficulty) {
        this.questions = questions;
        this.theme = theme;
        this.difficulty = difficulty;
        this.author = author;
    }

    public Quiz(String author, String theme, double difficulty) {
        this.theme = theme;
        this.difficulty = difficulty;
        this.author = author;
    }

    public void addQuestion(String diff, Question q) {
        ArrayList<Question> myquestionlist = questions.get(diff);
        if (myquestionlist == null) {
            myquestionlist = new ArrayList<>();
        }
        myquestionlist.add(q);
        questions.replace(diff, myquestionlist);
    }


    public ArrayList<Question> getQuestionByDifficulty(String diff) {
        return questions.get(diff);
    }


    public ArrayList<String> getDifficulties() {
        ArrayList<String> rep = new ArrayList<>();
        for (String dif : questions.keySet()) {
            rep.add(dif);
        }
        return rep;
    }

    public String toString() {
        StringBuilder str = new StringBuilder(theme + " by " + author + " | difficulty : " + difficulty + "\n");
        for (Map.Entry<String, ArrayList<Question>> eq : questions.entrySet()) {
            str.append(eq.getKey() + "\n");
            for (Question q : eq.getValue()) {
                str.append(q.toString() + "\n");
            }
        }
        return str.toString();
    }

    public double getDifficulty() {
        return difficulty;
    }

    public String getAuthor() {
        return author;
    }

    public String getTheme() {
        return theme;
    }

}
