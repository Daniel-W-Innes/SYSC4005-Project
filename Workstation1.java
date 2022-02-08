import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class Workstation1 extends Component {
    private static final int MAX_DELAY = 5;
    private final Random generator;

    public Workstation1(Random generator) {
        this.generator = generator;
    }

    @Override
    Optional<Event> process(Event event) {
        if (event.eventType() == EventType.ARRIVAL) {
            return Optional.of(new Event(event.time() + generator.nextInt(MAX_DELAY), EventType.DEPARTURE, event.destination(), Set.of(), Set.of(ResourceID.WORKSTATION_1), Distinguisher.P1));
        }
        return Optional.empty();
    }
}
