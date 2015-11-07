package pl.rembol.jme3.world.pathfinding.algorithms;

import com.jme3.math.FastMath;
import pl.rembol.jme3.world.pathfinding.Vector2i;

import java.util.function.Function;

public class BresenhamAlgorithm {

    public static boolean isDirectPathPossible(Vector2i start, Vector2i end,
                                               Function<Vector2i, Boolean> isBlockFreeFunction) {

        if (!isBlockFreeFunction.apply(end)) {
            return false;
        }

        if (start.x == end.x) {
            for (int y = Math.min(start.y, end.y); y <= Math
                    .max(start.y, end.y); ++y) {
                if (!isBlockFreeFunction.apply(new Vector2i(start.x, y))) {
                    return false;
                }
            }
            return true;
        }

        if (start.y == end.y) {
            for (int x = Math.min(start.x, end.x); x <= Math
                    .max(start.x, end.x); ++x) {
                if (!isBlockFreeFunction.apply(new Vector2i(x, start.y))) {
                    return false;
                }
            }
            return true;
        }

        int sgnY = FastMath.sign(end.y - start.y);
        int sgnX = FastMath.sign(end.x - start.x);

        float error = 0;

        if (Math.abs(end.y - start.y) < Math.abs(end.x - start.x)) {
            float alpha = FastMath.abs((float) (end.y - start.y)
                    / (end.x - start.x));
            int x = start.x;

            for (int y = start.y; y != end.y + sgnY; y += sgnY) {
                if (!isBlockFreeFunction.apply(new Vector2i(x, y))) {
                    return false;
                }

                while (error <= 0.5 && x != end.x) {
                    if (!isBlockFreeFunction.apply(new Vector2i(x, y))) {
                        return false;
                    }

                    x += sgnX;
                    error += alpha;
                }

                error -= 1f;
            }
        } else {
            float alpha = FastMath.abs((float) (end.x - start.x)
                    / (end.y - start.y));
            int y = start.y;

            for (int x = start.x; x != end.x + sgnX; x += sgnX) {
                if (!isBlockFreeFunction.apply(new Vector2i(x, y))) {
                    return false;
                }

                while (error <= 0.5 && y != end.y) {
                    if (!isBlockFreeFunction.apply(new Vector2i(x, y))) {
                        return false;
                    }

                    y += sgnY;
                    error += alpha;
                }

                error -= 1f;
            }

        }

        return true;
    }

}
