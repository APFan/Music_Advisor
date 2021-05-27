package advisor.model;

import advisor.access.AccessServiceException;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public abstract class UserRequest {
    public abstract List<List<String>> make(String apiPath, String accessToken);

    protected static JsonObject getJson(HttpRequest request) {
        try {
            HttpClient client = HttpClient.newBuilder().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            var jo = JsonParser.parseString(response.body()).getAsJsonObject();

            if (jo.has("error")) {
                throw new AccessServiceException(jo.getAsJsonObject("error").get("message").getAsString());
            }

            if (response.statusCode() != 200) {
                throw new AccessServiceException("unknown error with your request");
            }

            return jo;
        } catch (IOException | InterruptedException e) {
            throw new AccessServiceException("couldn't send your request");
        }
    }

    protected static List<List<String>> getPlaylists(JsonArray playlists) {
        List<List<String>> retList = new ArrayList<>();

        for (var playlist: playlists) {
            retList.add(List.of(playlist.getAsJsonObject().get("name").getAsString(),
                    playlist.getAsJsonObject().getAsJsonObject("external_urls").get("spotify").getAsString()));
        }

        return retList;
    }
}