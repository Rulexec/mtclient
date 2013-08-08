package by.muna.mt.by.muna.mt.keys;

import by.muna.mt.crypto.Hashes;
import by.muna.util.BytesUtil;
import by.muna.util.LongUtil;

public class MTAuthKey {
    private byte[] authKey;
    private long authKeyId;

    public MTAuthKey(byte[] authKey) {
        this.authKey = authKey;

        this.authKeyId = MTAuthKey.calcAuthKeyId(this.authKey);
    }

    public long getAuthKeyId() {
        return this.authKeyId;
    }

    public byte[] getBytes() {
        return this.authKey;
    }

    public static long calcAuthKeyId(byte[] authKey) {
        byte[] sha1 = BytesUtil.slice(Hashes.SHA1(authKey), 12, 8);
        return LongUtil.asLE(sha1);
    }
}
