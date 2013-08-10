package by.muna.util;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;

public class BytesUtil {
    public static byte[] asBE(BigInteger bigInteger) {
        byte[] pre = bigInteger.toByteArray();

        int i = 0;
        for (; i < pre.length; i++) {
            if (pre[i] != 0) break;
        }

        if (i == 0) return pre;
        if (i == pre.length) return new byte[] { 0 };

        byte[] result = new byte[pre.length - i];

        System.arraycopy(pre, i, result, 0, result.length);

        return result;
    }
    public static byte[] asBE(BigInteger bigInteger, int bytesCount) {
        byte[] pre = bigInteger.toByteArray();

        return BytesUtil.fit(pre, bytesCount);
    }

    public static byte[] fromHex(String hex) {
        int length = hex.length();

        if (length % 2 == 1) {
            length++;
            hex = '0' + hex;
        }

        byte[] result = new byte[length / 2];

        for (int i = 0; i < length; i += 2) {
            char a = hex.charAt(i);
            char b = hex.charAt(i + 1);

            byte c = (byte) (((a <= '9' ? a - '0' : a - 'a' + 10) << 4) +
                ((b <= '9' ? b - '0' : b - 'a' + 10)));

            result[i / 2] = c;
        }

        return result;
    }

    public static boolean equals(byte[] a, byte[] b) {
        if (a.length != b.length) return false;

        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) return false;
        }

        return true;
    }

    public static byte[] slice(byte[] bytes, int offset, int length) {
        byte[] result = new byte[length];

        System.arraycopy(bytes, offset, result, 0, length);

        return result;
    }

    public static byte[] union(byte[]... arrays) {
        int totalLength = 0;

        for (byte[] array : arrays) {
            totalLength += array.length;
        }

        byte[] result = new byte[totalLength];

        int destOffset = 0;
        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, destOffset, array.length);

            destOffset += array.length;
        }

        return result;
    }

    public static void xorInPlace(byte[] array, byte[] xor) {
        for (int i = 0; i < array.length; i++) {
            array[i] ^= xor[i];
        }
    }

    public static byte[] fit(byte[] bytes, int neededLength) {
        if (bytes.length > neededLength) {
            return BytesUtil.slice(bytes, bytes.length - neededLength, neededLength);
        } else if (bytes.length < neededLength) {
            byte[] result = new byte[neededLength];

            System.arraycopy(bytes, 0, result, neededLength - bytes.length, bytes.length);

            return result;
        } else {
            return bytes;
        }
    }

    public static byte[] toUTF8(String string) {
        try {
            return string.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("UTF-8 not supported.");
        }
    }
}
