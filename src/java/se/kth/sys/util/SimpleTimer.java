package se.kth.sys.util;

/**
 * A timer.
 * Converting this object to String gives the time since it was created, e.g "4711 ms".
 */
public final class SimpleTimer {

    private static final float PER_SECOND = 0.001f;
    private final long start = System.currentTimeMillis();

    /**
     * @return the number of milliseconds since this object was created.
     */
    public long elapsed() {
        return System.currentTimeMillis() - start;
    }

    public float elapsedSeconds() {
        return elapsed() * PER_SECOND;
    }

    /**
     * @return a string representation of how long ago this object was created.
     */
    public String toString() {
        return elapsed() + " ms";
    }
}
