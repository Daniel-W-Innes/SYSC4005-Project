import java.util.*;

public class Orchestrator implements Runnable {
    private final Map<Integer, Set<Event>> futureEventList;
    private final PriorityQueue<Integer> nextTime;
    private final Map<ComponentID, Component> components;
    private final Map<ResourceID, Resource> resources;
    private boolean stop;

    public Orchestrator(Map<ComponentID, Component> components, Map<ResourceID, Resource> resources) {
        this.components = components;
        this.resources = resources;
        futureEventList = new HashMap<>();
        nextTime = new PriorityQueue<>();
        stop = false;
    }

    @Override
    public void run() {
        while (!stop) {
            futureEventList.get(nextTime.poll()).parallelStream()
                    .map((event -> {
                        Set<ResourceID> acquired = new HashSet<>();
                        boolean canRun = true;
                        for (ResourceID resourceID: event.requiredResource()) {
                            canRun = resources.get(resourceID).acquire();
                            if (canRun){
                                acquired.add(resourceID);
                            } else {
                                break;
                            }
                        }
                        if (canRun){
                            Event nextEvent = components.get(event.destination()).process(event);
                            nextEvent.producesResource().forEach(resourceID -> resources.get(resourceID).release());
                            return nextEvent;
                        }else {
                            acquired.forEach(resourceID ->  resources.get(resourceID).release());
                            return event;
                        }
                    }))
                    .forEach(event -> {
                        if (!nextTime.contains(event.time())) {
                            nextTime.add(event.time());
                            futureEventList.put(event.time(), Set.of(event));

                        } else {
                            futureEventList.get(event.time()).add(event);
                        }
                    });
        }
    }

    public void stop() {
        stop = true;
    }
}
