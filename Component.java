import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

public abstract class Component implements Resource {
    private final AtomicBoolean busy = new AtomicBoolean(false);

    @Override
    public boolean acquire(Distinguisher distinguisher) {
        return busy.compareAndSet(false, true);
    }

    @Override
    public void release() {
        busy.set(false);
    }

    abstract Optional<Event> process(Event event);
}
