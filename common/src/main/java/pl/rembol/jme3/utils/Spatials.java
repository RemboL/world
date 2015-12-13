package pl.rembol.jme3.utils;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Stream;

public class Spatials {

    private Spatials() {}

    public static Stream<Spatial> getDescendants(Spatial spatial) {
        if (spatial == null) {
            return new ArrayList<Spatial>().stream();
        }

        if (!(spatial instanceof Node)) {
            return Collections.singletonList(spatial).stream();
        }

        Node node = (Node) spatial;

        return Stream.concat(Stream.of(node), node.getChildren().stream().flatMap(Spatials::getDescendants));
    }


}
