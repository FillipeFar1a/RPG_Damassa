package util;

import java.util.concurrent.ThreadLocalRandom;

public final class Dado {
    private Dado() {}
    public static int rolar(int faces) {
        return ThreadLocalRandom.current().nextInt(1, faces + 1);
    }
    public static int d6() { return rolar(6); }
    public static int d20() { return rolar(20); }
}
