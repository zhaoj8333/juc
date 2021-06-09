package juc.aqs;

/**
 * AQS使用state属性标识资源状态，分为独占 与 共享
 *
 *  getState - 获取state状态
 *  setState - 设置state状态
 *
 *  compareAndSetState - CAS机制设置state状态
 *
 *  独占：只有一个线程可以反问资源，
 *  共享：可以允许多个线程访问资源
 *
 *  提供FIFO等待队列，类似于Monitor的EntryList
 *  条件变量拉实现等待，唤醒机制，支持多个变量，类似于Monitor的WaitSet
 *
 *  AQS主要方法：
 *     {@link java.util.concurrent.locks.AbstractQueuedSynchronizer}.tryAcquire(arg)
 *     if (!tryAcquire(arg)) {
 *         // 入队，阻塞当前线程 park
 *     }
 *
 *     {@link java.util.concurrent.locks.AbstractQueuedSynchronizer}.tryRelease(arg)
 *     if (tryRelease()) {
 *         // 阻塞线程恢复运行
 *     }
 *
 *
 *     {@link java.util.concurrent.locks.AbstractQueuedSynchronizer}.tryAcquireShared(arg)
 *
 *     {@link java.util.concurrent.locks.AbstractQueuedSynchronizer}.tryReleaseShared(arg)
 *
 *     {@link java.util.concurrent.locks.AbstractQueuedSynchronizer}.isHeldExclusively()
 */
public class AqsState {
}
