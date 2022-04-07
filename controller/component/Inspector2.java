package controller.component;

import model.*;

import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class Inspector2 extends Component {
    private static final int MAX_DELAY = 5;
    private final Random generator;
    public Inspector2(Random generator) {
        this.generator = generator;
    }

    @Override
    public Optional<Event> process(Event event) {
        if (event.eventType() == EventType.ARRIVAL) {
            return Optional.of(new Event(event.time() + generator.nextInt(MAX_DELAY), EventType.DEPARTURE, ComponentID.INSPECTOR_2, Set.of(ResourceID.BUFFER_3), Set.of(ResourceID.INSPECTOR_2), Distinguisher.C2));
        }
        return Optional.of(new Event(event.time(), EventType.ARRIVAL, ComponentID.WORKSTATION_2, Set.of(ResourceID.WORKSTATION_2), Set.of(ResourceID.BUFFER_3), Distinguisher.C2));
    }
}
