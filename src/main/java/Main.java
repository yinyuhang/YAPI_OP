import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private static final int COUNT_BITS = Integer.SIZE - 3;
    private static final int CAPACITY = (1 << COUNT_BITS) - 1;

    // runState is stored in the high-order bits
    private static final int RUNNING = -1 << COUNT_BITS;
    private static final AtomicInteger ctl = new AtomicInteger(ctlOf(RUNNING, 0));
    private static final int SHUTDOWN = 0 << COUNT_BITS;
    private static final int STOP = 1 << COUNT_BITS;
    private static final int TIDYING = 2 << COUNT_BITS;
    private static final int TERMINATED = 3 << COUNT_BITS;

    // Packing and unpacking ctl
    private static int runStateOf(int c) {
        return c & ~CAPACITY;
    }

    private static int workerCountOf(int c) {
        return c & CAPACITY;
    }

    private static int ctlOf(int rs, int wc) {
        return rs | wc;
    }

    public static void main(String[] args) throws InterruptedException {
        FutureTask<String> t = new FutureTask<>(() -> {
            System.out.println(Thread.currentThread());
            try {
                Thread.sleep(100_000L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }, UUID.randomUUID().toString());
        Callable<String> c = ()-> "HelloWorld";
        try {
            ThreadPoolExecutor tpe = new ThreadPoolExecutor(2, 4,
                    1000L, TimeUnit.MICROSECONDS, new ArrayBlockingQueue<>(100));
            Future<String> future = tpe.submit(c);
            String o = future.get();
            new Thread(t).start();
            Thread.sleep(1000L);
            t.cancel(true);
            Thread.sleep(1000L);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            try {
                t.get();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    private static void printState() {
        System.out.println(toBinaryString(RUNNING) + " RUNNING  " + RUNNING);
        System.out.println(toBinaryString(SHUTDOWN) + " SHUTDOWN  " + SHUTDOWN);
        System.out.println(toBinaryString(STOP) + " STOP  " + STOP);
        System.out.println(toBinaryString(TIDYING) + " TIDYING  " + TIDYING);
        System.out.println(toBinaryString(TERMINATED) + " TERMINATED  " + TERMINATED);
        System.out.println(toBinaryString(CAPACITY) + " CAPACITY  " + CAPACITY);
        System.out.println(toBinaryString(~CAPACITY) + " OP CAPACITY  " + ~CAPACITY);
        System.out.println(toBinaryString(-9) + " -9  ");
        System.out.println(toBinaryString(-10) + " -10  ");
        System.out.println(toBinaryString(-11) + " -11  ");
        System.out.println(toBinaryString(-12) + " -12  ");
        System.out.println(ctl.get());
        System.out.println(runStateOf(ctl.getAndAdd(1)));
        System.out.println(workerCountOf(ctl.getAndAdd(1)));
        System.out.println(ctlOf(12, 0));
    }

    private static String toBinaryString(int i) {
        String s = Integer.toBinaryString(i);
        while (s.length() < 32) {
            s = "0" + s;
        }
        String r = "";
        for (Integer index : Arrays.asList(0, 4, 8, 16, 20, 24, 28, 32)) {
            if (index + 4 == 36) {
                r = r + s.substring(index) + " ";
            } else
                r = r + s.substring(index, index + 4) + " ";
        }
        return r;
    }
}
