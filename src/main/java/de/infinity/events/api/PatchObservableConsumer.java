package de.infinity.events.api;

import com.esotericsoftware.kryo.io.Input;
import com.rabbitmq.client.*;
import de.infinity.events.domain.PatchEvent;
import de.infinity.events.utils.KryoUtils;
import rx.Observable;
import rx.Subscriber;

import java.io.IOException;

public class PatchObservableConsumer extends DefaultConsumer {
    private Subscriber<? super PatchEvent> observer;

    public PatchObservableConsumer(final Channel channel) {
        super(channel);
    }

    public Observable<PatchEvent> asObservable() {
        return Observable.create(
                new Observable.OnSubscribe<PatchEvent>() {
                    public void call(Subscriber<? super PatchEvent> observer) {
                        PatchObservableConsumer.this.observer = observer;
                    }
                }
        );
    }

    @Override
    public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
        if (observer != null && !observer.isUnsubscribed()) {
            observer.onNext(KryoUtils.kryoThreadLocal.get().readObject(new Input(body), PatchEvent.class));
        }
    }


    @Override
    public void handleShutdownSignal(String consumerTag, ShutdownSignalException sig) {
        if (observer != null && !observer.isUnsubscribed()) {
            //onCompleted is called only when shutdown initiated by application in our case by flume lifecycle supervisor.
            //We don't care about hard errors:)because they are handled by auto-recoverable Rabbit connection
            if (sig.isInitiatedByApplication()) {
                observer.onCompleted();
            }
        }
    }

}

