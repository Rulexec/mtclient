package by.muna.mt.test;

import by.muna.mt.crypto.PublicKeys;
import by.muna.util.BytesUtil;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

public class PublicKeysTest {
    @Test
    public void fingerprint() {
        byte[] modulusBytes = BytesUtil.fromHex(
            "c150023e2f70db7985ded064759cfecf0af328e69a41daf4d6f01b538135a6f9" +
            "1f8f8b2a0ec9ba9720ce352efcf6c5680ffc424bd634864902de0b4bd6d49f4e" +
            "580230e3ae97d95c8b19442b3c0a10d8f5633fecedd6926a7f6dab0ddb7d457f" +
            "9ea81b8465fcd6fffeed114011df91c059caedaf97625f6c96ecc74725556934" +
            "ef781d866b34f011fce4d835a090196e9a5f0e4449af7eb697ddb9076494ca5f" +
            "81104a305b6dd27665722c46b60e5df680fb16b210607ef217652e60236c255f" +
            "6a28315f4083a96791d7214bf64c1df4fd0db1944fb26a2a57031b32eee64ad1" +
            "5a8ba68885cde74a5bfc920f6abf59ba5c75506373e7130f9042da922179251f"
        );
        byte[] exponentBytes = BytesUtil.fromHex("010001");

        BigInteger modulus = new BigInteger(1, modulusBytes);
        BigInteger exponent = new BigInteger(1, exponentBytes);

        Assert.assertEquals(
            0xc3b42b026ce86b21L,
            PublicKeys.calcPublicKeyFingerprint(modulus, exponent)
        );
    }
}
