package obj.share.sync;

import java.util.concurrent.atomic.AtomicInteger;

public class UnSafeCounter {
    private volatile Integer value;

    public UnSafeCounter(int value) {
        this.value = value;
    }

    public int getAndIncrement() {
        return value++;
    }
}
