package controller;

import controller.component.Component;
import controller.component.Inspector1;
import controller.resource.Buffer;
import controller.resource.Resource;
import model.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Orchestrator implements Runnable {
    private final Random generator;
    private final PriorityQueue<Event> futureEventList;
    private final Map<ComponentID, Component> components;
    private final Map<ResourceID, Resource> resources;
    private final Map<Distinguisher, Integer> durations;
    private boolean stop;

    public Orchestrator(Random generator, Map<ComponentID, Component> components, Map<ResourceID, Resource> resources) {
        this.generator = generator;
        this.components = components;
        this.resources = resources;
        futureEventList = new PriorityQueue<>();
        durations = new HashMap<>();
        stop = false;
    }


    @Override
    public void run() {
        futureEventList.add(new Event(0, EventType.ARRIVAL, ComponentID.INSPECTOR_1, Set.of(ResourceID.INSPECTOR_1), Set.of(), Distinguisher.C1));
        futureEventList.add(new Event(0, EventType.ARRIVAL, ComponentID.INSPECTOR_2, Set.of(ResourceID.INSPECTOR_2), Set.of(), generator.nextBoolean() ? Distinguisher.C2 : Distinguisher.C3));

        try (FileWriter f = new FileWriter("output.csv");
             FileWriter p1 = new FileWriter("P1.csv");
             FileWriter p2 = new FileWriter("P2.csv");
             FileWriter p3 = new FileWriter("P3.csv");
             FileWriter b1 = new FileWriter("b1.csv");
             FileWriter b2 = new FileWriter("b2.csv");
             FileWriter b3 = new FileWriter("b3.csv");
             FileWriter b4 = new FileWriter("b4.csv");
             FileWriter b5 = new FileWriter("b5.csv");
             FileWriter i1 = new FileWriter("i1.csv");
             FileWriter i2 = new FileWriter("i2.csv")){
            f.write(Event.toHead());
            while (!stop) {
                if (futureEventList.isEmpty())
                    break;
                Event event = futureEventList.peek();
                System.out.println("checking: " + event);
                if (event.eventType() == EventType.DEPARTURE) {
                    if (event.destination() == ComponentID.INSPECTOR_1 && futureEventList.parallelStream().noneMatch(event1 -> event1.destination() == ComponentID.INSPECTOR_1 && event1.eventType() == EventType.ARRIVAL)) {
                        futureEventList.add(new Event(event.time(), EventType.ARRIVAL, ComponentID.INSPECTOR_1, Set.of(ResourceID.INSPECTOR_1), Set.of(), Distinguisher.C1, event.fudged()));
                    } else if (event.destination() == ComponentID.INSPECTOR_2 && futureEventList.parallelStream().noneMatch(event1 -> event1.destination() == ComponentID.INSPECTOR_2 && event1.eventType() == EventType.ARRIVAL)) {
                        futureEventList.add(new Event(event.time(), EventType.ARRIVAL, ComponentID.INSPECTOR_2, Set.of(ResourceID.INSPECTOR_2), Set.of(), generator.nextBoolean() ? Distinguisher.C2 : Distinguisher.C3, event.fudged()));
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
                    f.write(event.toCSV());
                    b1.write(event.time()+","+((Buffer) resources.get(ResourceID.BUFFER_1)).size()+"\n");
                    b2.write(event.time()+","+((Buffer) resources.get(ResourceID.BUFFER_2)).size()+"\n");
                    b3.write(event.time()+","+((Buffer) resources.get(ResourceID.BUFFER_3)).size()+"\n");
                    b4.write(event.time()+","+((Buffer) resources.get(ResourceID.BUFFER_4)).size()+"\n");
                    b5.write(event.time()+","+((Buffer) resources.get(ResourceID.BUFFER_5)).size()+"\n");
                    if (event.eventType() == EventType.DEPARTURE && event.destination() == ComponentID.INSPECTOR_1){
                        i1.write(event.time()+","+event.getTimeFudged()+"\n");
                    }else if (event.eventType() == EventType.DEPARTURE && event.destination() == ComponentID.INSPECTOR_2){
                        i2.write(event.time()+","+event.getTimeFudged()+"\n");
                    } else if (event.distinguisher() == Distinguisher.P1 || event.distinguisher() == Distinguisher.P2 || event.distinguisher() == Distinguisher.P3 ){
                        if (durations.containsKey(event.distinguisher())){
                            switch (event.distinguisher()){
                                case P1 -> p1.write((event.time()-durations.get(event.distinguisher()))+"\n");
                                case P2 -> p2.write((event.time()-durations.get(event.distinguisher()))+"\n");
                                case P3 -> p3.write((event.time()-durations.get(event.distinguisher()))+"\n");
                            }
                        }
                        durations.put(event.distinguisher(),event.time());
                    }
                    System.out.println("processed: " + event);
                    event.producesResource().forEach(resourceID -> resources.get(resourceID).release());
                } else {
                    acquired.forEach(resourceID -> resources.get(resourceID).release());
                    event.fudge();
                    futureEventList.add(event);
                }
                Assertions.checkBuffers(resources, futureEventList);
                if (event.time() >= 100000){
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        stop = true;
    }
}
