package thrd.pool.locality;

/**
 * 由于计算机存储组织架构原因，cpu从内存读取数据时，是 按块 的读取，然后将数据放入三级缓存，然后二级缓存，然后一级缓存
 *
 * 缓存行：
 *     缓存行越大，局部性空间效率越高，但读取时间慢
 *     缓存行越小，局部性空间效率低，单读取时间快
 *
 *     取折中值，目前多用64字节
 *
 * 缓存行：
 *      int
 *     ---- ----  ---- ----  ---- ----  ---- ----  ---- ----  ---- ----  ---- ----  ---- ----
 *
 * 伪共享：
 */
public class Locality {
    public static void main(String[] args) {

        int[] row = new int[16];
        int[] col = new int[16];

        int sum = testByArray(row, col);
        System.out.println(sum);
    }

    /**
     * 长度为16的row和column数组，在Cache Line 64字节数据块上内存地址是连续的，
     * 能被一次加载到Cache Line中，所以在访问数组时，Cache Line命中率高，性能发挥到极致
     * 但是多线程下，多个线程（CPU内核）争抢修改同一个cache line的数据时，MESI发挥作用，程序几乎变成了串行程序，
     * 并发性降低，此时需要将共享在多线程间的数据进行隔离，使他们处于不同的cache line上，提高性能
     */
    private static int testByArray(int[] row, int[] col) {

        int sum = 0;
        for (int i = 0; i < 16; i++) {
            sum += row[i] * col[i];
        }
        return sum;
    }


}
