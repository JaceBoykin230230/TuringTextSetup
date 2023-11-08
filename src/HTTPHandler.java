import org.eclipse.jetty.server.handler.AbstractHandler;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class HTTPHandler extends AbstractHandler {
    Queue<String> judgeQueue = new LinkedList<String>();
    Queue<String> playerQueue = new LinkedList<String>();
    Map<String, Game> games = new HashMap<String, Game>();
    @Override
    public void handle(String target, org.eclipse.jetty.server.Request baseRequest, javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws java.io.IOException, javax.servlet.ServletException {
        // make sure it's a post request

        if (!request.getMethod().equals("POST")) {
            response.setContentType("text/html;charset=utf-8");
            response.setStatus(javax.servlet.http.HttpServletResponse.SC_OK);
            baseRequest.setHandled(true);
            response.getWriter().println("Wrong request method");
            return;
        }

        String from = request.getParameter("From");
        String body = request.getParameter("Body");
        System.out.println("From: " + from);
        System.out.println("Body: " + body);

        response.setContentType("text/html;charset=utf-8");
        response.setStatus(javax.servlet.http.HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);

        if(body.equalsIgnoreCase("play")) {
            SMSSender.sendSMS("Text back 'judge' or 'player'.", from);
        } else if(body.equalsIgnoreCase("player")) {
            if(judgeQueue.contains(from)) {
                SMSSender.sendSMS("You are already in the judge queue.", from);
            } else if(!judgeQueue.isEmpty()) {
                Game g = new Game(judgeQueue.poll(), from);
                games.put(from, g);
                games.put(g.getJudgePhone(), g);
                SMSSender.sendSMS("You have been paired with a judge. You will be texted in the future when the judge asks a question.", from);
                SMSSender.sendSMS("You have been paired with a player. Ask your first question. Indicate A or B at the start of the question, e.g., 'A who are you?'. When ready, type 'guess'.", g.getJudgePhone());
            } else {
                if(playerQueue.contains(from)) {
                    SMSSender.sendSMS("You are already in the player queue.", from);
                } else {
                    playerQueue.add(from);
                    SMSSender.sendSMS("You are now in the player queue. You will be paired with a judge when one becomes available.", from);
                }
            }
        } else if(body.equalsIgnoreCase("judge")) {
            if(playerQueue.contains(from)) {
                SMSSender.sendSMS("You are already in the player queue.", from);
            } else if(!playerQueue.isEmpty()) {
                Game g = new Game(from, playerQueue.poll());
                games.put(from, g);
                games.put(g.getPlayerPhone(), g);
                SMSSender.sendSMS("You have been paired with a player. Ask your first question. Indicate A or B at the start of the question.", from);
                SMSSender.sendSMS("You have been paired with a judge. You will be texted in the future when the judge asks a question.", g.getPlayerPhone());
            } else {
                if(judgeQueue.contains(from)) {
                    SMSSender.sendSMS("You are already in the judge queue.", from);
                } else {
                    judgeQueue.add(from);
                    SMSSender.sendSMS("You are now in the judge queue. You will be paired with a player when one becomes available.", from);
                }
            }
        } else {
            Game g = games.get(from);
            if(g == null) {
                SMSSender.sendSMS("You are not in a game. Text 'play' to start a game.", from);
            } else {
                if(g.getJudgePhone().equals(from)) {
                    if(body.equalsIgnoreCase("guess")) {
                        SMSSender.sendSMS("Text back 'AI is a' or 'AI is b'.", from);
                    } else if(body.equalsIgnoreCase("AI is a")) {
                        if(g.isAiPlayerA()) {
                            SMSSender.sendSMS("You are correct! The AI is player A.", from);
                        } else {
                            SMSSender.sendSMS("You are incorrect! The AI is player B.", from);
                        }
                        games.remove(from);
                        games.remove(g.getPlayerPhone());
                        SMSSender.sendSMS("The judge has left the game.", g.getPlayerPhone());
                    } else if(body.equalsIgnoreCase("AI is b")) {
                        if (g.isAiPlayerA()) {
                            SMSSender.sendSMS("You are incorrect! The AI is player A.", from);
                        } else {
                            SMSSender.sendSMS("You are correct! The AI is player B.", from);
                        }
                        games.remove(from);
                        games.remove(g.getPlayerPhone());
                        SMSSender.sendSMS("The judge has left the game.", g.getPlayerPhone());
                    } else if((body.toLowerCase().startsWith("a") && g.isAiPlayerA()) ||
                            (body.toLowerCase().startsWith("b") && !g.isAiPlayerA())) {
                        String txt = OpenAI.chatGPT(body.substring(1), g.getMessages());
                        g.addMessage("judge", body);
                        g.addMessage("ai", txt);
                        SMSSender.sendSMS("Player " + body.charAt(0) + " said: " + txt, from);
                    } else {
                        SMSSender.sendSMS("Judge asks: " + body, g.getPlayerPhone());
                    }
                } else {
                    // player texted back an answer to the judges question
                    SMSSender.sendSMS("Player " + (g.isAiPlayerA() ? "b" : "a") + " said: " + body, g.getJudgePhone());
                }
            }
        }
    }
}
