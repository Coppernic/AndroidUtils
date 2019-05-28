package fr.coppernic.lib.test.utils;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.awaitility.Awaitility.await;

public class TestUtils {

    private AtomicBoolean unblock = new AtomicBoolean(false);

    public void unblock() {
        unblock.set(true);
    }

    public void block() {
        await().untilTrue(unblock);
        unblock.set(false);
    }

    public void block(long t, TimeUnit unit) {
        await().atMost(t, unit).untilTrue(unblock);
        unblock.set(false);
    }

}
