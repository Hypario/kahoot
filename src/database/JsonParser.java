package database;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import common.BDCreate;
import common.Proposition;
import common.Question;
import common.Quiz;

public class JsonParser {

    /* STRUCTURE DU FICHIER JSON DONNÉ EN ENTRÉE :
     * {
     *  "fournisseur", // string
     * 	"rédacteur", // string
     * 	"difficulté", // string
     *  "quizz" : { // json object
     *      "na" : { // json object (lang)
     *      	"diff" : [ // json array (difficulty)
     *      		{
     *      			"id", // int (question nr)
     *      			"question", // string,
     *      			"propositions" : [ // json array
     *      				"rep" // string
     *      			]
     *      			"réponse", // string (same as one of propositions)
     *      			"anectode" // string
     *      		}
     *      	]
     *      }
     *     }
     *  }
     *  On ne traite que les questions en français également.
     */


    String filename;
    StringBuilder json_content = new StringBuilder("");
    Quiz parsedQuizz;

    Connection conn;

    public JsonParser(String filename) {
        conn = BDCreate.getInstance().connect();
        this.filename = filename;
    }

    public void parse() throws IOException, ParseException {

        // Ouverture / Lecture du fichier

        File fic = new File(filename);
        BufferedReader br = new BufferedReader(new FileReader(fic));
        String currentLine;
        while ((currentLine = br.readLine()) != null) {
            json_content.append(currentLine).append("\n");
        }
        // Parser JSON du fichier

        JSONParser jparser = new JSONParser();
        JSONObject obj = (JSONObject) jparser.parse(json_content.toString());

        // Récupération des données de base du quizz

        String fournisseur, redacteur, theme, difficulte;

        fournisseur = (String) obj.get("fournisseur");
        redacteur = (String) obj.get("rédacteur");
        theme = (String) obj.get("thème");
        difficulte = (String) obj.get("difficulté");

        // Définition de la liste de question par rapport à la difficulté

        HashMap<String, ArrayList<Question>> question_quizz = new HashMap<>();

        // Récupération des questions FR

        JSONObject questions_fr = (JSONObject) ((JSONObject) obj.get("quizz")).get("fr");
        @SuppressWarnings("unchecked")
        Iterator<String> keys = questions_fr.keySet().iterator();

        // generated by Intellij
        //noinspection WhileLoopReplaceableByForEach
        while (keys.hasNext()) {

            // Pour chaque difficulté

            String diff = keys.next();

            ArrayList<Question> question_list = new ArrayList<>();

            JSONArray questions = (JSONArray) questions_fr.get(diff);
            @SuppressWarnings("unchecked")
            Iterator<JSONObject> question_array = questions.iterator();

            // generated by Intellij
            //noinspection WhileLoopReplaceableByForEach
            while (question_array.hasNext()) {

                // Pour chaque question

                String question_txt, ganswer, annecdote;

                ArrayList<Proposition> answers = new ArrayList<>();
                JSONObject question = question_array.next();
                Proposition ganswer_obj = null;

                question_txt = (String) question.get("question");
                ganswer = (String) question.get("réponse");
                annecdote = (String) question.get("anecdote");

                JSONArray props = (JSONArray) question.get("propositions");

                for (Object rp : props) {
                    String current_answer = (String) rp;
                    Proposition proposition = new Proposition(current_answer);
                    answers.add(proposition);
                    if (rp.equals(ganswer)) {
                        ganswer_obj = proposition;
                    }
                }

                Question q = new Question(question_txt, answers, ganswer_obj, annecdote);
                question_list.add(q);

            }

            question_quizz.put(diff, question_list);


        }

        // Création de l'objet quizz

        double diff_quizz = (double) Integer.parseInt(difficulte.split(" / ")[0]) / Integer.parseInt(difficulte.split(" / ")[1]);

        parsedQuizz = new Quiz(question_quizz, redacteur, theme, diff_quizz);

    }

    public Quiz getParsedQuizz() {
        return parsedQuizz;
    }

    public void show() {
        System.out.println(json_content.toString());
    }

