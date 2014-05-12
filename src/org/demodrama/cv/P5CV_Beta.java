package org.demodrama.cv;

import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.io.File;
import java.io.OutputStream;

import org.demodrama.cv.blobDetector.BBlob;
import org.demodrama.cv.blobDetector.BlobDetector;
import org.demodrama.cv.blobDetector.DiewaldBlobDetector;
import org.demodrama.cv.blobDetector.OpenCVBlobDetector;
import org.demodrama.cv.calibrator.QuadCalibrator;
import org.demodrama.cv.camera.CameraManager;
import org.demodrama.cv.camera.OpenNICamera;
import org.demodrama.cv.camera.PS3Camera;
import org.demodrama.cv.tuioTracker.TrackedBlob;
import org.demodrama.cv.tuioTracker.TuioBlobTracker;

import codeanticode.glgraphics.GLConstants;

import controlP5.ControlP5;

import cl.eye.CaptureThread;
import monclubelec.javacvPro.Blob;
import monclubelec.javacvPro.OpenCV;
import netP5.NetAddress;
import oscP5.OscBundle;
import oscP5.OscMessage;
import oscP5.OscP5;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import processing.xml.XMLElement;
import processing.xml.XMLWriter;
import SimpleOpenNI.SimpleOpenNI;
import TUIO.TuioCursor;
import TUIO.TuioPoint;
import TUIO.TuioServer;

public class P5CV_Beta extends PApplet {

	int widthCapture = 640; // largeur image capture
	int heightCapture = 480; // hauteur image capture
	//int widthCapture = 320; // largeur image capture
	//int heightCapture = 240; // hauteur image capture
	int fpsCapture = 30; // framerate de Capture

	ControlP5 controlP5;
	public static int SETUP_MODE = 0;
	public static int MINI_MODE = 1;
	public static int CALIB_MODE = 2;
	int appMode = SETUP_MODE;

	int resolutionWidth;
	int resolutionHeight;

	static GraphicsDevice grafica = GraphicsEnvironment
			.getLocalGraphicsEnvironment().getDefaultScreenDevice();

	int guiX = 50;
	int guiY = 50;

	int guiXOffset = 50;
	int guiYOffset = 20;

	int guiImgWidth = widthCapture / 2;
	int guiImgHeight = heightCapture / 2;
	int guiSmallImgWidth = widthCapture / 4;
	int guiSmallImgHeight = heightCapture / 4;

	PImage currentFrame = null;

	// Blob detector
	BlobDetector blobDetector;

	// Cameras
	CameraManager cameras;

	// Calibrator
	QuadCalibrator calibrator;

	// Tracker variables
	TuioBlobTracker tracker;

	// TUIO server
	TuioServer tuioServer;

	public void setup() {

		// TODO: Manage multiple screens
		GraphicsEnvironment ge = GraphicsEnvironment
				.getLocalGraphicsEnvironment();
		GraphicsDevice[] gs = ge.getScreenDevices();

		for (int i = 0; i < gs.length; i++) {
			DisplayMode dm = gs[i].getDisplayMode();
			resolutionWidth = dm.getWidth();
			resolutionHeight = dm.getHeight();
		}

		// Initialize screen
		// P2D or P3D needed for using processing texture() and shaders
		size(800, 300,P2D);

		// frame.setSize(widthCapture * 2, heightCapture * 2);
		frameRate(fpsCapture);

		// config = loadXML("config.xml");

		cameras = new CameraManager(this, widthCapture, heightCapture,
				fpsCapture);

		blobDetector = new DiewaldBlobDetector(this, widthCapture,
				heightCapture, cameras.getFrame());

//		blobDetector = new OpenCVBlobDetector(this, widthCapture,
//				heightCapture);

		tracker = new TuioBlobTracker();
				
		loadThreshold();
		
		calibrator = new QuadCalibrator(this, widthCapture, heightCapture,
				resolutionWidth, resolutionHeight, 20);
		calibrator.load("calibrator.xml");
		// XML xmlTuio = config.getChild("tuio");
		// tuioServer = new TuioServer(xmlTuio.getString("host", "127.0.0.1"),
		// xmlTuio.getInt("port", 3333));

		tuioServer = new TuioServer("127.0.0.1", 3333);

		//initGui();

	}

	
	public void loadThreshold(){
		File f = new File(this.dataPath("threshold.xml"));
		if(f.exists()) {
			XMLElement root = new XMLElement(this, this.dataPath("threshold.xml"));
			XMLElement simpleValue = root.getChild("simple");
			inputThreshold = simpleValue.getInt("value");
		} else {
			inputThreshold = 0;
		}
		threshold(inputThreshold);
	}
	
	public void saveThreshold(int thresh){
		XMLElement root = new XMLElement("threshold");
		XMLElement simple = new XMLElement("simple");
		simple.setInt("value", thresh);
		root.addChild(simple);
		try {
			OutputStream stream = this
					.createOutput(this.dataPath("threshold.xml"));
			XMLWriter writer = new XMLWriter(stream);
			writer.write(root, true);
		} catch (Exception e) {
			PApplet.println(e.getStackTrace());
		}
	}
	
