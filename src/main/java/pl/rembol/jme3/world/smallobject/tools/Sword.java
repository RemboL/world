package pl.rembol.jme3.world.smallobject.tools;

public class Sword extends Tool {

    @Override
    protected String modelFileName() {
        return "sword/sword.scene";
    }

    @Override
    public String iconName() {
        return "sword";
    }

}
