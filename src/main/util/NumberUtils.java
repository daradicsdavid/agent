package main.util;

import main.OutputWriter;

public class NumberUtils {

    public static int toNumber(String string) {
        try {
            int i = Integer.parseInt(string);
            return i;
        } catch (NumberFormatException e) {
            OutputWriter.print("%s nem szám!", string);
            throw e;
        }
    }
}
