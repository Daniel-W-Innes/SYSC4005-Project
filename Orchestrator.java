import java.util.*;

public class Orchestrator implements Runnable {
    private final Random generator;
    private final Map<Integer, Set<Event>> futureEventList;
    private final PriorityQueue<Integer> nextTime;
    private final Map<ComponentID, Component> components;
    private final Map<ResourceID, Resource> resources;
    private boolean stop;

    public Orchestrator(Random generator, Map<ComponentID, Component> components, Map<ResourceID, Resource> resources) {
        this.generator = generator;
        this.components = components;
        this.resources = resources;
        futureEventList = new HashMap<>();
        nextTime = new PriorityQueue<>();
        stop = false;
    }

    private void addEvent(Event event) {
        if (!nextTime.contains(event.time())) {
            nextTime.add(event.time());
            futureEventList.put(event.time(), new HashSet<>(Set.of(event)));
        } else {
            futureEventList.get(event.time()).add(event);
        }
    }

    @Override
    public void run() {
        addEvent(new Event(0, EventType.ARRIVAL, ComponentID.INSPECTOR_1, Set.of(ResourceID.INSPECTOR_1), Set.of(), Distinguisher.C1));
        addEvent(new Event(0, EventType.ARRIVAL, ComponentID.INSPECTOR_2, Set.of(ResourceID.INSPECTOR_2), Set.of(), generator.nextBoolean() ? Distinguisher.C2 : Distinguisher.C3));

        while (!stop) {
            if (nextTime.isEmpty())
                break;
            if (components.get(ComponentID.INSPECTOR_1).free(Distinguisher.C1)) {
                addEvent(new Event(nextTime.peek(), EventType.ARRIVAL, ComponentID.INSPECTOR_1, Set.of(ResourceID.INSPECTOR_1), Set.of(), Distinguisher.C1));
            }
            if (components.get(ComponentID.INSPECTOR_2).free(Distinguisher.C2)) {
                addEvent(new Event(nextTime.peek(), EventType.ARRIVAL, ComponentID.INSPECTOR_2, Set.of(ResourceID.INSPECTOR_2), Set.of(), generator.nextBoolean() ? Distinguisher.C2 : Distinguisher.C3));
            }
            futureEventList.get(nextTime.poll()).parallelStream()
                    .map((event -> {
                        Set<ResourceID> acquired = new HashSet<>();
                        boolean canRun = true;
                        for (ResourceID resourceID : event.requiredResource()) {
                            canRun = resources.get(resourceID).acquire(event.distinguisher());
                            if (canRun) {
                                acquired.add(resourceID);
                            } else {
                                break;
                            }
                        }
                        if (canRun) {
                            Optional<Event> nextEvent = components.get(event.destination()).process(event);
                            System.out.println(event);
                            event.producesResource().forEach(resourceID -> resources.get(resourceID).release());
                            return nextEvent;
                        } else {
                            acquired.forEach(resourceID -> resources.get(resourceID).release());
                            return Optional.of(event);
                        }
                    }))
                    .forEach(optionalEvent -> optionalEvent.ifPresent(this::addEvent));
        }
    }

    public void stop() {
        stop = true;
    }
}
