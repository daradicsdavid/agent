package application.util;

import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {

    public static int generatePort(Integer lowerPortBundary, Integer upperPortBundary, Integer exceptionPort) {
        int random = getRandom(lowerPortBundary, upperPortBundary);
        while (random == exceptionPort) {
            random = getRandom(lowerPortBundary, upperPortBundary);
        }
        return random;
    }

    public static int generatePort(Integer lowerPortBundary, Integer upperPortBundary) {
        return generatePort(lowerPortBundary, upperPortBundary, lowerPortBundary - 1);
    }

    public static int getRandom(int lowerBound, int upperBound) {
        return ThreadLocalRandom.current().nextInt(lowerBound, upperBound + 1);
    }
}
