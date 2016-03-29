package de.infinity.events.api;

public class ReceivePatches {

    private static final String EXCHANGE_NAME = "infinity.patch.testplugin3";

    public static void main(String[] argv) throws Exception {
        Queue queue = new Queue("192.168.99.100");
        System.out.println(" [*] Waiting for messages. To exit press CTRL+C");
        queue.consume("test.infinity.patch", patchEvent -> System.out.println(patchEvent.getId()));
    }
}
