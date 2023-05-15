package processing;

import java.io.IOException;
import java.io.Serializable;
import java.net.SocketException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

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
        ByteBuffer lengthBuffer = ByteBuffer.allocate(LENGTH_FIELD_SIZE);
        readCnt = channel.read(lengthBuffer);
        if (readCnt != LENGTH_FIELD_SIZE) {
            throw new IOException(
                "(1)Cannot read object from channel: " + readCnt + " != " + LENGTH_FIELD_SIZE
            );
        }
        int length = SerializationUtils.deserialize(lengthBuffer.array());
        ByteBuffer objectBuffer = ByteBuffer.allocate(length);
        readCnt = channel.read(objectBuffer);
        if (readCnt != length) {
            throw new IOException (
                "(2)Cannot read object from channel: " + readCnt + " != " + length
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
        int packages = (objectBytes.length + LENGTH_FIELD_SIZE - 1) / LENGTH_FIELD_SIZE;
        int objectBytesLength = objectBytes.length;
        byte[] objectPackagesCount = SerializationUtils.serialize(packages);
        channel.write(ByteBuffer.wrap(objectPackagesCount));
        for (int i = 0; i < packages; i++) {
            byte[] bytePackage = new byte[Math.min(LENGTH_FIELD_SIZE, objectBytesLength - LENGTH_FIELD_SIZE * i)];
            System.arraycopy(objectBytes,LENGTH_FIELD_SIZE * i, bytePackage, 0, bytePackage.length);
            channel.write(ByteBuffer.wrap(bytePackage));
        }
    }


    public static void clientWrite(SocketChannel channel, Serializable object) throws SocketException, IOException {
        byte[] objectBytes = SerializationUtils.serialize(object);
        byte[] objectLengthBytes = SerializationUtils.serialize(objectBytes.length);
        channel.write(ByteBuffer.wrap(ArrayUtils.addAll(objectLengthBytes, objectBytes)));
    }

    public static Serializable clientRead(SocketChannel channel) throws IOException {
        int readCnt;
        byte[] objectBytes = {};
        ByteBuffer packegesBuffer = ByteBuffer.allocate(LENGTH_FIELD_SIZE);
        readCnt = channel.read(packegesBuffer);
        if (readCnt == -1) {
            throw new IOException("Cannot read count of packages from server");
        }
        int packages = SerializationUtils.deserialize(packegesBuffer.array());
        for (int i = 0; i < packages; i++) {
            ByteBuffer packageBuffer = ByteBuffer.allocate(LENGTH_FIELD_SIZE);
            readCnt = channel.read(packageBuffer);
            if (readCnt == -1) {
                throw new IOException(String.format("Cannot read package %s from server", i));
            }
            objectBytes = ArrayUtils.addAll(objectBytes, packageBuffer.array());
        }
        Serializable object = SerializationUtils.deserialize(objectBytes);
        return object;
    }
    
    
}
