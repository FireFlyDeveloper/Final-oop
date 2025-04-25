package service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class PriceService {
    private final String baseUrl = "http://localhost:3000/prices";
    private final String authToken;

    public PriceService(String authToken) {
        this.authToken = authToken;
    }

    // CRUD Operations

    public String getAllPrices() throws IOException {
        return sendRequest(baseUrl, "GET", null);
    }

    public String createPrice(Map<String, Object> priceData) throws IOException {
        String requestBody = convertMapToJson(priceData);
        return sendRequest(baseUrl, "POST", requestBody);
    }

    public String updatePrice(Map<String, Object> priceData) throws IOException {
        String requestBody = convertMapToJson(priceData);
        return sendRequest(baseUrl, "PUT", requestBody);
    }

    public String deletePrice(String priceId) throws IOException {
        return sendRequest(baseUrl + "/" + priceId, "DELETE", null);
    }

    // Helper Methods

    private String sendRequest(String urlString, String method, String requestBody) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Accept", "application/json");
        conn.setRequestProperty("Authorization", "Bearer " + authToken);

        if (requestBody != null && (method.equals("POST") || method.equals("PUT"))) {
            conn.setDoOutput(true);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
        }

        int responseCode = conn.getResponseCode();
        if (responseCode >= HttpURLConnection.HTTP_BAD_REQUEST) {
            try (BufferedReader errorReader = new BufferedReader(
                    new InputStreamReader(conn.getErrorStream()))) {
                StringBuilder errorResponse = new StringBuilder();
                String line;
                while ((line = errorReader.readLine()) != null) {
                    errorResponse.append(line);
                }
                throw new IOException("HTTP Error " + responseCode + ": " + errorResponse.toString());
            }
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        conn.disconnect();

        return response.toString();
    }

    private String convertMapToJson(Map<String, Object> data) {
        // Simple JSON conversion (for production use a proper JSON library like Jackson or Gson)
        StringBuilder json = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (!first) {
                json.append(",");
            }
            json.append("\"").append(entry.getKey()).append("\":");
            if (entry.getValue() instanceof String) {
                json.append("\"").append(entry.getValue()).append("\"");
            } else {
                json.append(entry.getValue());
            }
            first = false;
        }
        json.append("}");
        return json.toString();
    }
}
