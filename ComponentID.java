public enum ComponentID {
    INSPECTOR_1 {
        ResourceID getResourceID() {
            return ResourceID.INSPECTOR_1;
        }
    },
    INSPECTOR_2 {
        ResourceID getResourceID() {
            return ResourceID.INSPECTOR_2;
        }
    },
    WORKSTATION_1 {
        ResourceID getResourceID() {
            return ResourceID.WORKSTATION_1;
        }
    },
    WORKSTATION_2 {
        ResourceID getResourceID() {
            return ResourceID.WORKSTATION_2;
        }
    },
    WORKSTATION_3 {
        ResourceID getResourceID() {
            return ResourceID.WORKSTATION_3;
        }
    };

    ResourceID getResourceID() {
        return null;
    }
}
