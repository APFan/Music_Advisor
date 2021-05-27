package advisor.view;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SimpleMenu implements Menu {
    private final Map<String, Consumer<String>> argOptions = new HashMap<>();
    private final Map<String, Runnable> options = new HashMap<>();

    public SimpleMenu addOption(Consumer<String> option, String command) {
        argOptions.put(command, option);
        return this;
    }

    public SimpleMenu addOption(Runnable option, String command) {
        options.put(command, option);
        return this;
    }

    @Override
    public void select(String command) {
        if (options.containsKey(command)) {
            options.get(command).run();
        } else if (argOptions.containsKey(command.split("\\s+")[0])) {
            String argument = command.contains(" ") ? command.substring(command.indexOf(' '))
                    .stripLeading() : "";

            argOptions.get(command.split("\\s+")[0]).accept(argument);
        } else {
            System.out.println("Invalid command!");
        }
    }
}