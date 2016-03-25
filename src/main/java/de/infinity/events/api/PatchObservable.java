package de.infinity.events.api;

import com.esotericsoftware.kryo.io.Input;
import de.infinity.events.domain.PatchEvent;
import de.infinity.events.utils.KryoUtils;
import io.nats.client.Connection;
import rx.Observable;

public class PatchObservable {

    public static Observable<PatchEvent> toObservable(final String channel, final Connection connection) {
        return Observable.create(subscriber -> {
            try {
                connection.subscribeAsync(channel, message -> {
                    final PatchEvent patch = KryoUtils.kryoThreadLocal.get().readObject(new Input(message.getData()), PatchEvent.class);
                    subscriber.onNext(patch);
                    subscriber.onCompleted();
                });
            }catch (Exception e) {
                subscriber.onError(e);
            }
        });
    }
}
