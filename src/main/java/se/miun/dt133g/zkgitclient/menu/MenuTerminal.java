package se.miun.dt133g.zkgitclient.menu;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

import java.io.IOException;

/**
 * A singleton class responsible for setting up and providing access to the terminal and line reader.
 * @author Leif Rogell
 */
public final class MenuTerminal {

    public static final MenuTerminal INSTANCE = new MenuTerminal();

    private Terminal terminal;
    private LineReader lineReader;

    /**
     * Private constructor that initializes the terminal and line reader.
     * Handles potential IOException when creating the terminal and line reader.
     */
    private MenuTerminal() {
        try {
            this.terminal = TerminalBuilder.builder().system(true).build();
            this.lineReader = LineReaderBuilder.builder().terminal(terminal).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the terminal instance.
     * @return the Terminal object used for terminal operations.
     */
    public Terminal getTerminal() {
        return terminal;
    }

    /**
     * Returns the line reader instance.
     * @return the LineReader object used for reading user input from the terminal.
     */
    public LineReader getLineReader() {
        return lineReader;
    }
}
