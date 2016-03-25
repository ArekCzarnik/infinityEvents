package de.infinity.events.api;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import de.infinity.events.domain.PatchEvent;
import de.infinity.events.utils.KryoUtils;
import io.nats.client.Connection;
import io.nats.client.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class Queue {

    private static final Logger LOG = LoggerFactory.getLogger(Queue.class);
    private final Kryo kryo;
    private final ConnectionFactory connectionFactory;
    private Connection connection;

    public Queue(final String url) {
        kryo = KryoUtils.kryoThreadLocal.get();
        connectionFactory = new ConnectionFactory(url);
        try {
            connection = connectionFactory.createConnection();
        } catch (IOException | TimeoutException e) {
            LOG.error("queue connection error:", e);
        }

    }

    public void sendPatchEvent(final String channel, final PatchEvent patchEvent) {
        try (Output output = new Output(4096 * 4)) {
            kryo.writeObject(output, patchEvent);
            connection.publish(channel, output.toBytes());
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
