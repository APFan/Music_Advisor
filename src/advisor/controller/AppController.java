package advisor.controller;

import advisor.access.AccessService;
import advisor.access.AccessServiceException;
import advisor.model.*;
import advisor.view.AccessMenu;
import advisor.view.Menu;
import advisor.view.Paginator;
import advisor.view.SimpleMenu;

import java.net.URI;
import java.util.Scanner;

public class AppController {
    private final Paginator paginator;
    private final Menu menu;
    private final Session session;

    public AppController(String serverPath, String apiPath, int pageEntries) {
        paginator = new Paginator(pageEntries);

        AccessService service = new AccessService(serverPath, URI.create("http://localhost:8080"));
        session = new Session(apiPath, service);

        menu = new AccessMenu(new SimpleMenu()
                .addOption(() -> {
                    paginator.setContent(session.makeRequest(new RequestNew()));
                    paginator.showContent();
                }, "new")
                .addOption(() -> {
                    paginator.setContent(session.makeRequest(new RequestFeatured()));
                    paginator.showContent();
                }, "featured")
                .addOption(() -> {
                    paginator.setContent(session.makeRequest(new RequestCategories()));
                    paginator.showContent();
                }, "categories")
                .addOption(input -> {
                    paginator.setContent(session.makeRequest(new RequestPlaylists(input)));
                    paginator.showContent();
                }, "playlists")
                .addOption(paginator::next, "next")
                .addOption(paginator::prev, "prev"),
                 "auth",
                () -> {
                    try {
                        service.requestToken();
                        return true;
                    } catch (AccessServiceException e) {
                        System.out.println(e.getMessage());
                        return false;
                    }
                });
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            String input = scanner.nextLine();

            if ("exit".equals(input)) {
                return;
            } else {
                try {
                    menu.select(input);
                } catch (RequestException | AccessServiceException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }
}