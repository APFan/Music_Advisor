package advisor.model;

import advisor.access.AccessService;

import java.util.List;

public class Session {
    private final AccessService accessService;
    private final String apiPath;

    public Session(String apiPath, AccessService accessService) {
        this.apiPath = apiPath;
        this.accessService = accessService;
    }

    public List<List<String>> makeRequest(UserRequest request) {
        return request.make(apiPath, accessService.getToken());
    }
}