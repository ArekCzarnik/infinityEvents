package de.infinity.events.api;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;
import de.infinity.events.domain.CreateFile;
import de.infinity.events.domain.PatchEvent;
import de.infinity.events.utils.KryoUtils;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class Queue {

    private static final int DEFAULT_BUFFER = 1024 * 100;

    private static final Logger LOG = LoggerFactory.getLogger(Queue.class);
    private final Kryo kryo;
    private final ConnectionFactory connectionFactory;
    private Connection connection;
    private Channel channel;

    public Queue(final String url, final String username, final String password) {
        kryo = KryoUtils.kryoThreadLocal.get();
        connectionFactory = new ConnectionFactory();
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setAutomaticRecoveryEnabled(true);
        // Timeout for connection establishment: 5s
        connectionFactory.setConnectionTimeout( 5000 );

// Configure automatic reconnections
        connectionFactory.setAutomaticRecoveryEnabled( true );

// Recovery interval: 10s
        connectionFactory.setNetworkRecoveryInterval( 10000 );

// Exchanges and so on should be redeclared if necessary
        connectionFactory.setTopologyRecoveryEnabled( true );
        connectionFactory.setHost(url);
        try {
            connection = connectionFactory.newConnection();
            channel = connection.createChannel();
        } catch (IOException | TimeoutException e) {
            LOG.error("queue connection error:", e);
        }

    }

    public void sendPatchEvent(final String topic, final PatchEvent patchEvent) {
        try (Output output = new Output(new ByteArrayOutputStream(), DEFAULT_BUFFER)) {
            kryo.writeObject(output, patchEvent);
            sendEvent(topic, output.toBytes());
        }
    }

    public void sendCreateFileEvent(final String topic, final CreateFile createFile) {
        try (Output output = new Output(new ByteArrayOutputStream(), DEFAULT_BUFFER)) {
            kryo.writeObject(output, createFile);
            sendEvent(topic, output.toBytes());
        }
    }

    private void sendEvent(final String topic, byte[] bytes) {
        try {
            channel.exchangeDeclare(topic, "fanout");
            channel.basicPublish(topic, "", null, bytes);
        } catch (IOException e) {
            LOG.error("error", e);
        }
    }

    public Flowable<PatchEvent> consume(final String topic) throws IOException {
        final String queue = channel.queueDeclare().getQueue();
        channel.exchangeDeclare(topic, "fanout");
        channel.queueBind(queue, topic, "");
        final QueueingConsumer queueingConsumer = new QueueingConsumer(channel);
        channel.basicConsume(queue, true, queueingConsumer);
        return Flowable.create(emitter -> handleEmitter(emitter, queueingConsumer), BackpressureStrategy.BUFFER);
    }

    private void handleEmitter(FlowableEmitter emitter, QueueingConsumer queueingConsumer) {
        try {
            while (true) {
                byte[] body = queueingConsumer.nextDelivery().getBody();
                PatchEvent patchEvent = KryoUtils.kryoThreadLocal.get().readObject(new Input(body), PatchEvent.class);
                emitter.onNext(patchEvent);
            }
        } catch (InterruptedException e) {
            emitter.onComplete();
            LOG.error("error", e);
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
