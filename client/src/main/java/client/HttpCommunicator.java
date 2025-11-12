package client;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;

public class HttpCommunicator {
    String baseUrl;
    ServerFacade facade;

    public HttpCommunicator(ServerFacade facade, String serverDomain) {
        baseUrl = "http://" + serverDomain;
        this.facade = facade;
    }

    public boolean register(String username, String password, String email) {
        Map<String, String> body = Map.of("username", username, "password", password, "email", email);
        var jsonBody = new Gson().toJson(body);
        Map response = request("POST", "/user", jsonBody);
        if (response.containsKey("Error")) {
            return false;
        }
        facade.setAuthToken((String) response.get("authToken"));
        return true;
    }

    public boolean login(String username, String password) {
        Map<String, String> body = Map.of("username", username, "password", password);
        var jsonBody = new Gson().toJson(body);
        Map response = request("POST", "/session", jsonBody);
        if (response.containsKey("Error")) {
            return false;
        }
        facade.setAuthToken((String) response.get("authToken"));
        return true;
    }

    public boolean logout() {
        Map response = request("DELETE", "/session");
        if (response.containsKey("Error")) {
            return false;
        }
        facade.setAuthToken(null);
        return true;
    }

    private Map request (String method, String endpoint) {
        return request(method, endpoint, null);
    }

    private Map request(String method, String endpoint, String body) {
        Map responseMap;
        try {
            HttpURLConnection conn = makeConnection(method, endpoint, body);
            try {
                if (conn.getResponseCode() == 401) {
                    return Map.of("Error", 401);
                }
            } catch (IOException e) {
                return Map.of("Error", 401);
            }
            try (InputStream responseBody = conn.getInputStream()) {
                InputStreamReader inputStreamReader = new InputStreamReader(responseBody);
                responseMap = new Gson().fromJson(inputStreamReader, Map.class);
            }
        } catch (URISyntaxException | IOException e) {
            return Map.of("Error", e.getMessage());
        }

        return responseMap;
    }

    private HttpURLConnection makeConnection(String method, String endpoint, String body) throws URISyntaxException, IOException {
        URI uri = new URI(baseUrl + endpoint);
        HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
        conn.setRequestMethod(method);

        if (facade.getAuthToken() != null) {
            conn.addRequestProperty("authorization", facade.getAuthToken());
        }

        if (!Objects.equals(body, null)) {
            conn.setDoOutput(true);
            conn.addRequestProperty("Content-Type", "application/json");
            try (var outputStream = conn.getOutputStream()) {
                outputStream.write(body.getBytes());
            }
        }

        conn.connect();
        return conn;
    }
}
