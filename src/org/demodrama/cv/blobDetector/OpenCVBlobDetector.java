package org.demodrama.cv.blobDetector;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
import monclubelec.javacvPro.Blob;
import monclubelec.javacvPro.OpenCV;

public class OpenCVBlobDetector implements BlobDetector {

	// Main objects
	PApplet parent;
	Blob[] blobs;
	OpenCV opencv; // d\u00e9clare un objet OpenCV principal

	// Parameters
	public float threshold = 0.4f;
	public int blur = 6;
	public long minArea = 50;
	public long maxArea = 100000000;	
	
	int captureWidth;
	int captureHeight;

	int maxBlob = 20;
	boolean inHoles = false;
	boolean debug = false;
	int maxVertices = 1000;
	
	// Aux vars
	boolean takeBackground = true;
	boolean withBackground = false;
	
	public OpenCVBlobDetector(PApplet parent, int captureWidth,
			int captureHeight) {
		this.parent = parent;
		this.captureWidth = captureWidth;
		this.captureHeight = captureHeight;
		// Initialize OpenCV
		opencv = new OpenCV(parent);
		opencv.allocate(captureWidth, captureHeight);
	}

	/* (non-Javadoc)
	 * @see org.demodrama.cv.blobDetector.BlobDetector#update(processing.core.PImage)
	 */
	@Override
	public BBlob[] update(PImage captureImg) {
		if (takeBackground) {
			opencv.copy(captureImg);
			opencv.remember();
			takeBackground = false;
			withBackground = true;
		}
		else {
			opencv.copy(captureImg); // autre possibilit\u00e9 - charge
		}
		
		if (withBackground) {
			opencv.absDiff();
			opencv.copyTo(opencv.Memory2, opencv.Buffer);
		}
		
		opencv.blur(blur);
		opencv.threshold(threshold, "BINARY"); // seuillage
												// binaire pour
		blobs = opencv.blobs(minArea, maxArea, maxBlob, inHoles,
				maxVertices, debug); // blobs javacvPro +/- debug
		BBlob[] bblobs = new BBlob[blobs.length];
		
		for(int i = 0; i < blobs.length; i++){
			bblobs[i] = new BBlob();
			bblobs[i].id = blobs[i].indiceContour;
			bblobs[i].centroid = new PVector(blobs[i].centroid.x,blobs[i].centroid.y); 
		}
		
		
		return bblobs;
	}
	
	/* (non-Javadoc)
	 * @see org.demodrama.cv.blobDetector.BlobDetector#captureBackground()
	 */
	@Override
	public void captureBackground() {
		takeBackground = true;
	}
	
	/* (non-Javadoc)
	 * @see org.demodrama.cv.blobDetector.BlobDetector#drawBlurImg(int, int, float)
	 */
	@Override
	public void drawBlurImg(int x, int y, float scale) {
		parent.noFill();
		parent.stroke(255);
		parent.rect(x, y, (int)(captureWidth*scale), (int)(captureHeight*scale));
		parent.fill(255);
		parent.textAlign(PApplet.CENTER);
		parent.text("Blur image\nnot available", (int)(x + (captureWidth*scale)/2), (int)(y + (captureHeight*scale)/2));
		parent.textAlign(PApplet.LEFT);				
	}
	
	/* (non-Javadoc)
	 * @see org.demodrama.cv.blobDetector.BlobDetector#drawHighPassImg(int, int, float)
	 */
	@Override
	public void drawHighPassImg(int x, int y, float scale) {
		parent.noFill();
		parent.stroke(255);
		parent.rect(x, y, (int)(captureWidth*scale), (int)(captureHeight*scale));
		parent.fill(255);
		parent.textAlign(PApplet.CENTER);
		parent.text("HighPass image\nnot available", (int)(x + (captureWidth*scale)/2), (int)(y + (captureHeight*scale)/2));
		parent.textAlign(PApplet.LEFT);		
	}
	
	/* (non-Javadoc)
	 * @see org.demodrama.cv.blobDetector.BlobDetector#drawAmplifiedImg(int, int, float)
	 */
	@Override
	public void drawAmplifiedImg(int x, int y, float scale) {
		parent.noFill();
		parent.stroke(255);
		parent.rect(x, y, (int)(captureWidth*scale), (int)(captureHeight*scale));
		parent.fill(255);
		parent.textAlign(PApplet.CENTER);
		parent.text("Amplified image\nnot available", (int)(x + (captureWidth*scale)/2), (int)(y + (captureHeight*scale)/2));
		parent.textAlign(PApplet.LEFT);		
	}
	
	/* (non-Javadoc)
	 * @see org.demodrama.cv.blobDetector.BlobDetector#drawThresholdImg(int, int, float)
	 */
	@Override
	public void drawThresholdImg(int x, int y, float scale) {
		parent.noFill();
		parent.stroke(255);
		parent.rect(x, y, (int)(captureWidth*scale), (int)(captureHeight*scale));
		parent.fill(255);
		parent.textAlign(PApplet.CENTER);
		parent.text("Threshold image\nnot available", (int)(x + (captureWidth*scale)/2), (int)(y + (captureHeight*scale)/2));
		parent.textAlign(PApplet.LEFT);		
	}
	
	/* (non-Javadoc)
	 * @see org.demodrama.cv.blobDetector.BlobDetector#drawBackground(int, int, float)
	 */
	@Override
	public void drawBackground(int x, int y, float scale) {
		PImage imgTemp = opencv.getMemory();
		if (imgTemp != null) {
			imgTemp.resize((int)(captureWidth*scale), (int)(captureHeight*scale));
			parent.image(imgTemp, x, y);
		}
		else {
			parent.noFill();
			parent.stroke(255);
			parent.rect(x, y, (int)(captureWidth*scale), (int)(captureHeight*scale));
			parent.fill(255);
			parent.textAlign(PApplet.CENTER);
			parent.text("Background image\nnot available", (int)(x + (captureWidth*scale)/2), (int)(y + (captureHeight*scale)/2));
			parent.textAlign(PApplet.LEFT);
		}
	}

	/* (non-Javadoc)
	 * @see org.demodrama.cv.blobDetector.BlobDetector#drawBlobs(int, int, float)
	 */
	@Override
	public void drawBlobs(int x, int y, float scale) {

		// OpenCV Scale dont work :( 

		//opencv.drawCentroidBlobs(blobs, x, y, scale); // trace
		//opencv.drawRectBlobs(blobs, x, y, scale); // trace
		//opencv.drawBlobs(blobs, x, y, scale); // trace		
		parent.pushMatrix();
		parent.translate(x, y);
		float lastX;
		float lastY;
		for(int i = 0; i < blobs.length; i++) {
			Blob b = blobs[i];
			parent.stroke(0,100,255);
			for(int j = 1; j < b.points.length; j++) {
				parent.line(b.points[j].x*scale, b.points[j].y*scale, b.points[j-1].x*scale, b.points[j-1].y*scale);
			}
			parent.stroke(0,255,255);
			parent.noFill();
			parent.rect(b.rectangle.x*scale, b.rectangle.y*scale, b.rectangle.width*scale, b.rectangle.height*scale);
			parent.stroke(255,0,0);
			parent.ellipse(b.centroid.x*scale, b.centroid.y*scale, 5, 5);
		}
		parent.popMatrix();
		
	}

	public void setBlur(float blur) {
		// TODO Auto-generated method stub
		this.blur = (int)blur;
	}

	public void setThreshold(float threshold) {
		// TODO Auto-generated method stub
		this.threshold = threshold;	
	}

}
