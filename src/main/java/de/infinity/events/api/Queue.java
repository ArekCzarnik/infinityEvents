package de.infinity.events.api;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import de.infinity.events.domain.CreateFile;
import de.infinity.events.domain.PatchEvent;
import de.infinity.events.utils.KryoUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

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

    public Queue(final String url,final String username,final String password) {
        kryo = KryoUtils.kryoThreadLocal.get();
        connectionFactory = new ConnectionFactory();
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setAutomaticRecoveryEnabled(true);
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
            sendEvent(topic,output.toBytes());
        }
    }

    private void sendEvent(final String topic,byte[] bytes) {
        try {
            channel.exchangeDeclare(topic, "fanout");
            channel.basicPublish(topic, "", null, bytes);
        } catch (IOException e) {
            LOG.error("error", e);
        }
    }

    public Subscription consume(final String topic, final Action1<? super PatchEvent> onNext) throws IOException {
        final String queue = channel.queueDeclare().getQueue();
        channel.exchangeDeclare(topic, "fanout");
        channel.queueBind(queue, topic, "");
        PatchObservableConsumer patchObservableConsumer = new PatchObservableConsumer(channel);
        final Observable<PatchEvent> patchEventObservable = patchObservableConsumer.asObservable();
        channel.basicConsume(queue, true, patchObservableConsumer);
        return patchEventObservable.subscribe(onNext);
    }

    public Channel getChannel() {
        return channel;
    }

    public void close() throws IOException, TimeoutException {
        channel.close();
        connection.close();
    }
}
