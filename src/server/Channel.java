package server;

import common.*;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
                    if (client.getProposition() != null && client.getProposition().equals(answer)) {
                        client.setProposition(null);
                        // TODO : add to score
                    }
                }

                // send the answer to show it
                broadcast(new Message(MessageType.Answer, answer));
                Thread.sleep(5000);
            }
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

    private ArrayList<Question> getQuestions() {
        BDCreate bdd = BDCreate.getInstance();
        java.sql.Connection con = bdd.connect();

        ArrayList<Question> questions = new ArrayList<>();

        try {
            Statement statement = con.createStatement();
            statement.execute("SELECT question.*, p.text_proposition FROM question JOIN question_quizz qq on question.idQuestion = qq.idQuestion JOIN quiz q on qq.idQuiz = q.idQuiz JOIN proposition p on question.rep_id = p.idProposition WHERE qq.idQuiz = q.idQuiz AND question.rep_id = p.idProposition");
            ResultSet result = statement.getResultSet();

            while (result.next()) {
                String text = result.getString("text_question");
                ArrayList<Proposition> propositions = getPropositions(result.getInt("question.idQuestion"));
                Proposition correct_answer = new Proposition(result.getString("p.text"));
                String correct_text = result.getString("question.anecdote");
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
            statement.executeUpdate();

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
