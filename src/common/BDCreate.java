package common;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class BDCreate {

    private String host = "localhost", username = "root", password = "root", dbname = "kahoot", driver = "mysql", port = "3306";
    private Connection conn;

    private static BDCreate INSTANCE;

    private static final int loginTimeout = 10;

    private BDCreate() {
        Properties props = new Properties();

        try {
            props.load(new FileInputStream("./.env"));

            host = props.getProperty("host");
            username = props.getProperty("username");
            password = props.getProperty("password");
            dbname = props.getProperty("dbname");
            driver = props.getProperty("driver");
            port = props.getProperty("port");

            DriverManager.setLoginTimeout(loginTimeout);
        } catch (IOException e) {
            System.err.println("Unable to find .env");
        }
    }

    public static BDCreate getInstance()
    {
        if (INSTANCE == null)
            INSTANCE = new BDCreate();
        return INSTANCE;
    }

    public Connection connect() {
        try {
            if (conn == null)
                conn = DriverManager.getConnection("jdbc:" + driver + "://" + host + ":" + port + "/" + dbname, username, password);
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return null;
    }

}
