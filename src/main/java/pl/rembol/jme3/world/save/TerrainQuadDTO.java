package pl.rembol.jme3.world.save;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.springframework.util.Base64Utils;

import com.jme3.terrain.geomipmap.TerrainQuad;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("terrainQuad")
public class TerrainQuadDTO {

	private String name;

	private int patchSize;

	private int totalSize;

	private String heightMapBase64;

	public TerrainQuadDTO(TerrainQuad terrain) {
		this.name = terrain.getName();
		this.patchSize = terrain.getPatchSize();
		this.totalSize = terrain.getTotalSize();
		this.heightMapBase64 = generateBase64(terrain.getHeightMap());
	}

	private float[] getHeightMap() {
		FloatBuffer buffer = ByteBuffer.wrap(
				Base64Utils.decodeFromString(heightMapBase64)).asFloatBuffer();
		float[] heightMap = new float[buffer.capacity()];
		buffer.get(heightMap, 0, buffer.capacity());

		return heightMap;
	}

	private String generateBase64(float[] heightMap) {
		ByteBuffer buffer = ByteBuffer.allocate(4 * heightMap.length);
		for (float f : heightMap) {
			buffer.putFloat(f);
		}

		return Base64Utils.encodeToString(buffer.array());
	}

	public TerrainQuad toTerrainQuad() {
		return new TerrainQuad(name, patchSize, totalSize, getHeightMap());
	}

}
