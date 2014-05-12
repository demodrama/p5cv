package org.demodrama.cv.camera;

import SimpleOpenNI.SimpleOpenNI;
import processing.core.PApplet;
import processing.core.PImage;

// TODO: Handle multiple devices
public class OpenNICamera {

	PApplet parent;

	boolean available = false;

	int cameraWidth = 640;
	int cameraHeight = 480;
	int cameraRate = 30;

	SimpleOpenNI context;

	public OpenNICamera(PApplet parent) {
		this.parent = parent;
	}

	public OpenNICamera(PApplet parent, int cameraWidth, int cameraHeight,
			int cameraRate) {
		this.parent = parent;
		setup(cameraWidth, cameraHeight, cameraRate);
	}

	// TODO: Control camera resolution and framerate
	public boolean setup(int cameraWidth, int cameraHeight, int cameraRate) {

		this.cameraWidth = cameraWidth;
		this.cameraHeight = cameraHeight;
		this.cameraRate = cameraRate;

		 //context = new SimpleOpenNI(parent,
		// SimpleOpenNI.RUN_MODE_MULTI_THREADED);
		context = new SimpleOpenNI(parent);

		if (context != null) {
			context.enableIR(cameraWidth,cameraHeight,cameraRate);
			context.enableDepth(cameraWidth,cameraHeight,cameraRate);
			available = true;
		} else {
			available = false;
		}

		return available;
	}

	public boolean isAvailable() {
		return available;
	}

	public PImage getFrame() {
		if (available) {
			context.update();
			return context.irImage();
		} else {
			return null;
		}
	}

}
