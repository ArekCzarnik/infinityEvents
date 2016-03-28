package de.infinity.events.api;

import com.esotericsoftware.kryo.io.Input;
import com.rabbitmq.client.*;
import de.infinity.events.domain.PatchEvent;
import de.infinity.events.utils.KryoUtils;
import rx.Observable;

import java.io.IOException;

public class PatchObservable {

    public static Observable<PatchEvent> toObservable(final String topic, final Channel channel) {
        return Observable.create(subscriber -> {
            try {
                channel.queueBind(channel.queueDeclare().getQueue(), topic, "");
                Consumer consumer = new DefaultConsumer(channel) {
                    @Override
                    public void handleDelivery(String consumerTag, Envelope envelope,
                                               AMQP.BasicProperties properties, byte[] body) throws IOException {
                        final PatchEvent patch = KryoUtils.kryoThreadLocal.get().readObject(new Input(body), PatchEvent.class);
                        subscriber.onNext(patch);
                    }
                };
                channel.basicConsume(channel.queueDeclare().getQueue(), true, consumer);
            } catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }
}
