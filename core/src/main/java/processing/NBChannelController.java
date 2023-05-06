package processing;

import java.io.IOException;
import java.io.Serializable;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.apache.commons.lang3.SerializationUtils;

public class NBChannelController {    
    private static final int LENGTH_FIELD_SIZE = SerializationUtils.serialize(Integer.MAX_VALUE).length;


    /**
     * Reads one object from the channel and returns it.
     *
     * @param channel channel to read from
     * @return object read from the channel
     * @throws IOException if failed to read from channel
     */
    public static Serializable read(SocketChannel channel) throws IOException {
        int readCnt;

        ByteBuffer lengthBuffer = ByteBuffer.allocate(LENGTH_FIELD_SIZE);
        readCnt = channel.read(lengthBuffer);
        if (readCnt != LENGTH_FIELD_SIZE) {
            throw new IOException(
                "Cannot read object from channel: " + readCnt + " != " + LENGTH_FIELD_SIZE
            );
        }

        int length = SerializationUtils.deserialize(lengthBuffer.array());

        ByteBuffer objectBuffer = ByteBuffer.allocate(length);
        readCnt = channel.read(objectBuffer);
        if (readCnt != length) {
            throw new IOException (
                "Cannot read object from channel: " + readCnt + " != " + length
            );
        }

        Serializable object = SerializationUtils.deserialize(objectBuffer.array());
        return object;
    }

    /**
     * Writes one object to the channel.
     *
     * @param channel channel to write to
     * @param object object to be written
     * @throws IOException if failed to write to channel
     */
    public static void write(SocketChannel channel, Serializable object) throws SocketException, IOException {
        byte[] objectBytes = SerializationUtils.serialize(object);
        byte[] objectLengthBytes = SerializationUtils.serialize(objectBytes.length);

        ByteBuffer.wrap(objectLengthBytes);

        channel.write(ByteBuffer.wrap(objectLengthBytes));
        channel.write(ByteBuffer.wrap(objectBytes));
    }
    
}
