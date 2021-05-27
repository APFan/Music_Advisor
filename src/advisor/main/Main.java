package advisor.main;

import advisor.controller.AppController;
import com.beust.jcommander.JCommander;

public class Main {
    public static void main(String[] args) {
        Args arguments = new Args();

        JCommander.newBuilder()
                .addObject(arguments)
                .build()
                .parse(args);

        AppController controller = new AppController(arguments.serverPath, arguments.apiPath, arguments.pageSize);
        controller.run();
    }
}