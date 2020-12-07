package database;

import java.io.IOException;
import java.sql.SQLException;

import org.json.simple.parser.ParseException;

public class Main {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        JsonParser parser = new JsonParser("quizz.json");

        try {
            parser.parse();
            System.out.println("parsing...");
            parser.addQuiz(parser.getParsedQuizz());
            System.out.println("parsing successful");
        } catch (IOException | ParseException | SQLException e) {
            e.printStackTrace();
        }
        // parser.show();
    }

}