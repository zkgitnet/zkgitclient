package se.miun.dt133g.zkgitclient.menu;

import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

import java.io.IOException;

public final class MenuTerminal {

    public static final MenuTerminal INSTANCE = new MenuTerminal();

    private Terminal terminal;
    private LineReader lineReader;

    private MenuTerminal() {
        try {
            this.terminal = TerminalBuilder.builder().system(true).build();
            this.lineReader = LineReaderBuilder.builder().terminal(terminal).build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Terminal getTerminal() {
        return terminal;
    }

    public LineReader getLineReader() {
        return lineReader;
    }
}
