package controller.resource;

import model.Distinguisher;

public class Buffer implements Resource {
    private static final int CAPACITY = 2;
    public int space;

    public Buffer() {
        space = CAPACITY;
    }

    @Override
    public synchronized boolean acquire(Distinguisher distinguisher) {
        if (space == 0) {
            return false;
        } else {
            space--;
            return true;
        }
    }

    @Override
    public synchronized void release() {
        if (space < CAPACITY) {
            space++;
        }
    }
}
