package pl.rembol.jme3.utils;

import java.util.function.BinaryOperator;

public class Booleans {

    private Booleans() {
    }

    public static final BinaryOperator<Boolean> OR_REDUCTOR = (aBoolean, aBoolean2) -> aBoolean | aBoolean2;
}
