package ru.yandex.practicum.tasks.util;

import java.util.Scanner;

/**
 * Utility class.
 * Contains console operations - reading and writing
 */
public class ConsoleHelper {
    private final Scanner scanner = new Scanner(System.in);

    /**
     * Prints a message to the console
     * @param message output message
     */
    public void writeMessage(String message) {
        System.out.println(message);
    }

    /**
     * Reads a line from the console
     * @return user input string
     */
    public String readString() {
        return scanner.nextLine();
    }

    /**
     * Reads a number from the console
     * @return user input string
     */
    public int readInt() {
        return Integer.parseInt(scanner.nextLine());
    }
}