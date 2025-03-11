package kz.medet.productservice.util;

import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

public class MixedSerializer implements Serializer<Object> {

    private final JsonSerializer<Object> jsonSerializer = new JsonSerializer<>();
    private final StringSerializer stringSerializer = new StringSerializer();

    @Override
    public byte[] serialize(String topic, Object data) {
        if (data instanceof String) {
            return stringSerializer.serialize(topic, (String) data);
        }
        return jsonSerializer.serialize(topic, data);
    }
}

