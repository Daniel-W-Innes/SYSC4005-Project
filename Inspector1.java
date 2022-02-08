import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class Inspector1 implements Component{
    private  final Queue<ComponentID> destination;
    private static final Map<ComponentID, ResourceID> buffers = Map.of(ComponentID.WORKSTATION_1,ResourceID.BUFFER_1,  ComponentID.WORKSTATION_2,ResourceID.BUFFER_2,ComponentID.WORKSTATION_3,ResourceID.BUFFER_4);
    private final Random generator;
    private static final int MAX_DELAY = 5;
    private final AtomicBoolean busy;
    private ComponentID nextComponentID;

    public Inspector1( Random generator) {
        this.generator = generator;
        destination =new ArrayDeque<>(3);
        destination.add(ComponentID.WORKSTATION_1);
        destination.add(ComponentID.WORKSTATION_2);
        destination.add(ComponentID.WORKSTATION_3);
        busy = new AtomicBoolean(false);
    }

    @Override
    public Event process(Event event) {
        if (event.eventType() == EventType.ARRIVAL){
            nextComponentID = destination.poll();
            return new Event(event.time()+ generator.nextInt(MAX_DELAY),EventType.DEPARTURE,ComponentID.INSPECTOR_1,Set.of(buffers.get(nextComponentID)),Set.of(ResourceID.INSPECTOR_1));
        }
        Event nextEvent = new Event(event.time()+ generator.nextInt(MAX_DELAY),EventType.ARRIVAL,nextComponentID,Set.of(nextComponentID.getResourceID()),Set.of(buffers.get(nextComponentID)));
        destination.add(nextComponentID);
        return nextEvent;
    }

    @Override
    public synchronized boolean acquire() {
        return busy.compareAndSet(false,true);
    }

    @Override
    public synchronized void release() {
        busy.set(false);
    }
}
