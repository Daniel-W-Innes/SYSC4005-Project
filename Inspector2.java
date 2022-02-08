import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Inspector2 extends Component {
    private static final Map<ComponentID, ResourceID> buffers = Map.of(ComponentID.WORKSTATION_2, ResourceID.BUFFER_2, ComponentID.WORKSTATION_3, ResourceID.BUFFER_4);
    private static final int MAX_DELAY = 5;
    private final Random generator;

    public Inspector2(Random generator) {
        this.generator = generator;
    }

    @Override
    public Event process(Event event) {
        ResourceID bufferID = event.distinguisher() == Distinguisher.C2 ? ResourceID.BUFFER_3 : ResourceID.BUFFER_5;
        if (event.eventType() == EventType.ARRIVAL) {
            return new Event(event.time() + generator.nextInt(MAX_DELAY), EventType.DEPARTURE, ComponentID.INSPECTOR_1, Set.of(bufferID), Set.of(ResourceID.INSPECTOR_2), event.distinguisher());
        }
        ComponentID nextComponentID = event.distinguisher() == Distinguisher.C2 ? ComponentID.WORKSTATION_2 : ComponentID.WORKSTATION_3;
        return new Event(event.time(), EventType.ARRIVAL, nextComponentID, Set.of(nextComponentID.getResourceID()), Set.of(buffers.get(nextComponentID)), event.distinguisher());
    }
}
