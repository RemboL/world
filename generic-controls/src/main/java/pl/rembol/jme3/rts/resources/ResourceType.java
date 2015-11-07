package pl.rembol.jme3.rts.resources;

public class ResourceType {

    private final String resourceName;
    private final boolean limited;

    public ResourceType(String resourceName, boolean limited) {

        this.resourceName = resourceName;
        this.limited = limited;
    }

    public String resourceName() {
        return resourceName;
    }

    public boolean isLimited() {
        return limited;
    }
}
