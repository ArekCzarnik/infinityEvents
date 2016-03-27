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
        final String url = System.getenv().get("NATS_URL");
        if (url != null && !url.isEmpty()) {
            queue = new Queue(url);
        } else {
            queue = new Queue("nats://localhost:4222");
        }
    }

    @Test
    public void sendPatchEvent() throws Exception {
        queue.sendPatchEvent("test.infinity.patch", new PatchEvent("vws://testplugin3/src/test", "test", "0cc175b9c0f1b6a831c399e269772661", "@@ -1 +0,0 @@\n" +
                "-a\n"));
    }

    @Test
    public void relivePatchEvent() throws Exception {
        final Observable<PatchEvent> patchEventObservable = PatchObservable.toObservable("test.infinity.patch", queue.getConnection());
        patchEventObservable.subscribeOn(Schedulers.io()).subscribe(patchEvent -> {
            System.out.println(patchEvent.getId());
        });
    }

}