package advisor.main;

import com.beust.jcommander.Parameter;

public class Args {
    @Parameter(names = "-access", description = "Path to token request server")
    public String serverPath = "https://accounts.spotify.com";

    @Parameter(names = "-resource", description = "API path")
    public String apiPath = "https://api.spotify.com";

    @Parameter(names = "-page", description = "Page size")
    public Integer pageSize = 5;
}