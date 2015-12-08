package pl.rembol.jme3.rts.gui.window;

import com.jme3.math.Vector3f;
import com.jme3.scene.Mesh;
import com.jme3.scene.VertexBuffer;

public class QuadrangleMesh extends Mesh {

    public QuadrangleMesh(Vector3f vertex1, Vector3f vertex2, Vector3f vertex3, Vector3f vertex4) {
        this.setBuffer(VertexBuffer.Type.Position, 3, new float[]{
                vertex1.x, vertex1.y, vertex1.z,
                vertex2.x, vertex2.y, vertex2.z,
                vertex3.x, vertex3.y, vertex3.z,
                vertex4.x, vertex4.y, vertex4.z});
        this.setBuffer(VertexBuffer.Type.TexCoord, 2, new float[]{
                0.0F, 0.0F,
                1.0F, 0.0F,
                1.0F, 1.0F,
                0.0F, 1.0F});

        this.setBuffer(VertexBuffer.Type.Normal, 3, new float[]{
                0.0F, 0.0F, 1.0F,
                0.0F, 0.0F, 1.0F,
                0.0F, 0.0F, 1.0F,
                0.0F, 0.0F, 1.0F});

        this.setBuffer(VertexBuffer.Type.Index, 3, new short[]{
                (short) 0, (short) 1, (short) 2,
                (short) 0, (short) 2, (short) 3});

        this.updateBound();
    }

}
