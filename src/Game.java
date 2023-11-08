import com.fasterxml.jackson.dataformat.xml.XmlAnnotationIntrospector;

import java.util.ArrayList;
import java.util.List;

public class Game {
    private final String judgePhone;
    private final String playerPhone;
    private final boolean aiIsPlayerA;

    private final List<Pair<String, String>> messages = new ArrayList<>();

    public Game(String judgePhone, String playerPhone) {
        this.judgePhone = judgePhone;
        this.playerPhone = playerPhone;
        // randomly decide if the AI is player A or B
        this.aiIsPlayerA = Math.random() < 0.5;
    }

    public String getJudgePhone() {
        return judgePhone;
    }

    public String getPlayerPhone() {
        return playerPhone;
    }

    public boolean isAiPlayerA() {
        return aiIsPlayerA;
    }

    public void addMessage(String from, String msg) {
        messages.add(new Pair<>(from, msg));
    }

    public List<Pair<String, String>> getMessages() {
        return messages;
    }
}
