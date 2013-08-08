package by.muna.util;

public class StringUtil {
    public static String toHex(long number) {
        return StringUtil.toHex((int) (number >>> 32)) +
            StringUtil.toHex((int) number);
    }

    public static String toHex(int number) {
        return Long.toHexString(number & 0xffffffffL);
    }
}
