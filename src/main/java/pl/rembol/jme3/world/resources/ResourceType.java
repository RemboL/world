package pl.rembol.jme3.world.resources;

public enum ResourceType {
    FOOD("food"), WOOD("wood"), STONE("stone"), HOUSING("housing");

    private String iconName;

    ResourceType(String iconName) {
        this.iconName = iconName;

    }

    public String resourceName() {
        return iconName;
    }
}
