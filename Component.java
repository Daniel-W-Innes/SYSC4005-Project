import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Component implements Resource {
    private final AtomicBoolean busy = new AtomicBoolean(false);

    @Override
    public synchronized boolean acquire() {
        return busy.compareAndSet(false, true);
    }

    @Override
    public synchronized void release() {
        busy.set(false);
    }

    abstract Event process(Event event);
}
