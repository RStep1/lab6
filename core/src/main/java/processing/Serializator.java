package processing;

import org.apache.commons.lang3.SerializationUtils;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class Serializator {
    public static <T extends Serializable> ByteBuffer serialize(T object) {
        byte[] objectBytes = SerializationUtils.serialize(object);
        return ByteBuffer.wrap(objectBytes);
    }

    public static <T extends Serializable> T deserialize(ByteBuffer byteBuffer) {
        T result = SerializationUtils.deserialize(byteBuffer.array());
        byteBuffer.clear();
        return result;
    }
}
