package org.demodrama.cv.camera;

import processing.core.PApplet;
import processing.core.PImage;
import cl.eye.*;

public class PS3QuickCamera {

	// Camera Variables
	int numCams;
	CLCamera myCameras[] = new CLCamera[2];
	PImage[] myImages = null;
	PImage frame = null;
	int cameraWidth = 640;
	int cameraHeight = 480;
	int cameraRate = 60;

	// Animation Variables (not required)
	boolean animate = true;
	float zoomVal, zoomDelta;
	float rotateVal, rotateDelta;

	CaptureThread captureThread;

	PApplet parent;

	boolean available = false;

	public PS3QuickCamera(PApplet parent) {
		this.parent = parent;
	}

	public PS3QuickCamera(PApplet parent, int cameraWidth, int cameraHeight,
			int cameraRate) {
		this.parent = parent;
		setup(cameraWidth, cameraHeight, cameraRate);
	}

	public boolean setup(int cameraWidth, int cameraHeight, int cameraRate) {

		// Library loading via native interface (JNI)
		// If you see "UnsatisfiedLinkError" then target the library path
		// otherwise leave it commented out.
		// CLCamera.loadLibrary("C://PATH//TO//CL-EYE SDK FOLDER//Bin/CLEyeMulticam.dll");

		// Verifies the native library loaded
		// if (!setupCameras())
		// exit();
		// // Setups animated variables
		// if (animate)
		// setupAnimation();

		captureThread = new CaptureThread(parent);
		available = captureThread.setupCameras();
		captureThread.start();

		myImages = new PImage[2];

		myImages[0] = parent
				.createImage(cameraWidth, cameraHeight, PApplet.RGB);

		frame = parent.createImage(cameraWidth, cameraHeight, PApplet.RGB);

		return available;
	}

	public boolean isAvailable() {
		return available;
	}

	public PImage getFrame() {
		if (available) {
			// Loops through available cameras and updates
			// for (int i = 0; i < numCams; i++) {
			// --------------------- (image destination, wait timeout)
			// myCameras[i].getCameraFrame(myImages[i].pixels, (i == 0) ? 1000
			// : 0);
			// myImages[0].updatePixels();
			// parent.image(myImages[i], 0, 0);
			// // }
			// frame.copy(myImages[0], 0, 0, myImages[0].width,
			// myImages[0].height, 0, 0, myImages[0].width,
			// myImages[0].height);
			frame.pixels = captureThread.getPixels();
			frame.updatePixels();
			return frame;
		} else {
			return null;
		}
	}

	// public void draw() {
	// // // Loops through available cameras and updates
	// // for (int i = 0; i < numCams; i++) {
	// // // --------------------- (image destination, wait timeout)
	// // myCameras[i]
	// // .getCameraFrame(myImages[i].pixels, (i == 0) ? 1000 : 0);
	// // myImages[i].updatePixels();
	// // image(myImages[i], cameraWidth * i, 0);
	// // }
	// // Updates the animation
	// // if (animate)
	// // updateAnimation();
	//
	// if (frameCount > 10) {
	// try {
	// myImages.pixels = captureThread.getPixels();
	// myImages.updatePixels();
	// image(myImages,0,0);
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }
	// text(frameRate, 10, 10);
	// }

	// boolean setupCameras() {
	// println("Getting number of cameras");
	// // Checks available cameras
	// numCams = CLCamera.cameraCount();
	// println("Found " + numCams + " cameras");
	// if (numCams == 0)
	// return false;
	// // create cameras and start capture
	// for (int i = 0; i < numCams; i++) {
	// // Prints Unique Identifier per camera
	// println("Camera " + (i + 1) + " UUID " + CLCamera.cameraUUID(i));
	// // New camera instance per camera
	// myCameras[i] = new CLCamera(this);
	// // ----------------------(i, CLEYE_GRAYSCALE/COLOR, CLEYE_QVGA/VGA,
	// // Framerate)
	// myCameras[i].createCamera(i, CLCamera.CLEYE_COLOR_PROCESSED,
	// CLCamera.CLEYE_VGA, cameraRate);
	// // Starts camera captures
	// myCameras[i].startCamera();
	// myImages = createImage(cameraWidth, cameraHeight, RGB);
	// }
	// // resize the output window
	// size(cameraWidth * numCams, cameraHeight, P2D);
	// println("Complete Initializing Cameras");
	// return true;
	// }

	// void setupAnimation() {
	// // General Animation Variables
	// zoomVal = 0;
	// zoomDelta = TWO_PI / 75.0f;
	// rotateVal = 0;
	// rotateDelta = TWO_PI / 125.0f;
	// }

	// void updateAnimation() {
	// myCameras[0].setCameraParam(CLCamera.CLEYE_HKEYSTONE,
	// (int) (150 * sin(rotateVal)));
	// myCameras[0].setCameraParam(CLCamera.CLEYE_VKEYSTONE,
	// (int) (200 * cos(rotateVal)));
	// // myCameras[0].setCameraParam(Multicam.CLEYE_LENSCORRECTION1, (int)(75
	// // * sin(rotateVal)));
	// if (numCams > 1) {
	// myCameras[1].setCameraParam(CLCamera.CLEYE_ZOOM,
	// (int) (200 * sin(zoomVal)));
	// }
	// rotateVal += rotateDelta;
	// zoomVal += zoomDelta;
	// }
	//
	// static public void main(String args[]) {
	// PApplet.main(new String[] { "--bgcolor=#F0F0F0",
	// "cl.eye.TestGlGraphics" });
	// }
}
