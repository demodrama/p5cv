package org.demodrama.cv.camera;

import processing.core.PApplet;
import processing.core.PImage;

public class CameraManager_backup25012013 {

	static final int PS3_EYE = 0;
	static final int OPEN_NI = 1;

	int selectedCamera = PS3_EYE;

	// PS3Camera ps3Camera;
	PS3QuickCamera ps3Camera;
	OpenNICamera openniCamera;

	PApplet parent;

	PImage currentFrame;

	public CameraManager_backup25012013(PApplet parent, PImage currentFrame, int cameraWidth,
			int cameraHeight, int cameraRate) {
		this.parent = parent;
		// ps3Camera = new PS3Camera(parent, cameraWidth, cameraHeight,
		// cameraRate);
		ps3Camera = new PS3QuickCamera(parent, cameraWidth, cameraHeight,
				cameraRate);
		if (!ps3Camera.isAvailable()) {
			selectedCamera = OPEN_NI;
		}
		openniCamera = new OpenNICamera(parent, cameraWidth, cameraHeight,
				cameraRate);
		this.currentFrame = currentFrame;
	}

	public PImage getFrame() {

		PImage ret = null;

		if (selectedCamera == PS3_EYE) {
			if (isPS3Available())
				ret = ps3Camera.getFrame();
		} else {
			if (isOPENNIAvailable())
				ret = openniCamera.getFrame();
		}

		if (ret != null) {
			currentFrame.copy(ret, 0, 0, currentFrame.width,
					currentFrame.height, 0, 0, currentFrame.width,
					currentFrame.height);
		}

		return currentFrame;
	}

	public void selectCamera(int cameraID) {
		selectedCamera = cameraID;
	}

	public boolean isPS3Available() {
		return ps3Camera.isAvailable();
	}

	public boolean isOPENNIAvailable() {
		return openniCamera.isAvailable();
	}
}
