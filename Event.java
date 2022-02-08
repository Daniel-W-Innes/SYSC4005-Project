import java.util.Set;

public record Event(int time, ComponentID destination, Set<ResourceID> requiredResource, Set<ResourceID> producesResource){

}
