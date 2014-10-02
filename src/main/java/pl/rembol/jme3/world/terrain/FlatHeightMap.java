package pl.rembol.jme3.world.terrain;

import com.jme3.terrain.heightmap.AbstractHeightMap;

public class FlatHeightMap extends AbstractHeightMap {

	public boolean load() {
		if (null != this.heightData) {
			unloadHeightMap();
		}
		this.heightData = new float[this.size * this.size];
		return true;
	}

	public FlatHeightMap(int size) {
		size = Math.abs(size);

		this.size = size;

		load();
	}
}
