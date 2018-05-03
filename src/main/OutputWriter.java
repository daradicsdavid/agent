package main;

public class OutputWriter {

    public static void print(String message, Object... args) {
        System.out.println(String.format(message, args));
    }
}
