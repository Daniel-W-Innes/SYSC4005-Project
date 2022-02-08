public class Buffer implements Resource {
    private static final int CAPACITY = 2;
    private int space;

    public Buffer() {
        space = CAPACITY;
    }

    @Override
    public synchronized boolean acquire() {
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
