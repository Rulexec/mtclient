package by.muna.util;

public class LongUtil {
    public static long asLE(byte[] bytes) {
        return LongUtil.asLE(bytes, 0);
    }
    public static long asLE(byte[] bytes, int offset) {
        long result = 0;

        for (int i = 0; i < 8; i++) {
            result += ((bytes[i + offset] & 0xffL) << (i * 8));
        }

        return result;
    }
}
