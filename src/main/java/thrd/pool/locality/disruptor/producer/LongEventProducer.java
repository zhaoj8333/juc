package thrd.pool.locality.disruptor.producer;

import com.lmax.disruptor.RingBuffer;
import thrd.pool.locality.disruptor.consumer.LongEvent;

import java.nio.ByteBuffer;

public class LongEventProducer {
    private final RingBuffer<LongEvent> ringBuffer;

    public LongEventProducer(RingBuffer<LongEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void onData(ByteBuffer bb) {
        long seq = ringBuffer.next();
        try {
            LongEvent event = ringBuffer.get(seq);
            event.setValue(bb.getLong(0));
        } finally {
            ringBuffer.publish(seq);
        }
    }

}
