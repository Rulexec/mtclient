package by.muna.util;

import java.util.zip.CRC32;

public class ChecksumUtil {
    public static int CRC32(byte[] bytes, int offset, int length) {
        CRC32 crc32 = new CRC32();

        crc32.update(bytes, offset, length);

        return (int) crc32.getValue();
    }
}
