package io.jvm.jsad;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class RandomTag {
    public final String major;
    private final AtomicLong minor;

    public RandomTag(final String major) {
        this.major = major;
        this.minor = new AtomicLong();
    }

    public RandomTag(final long major) {
        this(String.format("%08X", major));
    }

    public RandomTag() {
        this(random.nextLong());
    }

    @Override
    public String toString() {
        return String.format("%s-%08X", major, minor.incrementAndGet());
    }

// -----------------------------------------------------------------------------

    private static final Random random = new Random();
}