	public void initGui() {

		controlP5 = new ControlP5(this);
		controlP5.addSlider("threshold", 0, 100, 50f,
				guiX + (guiImgWidth + guiXOffset) * 2, 350, 100, 14)
				.setNumberOfTickMarks(100);

		controlP5.addSlider("blur", 1, 10, 8,
				guiX + (guiImgWidth + guiXOffset) * 2, 365, 100, 14)
				.setNumberOfTickMarks(100);

		controlP5.addBang("background")
				.setPosition(guiX + (guiImgWidth + guiXOffset) * 2, 380)
				.setSize(12, 12);

	}

	public void blur(int theValue) {
		println(theValue);
		blobDetector.setBlur(theValue);
	}

	public void background() {
		println("background");
		blobDetector.captureBackground();
	}

	public void threshold(float theValue) {
		println(theValue);
		blobDetector.setThreshold(0.01f + theValue);
	}

	public void draw() {
		background(0);

		currentFrame = cameras.getFrame();

		if (currentFrame == null) {
			return;
		}

		// image(currentFrame,0,0);

		BBlob[] blobs = blobDetector.update(currentFrame);
		TuioCursor[] tuioCursors = tracker.process(blobs, calibrator,widthCapture,heightCapture);
		tuioServer.send("update", tuioCursors);
		//
		if (appMode == CALIB_MODE) {
			calibrator.draw(currentFrame);
		} else {
			drawDebugMode();
		}
		//
		// fill(255);
		text(frameRate, 10, 10);
	}

	private void drawDebugMode() {

		// PINTAMOS BACKGROUND

		pushMatrix();

		translate(guiX, guiY);

		image(currentFrame, 0, 0, guiImgWidth, guiImgHeight);
		blobDetector.drawBlobs(0, 0, 0.5f);
		//blobDetector.drawThresholdImg(guiImgWidth + guiXOffset, 0, 0.5f);
		//blobDetector.drawBlobs(guiImgWidth + guiXOffset, 0, 0.5f);

	/*	blobDetector.drawBackground(0, guiImgHeight + 2 * guiYOffset, 0.25f);
		blobDetector.drawBlurImg(guiSmallImgWidth + guiXOffset / 3,
				guiImgHeight + 2 * guiYOffset, 0.25f);
		blobDetector.drawHighPassImg(2 * guiSmallImgWidth + 2 * guiXOffset / 3,
				guiImgHeight + 2 * guiYOffset, 0.25f);
		blobDetector.drawAmplifiedImg(3 * guiSmallImgWidth + guiXOffset,
				guiImgHeight + 2 * guiYOffset, 0.25f);
*/
//		text("c calibrate", (guiImgWidth + guiXOffset) * 2, 20);
//		text("x setup", (guiImgWidth + guiXOffset) * 2, 40);
		fill(255,255,0);
		text("threshold : " + inputThreshold + " (pulsa 1 para disminuir y 2 para incrementar)", (guiImgWidth + guiXOffset) , 40);
		text("space minimode", (guiImgWidth + guiXOffset) , 60);
		text("s save conf", (guiImgWidth + guiXOffset) , 80);
		text("l load conf", (guiImgWidth + guiXOffset) , 100);

		popMatrix();

		//controlP5.draw();
		fill(150);
		text("P5CV por Kike Esteban y Eduardo Moriana @ demodrama-faces 2014", (guiImgWidth + guiXOffset + 10) , 280);
	}

	int inputThreshold = 50;
	
	@Override
	public void keyPressed() {
		if (key == ' ')
			if (appMode == SETUP_MODE) {
				appMode = MINI_MODE;
				frame.setSize(200, 80);
			} else if (appMode == MINI_MODE) {
				appMode = SETUP_MODE;
				frame.setSize(800, 330);
			}

		switch (key) {
		case '1':
			inputThreshold--;
			if(inputThreshold<0)
				inputThreshold=0;
			threshold(inputThreshold);
			break;
		case '2':
			inputThreshold++;
			if(inputThreshold>100)
				inputThreshold=100;
			threshold(inputThreshold);			
		case 's':
			// saves the layout
			//calibrator.save("calibrator.xml");
			saveThreshold(inputThreshold);
			break;
		case 'l':
			// loads the saved layout
			//calibrator.load("calibrator.xml");
			loadThreshold();
			break;

		case 'c':
			// loads the saved layout
			//appMode = CALIB_MODE;
			//frame.setBounds(0, -20, resolutionWidth, resolutionHeight + 50);
			// frame.setUndecorated(true);
			break;
		case 'x':
			// loads the saved layout
			//appMode = SETUP_MODE;
			//frame.setSize(widthCapture * 2, heightCapture * 2);
			// frame.setUndecorated(false);
			break;
		case 'w':
			if (appMode == CALIB_MODE)
				calibrator.warpedMode = !calibrator.warpedMode;
		default:
			break;
		}
	}

	@Override
	public void mousePressed() {
		if (appMode == CALIB_MODE) {
			calibrator.mousePressed();
		}
	}

	@Override
	public void mouseDragged() {
		if (appMode == CALIB_MODE) {
			calibrator.mouseDragged();
		}
	}

	@Override
	public void mouseReleased() {
		if (appMode == CALIB_MODE) {
			calibrator.mouseReleased();
		}
	}

	static public void main(String args[]) {
		int a = 0;

		PApplet.main(new String[] { "--bgcolor=#F0F0F0",
				"org.demodrama.cv.P5CV_Beta" });
	}
}
