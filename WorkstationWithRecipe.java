import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;

public class WorkstationWithRecipe extends Component {
    private static final int MAX_DELAY = 5;
    private final Random generator;
    private final Set<Distinguisher> hasDistinguisher;
    private final Set<Distinguisher> recipe;
    private final ResourceID resourceID;
    private final Distinguisher output;

    public WorkstationWithRecipe(Random generator, Set<Distinguisher> recipe, ResourceID resourceID, Distinguisher output) {
        this.generator = generator;
        this.recipe = recipe;
        this.resourceID = resourceID;
        this.output = output;
        hasDistinguisher = new HashSet<>();
    }

    @Override
    synchronized Optional<Event> process(Event event) {
        if (event.eventType() == EventType.ARRIVAL && hasDistinguisher.containsAll(recipe)) {
            return Optional.of(new Event(event.time() + generator.nextInt(MAX_DELAY), EventType.DEPARTURE, event.destination(), Set.of(), Set.of(resourceID), output));
        }
        return Optional.empty();
    }


    @Override
    public synchronized boolean acquire(Distinguisher distinguisher) {
        if (hasDistinguisher.contains(distinguisher)) {
            return false;
        }
        hasDistinguisher.add(distinguisher);
        return true;
    }

    @Override
    public synchronized void release() {
        hasDistinguisher.clear();
    }

}
