package pl.rembol.jme3.world;

import com.jme3.scene.Spatial;

public class SpatialToBeDetached {

	private Spatial spatial;
	private long time;

	public SpatialToBeDetached(Spatial spatial, long time) {
		this.spatial = spatial;
		this.time = time;

	}

	public Spatial getSpatial() {
		return spatial;
	}

	public long getTime() {
		return time;
	}

}
