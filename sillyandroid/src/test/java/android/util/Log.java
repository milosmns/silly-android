package android.util;

/**
 * A replacement for the Android's {@code Log} class due to Mockito's inability to mock static methods.
 */
public class Log {

    /**
     * Logs a debug message to the console.
     *
     * @param tag A prefix to the message
     * @param msg What to log
     * @return Zero as a successful result
     */
    public static int d(String tag, String msg) {
        System.out.println("[DEBUG] " + tag + ": " + msg);
        return 0;
    }

    /**
     * Logs an info message to the console.
     *
     * @param tag A prefix to the message
     * @param msg What to log
     * @return Zero as a successful result
     */
    public static int i(String tag, String msg) {
        System.out.println("[INFO] " + tag + ": " + msg);
        return 0;
    }

    /**
     * Logs a warning message to the console.
     *
     * @param tag A prefix to the message
     * @param msg What to log
     * @return Zero as a successful result
     */
    public static int w(String tag, String msg) {
        System.out.println("[WARN] " + tag + ": " + msg);
        return 0;
    }

    /**
     * Logs an error message to the console.
     *
     * @param tag A prefix to the message
     * @param msg What to log
     * @return Zero as a successful result
     */
    public static int e(String tag, String msg) {
        System.err.println("[ERROR] " + tag + ": " + msg);
        return 0;
    }

    /**
     * Logs a WTF!? message to the console.
     *
     * @param tag A prefix to the message
     * @param msg What to log
     * @return Zero as a successful result
     */
    public static int wtf(String tag, String msg) {
        System.err.println("[WTF!?] " + tag + ": " + msg);
        return 0;
    }

}
