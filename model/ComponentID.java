package model;

import java.util.Set;

public enum ComponentID {
    INSPECTOR_1 {
        @Override
        public ResourceID getResourceID() {
            return ResourceID.INSPECTOR_1;
        }

        @Override
        public int processBefore(ComponentID componentID) {
            return WORKSTATIONS.contains(componentID) ? 1 : 0;
        }
    },
    INSPECTOR_2 {
        @Override
        public ResourceID getResourceID() {
            return ResourceID.INSPECTOR_2;
        }

        @Override
        public int processBefore(ComponentID componentID) {
            return WORKSTATIONS.contains(componentID) ? 1 : 0;
        }
    },
    INSPECTOR_3 {
        @Override
        public ResourceID getResourceID() {
            return ResourceID.INSPECTOR_3;
        }

        @Override
        public int processBefore(ComponentID componentID) {
            return WORKSTATIONS.contains(componentID) ? 1 : 0;
        }
    },
    WORKSTATION_1 {
        @Override
        public ResourceID getResourceID() {
            return ResourceID.WORKSTATION_1;
        }

        @Override
        public int processBefore(ComponentID componentID) {
            return INSPECTORS.contains(componentID) ? -1 : 0;
        }
    },
    WORKSTATION_2 {
        @Override
        public ResourceID getResourceID() {
            return ResourceID.WORKSTATION_2;
        }

        @Override
        public int processBefore(ComponentID componentID) {
            return INSPECTORS.contains(componentID) ? -1 : 0;
        }
    },
    WORKSTATION_3 {
        @Override
        public ResourceID getResourceID() {
            return ResourceID.WORKSTATION_3;
        }

        @Override
        public int processBefore(ComponentID componentID) {
            return INSPECTORS.contains(componentID) ? -1 : 0;
        }
    };
    public static final Set<ComponentID> INSPECTORS = Set.of(INSPECTOR_1, INSPECTOR_2, INSPECTOR_3);
    public static final Set<ComponentID> WORKSTATIONS = Set.of(WORKSTATION_1, WORKSTATION_2, WORKSTATION_3);

    public ResourceID getResourceID() {
        return null;
    }

    public int processBefore(ComponentID componentID) {
        return 0;
    }
}
