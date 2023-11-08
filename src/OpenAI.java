import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class OpenAI {
    private static final String apiKey = System.getenv("OPENAI_API_KEY");
    public static String chatGPT(String prompt, List<Pair<String, String>> messages) throws IOException {
        String url = "https://api.openai.com/v1/chat/completions";
        String model = "gpt-4";

        try {
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setRequestProperty("Content-Type", "application/json");

            String systemPrompt = "You are playing a Turing Test game and you are trying to convince the judge that you are the human. You are also trying to convince the judge that the other player is the machine. Respond with very short txt messages. You don't know too many facts. Don't cite facts. Don't use punctuation, it's a txt message.";

            String messageJSON = "[{\"role\": \"system\", \"content\": \"" + systemPrompt + "\"},";
            for(Pair<?,?> p : messages) {
                messageJSON += "{\"role\": \"" + p.getFirst() + "\", \"content\": \"" + p.getSecond() + "\"},";
            }
            messageJSON += "{\"role\": \"user\", \"content\": \"" + prompt + "\"}]";

            // The request body
            String body = "{\"model\": \"" + model + "\", \"messages\": " + messageJSON + ", \"max_tokens\": 100, \"temperature\": 0.9, \"top_p\": 1, \"frequency_penalty\": 0, \"presence_penalty\": 0.6}";
            System.out.println(body);
            connection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(body);
            writer.flush();
            writer.close();

            // Response from ChatGPT
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;

            StringBuilder response = new StringBuilder();

            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readTree(response.toString());
            System.out.println(node);
            System.out.println(node.get("choices").get(0).get("message").get("content").asText());
            return node.get("choices").get(0).get("message").get("content").asText();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
