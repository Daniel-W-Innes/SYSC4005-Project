package controller.resource;

import model.Distinguisher;

public class BufferImpl implements Resource, Buffer {
    public static final int CAPACITY = 2;
    private int space;

    public BufferImpl() {
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

    @Override
    public int remainingSpace() {
        return space;
    }

    @Override
    public int size() {
        return CAPACITY - space;
    }
}
