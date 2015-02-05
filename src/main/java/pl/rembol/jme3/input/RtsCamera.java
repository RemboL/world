package pl.rembol.jme3.input;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

public class RtsCamera implements AnalogListener {

	private static final String MOVE_FORWARD = "rtsCamera_moveForward";

	private static final String MOVE_BACKWARD = "rtsCamera_moveBackward";

	private static final String MOVE_LEFT = "rtsCamera_moveLeft";

	private static final String MOVE_RIGHT = "rtsCamera_moveRight";

	private static final String ROTATE_LEFT = "rtsCamera_rotateLeft";

	private static final String ROTATE_RIGHT = "rtsCamera_rotateRight";

	private static final String TILT_UP = "rtsCamera_tiltUp";

	private static final String TILT_DOWN = "rtsCamera_tiltDown";

	private static final String ZOOM_IN = "rtsCamera_zoomIn";

	private static final String ZOOM_OUT = "rtsCamera_zoomOut";

	private Camera cam;

	private float rotation = 0f;

	private float cameraSpeed = 30f;

	private float rotationSpeed = 1f;

	private float zoomSpeed = .5f;

	private float tilt = 45f * FastMath.DEG_TO_RAD;

	private float zoomOut = 50f;

	private Vector3f cameraCenter;

	public RtsCamera(Camera cam) {
		this.cam = cam;
		cameraCenter = cam.getLocation().clone();

		updateCamera();
	}

	private void updateCamera() {
		Quaternion rotationQuaternion = new Quaternion().fromAngleAxis(
				rotation, Vector3f.UNIT_Y).mult(
				new Quaternion().fromAngleAxis(tilt, Vector3f.UNIT_X));
		cam.setRotation(rotationQuaternion);
		cam.setLocation(cameraCenter.subtract(rotationQuaternion
				.mult(Vector3f.UNIT_Z.mult(zoomOut))));
	}

	public void moveForward(float value) {
		Quaternion rot = new Quaternion().fromAngleAxis(rotation,
				Vector3f.UNIT_Y);
		Vector3f move = rot.mult(Vector3f.UNIT_Z.mult(value * cameraSpeed));
		cameraCenter.addLocal(move);

		updateCamera();
	}

	public void moveBackward(float value) {
		moveForward(-value);
	}

	public void moveRight(float value) {
		moveLeft(-value);
	}

	public void moveLeft(float value) {
		Quaternion rot = new Quaternion().fromAngleAxis(rotation,
				Vector3f.UNIT_Y);
		Vector3f move = rot.mult(Vector3f.UNIT_X.mult(value * cameraSpeed));
		cameraCenter.addLocal(move);

		updateCamera();
	}

	public void rotateLeft(float value) {
		rotation += value * rotationSpeed;

		if (rotation > FastMath.TWO_PI) {
			rotation -= FastMath.TWO_PI;
		}

		if (rotation < 0) {
			rotation += FastMath.TWO_PI;
		}
		updateCamera();
	}

	public void rotateRight(float value) {
		rotateLeft(-value);
	}

	public void tiltUp(float value) {
		tilt -= value;

		if (tilt < 0) {
			tilt = 0;
		}

		if (tilt > FastMath.HALF_PI) {
			tilt = FastMath.HALF_PI;
		}

		updateCamera();
	}

	public void tiltDown(float value) {
		tiltUp(-value);
	}

	public void zoomIn(float value) {
		zoomOut /= (1f + (zoomSpeed * value));

		if (zoomOut < 10f) {
			zoomOut = 10f;
		}

		if (zoomOut > 100f) {
			zoomOut = 100f;
		}

		updateCamera();
	}

	public void zoomOut(float value) {
		zoomOut *= (1f + (zoomSpeed * value));

		if (zoomOut < 10f) {
			zoomOut = 10f;
		}

		if (zoomOut > 100f) {
			zoomOut = 100f;
		}

		updateCamera();
	}

	@Override
	public void onAnalog(String name, float value, float tpf) {
		switch (name) {
		case MOVE_FORWARD:
			moveForward(value);
			break;
		case MOVE_BACKWARD:
			moveBackward(value);
			break;
		case MOVE_LEFT:
			moveLeft(value);
			break;
		case MOVE_RIGHT:
			moveRight(value);
			break;
		case ROTATE_LEFT:
			rotateLeft(value);
			break;
		case ROTATE_RIGHT:
			rotateRight(value);
			break;
		case TILT_UP:
			tiltUp(value);
			break;
		case TILT_DOWN:
			tiltDown(value);
			break;
		case ZOOM_IN:
			zoomIn(value);
			break;
		case ZOOM_OUT:
			zoomOut(value);
			break;
		}
	}

	public void registerInput(InputManager inputManager) {
		registerKey(inputManager, MOVE_FORWARD, KeyInput.KEY_UP);
		registerKey(inputManager, MOVE_BACKWARD, KeyInput.KEY_DOWN);
		registerKey(inputManager, MOVE_LEFT, KeyInput.KEY_LEFT);
		registerKey(inputManager, MOVE_RIGHT, KeyInput.KEY_RIGHT);

		registerKey(inputManager, ROTATE_LEFT, KeyInput.KEY_DELETE);
		registerKey(inputManager, ROTATE_RIGHT, KeyInput.KEY_PGDN);

		registerKey(inputManager, TILT_UP, KeyInput.KEY_HOME);
		registerKey(inputManager, TILT_DOWN, KeyInput.KEY_END);

		registerKey(inputManager, ZOOM_IN, KeyInput.KEY_INSERT);
		registerKey(inputManager, ZOOM_OUT, KeyInput.KEY_PGUP);

		inputManager.addMapping(ZOOM_IN, new MouseAxisTrigger(
				MouseInput.AXIS_WHEEL, false));
		inputManager.addMapping(ZOOM_OUT, new MouseAxisTrigger(
				MouseInput.AXIS_WHEEL, true));
	}

	private void registerKey(InputManager inputManager, String name, int key) {
		inputManager.addMapping(name, new KeyTrigger(key));
		inputManager.addListener(this, name);
	}

}
