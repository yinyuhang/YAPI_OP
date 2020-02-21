package com.shark.ctrip;

public abstract class Callable<T> implements Runnable {
    private static final int NEW = 0;
    private static final int COMPLETING = 1;
    private static final int CANCELLED = 2;

    private final Object lock = new Object();
    private int state = NEW;
    private volatile T outcome;
    private Thread runner;

    @Override
    public void run() {
        synchronized (lock) {
            if (runner != null)
                return;
            else
                runner = Thread.currentThread();
        }
        // biz operation
        synchronized (lock) {
            outcome = call();
            lock.notifyAll();
            state = COMPLETING;
        }
    }

    public int getState() {
        return state;
    }

    public T get() throws Exception {
        synchronized (lock) {
            if (outcome == null) {
                lock.wait();
            }
            return outcome;
        }
    }

    public void cancel() {
        synchronized (lock) {
            if (outcome == null && state == COMPLETING) {
                state = CANCELLED;
            }
            if (runner != null && !runner.isInterrupted() && outcome == null)
                runner.interrupt();
        }
    }

    protected abstract T call();
}
