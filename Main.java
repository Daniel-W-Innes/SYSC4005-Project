import controller.Orchestrator;
import controller.component.*;
import controller.resource.Buffer;
import controller.resource.Resource;
import model.ComponentID;
import model.Distinguisher;
import model.ResourceID;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        Random generator = new Random(1);
        Map<ResourceID, Resource> resources = Map.of(
                ResourceID.INSPECTOR_1, new Inspector1(generator),
                ResourceID.INSPECTOR_2, new Inspector2(generator),
                ResourceID.WORKSTATION_1, new Workstation1(generator),
                ResourceID.WORKSTATION_2, new WorkstationWithRecipe(generator, Set.of(Distinguisher.C1, Distinguisher.C2), ResourceID.WORKSTATION_2, Distinguisher.P2),
                ResourceID.WORKSTATION_3, new WorkstationWithRecipe(generator, Set.of(Distinguisher.C1, Distinguisher.C3), ResourceID.WORKSTATION_3, Distinguisher.P3),
                ResourceID.BUFFER_1, new Buffer(),
                ResourceID.BUFFER_2, new Buffer(),
                ResourceID.BUFFER_3, new Buffer(),
                ResourceID.BUFFER_4, new Buffer(),
                ResourceID.BUFFER_5, new Buffer()
        );
        Map<ComponentID, Component> components = new HashMap<>();
        for (ComponentID componentID : ComponentID.values()) {
            components.put(componentID, (Component) resources.get(componentID.getResourceID()));
        }
        Orchestrator orchestrator = new Orchestrator(generator, components, resources);
        orchestrator.run();
    }
}
