package obj.share.sync;

import java.util.concurrent.atomic.AtomicInteger;

public class SafeCounter {
    private volatile AtomicInteger value;

    public SafeCounter(int value) {
        this.value = new AtomicInteger(value);
    }

    public int getAndIncrement() {
        return value.getAndIncrement();
    }
}
