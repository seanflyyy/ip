package froggy.main;


import froggy.command.Command;
import froggy.exception.FroggyException;
import froggy.parser.Parser;
import froggy.storage.Storage;
import froggy.task.TaskList;
import froggy.ui.Ui;
import javafx.application.Platform;


/**
 * The Duke class is the core of the entire program.
 * This is where all the commands are first read.
 */
public class Froggy {

    private Storage storage;
    private TaskList tasks;
    private Ui ui;


    /**
     * Creates a Duke object.
     */
    public Froggy() {
        ui = new Ui();
        storage = new Storage("./data/froggy.txt");
        try {
            tasks = new TaskList(storage.load());
        } catch (FroggyException e) {
            ui.showLoadingError();
            tasks = new TaskList();
        }
    }

    public static void main(String[] args) {
        new Froggy().run();
    }

    /**
     * Executes the program.
     */
    public void run() {
        ui.showWelcome();
        boolean isExit = false;
        while (!isExit) {
            try {
                String fullCommand = ui.readCommand();
                ui.showLine(); // show the divider line ("_______")
                Command c = Parser.parse(fullCommand);
                c.execute(tasks, ui, storage);
                isExit = c.isExit();
            } catch (FroggyException e) {
                ui.showError(e.getMessage());
            } finally {
                ui.showLine();
            }
        }
    }

    /**
     * Returns the response generated from the bot.
     */
    public String getResponse(String input) {
        try {
            Command c = Parser.parse(input);
            String output = c.execute(tasks, ui, storage);
            if (c.isExit()) {
                Thread.sleep(500);
                Platform.exit();
            }
            return output;
        } catch (FroggyException | InterruptedException e) {
            return ui.showError(e.getMessage());
        } finally {
            ui.showLine();
        }
    }

}
