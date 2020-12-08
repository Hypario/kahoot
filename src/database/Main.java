package database;

import java.io.IOException;
import java.sql.SQLException;

import org.json.simple.parser.ParseException;

public class Main {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
    	for (String s : args) {
    		JsonParser parser = new JsonParser(s);
    		try {
                parser.parse();
                System.out.println("parsing...");
                parser.addQuiz(parser.getParsedQuizz());
                System.out.println("parsing successful");
            } catch (IOException | ParseException | SQLException e) {
            	System.err.println("Impossible d'accéder au fichier. Merci de vérifier que celui-ci est bien présent dans le dossier");
                e.printStackTrace();
            }
    	}
        

        
        // parser.show();
    }

}