    public void addQuiz(Quiz quiz) throws SQLException {
        // Ajout des données de base du quizz
        int idQuiz = 0;
        HashMap<String, Integer> idTypeQuestion = new HashMap<>();


        String request_quiz = "INSERT INTO quiz(author, theme, difficulty) VALUES (?,?,?)";
        PreparedStatement pstmt_quiz = conn.prepareStatement(request_quiz, Statement.RETURN_GENERATED_KEYS);
        pstmt_quiz.setString(1, quiz.getAuthor());
        pstmt_quiz.setString(2, quiz.getTheme());
        pstmt_quiz.setDouble(3, quiz.getDifficulty());

        pstmt_quiz.executeUpdate();
        ResultSet res_quiz = pstmt_quiz.getGeneratedKeys();
        if (res_quiz.next()) {
            idQuiz = res_quiz.getInt(1);
        }
        res_quiz.close();
        pstmt_quiz.close();

        // Ajout des types de question
        for (String diff : quiz.getDifficulties()) {
            String stmt_diff = "INSERT INTO type_question(label, quizz_id) VALUES (?,?)";
            PreparedStatement pstmt_diff = conn.prepareStatement(stmt_diff, Statement.RETURN_GENERATED_KEYS);
            pstmt_diff.setString(1, diff);
            pstmt_diff.setInt(2, idQuiz);
            pstmt_diff.executeUpdate();
            ResultSet res_diff = pstmt_diff.getGeneratedKeys();
            if (res_diff.next()) {
                idTypeQuestion.put(diff, res_diff.getInt(1));
            }
            res_diff.close();
            pstmt_diff.close();
        }

        // Ajout des questions pour chaque difficulté
        for (Map.Entry<String, Integer> entry : idTypeQuestion.entrySet()) {

            ArrayList<Question> questions = quiz.getQuestionByDifficulty(entry.getKey());
            for (Question q : questions) {
                // On ajoute d'abord les propositions
                ArrayList<Integer> props_id = new ArrayList<>();
                int answerid = 0;
                int idquestion = 0;
                for (Proposition p : q.getPropositionList()) {
                    int this_id = 0;
                    String stmt_prop = "INSERT INTO proposition(text_proposition) VALUES(?)";
                    PreparedStatement pstmt_prop = conn.prepareStatement(stmt_prop, Statement.RETURN_GENERATED_KEYS);
                    pstmt_prop.setString(1, p.getText());
                    pstmt_prop.executeUpdate();
                    ResultSet res_prop = pstmt_prop.getGeneratedKeys();
                    if (res_prop.next()) {
                        this_id = res_prop.getInt(1);
                        props_id.add(this_id);
                    }

                    if (p.equals(q.getAnswer())) {
                        answerid = this_id;
                    }
                    res_prop.close();
                    pstmt_prop.close();

                }

                // On ajoute ensuite la question

                String stmt_quest = "INSERT INTO question(text_question, annecdote, rep_id) VALUES (?,?,?)";
                PreparedStatement pstmt_quest = conn.prepareStatement(stmt_quest, Statement.RETURN_GENERATED_KEYS);
                pstmt_quest.setString(1, q.getText());
                pstmt_quest.setString(2, q.getAnnec());
                pstmt_quest.setInt(3, answerid);

                pstmt_quest.executeUpdate();
                ResultSet res_quest = pstmt_quest.getGeneratedKeys();
                if (res_quest.next()) {
                    idquestion = res_quest.getInt(1);
                }
                res_quest.close();
                pstmt_quest.close();

                // On ajoute la correspondance question / réponse
                for (int prop_id : props_id) {
                    String stmt_qr = "INSERT INTO question_proposition(QuestionId, PropositionId) VALUES (?,?)";
                    PreparedStatement pstmt_qr = conn.prepareStatement(stmt_qr);
                    pstmt_qr.setInt(1, idquestion);
                    pstmt_qr.setInt(2, prop_id);
                    pstmt_qr.executeUpdate();

                    pstmt_qr.close();

                }

                // On ajoute la question a la correspondance difficulté / question / quizz
                String stmt_qqd = "INSERT INTO question_quizz(idQuiz, idQuestion, idDifficulte) VALUES (?,?,?)";
                PreparedStatement pstmt_qqd = conn.prepareStatement(stmt_qqd);
                pstmt_qqd.setInt(1, idQuiz);
                pstmt_qqd.setInt(2, idquestion);
                pstmt_qqd.setInt(3, entry.getValue());
                pstmt_qqd.executeUpdate();
                pstmt_qqd.close();
            }
        }
    }


}
