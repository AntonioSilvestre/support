package it.agevoluzione.tools.android.utils;

public class Locker<E> {
    private E payload;
    private final Object locker;
    private int waiterCount;

    public boolean isLock() {
        synchronized (this) {
            return 0 < waiterCount;
        }
    }

    public int getWaiterCount() {
        synchronized (this) {
            return waiterCount;
        }
    }

    public Locker() {
        locker = new Object();
    }

    public void setPayload(E payload) {
        synchronized (locker) {
            this.payload = payload;
        }
    }

    public E getPayload() {
        synchronized (locker) {
            return payload;
        }
    }

    public void lock() {
        lock(null);
    }

    public void lock(Long millis) {
        synchronized (this) {
            waiterCount++;
        }
        synchronized (locker) {
            try {
                if (null == millis) {
                    locker.wait();
                } else {
                    locker.wait(millis);
                }
            } catch (InterruptedException ignored) {}
        }
        synchronized (this) {
            waiterCount--;
        }
    }

    public void unlock() {
        synchronized (locker) {
            locker.notify();
        }
    }

    public void unlockAll() {
        synchronized (locker) {
            locker.notifyAll();
        }
    }

}