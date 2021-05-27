package advisor.view;

import java.util.function.Supplier;

public class AccessMenu implements Menu {
    private boolean granted = false;
    private final Menu menu;
    private final Supplier<Boolean> accessOption;
    private final String accessOptionCommand;

    public AccessMenu(Menu menu, String accessOptionCommand, Supplier<Boolean> accessAction) {
        this.menu = menu;
        this.accessOptionCommand = accessOptionCommand;
        accessOption = accessAction;
    }

    @Override
    public void select(String command) {
        if (granted) {
            menu.select(command);
        } else if (command.equals(accessOptionCommand)) {
            if (accessOption.get()) {
                granted = true;
                System.out.println("Success!");
            }
        } else {
            System.out.println("Please, provide access for application.");
        }
    }
}