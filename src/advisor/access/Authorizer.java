package advisor.access;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Authorizer {
    private final String clientID;
    private final URI redirectURI;
    private final InetSocketAddress serverAddress;

    private String code;

    private volatile boolean receivedRequest = false;

    Authorizer(URI redirectURI, String clientID) {
        this.clientID = clientID;
        this.redirectURI = redirectURI;
        serverAddress = new InetSocketAddress(redirectURI.getHost(), redirectURI.getPort());
    }

    private void authorize() {
        try {
            HttpServer server = HttpServer.create();
            server.bind(serverAddress, 0);

            receivedRequest = false;

            server.createContext("/",
                    (exchange -> {
                        Optional<String> query = Optional.ofNullable(exchange.getRequestURI().getQuery());

                        Map<String, String> params = query.map(Authorizer::getQueryMap).orElse(Collections.emptyMap());

                        if (!params.containsKey("code")) {
                            String response = "Authorization code not found. Try again.";
                            exchange.sendResponseHeaders(400, response.length());
                            exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
                            exchange.getResponseBody().close();
                        } else {
                            String response = "Got the code. Return back to your program.";
                            exchange.sendResponseHeaders(200, response.length());
                            exchange.getResponseBody().write(response.getBytes(StandardCharsets.UTF_8));
                            exchange.getResponseBody().close();

                            code = params.get("code");
                        }

                        receivedRequest = true;
                    }));

            server.start();

            URI authURI = URI.create("https://accounts.spotify.com/authorize?client_id="
                    + clientID + "&redirect_uri=" + redirectURI + "&response_type=code");
            System.out.println("use this link to request access code:");
            System.out.println(authURI);

            long startTime = System.currentTimeMillis();

            while (!receivedRequest) {
                if (System.currentTimeMillis() - startTime >= 5000) {
                    server.stop(1);
                    throw new AccessServiceException("timeout error");
                }
            }

            server.stop(1);

            if (code == null) {
                throw new AccessServiceException("couldn't receive code");
            }
        } catch (IOException e) {
            throw new AccessServiceException("couldn't receive code");
        }
    }

    public String getCode() {
        if (code == null) {
            authorize();
        }
        return code;
    }

    private static Map<String, String> getQueryMap(String query) {
        return Arrays.stream(query.split("&"))
                .collect(Collectors.toMap(param -> param.split("=")[0], param -> param.split("=")[1]));
    }

    public URI getRedirectURI() {
        return redirectURI;
    }
}