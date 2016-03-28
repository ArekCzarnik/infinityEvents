package de.infinity.events.api;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import de.infinity.events.domain.PatchEvent;
import de.infinity.events.utils.KryoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class Queue {

    private static final Logger LOG = LoggerFactory.getLogger(Queue.class);
    private final Kryo kryo;
    private final ConnectionFactory connectionFactory;
    private Connection connection;
    private Channel channel;

    public Queue(final String url) {
        kryo = KryoUtils.kryoThreadLocal.get();
        connectionFactory = new ConnectionFactory();
        connectionFactory.setHost(url);
        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();
        } catch (IOException | TimeoutException e) {
            LOG.error("queue connection error:", e);
        }

    }

    public void sendPatchEvent(final String topic, final PatchEvent patchEvent) {
        try (Output output = new Output(4096 * 4)) {
            kryo.writeObject(output, patchEvent);
            try {
                channel.exchangeDeclare(topic,"fanout");
                channel.basicPublish(topic, "", null, output.toBytes());
            } catch (IOException e) {
                LOG.error("error",e);
            }
        }
    }

    public Channel getChannel() {
        return channel;
    }

    public void close() throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }
}
