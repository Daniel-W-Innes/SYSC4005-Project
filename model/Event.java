package model;

import java.util.Objects;
import java.util.Set;

public final class Event implements Comparable<Event> {
    private final EventType eventType;
    private final ComponentID destination;
    private final Set<ResourceID> requiredResource;
    private final Set<ResourceID> producesResource;
    private final Distinguisher distinguisher;
    private int time;
    private boolean fudged;
    private int timeFudged;

    public Event(int time, EventType eventType, ComponentID destination, Set<ResourceID> requiredResource,
                 Set<ResourceID> producesResource, Distinguisher distinguisher, boolean fudged) {
        this.time = time;
        this.eventType = eventType;
        this.destination = destination;
        this.requiredResource = requiredResource;
        this.producesResource = producesResource;
        this.distinguisher = distinguisher;
        this.fudged = fudged;
        timeFudged = 0;
    }

    public Event(int time, EventType eventType, ComponentID destination, Set<ResourceID> requiredResource,
                 Set<ResourceID> producesResource, Distinguisher distinguisher) {
        this(time, eventType, destination, requiredResource, producesResource, distinguisher, false);
    }

    @Override
    public int compareTo(Event o) {
        int compare = Integer.compare(time, o.time);
        if (compare != 0) {
            return compare;
        }
        if (!fudged && o.fudged) {
            return -1;
        }
        if (!o.fudged && fudged) {
            return 1;
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

    public int time() {
        return time;
    }

    public EventType eventType() {
        return eventType;
    }

    public ComponentID destination() {
        return destination;
    }

    public Set<ResourceID> requiredResource() {
        return requiredResource;
    }

    public Set<ResourceID> producesResource() {
        return producesResource;
    }

    public Distinguisher distinguisher() {
        return distinguisher;
    }


    public boolean fudged() {
        return fudged;
    }

    public void fudge() {
        if (fudged) {
            time++;
            timeFudged++;
            fudged = false;
        } else {
            fudged = true;
        }
    }
    public int getTimeFudged() {
        return timeFudged;
    }
    public static String toHead() {
        return "time,type,destination,distinguisher" + "\n";
    }

    public String toCSV() {
        return time + "," + eventType + "," + destination + "," + distinguisher + "\n";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Event) obj;
        return this.time == that.time &&
                Objects.equals(this.eventType, that.eventType) &&
                Objects.equals(this.destination, that.destination) &&
                Objects.equals(this.requiredResource, that.requiredResource) &&
                Objects.equals(this.producesResource, that.producesResource) &&
                Objects.equals(this.distinguisher, that.distinguisher) &&
                Objects.equals(this.fudged, that.fudged);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, eventType, destination, requiredResource, producesResource, distinguisher, fudged);
    }

    @Override
    public String toString() {
        return "model.Event[" +
                "time=" + time + ", " +
                "eventType=" + eventType + ", " +
                "destination=" + destination + ", " +
                "requiredResource=" + requiredResource + ", " +
                "producesResource=" + producesResource + ", " +
                "distinguisher=" + distinguisher + ", " +
                "fudged=" + fudged + ']';
    }
}
