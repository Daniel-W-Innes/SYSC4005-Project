package controller;

import controller.component.Component;
import controller.resource.Buffer;
import controller.resource.Resource;
import model.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Orchestrator implements Runnable {
    private static final int MAX_TIME = 100000;
    private final PriorityQueue<Event> futureEventList;
    private final Map<ComponentID, Component> components;
    private final Map<ResourceID, Resource> resources;
    private final Map<Distinguisher, Integer> durations;
    private final Map<Distinguisher, Integer> produced;
    private final Map<ComponentID, Integer> blocked;
    private boolean stop;

    public Orchestrator(Map<ComponentID, Component> components, Map<ResourceID, Resource> resources) {
        this.components = components;
        this.resources = resources;
        futureEventList = new PriorityQueue<>();
        durations = new HashMap<>();
        produced = new HashMap<>();
        blocked = new HashMap<>();
        stop = false;
    }


    @Override
    public void run() {
        //Kickstart the system with initial events
        futureEventList.add(new Event(0, EventType.ARRIVAL, ComponentID.INSPECTOR_1, Set.of(ResourceID.INSPECTOR_1), Set.of(), Distinguisher.C1));
        futureEventList.add(new Event(0, EventType.ARRIVAL, ComponentID.INSPECTOR_2, Set.of(ResourceID.INSPECTOR_2), Set.of(), Distinguisher.C2));
        futureEventList.add(new Event(0, EventType.ARRIVAL, ComponentID.INSPECTOR_3, Set.of(ResourceID.INSPECTOR_3), Set.of(), Distinguisher.C3));

        //Open output files for analysing the system
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
             FileWriter i2 = new FileWriter("i2.csv");
             FileWriter i3 = new FileWriter("i3.csv")) {
            f.write(Event.toHead());
            while (!stop) {
                //If there are no events left stop looping
                if (futureEventList.isEmpty())
                    break;
                Event event = futureEventList.peek();

                //Add arrival events my necessary to ensure inspectors are never idle
                System.out.println("checking: " + event);
                if (event.eventType() == EventType.DEPARTURE) {
                    if (event.destination() == ComponentID.INSPECTOR_1 && futureEventList.parallelStream().noneMatch(event1 -> event1.destination() == ComponentID.INSPECTOR_1 && event1.eventType() == EventType.ARRIVAL)) {
                        futureEventList.add(new Event(event.time(), EventType.ARRIVAL, ComponentID.INSPECTOR_1, Set.of(ResourceID.INSPECTOR_1), Set.of(), Distinguisher.C1, event.fudged()));
                    } else if (event.destination() == ComponentID.INSPECTOR_2 && futureEventList.parallelStream().noneMatch(event1 -> event1.destination() == ComponentID.INSPECTOR_2 && event1.eventType() == EventType.ARRIVAL)) {
                        futureEventList.add(new Event(event.time(), EventType.ARRIVAL, ComponentID.INSPECTOR_2, Set.of(ResourceID.INSPECTOR_2), Set.of(), Distinguisher.C2, event.fudged()));
                    } else if (event.destination() == ComponentID.INSPECTOR_3 && futureEventList.parallelStream().noneMatch(event1 -> event1.destination() == ComponentID.INSPECTOR_3 && event1.eventType() == EventType.ARRIVAL)) {
                        futureEventList.add(new Event(event.time(), EventType.ARRIVAL, ComponentID.INSPECTOR_3, Set.of(ResourceID.INSPECTOR_3), Set.of(), Distinguisher.C3, event.fudged()));
                    }
                }

                event = futureEventList.poll();
                System.out.println("processing: " + event);
                if (event == null)
                    break;

                //Acquire resources required by event
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
                    //If all resources are acquired successfully run the event and add any events it outputs to the future event list
                    components.get(event.destination()).process(event).ifPresent(futureEventList::add);

                    //Record a bunch of information to files
                    //Log of all processed events
                    f.write(event.toCSV());
                    //Log buffer usage
                    b1.write(event.time() + "," + ((Buffer) resources.get(ResourceID.BUFFER_1)).size() + "\n");
                    b2.write(event.time() + "," + ((Buffer) resources.get(ResourceID.BUFFER_2)).size() + "\n");
                    b3.write(event.time() + "," + ((Buffer) resources.get(ResourceID.BUFFER_3)).size() + "\n");
                    b4.write(event.time() + "," + ((Buffer) resources.get(ResourceID.BUFFER_4)).size() + "\n");
                    b5.write(event.time() + "," + ((Buffer) resources.get(ResourceID.BUFFER_5)).size() + "\n");
                    //Log if inspectors are blocked
                    if (event.eventType() == EventType.DEPARTURE && event.destination() == ComponentID.INSPECTOR_1) {
                        i1.write(event.time() + "," + event.getTimeFudged() + "\n");
                    } else if (event.eventType() == EventType.DEPARTURE && event.destination() == ComponentID.INSPECTOR_2) {
                        i2.write(event.time() + "," + event.getTimeFudged() + "\n");
                    } else if (event.eventType() == EventType.DEPARTURE && event.destination() == ComponentID.INSPECTOR_3) {
                        i3.write(event.time() + "," + event.getTimeFudged() + "\n");
                    } else if (event.distinguisher() == Distinguisher.P1 || event.distinguisher() == Distinguisher.P2 || event.distinguisher() == Distinguisher.P3) {
                        if (durations.containsKey(event.distinguisher())) {
                            //Log time between products
                            switch (event.distinguisher()) {
                                case P1 -> p1.write((event.time() - durations.get(event.distinguisher())) + "\n");
                                case P2 -> p2.write((event.time() - durations.get(event.distinguisher())) + "\n");
                                case P3 -> p3.write((event.time() - durations.get(event.distinguisher())) + "\n");
                            }
                            //Store time between products for print statement at the end of the program
                            produced.put(event.distinguisher(), produced.get(event.distinguisher()) + 1);
                        } else {
                            produced.put(event.distinguisher(), 1);
                        }
                        durations.put(event.distinguisher(), event.time());
                    }
                    //Store if inspectors for print statement at the end of the program
                    if (event.eventType() == EventType.DEPARTURE) {
                        if (blocked.containsKey(event.destination())) {
                            blocked.put(event.destination(), blocked.get(event.destination()) + event.getTimeFudged());
                        } else {
                            blocked.put(event.destination(), event.getTimeFudged());
                        }
                    }

                    System.out.println("processed: " + event);

                    //Release prescribed resources
                    event.producesResource().forEach(resourceID -> resources.get(resourceID).release());
                } else {
                    //Handle partial success for acquiring resources by releasing any resources that were acquired
                    acquired.forEach(resourceID -> resources.get(resourceID).release());

                    //Fudge the event so that it has a lower priority and therefore will be handled later in the timeslot
                    event.fudge();
                    futureEventList.add(event);
                }

                //Sandy check to ensure that there are no missing events
                Assertions.checkBuffers(resources, futureEventList);

                //End the simulation after MAX_TIME
                if (event.time() >= MAX_TIME) {
                    System.out.println(produced);
                    System.out.println(blocked);
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
