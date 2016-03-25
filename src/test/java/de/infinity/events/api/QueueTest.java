package de.infinity.events.api;

import de.infinity.events.domain.PatchEvent;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import rx.Observable;
import rx.schedulers.Schedulers;

public class QueueTest {

    private Queue queue;


    @Before
    public void setup() {
        queue = new Queue("nats://192.168.99.100:32771");
    }

    @Ignore
    @Test
    public void sendPatchEvent() throws Exception {
        queue.sendPatchEvent("infinity.patch", new PatchEvent("id", "test", "md5", "patch"));
    }

    @Ignore
    @Test
    public void relivePatchEvent() throws Exception {
        final Observable<PatchEvent> patchEventObservable = PatchObservable.toObservable("test", queue.getConnection());
        patchEventObservable.subscribeOn(Schedulers.io()).subscribe(patchEvent -> {
            System.out.println(patchEvent.getId());
        });
    }

}