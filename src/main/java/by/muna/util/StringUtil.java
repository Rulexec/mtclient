package by.muna.util;

import java.io.UnsupportedEncodingException;

public class StringUtil {
    public static String toHex(long number) {
        return StringUtil.toHex((int) (number >>> 32)) +
            StringUtil.toHex((int) number);
    }

    public static String toHex(int number) {
        return Long.toHexString(number & 0xffffffffL);
    }

    public static String fromUTF8(byte[] utf8) {
        try {
            return new String(utf8, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 not supported.");
        }
    }
}
