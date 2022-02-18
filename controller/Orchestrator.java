package controller;

import controller.component.Component;
import controller.resource.Resource;
import model.*;

import java.util.*;

public class Orchestrator implements Runnable {
    private final Random generator;
    private final PriorityQueue<Event> futureEventList;
    private final Map<ComponentID, Component> components;
    private final Map<ResourceID, Resource> resources;
    private boolean stop;

    public Orchestrator(Random generator, Map<ComponentID, Component> components, Map<ResourceID, Resource> resources) {
        this.generator = generator;
        this.components = components;
        this.resources = resources;
        futureEventList = new PriorityQueue<>();
        stop = false;
    }


    @Override
    public void run() {
        futureEventList.add(new Event(0, EventType.ARRIVAL, ComponentID.INSPECTOR_1, Set.of(ResourceID.INSPECTOR_1), Set.of(), Distinguisher.C1));
        futureEventList.add(new Event(0, EventType.ARRIVAL, ComponentID.INSPECTOR_2, Set.of(ResourceID.INSPECTOR_2), Set.of(), generator.nextBoolean() ? Distinguisher.C2 : Distinguisher.C3));
        while (!stop) {
            if (futureEventList.isEmpty())
                break;
            Event event = futureEventList.peek();
            System.out.println("checking: " + event);
            if (event.eventType() == EventType.DEPARTURE ) {
                if (event.destination() == ComponentID.INSPECTOR_1) {
                    futureEventList.add(new Event(event.time(), EventType.ARRIVAL, ComponentID.INSPECTOR_1, Set.of(ResourceID.INSPECTOR_1), Set.of(), Distinguisher.C1,event.fudged()));
                } else if (event.destination() == ComponentID.INSPECTOR_2) {
                    futureEventList.add(new Event(event.time(), EventType.ARRIVAL, ComponentID.INSPECTOR_2, Set.of(ResourceID.INSPECTOR_2), Set.of(), generator.nextBoolean() ? Distinguisher.C2 : Distinguisher.C3,event.fudged()));
                }

            }
            event = futureEventList.poll();
            System.out.println("processing: " + event);
            if (event == null)
                break;
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
                components.get(event.destination()).process(event).ifPresent(futureEventList::add);
                System.out.println("processed: " + event);
                event.producesResource().forEach(resourceID -> resources.get(resourceID).release());
            } else {
                acquired.forEach(resourceID -> resources.get(resourceID).release());
                event.fudge();
                futureEventList.add(event);
            }
        }
    }

    public void stop() {
        stop = true;
    }
}
