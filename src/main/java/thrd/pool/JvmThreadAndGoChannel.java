package thrd.pool;

/**
 * java:
 *     每一个java线程都对应一个内核线程，所以java的线程池等使用的线程数量是有限的
 *
 * go: go协程的容量比java线程多得多
 *     go程序启动后，会自动一堆内核线程，go 方式启动一个协程后，会被放入多个队列，队列与一个内核线程对应，这些协程交给内核现场去执行
 */
public class JvmThreadAndGoChannel {
}
