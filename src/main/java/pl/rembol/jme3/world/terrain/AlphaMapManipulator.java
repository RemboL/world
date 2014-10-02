package pl.rembol.jme3.world.terrain;

import java.nio.ByteBuffer;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;

public class AlphaMapManipulator {

	public void addBlop(Texture alphaMap, Vector2f spot, float radius,
			ColorRGBA channel) {
		Image image = alphaMap.getImage();

		float width = image.getWidth();
		float height = image.getHeight();

		int minx = (int) Math.max(0, (spot.x * width - radius * width));
		int maxx = (int) Math.min(width, (spot.x * width + radius * width));
		int miny = (int) Math.max(0, (spot.y * height - radius * height));
		int maxy = (int) Math.min(height, (spot.y * height + radius * height));

		Vector2f currentSpot = new Vector2f();
		float radiusSquared = radius * radius;
		ColorRGBA currentColor = new ColorRGBA();
		for (int y = miny; y < maxy; y++) {
			for (int x = minx; x < maxx; x++) {
				currentSpot.set((float) x / width, (float) y / height);

				float distanceSquared = spot.distanceSquared(currentSpot);
				if (distanceSquared < radiusSquared) {
					getPixelColor(currentColor, image, x, y);

					float intensity = (1.0f - (distanceSquared / radiusSquared));
					currentColor.interpolate(channel, intensity);

					savePixelColor(currentColor, image, x, y);
				}
			}
		}

		image.getData(0).rewind();
		image.setUpdateNeeded();

	}

	private void savePixelColor(ColorRGBA color, Image image, int x, int y) {
		ByteBuffer buf = image.getData(0);
		int width = image.getWidth();

		int position = (y * width + x) * getPixelSize(image);

		if (position > buf.capacity() - 1 || position < 0)
			return;

		switch (image.getFormat()) {
		case RGBA8:
			buf.position(position);
			buf.put(float2byte(color.r)).put(float2byte(color.g))
					.put(float2byte(color.b)).put(float2byte(color.a));
			return;
		case BGR8:
			buf.position(position);
			buf.put(float2byte(color.b)).put(float2byte(color.g))
					.put(float2byte(color.r));
			return;
		case ABGR8:
			buf.position(position);
			buf.put(float2byte(color.a)).put(float2byte(color.b))
					.put(float2byte(color.g)).put(float2byte(color.r));
			return;
		default:
			throw new UnsupportedOperationException("Image format: "
					+ image.getFormat());
		}
	}

	private void getPixelColor(ColorRGBA color, Image image, int x, int y) {
		ByteBuffer buf = image.getData(0);
		int width = image.getWidth();

		int position = (y * width + x) * getPixelSize(image);

		if (position > buf.capacity() - 1 || position < 0) {
			return;
		}

		float r, g, b, a;

		switch (image.getFormat()) {
		case RGBA8:
			buf.position(position);
			color.set(byte2float(buf.get()), byte2float(buf.get()),
					byte2float(buf.get()), byte2float(buf.get()));
			return;
		case BGR8:
			buf.position(position);
			b = byte2float(buf.get());
			g = byte2float(buf.get());
			r = byte2float(buf.get());
			color.set(r, g, b, 1f);
			return;
		case ABGR8:
			buf.position(position);
			a = byte2float(buf.get());
			b = byte2float(buf.get());
			g = byte2float(buf.get());
			r = byte2float(buf.get());
			color.set(r, g, b, a);
			return;
		default:
			throw new UnsupportedOperationException("Image format: "
					+ image.getFormat());
		}
	}

	private int getPixelSize(Image image) {
		switch (image.getFormat()) {
		case RGBA8:
		case ABGR8:
			return 4;
		case BGR8:
			return 3;
		default:
			return 1;
		}

	}

	private float byte2float(byte b) {
		return ((float) (b & 0xFF)) / 255f;
	}

	private byte float2byte(float f) {
		return (byte) (f * 255f);
	}
}
