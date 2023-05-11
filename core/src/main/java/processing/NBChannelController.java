package processing;

import java.io.IOException;
import java.io.Serializable;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
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
        // channel.configureBlocking(false);
        ByteBuffer lengthBuffer = ByteBuffer.allocate(LENGTH_FIELD_SIZE);
        readCnt = channel.read(lengthBuffer);
        // System.out.println(Arrays.asList(lengthBuffer));

        // System.out.println("readCnt: " + readCnt);
        if (readCnt != LENGTH_FIELD_SIZE) {
            throw new IOException(
                "(1)Cannot read object from channel: " + readCnt + " != " + LENGTH_FIELD_SIZE
            );
        }
        int length = SerializationUtils.deserialize(lengthBuffer.array());
        // System.out.println("len: " + length);
        ByteBuffer objectBuffer = ByteBuffer.allocate(length);
        // System.out.println("blocking: " + channel.isBlocking());

        readCnt = channel.read(objectBuffer);
        if (readCnt != length) {
            throw new IOException (
                "(2)Cannot read object from channel: " + readCnt + " != " + length
            );
        }

        Serializable object = SerializationUtils.deserialize(objectBuffer.array());
        return object;


        // int readCnt;
        // ByteBuffer objectBuffer = ByteBuffer.allocate(4);
        // readCnt = channel.read(objectBuffer);
        // if (readCnt != LENGTH_FIELD_SIZE) {
        //     throw new IOException(
        //         "(1)Cannot read object from channel: " + readCnt + " != " + LENGTH_FIELD_SIZE
        //     );
        // }
        // Serializable object = SerializationUtils.deserialize(objectBuffer.array());
        // return object;
    }

    /**
     * Writes one object to the channel.
     *
     * @param channel channel to write to
     * @param object object to be written
     * @throws IOException if failed to write to channel
     */
    public static void write(SocketChannel channel, Serializable object) throws SocketException, IOException {
        // byte[] objectBytes = SerializationUtils.serialize(object);
        // byte[] objectLengthBytes = SerializationUtils.serialize(objectBytes.length);
        // System.out.println(objectBytes.length);

        // // ByteBuffer.wrap(objectLengthBytes);
        // channel.write(ByteBuffer.wrap(objectLengthBytes));
        // // Console.println("blocking: " + channel.isBlocking());
        // channel.write(ByteBuffer.wrap(objectBytes));

        
        // byte[] objectBytes = SerializationUtils.serialize(object);
        // channel.write(ByteBuffer.wrap(objectBytes));

        byte[] objectBytes = SerializationUtils.serialize(object);
        byte[] objectLengthBytes = SerializationUtils.serialize(objectBytes.length);
        channel.write(ByteBuffer.wrap(ArrayUtils.addAll(objectLengthBytes, objectBytes)));
    }
    
}
