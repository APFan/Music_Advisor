package advisor.access;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.ResourceBundle;

public class AccessService {
    static {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("config");
        clientID = resourceBundle.getString("clientID");
        clientSecret = resourceBundle.getString("clientSecret");
    }

    private static final String clientID;
    private static final String clientSecret;

    private final Authorizer authorizer;
    private final String serverPath;

    private AuthCredentials credentials;

    public AccessService(String serverPath, URI redirectURI) {
        this.serverPath = serverPath;
        this.authorizer = new Authorizer(redirectURI, clientID);
    }

    public void requestToken() {
        try {
            String code = authorizer.getCode();

            System.out.println("code received");

            HttpClient client = HttpClient.newBuilder().build();

            HttpRequest request = HttpRequest.newBuilder()
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((clientID + ":" + clientSecret).getBytes(StandardCharsets.UTF_8)))
                    .uri(URI.create(serverPath + "/api/token"))
                    .POST(HttpRequest.BodyPublishers.ofString("grant_type=authorization_code" +
                            "&code=" + code + "&redirect_uri=" + authorizer.getRedirectURI()))
                    .build();

            System.out.println("making http request for access_token...");

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new AccessServiceException("couldn't get token");
            }

            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();

            credentials = new AuthCredentials(json.get("access_token").getAsString(), json.get("refresh_token").getAsString(),
                    Instant.now(), json.get("expires_in").getAsLong());

        } catch (IOException | InterruptedException e) {
            throw new AccessServiceException("exception happened!");
        }
    }

    private void refreshToken() {
        try {
            HttpClient client = HttpClient.newBuilder().build();

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((clientID + ":" + clientSecret).getBytes(StandardCharsets.UTF_8)))
                    .uri(URI.create(serverPath + "/api/token"))
                    .POST(HttpRequest.BodyPublishers.ofString("grant_type=refresh_token&refresh_token="
                            + credentials.getRefreshToken()))
                    .build();

            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new AccessServiceException("bad response code while refreshing token");
            }

            JsonObject jo = JsonParser.parseString(response.body()).getAsJsonObject();

            credentials = new AuthCredentials(jo.get("access_token").getAsString(), credentials.getRefreshToken(),
                    Instant.now(), jo.get("expires_in").getAsLong());

        } catch (IOException | InterruptedException e) {
            throw new AccessServiceException("couldn't refresh the access token");
        }
    }

    public String getToken() {
        if (credentials == null) {
            requestToken();
        }
        if (tokenExpired()) {
            refreshToken();
        }
        return credentials.getAccessToken();
    }

    private boolean tokenExpired() {
        return Instant.now().minusSeconds(credentials.getAuthTime().getEpochSecond()).getEpochSecond() >= credentials.getExpiresIn();
    }
}