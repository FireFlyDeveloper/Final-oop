package service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class AuthService {
    private final String baseUrl;
    private final String authToken = "";
    
    public AuthService(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String registerUser(Map<String, Object> userData) throws IOException {
        String requestBody = convertMapToJson(userData);
        return sendRequest(baseUrl + "/register", "POST", requestBody);
    }

    public String loginUser(Map<String, Object> loginData) throws IOException {
        String requestBody = convertMapToJson(loginData);
        return sendRequest(baseUrl + "/login", "POST", requestBody);
    }

    public String getUser(String userId) throws IOException {
        return sendRequest(baseUrl + "/" + userId, "GET", null);
    }

    public String updateUser(String userId, Map<String, Object> userData) throws IOException {
        String requestBody = convertMapToJson(userData);
        return sendRequest(baseUrl + "/" + userId, "PUT", requestBody);
    }

    public String changePassword(String userId, Map<String, Object> passwordData) throws IOException {
        String requestBody = convertMapToJson(passwordData);
        return sendRequest(baseUrl + "/" + userId + "/password", "PUT", requestBody);
    }

    public String deleteUser(String userId) throws IOException {
        return sendRequest(baseUrl + "/" + userId, "DELETE", null);
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
