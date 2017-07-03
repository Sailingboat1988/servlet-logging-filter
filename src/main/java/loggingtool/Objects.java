package loggingtool;

/**
 * Created by pengyu on 2017/7/3.
 */
public class Objects {
    public static <T> T requireNonNull(T obj, String message) {
        if (obj == null)
            throw new NullPointerException(message);
        return obj;
    }
}
