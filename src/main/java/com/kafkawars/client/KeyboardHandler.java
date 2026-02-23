package com.kafkawars.client;

import java.io.IOException;

public class KeyboardHandler {

    /**
     * Reads a single character from the terminal without waiting for Enter.
     * This works on most Unix-like systems (Linux, macOS) by putting the terminal in raw mode.
     */
    public static int readKey() {
        try {
            // Put terminal in raw mode
            String[] cmd = {"/bin/sh", "-c", "stty raw -echo </dev/tty"};
            Runtime.getRuntime().exec(cmd).waitFor();

            int ch = System.in.read();

            // Restore terminal to normal mode
            cmd[0] = "/bin/sh";
            cmd[1] = "-c";
            cmd[2] = "stty cooked echo </dev/tty";
            Runtime.getRuntime().exec(cmd).waitFor();

            return ch;
        } catch (Exception e) {
            try {
                return System.in.read();
            } catch (IOException ex) {
                return -1;
            }
        }
    }
}
