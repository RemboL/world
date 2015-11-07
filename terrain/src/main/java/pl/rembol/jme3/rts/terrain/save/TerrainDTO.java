package pl.rembol.jme3.rts.terrain.save;

import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Image;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("pl/rembol/jme3/rts/terrain/terrain")
public class TerrainDTO {

    private TerrainQuadDTO terrainQuad;

    private AlphaMapDTO alphaMap;

    public TerrainDTO(TerrainQuad terrainQuad, Image alphaMap) {
        this.terrainQuad = new TerrainQuadDTO(terrainQuad);
        this.alphaMap = new AlphaMapDTO(alphaMap);
    }

    public TerrainQuad toTerrainQuad() {
        return terrainQuad.toTerrainQuad();
    }

    public Image toAlphaMap() {
        return alphaMap.toImage();
    }

}
