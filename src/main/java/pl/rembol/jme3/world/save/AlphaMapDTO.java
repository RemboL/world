package pl.rembol.jme3.world.save;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.springframework.util.Base64Utils;

import com.jme3.texture.Image;
import com.jme3.texture.Image.Format;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("alphaMap")
public class AlphaMapDTO {

	private int width;

	private int height;

	private int depth;

	private List<String> alphaMapBase64;

	public AlphaMapDTO(Image alphaMap) {
		this.width = alphaMap.getWidth();
		this.height = alphaMap.getHeight();
		this.depth = alphaMap.getDepth();
		this.alphaMapBase64 = getStrings(alphaMap.getData());
	}

	public Image toImage() {
		return new Image(Format.BGR8, width, height, depth, getBuffers());
	}

	private List<String> getStrings(List<ByteBuffer> buffers) {
		List<String> result = new ArrayList<>();

		for (ByteBuffer buffer : buffers) {
			buffer.rewind();
			byte[] byteArray = new byte[buffer.capacity()];
			buffer.get(byteArray, 0, buffer.capacity());

			result.add(Base64Utils.encodeToString(byteArray));
		}

		return result;
	}

	private ArrayList<ByteBuffer> getBuffers() {
		ArrayList<ByteBuffer> buffers = new ArrayList<>();
		for (String string : alphaMapBase64) {
			byte[] byteArray = Base64Utils.decodeFromString(string);
			ByteBuffer buffer = ByteBuffer.allocateDirect(byteArray.length);
			buffer.put(byteArray);
			buffers.add(buffer);
		}
		return buffers;
	}
}
