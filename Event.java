import java.util.Set;

public record Event(int time, EventType eventType, ComponentID destination, Set<ResourceID> requiredResource,
                    Set<ResourceID> producesResource, Distinguisher distinguisher) implements Comparable<Event> {

    @Override
    public int compareTo(Event o) {
        int compare = Integer.compare(time, o.time);
        if (compare != 0) {
            return compare;
        }
        compare = destination.processBefore(o.destination);
        if (compare != 0) {
            return compare;
        }
        if (!eventType.equals(o.eventType)) {
            return eventType == EventType.ARRIVAL ? 1 : -1;
        }
        return 0;
    }
}
