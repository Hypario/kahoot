package common;

/**
 * Object to be used inside Message object attribute with the type CreateChannel
 */
public class CreateChannel {

    private String channelName;
    private String admin;
    private Quiz quizz;

    public CreateChannel(String channelName, String admin, Quiz quizz) {
        this.channelName = channelName;
        this.admin = admin;
        this.quizz = quizz;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getAdmin() {
        return admin;
    }

    public Quiz getQuizz() {
        return quizz;
    }
}
