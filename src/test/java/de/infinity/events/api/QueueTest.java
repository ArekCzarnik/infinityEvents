package de.infinity.events.api;

import com.rabbitmq.client.Channel;
import de.infinity.events.domain.CreateFile;
import de.infinity.events.domain.PatchEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class QueueTest {

    private Queue queue;
    private Channel channel;

    @Before
    public void setup() {
        final String url = System.getenv().get("QUEUE_URL");
        if (url != null && !url.isEmpty()) {
            queue = new Queue(url,"guest","guest");
        } else {
            queue = new Queue("localhost","guest","guest");
        }
        channel = queue.getChannel();
    }

    @After
    public void teardown() throws IOException, TimeoutException {
        queue.close();
    }

    @Test
    public void sendPatchEvent() throws Exception {
        queue.sendPatchEvent("test.infinity.patch", new PatchEvent("vws://testplugin3/src/test", "clientId", "test", "0cc175b9c0f1b6a831c399e269772661", "@@ -1 +0,0 @@\n" + "-a\n"));
    }

    @Test
    public void sendCreateFile() throws Exception {
        queue.sendCreateFileEvent("test.infinity.patch", new CreateFile("vws://testplugin3/src/test", "this is the content", "/src/test", "0cc175b9c0f1b6a831c399e269772661", "UTF-8"));
    }

    @Test
    public void receivePatchEvent() throws Exception {
        queue.consume("test.infinity.patch",patchEvent -> {
            System.out.println(patchEvent.getId());
        });
    }

}