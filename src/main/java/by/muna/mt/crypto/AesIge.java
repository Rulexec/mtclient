package by.muna.mt.crypto;

import by.muna.util.BytesUtil;
import org.spongycastle.crypto.BlockCipher;
import org.spongycastle.crypto.engines.AESEngine;
import org.spongycastle.crypto.params.KeyParameter;

public class AesIge {
    private byte[] iv_1, iv_2;

    private BlockCipher cipherEncrypt;
    private BlockCipher cipherDecrypt;

    public AesIge(byte[] key, byte[] iv) {
        this(key, BytesUtil.slice(iv, 0, 16), BytesUtil.slice(iv, 16, 16));
    }
    public AesIge(byte[] key, byte[] iv_1, byte[] iv_2) {
        this.iv_1 = iv_1;
        this.iv_2 = iv_2;

        this.cipherEncrypt = new AESEngine();
        this.cipherEncrypt.init(true, new KeyParameter(key));

        this.cipherDecrypt = new AESEngine();
        this.cipherDecrypt.init(false, new KeyParameter(key));
    }

    public byte[] encrypt(byte[] data) {
        return this.crypt(data, true);
    }
    public byte[] decrypt(byte[] data) {
        return this.crypt(data, false);
    }

    private byte[] crypt(byte[] data, boolean isEncrypt) {
        byte[] result = new byte[data.length];

        int dataOffset = 0;

        byte[] prevTop;
        byte[] prevBottom;

        if (isEncrypt) {
            prevTop = this.iv_1;
            prevBottom = this.iv_2;
        } else {
            prevTop = this.iv_2;
            prevBottom = this.iv_1;
        }

        byte[] current = new byte[16];

        while (dataOffset < data.length) {
            System.arraycopy(data, dataOffset, current, 0, 16);

            byte[] newBottom = current.clone();

            BytesUtil.xorInPlace(current, prevTop);

            current = this.pureAESCrypt(current, isEncrypt);

            BytesUtil.xorInPlace(current, prevBottom);

            byte[] newTop = current.clone();

            System.arraycopy(current, 0, result, dataOffset, 16);

            prevTop = newTop;
            prevBottom = newBottom;

            dataOffset += 16;
        }

        return result;
    }

    private byte[] pureAESCrypt(byte[] data, boolean isEncrypt) {
        byte[] result = new byte[data.length];

        if (isEncrypt) {
            this.cipherEncrypt.processBlock(data, 0, result, 0);
        } else {
            this.cipherDecrypt.processBlock(data, 0, result, 0);
        }

        return result;
    }
}
