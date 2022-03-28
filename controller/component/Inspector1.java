package controller.component;

import controller.resource.Buffer;
import model.*;

import java.util.*;

public class Inspector1 extends Component {
    private static final Map<ComponentID, ResourceID> BUFFER_IDS = Map.of(ComponentID.WORKSTATION_1, ResourceID.BUFFER_1, ComponentID.WORKSTATION_2, ResourceID.BUFFER_2, ComponentID.WORKSTATION_3, ResourceID.BUFFER_4);
    private static final int MAX_DELAY = 5;
    private final Queue<ComponentID> destination;
    private final Random generator;
    private final Map<ComponentID, Buffer> buffers;
    private ComponentID nextComponentID;

    public Inspector1(Random generator, Map<ComponentID, Buffer> buffers) {
        this.generator = generator;
        this.buffers = buffers;
        destination = new ArrayDeque<>(3);
        destination.add(ComponentID.WORKSTATION_1);
        destination.add(ComponentID.WORKSTATION_2);
        destination.add(ComponentID.WORKSTATION_3);
    }

    @Override
    public Optional<Event> process(Event event) {
        if (event.eventType() == EventType.ARRIVAL) {
            for (int i = 0; i < 3; i++) {
                if (buffers.get(destination.peek()).remainingSpace() != 0) {
                    break;
                } else {
                    destination.add(destination.poll());
                }
            }
            nextComponentID = destination.poll();
            return Optional.of(new Event(event.time() + generator.nextInt(MAX_DELAY), EventType.DEPARTURE, ComponentID.INSPECTOR_1, Set.of(BUFFER_IDS.get(nextComponentID)), Set.of(ResourceID.INSPECTOR_1), Distinguisher.C1));
        }
        Event nextEvent = new Event(event.time(), EventType.ARRIVAL, nextComponentID, Set.of(nextComponentID.getResourceID()), Set.of(BUFFER_IDS.get(nextComponentID)), Distinguisher.C1);
        destination.add(nextComponentID);
        return Optional.of(nextEvent);
    }
}
