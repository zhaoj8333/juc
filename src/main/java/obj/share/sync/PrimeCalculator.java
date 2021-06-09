package obj.share.sync;

import java.util.ArrayList;

public abstract class PrimeCalculator {

    protected ArrayList<Thread> threads = new ArrayList<>(threadSize);;

    protected static final int threadSize = 10;

    protected static void execTask(int min, int max) {

    }
}
