package controller.resource;

import model.Distinguisher;

public class Buffer implements Resource {
    public static final int CAPACITY = 2;
    private int space;

    public Buffer() {
        space = CAPACITY;
    }

    public int getSpace() {
        return space;
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
