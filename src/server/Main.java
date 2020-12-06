package server;

public class Main {

    public static void main(String[] args) {
        Server server = new Server(50000);
        server.run();
    }

}
