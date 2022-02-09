import java.util.*;

public class Orchestrator implements Runnable {
    private final Random generator;
    private final Map<Integer, Set<Event>> futureEventList;
    private final PriorityQueue<Integer> nextTimes;
    private final Map<ComponentID, Component> components;
    private final Map<ResourceID, Resource> resources;
    private boolean stop;

    public Orchestrator(Random generator, Map<ComponentID, Component> components, Map<ResourceID, Resource> resources) {
        this.generator = generator;
        this.components = components;
        this.resources = resources;
        futureEventList = new HashMap<>();
        nextTimes = new PriorityQueue<>();
        stop = false;
    }

    private void addEvent(Event event) {
        if (!nextTimes.contains(event.time())) {
            nextTimes.add(event.time());
            futureEventList.put(event.time(), new HashSet<>(Set.of(event)));
        } else {
            futureEventList.get(event.time()).add(event);
        }
        System.out.println("added: " + event);
    }

    @Override
    public void run() {
        boolean start = true;
//        addEvent(new Event(0, EventType.ARRIVAL, ComponentID.INSPECTOR_1, Set.of(ResourceID.INSPECTOR_1), Set.of(), Distinguisher.C1));
//        addEvent(new Event(0, EventType.ARRIVAL, ComponentID.INSPECTOR_2, Set.of(ResourceID.INSPECTOR_2), Set.of(), generator.nextBoolean() ? Distinguisher.C2 : Distinguisher.C3));
        while (!stop) {
            if (nextTimes.isEmpty() && !start)
                break;
            start = false;
            int nextTime = nextTimes.peek()==null? 0:nextTimes.poll();
            if (components.get(ComponentID.INSPECTOR_1).free(Distinguisher.C1)) {
                addEvent(new Event(nextTime, EventType.ARRIVAL, ComponentID.INSPECTOR_1, Set.of(ResourceID.INSPECTOR_1), Set.of(), Distinguisher.C1));
            }
            if (components.get(ComponentID.INSPECTOR_2).free(Distinguisher.C2)) {
                addEvent(new Event(nextTime, EventType.ARRIVAL, ComponentID.INSPECTOR_2, Set.of(ResourceID.INSPECTOR_2), Set.of(), generator.nextBoolean() ? Distinguisher.C2 : Distinguisher.C3));
            }
            futureEventList.get(nextTime).parallelStream()
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
                            System.out.println("processed: " + event);
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
