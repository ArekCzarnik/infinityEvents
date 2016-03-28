package de.infinity.events.api;

import com.rabbitmq.client.Channel;
import de.infinity.events.domain.PatchEvent;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;

public class QueueTest {

    private Queue queue;
    private Channel channel;

    @Before
    public void setup() {
        final String url = System.getenv().get("QUEUE_URL");
        if (url != null && !url.isEmpty()) {
            queue = new Queue(url);
        } else {
            queue = new Queue("192.168.99.100");
        }
        channel = queue.getChannel();
    }

    @Test
    public void sendPatchEvent() throws Exception {
        queue.sendPatchEvent("test.infinity.patch", new PatchEvent("vws://testplugin3/src/test", "test", "0cc175b9c0f1b6a831c399e269772661", "@@ -1 +0,0 @@\n" + "-a\n"));
        queue.sendPatchEvent("test.infinity.patch", new PatchEvent("vws://testplugin3/src/test", "test", "0cc175b9c0f1b6a831c399e269772661", "@@ -1 +0,0 @@\n" + "-a\n"));
        queue.sendPatchEvent("test.infinity.patch", new PatchEvent("vws://testplugin3/src/test", "test", "0cc175b9c0f1b6a831c399e269772661", "@@ -1 +0,0 @@\n" + "-a\n"));
    }

    @Test
    public void receivePatchEvent() throws Exception {
        final Observable<PatchEvent> patchEventObservable = PatchObservable.toObservable("test.infinity.patch", channel);
        patchEventObservable.subscribe(patchEvent -> {
            System.out.println(patchEvent.getId());
        });
    }

}