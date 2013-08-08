package by.muna.mt.crypto;

import org.spongycastle.crypto.digests.SHA1Digest;

public class Hashes {
    public static byte[] SHA1(byte[]... arrays) {
        SHA1Digest digest = new SHA1Digest();

        for (byte[] array : arrays) {
            digest.update(array, 0, array.length);
        }

        byte[] result = new byte[20];

        digest.doFinal(result, 0);

        return result;
    }
    public static byte[] SHA1(byte[] bytes) {
        return Hashes.SHA1(bytes, 0, bytes.length);
    }
    public static byte[] SHA1(byte[] bytes, int offset, int length) {
        SHA1Digest digest = new SHA1Digest();

        byte[] result = new byte[20];

        digest.update(bytes, offset, length);

        digest.doFinal(result, 0);

        return result;
    }
}
