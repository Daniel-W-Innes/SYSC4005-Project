import controller.Orchestrator;
import controller.component.*;
import controller.resource.BufferImpl;
import controller.resource.Resource;
import model.ComponentID;
import model.Distinguisher;
import model.ResourceID;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class Main {
    public static final int seed = 723267672;

    public static void main(String[] args) {
        BufferImpl buffer1 = new BufferImpl();
        BufferImpl buffer2 = new BufferImpl();
        BufferImpl buffer4 = new BufferImpl();
        Map<ResourceID, Resource> resources = Map.of(
                ResourceID.INSPECTOR_1, new Inspector1(new Random(seed),
                        Map.of(ComponentID.WORKSTATION_1, buffer1,
                                ComponentID.WORKSTATION_2, buffer2,
                                ComponentID.WORKSTATION_3, buffer4)),
                ResourceID.INSPECTOR_2, new Inspector2(new Random(seed)),
                ResourceID.WORKSTATION_1, new Workstation1(new Random(seed)),
                ResourceID.WORKSTATION_2, new WorkstationWithRecipe(new Random(seed), Set.of(Distinguisher.C1, Distinguisher.C2), ResourceID.WORKSTATION_2, Distinguisher.P2),
                ResourceID.WORKSTATION_3, new WorkstationWithRecipe(new Random(seed), Set.of(Distinguisher.C1, Distinguisher.C3), ResourceID.WORKSTATION_3, Distinguisher.P3),
                ResourceID.BUFFER_1, buffer1,
                ResourceID.BUFFER_2, buffer2,
                ResourceID.BUFFER_3, new BufferImpl(),
                ResourceID.BUFFER_4, buffer4,
                ResourceID.BUFFER_5, new BufferImpl()
        );
        Map<ComponentID, Component> components = new HashMap<>();
        for (ComponentID componentID : ComponentID.values()) {
            components.put(componentID, (Component) resources.get(componentID.getResourceID()));
        }
        Orchestrator orchestrator = new Orchestrator(new Random(seed), components, resources);
        orchestrator.run();
    }
}
