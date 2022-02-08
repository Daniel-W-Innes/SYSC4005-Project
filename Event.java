import java.util.Set;

public record Event(int time,EventType eventType,ComponentID destination, Set<ResourceID> requiredResource, Set<ResourceID> producesResource){

}
