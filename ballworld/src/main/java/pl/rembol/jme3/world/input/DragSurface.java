package pl.rembol.jme3.world.input;

import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;

import java.nio.FloatBuffer;
import java.util.List;

public class DragSurface extends Mesh {

    private List<List<Vector3f>> vertices;

    public DragSurface(List<List<Vector3f>> vertices) {
        this.vertices = vertices;

        updateGeometryVertices();
        updateGeometryIndices();
    }

    @Override
    public DragSurface clone() {
        return new DragSurface(vertices);
    }

    protected void updateGeometryIndices() {
        if (getBuffer(VertexBuffer.Type.Index) == null) {

            short[] array = new short[(vertices.size() - 1)
                    * (vertices.get(0).size() - 1) * 6];

            int index = 0;
            for (int z = 0; z < vertices.size() - 1; ++z) {
                for (int x = 0; x < vertices.get(0).size() - 1; ++x) {
                    array[index++] = (short) ((z * (vertices.get(0).size() - 1) + x) * 4 + 2);
                    array[index++] = (short) ((z * (vertices.get(0).size() - 1) + x) * 4 + 1);
                    array[index++] = (short) ((z * (vertices.get(0).size() - 1) + x) * 4 + 0);
                    array[index++] = (short) ((z * (vertices.get(0).size() - 1) + x) * 4 + 3);
                    array[index++] = (short) ((z * (vertices.get(0).size() - 1) + x) * 4 + 2);
                    array[index++] = (short) ((z * (vertices.get(0).size() - 1) + x) * 4 + 0);
                }
            }

            setBuffer(VertexBuffer.Type.Index, 3,
                    BufferUtils.createShortBuffer(array));
        }
    }

    protected void updateGeometryVertices() {
        FloatBuffer fpb = BufferUtils.createVector3Buffer((vertices.size() - 1)
                * (vertices.get(0).size() - 1) * 4);

        float[] array = new float[(vertices.size() - 1)
                * (vertices.get(0).size() - 1) * 12];

        int index = 0;
        for (int z = 0; z < vertices.size() - 1; ++z) {
            for (int x = 0; x < vertices.get(0).size() - 1; ++x) {
                array[index++] = vertices.get(z).get(x).x;
                array[index++] = vertices.get(z).get(x).y;
                array[index++] = vertices.get(z).get(x).z;

                array[index++] = vertices.get(z).get(x + 1).x;
                array[index++] = vertices.get(z).get(x + 1).y;
                array[index++] = vertices.get(z).get(x + 1).z;

                array[index++] = vertices.get(z + 1).get(x + 1).x;
                array[index++] = vertices.get(z + 1).get(x + 1).y;
                array[index++] = vertices.get(z + 1).get(x + 1).z;

                array[index++] = vertices.get(z + 1).get(x).x;
                array[index++] = vertices.get(z + 1).get(x).y;
                array[index++] = vertices.get(z + 1).get(x).z;

            }

        }

        fpb.put(array);

        setBuffer(VertexBuffer.Type.Position, 3, fpb);
        updateBound();
    }
}