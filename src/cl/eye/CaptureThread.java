package cl.eye;

import processing.core.PApplet;
import processing.core.PGraphics;

public class CaptureThread extends Thread {

	PApplet invoker;
	boolean stop = false;

	CLCamera myCameras[] = new CLCamera[2];
	int numCams;

	int cameraWidth = 640;
	int cameraHeight = 480;
	int cameraRate = 60;

	int[] pixels = new int[cameraWidth * cameraHeight];
	int[] tempPixels = new int[cameraWidth * cameraHeight];

	boolean newFrame = false;

	public CaptureThread(PApplet invoking) {
		invoker = invoking;
//		setupCameras();
	}

	public boolean setupCameras() {
		
		invoker.println("Getting number of cameras");
		// Checks available cameras
		numCams = CLCamera.cameraCount();
		invoker.println("Found " + numCams + " cameras");
		if (numCams == 0)
			return false;
		// create cameras and start capture
		for (int i = 0; i < numCams; i++) {
			// Prints Unique Identifier per camera
			invoker.println("Camera " + (i + 1) + " UUID "
					+ CLCamera.cameraUUID(i));
			// New camera instance per camera
			myCameras[i] = new CLCamera(invoker);
			// ----------------------(i, CLEYE_GRAYSCALE/COLOR, CLEYE_QVGA/VGA,
			// Framerate)
			myCameras[i].createCamera(i, CLCamera.CLEYE_COLOR_PROCESSED,
					CLCamera.CLEYE_VGA, cameraRate);
			// Starts camera captures
			myCameras[i].startCamera();
		}
		// resize the output window
		invoker.println("Complete Initializing Cameras");
		return true;
	}

	public void run() {
		while (!stop) {
			boolean ok = myCameras[0].getCameraFrame(tempPixels, 1000);
			if (ok) {
				updateSynch(true);
			}
		}
	}

	private int[] updateSynch(boolean newFrame) {
		if (newFrame){
			System.arraycopy(tempPixels, 0, pixels, 0, pixels.length);
			this.newFrame = true;
			return null;
		}else{
			this.newFrame = false;
			return pixels;
		}
	}

	public int[] getPixels() {
		return updateSynch(false);
	}
}