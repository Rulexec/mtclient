package by.muna.mt.crypto;

import by.muna.mt.by.muna.mt.keys.MTAuthKey;
import by.muna.util.BytesUtil;

public class Encryption {
    public static byte[] encrypt(MTAuthKey authKey, byte[] messageKey, byte[] data) {
        AesIge aes = Encryption.createAesIge(authKey, messageKey, true);

        return aes.encrypt(data);
    }
    public static byte[] decrypt(MTAuthKey authKey, byte[] messageKey, byte[] data) {
        AesIge aes = Encryption.createAesIge(authKey, messageKey, false);

        return aes.decrypt(data);
    }

    public static byte[] calcMessageKey(byte[] serialized, int innerDataLength) {
        return BytesUtil.slice(Hashes.SHA1(serialized, 0, innerDataLength), 4, 16);
    }

    public static AesIge createAesIge(MTAuthKey authKey, byte[] messageKey, boolean toServer) {
        byte[] authKeyBytes = authKey.getBytes();

        int x = toServer ? 0 : 8;

        byte[] sha1_a = Hashes.SHA1(messageKey, BytesUtil.slice(authKeyBytes, x, 32));

        byte[] sha1_b = Hashes.SHA1(
            BytesUtil.slice(authKeyBytes, 32 + x, 16),
            messageKey,
            BytesUtil.slice(authKeyBytes, 48 + x, 16)
        );

        byte[] sha1_c = Hashes.SHA1(BytesUtil.slice(authKeyBytes, 64 + x, 32), messageKey);

        byte[] sha1_d = Hashes.SHA1(messageKey, BytesUtil.slice(authKeyBytes, 96 + x, 32));

        byte[] aesKey = BytesUtil.union(
            BytesUtil.slice(sha1_a, 0, 8),
            BytesUtil.slice(sha1_b, 8, 12),
            BytesUtil.slice(sha1_c, 4, 12)
        );

        byte[] aesIV = BytesUtil.union(
            BytesUtil.slice(sha1_a, 8, 12),
            BytesUtil.slice(sha1_b, 0, 8),
            BytesUtil.slice(sha1_c, 16, 4),
            BytesUtil.slice(sha1_d, 0, 8)
        );

        return new AesIge(aesKey, aesIV);
    }
}
