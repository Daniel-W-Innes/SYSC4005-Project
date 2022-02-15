import java.util.Set;

public enum ComponentID {
    INSPECTOR_1 {
        @Override
        ResourceID getResourceID() {
            return ResourceID.INSPECTOR_1;
        }

        @Override
        int processBefore(ComponentID componentID) {
            return WORKSTATIONS.contains(componentID) ? 1 : 0;
        }
    },
    INSPECTOR_2 {
        @Override
        ResourceID getResourceID() {
            return ResourceID.INSPECTOR_2;
        }

        @Override
        int processBefore(ComponentID componentID) {
            return WORKSTATIONS.contains(componentID) ? 1 : 0;
        }
    },
    WORKSTATION_1 {
        @Override
        ResourceID getResourceID() {
            return ResourceID.WORKSTATION_1;
        }

        @Override
        int processBefore(ComponentID componentID) {
            return INSPECTORS.contains(componentID) ? -1 : 0;
        }
    },
    WORKSTATION_2 {
        @Override
        ResourceID getResourceID() {
            return ResourceID.WORKSTATION_2;
        }

        @Override
        int processBefore(ComponentID componentID) {
            return INSPECTORS.contains(componentID) ? -1 : 0;
        }
    },
    WORKSTATION_3 {
        @Override
        ResourceID getResourceID() {
            return ResourceID.WORKSTATION_3;
        }

        @Override
        int processBefore(ComponentID componentID) {
            return INSPECTORS.contains(componentID) ? -1 : 0;
        }
    };
    public static final Set<ComponentID> INSPECTORS = Set.of(INSPECTOR_1, INSPECTOR_2);
    public static final Set<ComponentID> WORKSTATIONS = Set.of(WORKSTATION_1, WORKSTATION_2, WORKSTATION_3);

    ResourceID getResourceID() {
        return null;
    }

    int processBefore(ComponentID componentID) {
        return 0;
    }
}
