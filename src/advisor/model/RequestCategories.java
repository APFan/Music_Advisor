package advisor.model;

import com.google.gson.JsonArray;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;

public class RequestCategories extends UserRequest {
    @Override
    public List<List<String>> make(String apiPath, String accessToken) {
        JsonArray categories = getJson(HttpRequest.newBuilder()
                .header("Authorization", " Bearer " + accessToken)
                .uri(URI.create(apiPath + "/v1/browse/categories"))
                .GET().build())
                .getAsJsonObject("categories").getAsJsonArray("items");

        List<List<String>> retList = new ArrayList<>();
        categories.forEach(category -> retList.add(List.of(category.getAsJsonObject().get("name").getAsString())));
        return retList;
    }
}