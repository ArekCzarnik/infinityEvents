package de.infinity.events.api;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import de.infinity.events.domain.PatchEvent;
import de.infinity.events.utils.KryoUtils;
import io.nats.client.Connection;
import io.nats.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Queue {

    public static final String INFINITY_PATCH_CHANNEL = "infinity.patch";
    public static Logger Log = Logger.getInstance(Queue.class);
    private final Kryo kryo;
    private final ConnectionFactory connectionFactory;
    private Connection connection;

    public Queue() {
        kryo = KryoUtils.kryoThreadLocal.get();
        connectionFactory = new ConnectionFactory("nats://192.168.99.100:32771");
        try {
            connection = connectionFactory.createConnection();
        } catch (IOException | TimeoutException e) {
            Log.error(e);
        }

    }

    public void sendPatchEvent(final PatchEvent patchEvent) {
        try (Output output = new Output(4096 * 4)) {
            kryo.writeObject(output, patchEvent);
            connection.publish(INFINITY_PATCH_CHANNEL, output.toBytes());
        }
    }


}
