package pl.rembol.jme3.world.resources;

public class Cost {

    int wood = 0;
    int stone = 0;
    int housing = 0;

    public Cost(int wood, int stone, int housing) {
        this.wood = wood;
        this.stone = stone;
        this.housing = housing;
    }

    public int wood() {
        return wood;
    }

    public int stone() {
        return stone;
    }

    public int housing() {
        return housing;
    }

}
