package by.muna.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class BufferUtil {
    public static ByteBuffer allocateLE(int length) {
        ByteBuffer buffer = ByteBuffer.allocate(length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        return buffer;
    }

    public static ByteBuffer wrapLE(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        return buffer;
    }
}
