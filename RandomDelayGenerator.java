import java.util.Random;
import java.util.Set;

public class RandomDelayGenerator implements Component{
    private  final ComponentID destination;
    private final Random generator;
    private final java.util.Set<ResourceID> requiredResource;
    private final Set<ResourceID> producesResource;
    public static final int MAX_DELAY = 5;

    public RandomDelayGenerator(ComponentID destination, Random generator, Set<ResourceID> requiredResource, Set<ResourceID> producesResource) {
        this.destination = destination;
        this.generator = generator;
        this.requiredResource = requiredResource;
        this.producesResource = producesResource;
    }

    @Override
    public Event process(Event event) {
        return new Event(event.time()+ generator.nextInt(MAX_DELAY),destination,requiredResource,producesResource);
    }
}
