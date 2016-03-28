package de.infinity.events.api;
import com.esotericsoftware.kryo.io.Input;
import com.rabbitmq.client.*;
import de.infinity.events.domain.PatchEvent;
import de.infinity.events.utils.KryoUtils;

import java.io.IOException;

public class ReceivePatches {

    private static final String EXCHANGE_NAME = "patch";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("192.168.99.100");
        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(EXCHANGE_NAME, "fanout");
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, "");

        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope,
                                       AMQP.BasicProperties properties, byte[] body) throws IOException {
                final PatchEvent patch = KryoUtils.kryoThreadLocal.get().readObject(new Input(body), PatchEvent.class);
                System.out.println(" [x] Received '" + patch.getId() + "'");
            }
        };
        channel.basicConsume(queueName, true, consumer);
    }
}
