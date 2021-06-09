package thrd.pool.locality.disruptor;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import com.lmax.disruptor.util.DaemonThreadFactory;
import thrd.pool.locality.disruptor.consumer.LongEvent;
import thrd.pool.locality.disruptor.consumer.LongEventFactory;
import thrd.pool.locality.disruptor.consumer.LongEventHandler;
import thrd.pool.locality.disruptor.producer.LongEventProducer;
import java.nio.ByteBuffer;

/**
 * Disruptor实现了有界无锁队列，使用了环形数组 {@link com.lmax.disruptor.RingBuffer}
 */
public class DisruptorDemo {

    public static void main(String[] args) throws Exception {
        LongEventFactory factory = new LongEventFactory();
        int buffSize = 1024;
        Disruptor<LongEvent> disruptor = new Disruptor<>(
                factory,
                buffSize,
                DaemonThreadFactory.INSTANCE,
                ProducerType.SINGLE,
                new BlockingWaitStrategy()
        );

        disruptor.handleEventsWith(new LongEventHandler());

        System.out.println();
        System.out.println(disruptor);
        System.out.println();

        disruptor.start();

        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();
        LongEventProducer producer = new LongEventProducer(ringBuffer);
        ByteBuffer bb = ByteBuffer.allocate(8);
        for (int i = 0; i < 100; i++) {
            bb.putLong(0, i);
            producer.onData(bb);
            Thread.sleep(1000L);
        }

        disruptor.shutdown();
    }
}
