package pl.rembol.jme3.utils;

import com.jme3.math.FastMath;

public class Floats {
    
    private Floats() {}

    /**
     * casts float to int randomly with better chance of casting to closer integer.
     * 
     * For example, 1.5 will have 50% chance to be cast to either 1 or 2, 5.8 will have 80% chance to be cast to 6.
     * @param f float to be cast
     * @return resulting integer
     */
    public static int toInt(float f) {
        return ((int) FastMath.floor(f)) + (Math.random() < (f - (int) FastMath.floor(f)) ? 1 : 0); 
    }

}
