package advisor.model;

import com.google.gson.JsonArray;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.List;

public class RequestFeatured extends UserRequest {
    @Override
    public List<List<String>> make(String apiPath, String accessToken) {
        JsonArray playlists = getJson(HttpRequest.newBuilder()
                .header("Authorization", " Bearer " + accessToken)
                .uri(URI.create(apiPath + "/v1/browse/featured-playlists"))
                .GET().build())
                .getAsJsonObject("playlists").getAsJsonArray("items");

        return getPlaylists(playlists);
    }
}