package advisor.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class RequestPlaylists extends UserRequest {
    private final String categoryName;

    public RequestPlaylists(String categoryName) {
        this.categoryName = categoryName;
    }

    @Override
    public List<List<String>> make(String apiPath, String accessToken) {
        JsonArray categories = getJson(HttpRequest.newBuilder()
                .header("Authorization", " Bearer " + accessToken)
                .uri(URI.create(apiPath + "/v1/browse/categories"))
                .GET().build())
                .getAsJsonObject("categories").getAsJsonArray("items");

        var catMap = StreamSupport.stream(categories.spliterator(), false)
                .map(JsonElement::getAsJsonObject)
                .collect(Collectors.toMap(jo -> jo.get("name").getAsString(), jo -> jo.get("id").getAsString()));

        if (!catMap.containsKey(categoryName)) {
            throw new RequestException("Unknown category name.");
        }

        JsonArray catPlaylists = getJson(HttpRequest.newBuilder()
                .header("Authorization", " Bearer " + accessToken)
                .uri(URI.create(apiPath + "/v1/browse/categories/"
                        + catMap.get(categoryName) + "/playlists"))
                .GET().build())
                .getAsJsonObject("playlists").getAsJsonArray("items");

        return getPlaylists(catPlaylists);
    }
}