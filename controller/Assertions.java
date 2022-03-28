package controller;

import controller.resource.Buffer;
import controller.resource.Resource;
import model.*;

import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class Assertions {

    public static void checkBuffers(Map<ResourceID, Resource> resources, PriorityQueue<Event> futureEventList){
        Set.of(ResourceID.BUFFER_1,ResourceID.BUFFER_2,ResourceID.BUFFER_3,ResourceID.BUFFER_4,ResourceID.BUFFER_5)
                .parallelStream().forEach(resourceID -> checkBuffer(resourceID,resources,futureEventList));
    }
    public static void checkBuffer(ResourceID resourceID, Map<ResourceID, Resource> resources, PriorityQueue<Event> futureEventList) {
        Buffer buffer = ((Buffer) resources.get(resourceID));
        int i = Buffer.CAPACITY - buffer.getSpace();
        ComponentID componentID;
        Distinguisher distinguisher;
        switch (resourceID) {
            case BUFFER_2 -> {
                componentID = ComponentID.WORKSTATION_2;
                distinguisher = Distinguisher.C1;
            }
            case BUFFER_3 -> {
                componentID = ComponentID.WORKSTATION_2;
                distinguisher = Distinguisher.C2;
            }
            case BUFFER_4 -> {
                componentID = ComponentID.WORKSTATION_3;
                distinguisher = Distinguisher.C1;
            }
            case BUFFER_5 -> {
                componentID = ComponentID.WORKSTATION_3;
                distinguisher = Distinguisher.C3;
            }
            default -> {
                componentID = ComponentID.WORKSTATION_1;
                distinguisher = Distinguisher.C1;
            }
        }
        int j = (int) futureEventList.parallelStream().filter(event1 ->
                event1.destination() == componentID && event1.eventType() == EventType.ARRIVAL
                        && event1.distinguisher() == distinguisher).count();
        assert i == j: "Missing items from event queue";
    }
}
