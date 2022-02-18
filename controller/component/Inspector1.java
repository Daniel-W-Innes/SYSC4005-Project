package controller.component;

import model.*;

import java.util.*;

public class Inspector1 extends Component {
    private static final Map<ComponentID, ResourceID> buffers = Map.of(ComponentID.WORKSTATION_1, ResourceID.BUFFER_1, ComponentID.WORKSTATION_2, ResourceID.BUFFER_2, ComponentID.WORKSTATION_3, ResourceID.BUFFER_4);
    private static final int MAX_DELAY = 5;
    private final Queue<ComponentID> destination;
    private final Random generator;
    private ComponentID nextComponentID;

    public Inspector1(Random generator) {
        this.generator = generator;
        destination = new ArrayDeque<>(3);
        destination.add(ComponentID.WORKSTATION_1);
        destination.add(ComponentID.WORKSTATION_2);
        destination.add(ComponentID.WORKSTATION_3);
    }

    @Override
    public Optional<Event> process(Event event) {
        if (event.eventType() == EventType.ARRIVAL) {
            nextComponentID = destination.poll();
            return Optional.of(new Event(event.time() + generator.nextInt(MAX_DELAY), EventType.DEPARTURE, ComponentID.INSPECTOR_1, Set.of(buffers.get(nextComponentID)), Set.of(ResourceID.INSPECTOR_1), Distinguisher.C1));
        }
        Event nextEvent = new Event(event.time(), EventType.ARRIVAL, nextComponentID, Set.of(nextComponentID.getResourceID()), Set.of(buffers.get(nextComponentID)), Distinguisher.C1);
        destination.add(nextComponentID);
        return Optional.of(nextEvent);
    }
}
