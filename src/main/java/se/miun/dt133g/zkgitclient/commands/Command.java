package se.miun.dt133g.zkgitclient.commands;

/**
 * Interface representing a command that can be executed.
 * Any command implementation must provide the logic for the execution of the command.
 * @author Leif Rogell
 */
public interface Command {

    /**
     * Executes the command.
     * This method contains the logic to perform the command's action.
     * @return a String representing the result of the command execution.
     */
    String execute();

}
