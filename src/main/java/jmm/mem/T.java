package jmm.mem;

public class T {
    /**
     * 执行超过一万次后，jit会发生作用
     */
    public static void main(String[] args) {
        for (int i = 0; i < 100_0000; i++) {
            m();
            n();
        }
    }

    public static volatile int i = 0;

    public static synchronized void m() {

    }

    public static void n() {
        i = 1;
    }
}
