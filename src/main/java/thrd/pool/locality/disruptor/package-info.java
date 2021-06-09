package thrd.pool.locality.disruptor;

/*
    CAS:
        compare and set: 通过将内存中的值与指定数据进行比较，当数值一样时将内存中的数据替换为新的值

        int cas(long *addr, long old, long new) {
            if(*addr != old) {
                return 0;
            }
            *addr = new;
            return 1;
        }

        public final int getAndAddInt(Object o, long offset, int delta) {
            int v;
            do {
                v = getIntVolatile(o, offset);
            } while (!weakCompareAndSetInt(o, offset, v, v + delta));
            return v;
        }

    cost of CAS:
        CPU must lock its instruction pipeline to ensure atomicity and employ a memory barrier to make the changes visible to other threads

    Memory barrier:
    Cache Line:
        independent but concurrency written variables don't share the same cache-line
        CPUs are able to hide the latency of accessing main memory by predicting which memory is likely to be accessed next
        and pre-fetching it. When iterating an array, the stride is predictable and memory will be pre-fetched in cache lines,
        Strides have to be less than 2048 bytes. but for linked lists and trees, nodes are more distributed in memory to pre-fetch,
        accessing is less efficient;
    Problems of Queues:
        linked-lists, array
        Unbounded queues can exhaust the memory
        Queue has write contention on the head, tail and size variables
        large grain locks on the whole queue for put and take operations are simple to implement but is a significant bottleneck
        Queues are significant sources of garbage

    Pipelines and Graphs:
 */

/*
    Disruptor:
        Memory Allocation:

 */