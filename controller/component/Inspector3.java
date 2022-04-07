package controller.component;

import model.*;

import java.util.*;

public class Inspector3 extends Component {
    private static final int MAX_DELAY = 5;
    private final Random generator;
    public Inspector3(Random generator) {
        this.generator = generator;
    }

    @Override
    public Optional<Event> process(Event event) {
        if (event.eventType() == EventType.ARRIVAL) {
            return Optional.of(new Event(event.time() + generator.nextInt(MAX_DELAY), EventType.DEPARTURE, ComponentID.INSPECTOR_3, Set.of(ResourceID.BUFFER_5), Set.of(ResourceID.INSPECTOR_3), Distinguisher.C3));
        }
        return Optional.of(new Event(event.time(), EventType.ARRIVAL, ComponentID.WORKSTATION_3, Set.of(ResourceID.WORKSTATION_3), Set.of(ResourceID.BUFFER_5), Distinguisher.C3));
    }
}
