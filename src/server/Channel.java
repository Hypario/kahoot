package server;

import common.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class Channel extends Thread {

    private String channelName, admin;

    private Quiz quizz;

    private boolean start = false;

    // connected to the channel
    private static final ArrayList<Connection> connections = new ArrayList<>();

    public Channel(String name, String admin, Quiz quizz) {
        this.channelName = name;
        this.admin = admin;
        this.quizz = quizz;
    }

    public synchronized void add(Connection client) {
        connections.add(client);
        Server.removeConnection(client); // client isn't waiting for a channel anymore
    }

    public synchronized void remove(Connection client) {
        connections.remove(client);
        Server.removeConnection(client); // client isn't waiting for a channel anymore
    }

    public synchronized static void broadcast(Message message) {
        for (Connection connection : connections) {
            connection.sendMessage(message);
        }
    }

    @Override
    public void run() {
        try {
            while (!start) {
                Thread.sleep(1000);
            }

            ArrayList<Question> questions = getQuestions();

            for (Question question: questions) {
                broadcast(new Message(MessageType.Question, question));

                Proposition answer = question.getAnswer();

                Thread.sleep(10000);

                for (Connection client : connections) {
                    if (client.getProposition().getText().equals(answer.getText()))
                        client.correctAnswer();
                }

                // send the answer to show it
                broadcast(new Message(MessageType.Answer, answer));
                Thread.sleep(5000);
            }

            broadcast(new Message(MessageType.Score, getScore()));

            Thread.sleep(15000); // wait 15 seconds

            // move everyone in waiting list
            for (Connection connection: connections) {
                Server.addConnection(connection);
            }

            // delete channel
            Server.removeChannel(this.getChannelName());
            currentThread().interrupt();

        } catch (InterruptedException e) {
            Server.removeChannel(this.getChannelName());
            currentThread().interrupt();
        }
    }

    public void startQuizz() {
        start = true;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getAdmin() {
        return admin;
    }

    private HashMap<String, Integer> getScore() {
        HashMap<String, Integer> scores = new HashMap<>();
        for (Connection client :
                connections) {
            scores.put(client.getUsername(), client.getScore());
        }

        return scores;
    }

    private ArrayList<Question> getQuestions() {
        BDCreate bdd = BDCreate.getInstance();
        java.sql.Connection con = bdd.connect();

        ArrayList<Question> questions = new ArrayList<>();

        try {
            PreparedStatement statement = con.prepareStatement("SELECT question.*, p.text_proposition FROM question JOIN question_quizz qq on question.idQuestion = qq.idQuestion JOIN quiz q on qq.idQuiz = q.idQuiz JOIN proposition p on question.rep_id = p.idProposition WHERE qq.idQuiz = q.idQuiz AND question.rep_id = p.idProposition AND qq.idQuiz = ? ORDER BY RAND() LIMIT 10");
            statement.setInt(1, quizz.getId());
            statement.execute();
            ResultSet result = statement.getResultSet();

            while (result.next()) {
                String text = result.getString("text_question");
                ArrayList<Proposition> propositions = getPropositions(result.getInt("question.idQuestion"));
                Proposition correct_answer = new Proposition(result.getString("p.text_proposition"));
                String correct_text = result.getString("question.annecdote");
                questions.add(new Question(text, propositions, correct_answer, correct_text));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return questions;
    }

    private ArrayList<Proposition> getPropositions(int idQuestion) {
        BDCreate bdd = BDCreate.getInstance();
        java.sql.Connection con = bdd.connect();

        ArrayList<Proposition> propositions = new ArrayList<>();

        try {
            PreparedStatement statement = con.prepareStatement("SELECT * FROM question_proposition JOIN proposition p on question_proposition.PropositionId = p.idProposition WHERE QuestionId = ?");
            statement.setInt(1, idQuestion);
            statement.execute();

            ResultSet result = statement.getResultSet();
            while (result.next()) {
                propositions.add(new Proposition(result.getString("text_proposition")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return propositions;
    }
}
