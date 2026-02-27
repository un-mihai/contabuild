package app.service;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Properties;


public class GroqService {

    private static String API_KEY;

    static {
        try (InputStream input = GroqService.class.getResourceAsStream("/application/properties.ini")) {
            Properties properties = new Properties();
            properties.load(input);
            API_KEY = properties.getProperty("ai.api.key");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private static final String API_URL = "https://api.groq.com/openai/v1/chat/completions";


    /**
     * Interogheaza serviciul AI Groq pentru a genera o strategie de marketing scurta.
     * Construieste un prompt personalizat pe baza numelui si pretului produsului.
     *
     * @param numeProdus Numele produsului de promovat.
     * @param pret Pretul de vanzare (cu TVA).
     * @return Textul generat de AI continand descrierea, sloganul si publicul tinta.
     * @throws Exception Daca apelul HTTP esueaza sau API-ul returneaza o eroare.
     */
    public static String cereSfatMarketing(String numeProdus, double pret) throws Exception {
        String promptText = "Am un produs numit '" + numeProdus + "' la pretul de " + pret + " RON. " +
                "Scrie un scurt text de vanzare (maxim 3 fraze), " +
                "un slogan si publicul tinta. Raspunde in romana.";

        JSONObject jsonBody = new JSONObject();

        jsonBody.put("model", "llama-3.3-70b-versatile");

        JSONArray messages = new JSONArray();

        JSONObject systemMsg = new JSONObject();
        systemMsg.put("role", "system");
        systemMsg.put("content", "Esti un asistent de marketing util.");
        messages.put(systemMsg);

        JSONObject userMsg = new JSONObject();
        userMsg.put("role", "user");
        userMsg.put("content", promptText);
        messages.put(userMsg);

        jsonBody.put("messages", messages);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + API_KEY)
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody.toString(), StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            JSONObject jsonResponse = new JSONObject(response.body());
            return jsonResponse.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content");
        } else {
            return "Eroare Groq: " + response.statusCode() + "\n" + response.body();
        }
    }
}