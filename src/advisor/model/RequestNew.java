package advisor.model;

import com.google.gson.JsonArray;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class RequestNew extends UserRequest {

    @Override
    public List<List<String>> make(String apiPath, String accessToken) {
        JsonArray albums = getJson(HttpRequest.newBuilder()
                .header("Authorization", " Bearer " + accessToken)
                .uri(URI.create(apiPath + "/v1/browse/new-releases"))
                .GET().build())
                .getAsJsonObject("albums").getAsJsonArray("items");

        List<List<String>> retList = new ArrayList<>();

        for (var album: albums) {
            List<String> toAdd = new ArrayList<>();
            toAdd.add(album.getAsJsonObject().get("name").getAsString());

            String artists = "[" + StreamSupport.stream(album.getAsJsonObject().getAsJsonArray("artists").spliterator(), false)
                    .map(artist -> artist.getAsJsonObject().get("name").getAsString())
                    .collect(Collectors.joining(", ")) + "]";
            toAdd.add(artists);

            toAdd.add(album.getAsJsonObject().getAsJsonObject("external_urls").get("spotify").getAsString());

            retList.add(toAdd);
        }
        return retList;
    }
}