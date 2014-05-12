package org.demodrama.cv.camera;

import processing.core.PApplet;
import processing.core.PImage;
import cl.eye.*;

/**
 * PS3Camera
 * 
 * @author Enrique Esteban
 * 
 */

public class PS3Camera {

	PApplet parent;

	boolean available = false;
	PImage frame;

	// Camera Variables
	int numCams;
	CLCamera myCameras[] = new CLCamera[2];
	PImage myImages[] = new PImage[2];
	int cameraWidth = 640;
	int cameraHeight = 480;
	int cameraRate = 30;

	public PS3Camera(PApplet parent) {
		this.parent = parent;
	}
	
	public PS3Camera(PApplet parent, int cameraWidth, int cameraHeight, int cameraRate) {
		this.parent = parent;	
		setup(cameraWidth,cameraHeight,cameraRate);
	}
	
	public boolean setup(int cameraWidth, int cameraHeight, int cameraRate) {
		
		this.cameraWidth = cameraWidth;
		this.cameraHeight = cameraHeight;
		this.cameraRate = cameraRate;		
		parent.println("PS3Camera: Getting number of cameras");
		// Checks available cameras
		numCams = CLCamera.cameraCount();
		parent.println("Found " + numCams + " cameras");
		if (numCams == 0) {
			available = false;
		}
		else {
			// create cameras and start capture
			for (int i = 0; i < numCams; i++) {
				// Prints Unique Identifier per camera
				parent.println("Camera " + (i + 1) + " UUID "
						+ CLCamera.cameraUUID(i));
				// New camera instance per camera
				myCameras[i] = new CLCamera(parent);
				// ----------------------(i, CLEYE_GRAYSCALE/COLOR,
				// CLEYE_QVGA/VGA,
				// Framerate)
				myCameras[i].createCamera(i, CLCamera.CLEYE_COLOR_PROCESSED,
						CLCamera.CLEYE_VGA, cameraRate);
				// Starts camera captures
				myCameras[i].startCamera();
				myImages[i] = parent.createImage(cameraWidth, cameraHeight, PApplet.RGB);
			}
			frame = parent.createImage(myImages[0].width, myImages[0].height, PApplet.RGB);
			available = true;
			parent.println("Complete Initializing PS3 Camera");
		}	
		
		return available;
	}

	public boolean isAvailable(){
		return available;
	}
	
	public PImage getFrame() {
		if(available) {
			// Loops through available cameras and updates
			for (int i = 0; i < numCams; i++) {
				// --------------------- (image destination, wait timeout)
				myCameras[i]
						.getCameraFrame(myImages[i].pixels, (i == 0) ? 1000 : 0);
				myImages[i].updatePixels();
			}
			frame.copy(myImages[0],0, 0, myImages[0].width, myImages[0].height,
									0, 0, myImages[0].width, myImages[0].height);
			return frame;
		}
		else {
			return null;
		}
	}

	public void setHorizontalKeystone(int hkeystone) {
		if (myCameras.length > 0) {
			myCameras[0].setCameraParam(CLCamera.CLEYE_HKEYSTONE, hkeystone);
		}
	}

	public void setVerticalKeystone(int vkeystone) {
		if (myCameras.length > 0) {
			myCameras[0].setCameraParam(CLCamera.CLEYE_VKEYSTONE, vkeystone);
		}
	}

	public void setLenscorrection(int lensCorrection) {
		if (myCameras.length > 0) {
			myCameras[0].setCameraParam(CLCamera.CLEYE_LENSCORRECTION1,
					lensCorrection);
		}
	}

	public void setZoom(int zoom) {
		if (myCameras.length > 0) {
			myCameras[0].setCameraParam(CLCamera.CLEYE_ZOOM, zoom);
		}
	}

}
