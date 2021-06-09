package atomic;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;

public class Aba {


    public static void main(String[] args) throws Exception {
//        abaProblem();
        stampedRef();
    }

    static AtomicStampedReference<String> stamp = new AtomicStampedReference("A", 0);

    private static void stampedRef() {

    }

    static AtomicReference<String> ref = new AtomicReference<>("A");

    private static void abaProblem() throws Exception {
        String s = ref.get();
        for (int i = 0; i < 10; i++) {
            new Thread(() -> ref.compareAndSet(ref.get(), "A")).start();
            new Thread(() -> ref.compareAndSet(ref.get(), "B")).start();
        }
        TimeUnit.MILLISECONDS.sleep(500);
        System.out.println("A-C: " + ref.compareAndSet(s, "C"));
        System.out.println("ref: " + ref.get());
    }

}
