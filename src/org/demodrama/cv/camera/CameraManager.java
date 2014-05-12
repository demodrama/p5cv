package org.demodrama.cv.camera;

import processing.core.PApplet;
import processing.core.PImage;

public class CameraManager {

	static final int PS3_EYE = 0;
	static final int OPEN_NI = 1;

	int selectedCamera = OPEN_NI;

	OpenNICamera openniCamera;

	PApplet parent;

	PImage currentFrame;

	public CameraManager(PApplet parent, int cameraWidth,
			int cameraHeight, int cameraRate) {
		this.parent = parent;
		openniCamera = new OpenNICamera(parent, cameraWidth, cameraHeight,
				cameraRate);
		this.currentFrame = parent.createImage(cameraWidth, cameraHeight, PApplet.RGB);
	}

	public PImage getFrame() {

		PImage ret = null;

		if (selectedCamera == OPEN_NI) {
				ret = openniCamera.getFrame();
		}

		if (ret != null) {
//			currentFrame.copy(ret, 0, 0, currentFrame.width,
//					currentFrame.height, 0, 0, currentFrame.width,
//					currentFrame.height);
			ret.loadPixels();
			currentFrame.pixels = ret.pixels;
			currentFrame.updatePixels();
		}

		return currentFrame;
	}

	public void selectCamera(int cameraID) {
		selectedCamera = cameraID;
	}

	public boolean isOPENNIAvailable() {
		return openniCamera.isAvailable();
	}
}
