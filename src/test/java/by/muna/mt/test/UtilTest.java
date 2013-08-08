package by.muna.mt.test;

import by.muna.util.BytesUtil;
import by.muna.util.LongUtil;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

public class UtilTest {
    @Test
    public void longUtilLE() {
        byte[] x0000000000000001L = new byte[] {1, 0, 0, 0, 0, 0, 0, 0};

        Assert.assertEquals(
            0x0000000000000001L,
            LongUtil.asLE(x0000000000000001L)
        );

        byte[] x0000000000010101L = new byte[] {1, 1, 1, 0, 0, 0, 0, 0};

        Assert.assertEquals(
            0x0000000000010101L,
            LongUtil.asLE(x0000000000010101L)
        );

        byte[] x0100000000000000L = new byte[] {0, 0, 0, 0, 0, 0, 0, 1};

        Assert.assertEquals(
            0x0100000000000000L,
            LongUtil.asLE(x0100000000000000L)
        );
    }

    @Test
    public void bytesFromHex() {
        Assert.assertArrayEquals(
            new byte[] {0x01, 0x21, (byte) 0xff, 0x7c},
            BytesUtil.fromHex("0121ff7c")
        );

        Assert.assertArrayEquals(
            new byte[] { (byte) 0x92 },
            BytesUtil.fromHex("92")
        );
    }

    @Test
    public void bytesBigIntegerAsBE() {
        Assert.assertArrayEquals(
            new byte[] { (byte) 255 },
            BytesUtil.asBE(new BigInteger("255"))
        );

        Assert.assertArrayEquals(
            new byte[] { 0, 0, 0, (byte) 255 },
            BytesUtil.asBE(new BigInteger("255"), 4)
        );
    }
}
