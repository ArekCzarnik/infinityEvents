package de.infinity.events.api;

public class ReceivePatches {


    public static void main(String[] argv) throws Exception {
        Queue queue = new Queue("192.168.99.100","guest","guest");
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        queue.consume("test.infinity.patch", patchEvent -> System.out.println(patchEvent.getId()));
    }
}
