package org.demodrama.cv.blobDetector;

import java.util.ArrayList;

import codeanticode.glgraphics.GLTexture;
import codeanticode.glgraphics.GLTextureFilter;

import diewald_CV_kit.blobdetection.Blob;
import diewald_CV_kit.blobdetection.BlobDetector;
import diewald_CV_kit.blobdetection.Pixel;
import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class DiewaldBlobDetector implements
		org.demodrama.cv.blobDetector.BlobDetector {

	PApplet parent;
	int captureWidth;
	int captureHeight;

	BlobDetector blob_detector;
	BBlob blobs[];

	int detection_resolution_step = 1;

	public float threshold = 250;
	public int blurFactor = 6;

	boolean takeBackground = true;
	boolean withBackground = false;

	BLOBable_GRADIENT bloBable;
	int drawIds = 1;
	
	GLTexture srcTex, bloomMask, destTex;
	GLTexture tex0, tex2, tex4, tex8, tex16;

	GLTextureFilter extractBloom, blur, blend4, toneMap;

	

	public DiewaldBlobDetector(PApplet parent, int captureWidth,
			int captureHeight, PImage sample_img) {

		this.parent = parent;
		this.captureWidth = captureWidth;
		this.captureHeight = captureHeight;

		blob_detector = new BlobDetector(captureWidth, captureHeight);
		blob_detector.setResolution(detection_resolution_step);
		blob_detector.computeContours(true);
		blob_detector.computeBlobPixels(true);
		blob_detector.setMinMaxPixels(10 * 10, captureWidth * captureHeight);

		bloBable = new BLOBable_GRADIENT(sample_img);
		blob_detector.setBLOBable(bloBable);
		
	    // Loading required filters.
//	    extractBloom = new GLTextureFilter(parent, "ExtractBloom.xml");
//	    blur = new GLTextureFilter(parent, "Blur.xml");
//	    blend4 = new GLTextureFilter(parent, "Blend4.xml");  
//	    toneMap = new GLTextureFilter(parent, "ToneMap.xml");
//	       
//	    srcTex = new GLTexture(parent, "lights.jpg");
//	    int w = srcTex.width;
//	    int h = srcTex.height;
//	    destTex = new GLTexture(parent, w, h);
//
//	    // Initializing bloom mask and blur textures.
//	    bloomMask = new GLTexture(parent, w, h, GLTexture.FLOAT);
//	    tex0 = new GLTexture(parent, w, h, GLTexture.FLOAT);
//	    tex2 = new GLTexture(parent, w / 2, h / 2, GLTexture.FLOAT);
//	    tex4 = new GLTexture(parent, w / 4, h / 4, GLTexture.FLOAT);
//	    tex8 = new GLTexture(parent, w / 8, h / 8, GLTexture.FLOAT);
//	    tex16 = new GLTexture(parent, w / 16, h / 16, GLTexture.FLOAT);
		
		
	}

	
	
	
	@Override
	public BBlob[] update(PImage captureImg) {
		// TODO Auto-generated method stub
		//bloBable.set = new BLOBable_GRADIENT(sample_img);
		
//		bloBable = new BLOBable_GRADIENT(captureImg);
//		blob_detector.setBLOBable(bloBable);
		
		blob_detector.update();

		// get a list of all the blobs
		ArrayList<Blob> blob_list = blob_detector.getBlobs();

		ArrayList<BBlob> blobs = new ArrayList<BBlob>();

		for (int i = 0; i < blob_list.size(); i++) {
			Blob blob = blob_list.get(i);
			BBlob bBlob = new BBlob();
			bBlob.id = blob.getID();
			PVector centroid = new PVector();
			Pixel[] pixels = blob.getPixels();
			if (pixels == null)
				continue;
			bBlob.points = new PVector[blob.getNumberOfPixels()];
			for (int j = 0; j < blob.getNumberOfPixels(); j++) {

				Pixel pixel = pixels[j];
				bBlob.points[j] = new PVector(pixel.x_, pixel.y_);
				centroid.x += pixel.x_;
				centroid.y += pixel.y_;
			}
			centroid.x /= (float) blob.getNumberOfPixels();
			centroid.y /= (float) blob.getNumberOfPixels();
			bBlob.centroid = centroid;
			blobs.add(bBlob);
		}

		this.blobs = blobs.toArray(new BBlob[blobs.size()]);

		return this.blobs;
	}

	@Override
	public void captureBackground() {
		takeBackground = true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.demodrama.cv.blobDetector.BlobDetector#drawBlurImg(int, int,
	 * float)
	 */
	@Override
	public void drawBlurImg(int x, int y, float scale) {
		parent.noFill();
		parent.stroke(255);
		parent.rect(x, y, (int) (captureWidth * scale),
				(int) (captureHeight * scale));
		parent.fill(255);
		parent.textAlign(PApplet.CENTER);
		parent.text("Blur image\nnot available",
				(int) (x + (captureWidth * scale) / 2),
				(int) (y + (captureHeight * scale) / 2));
		parent.textAlign(PApplet.LEFT);
		
//		
//	    float fx = PApplet.parseFloat(parent.mouseX) / parent.width;
//	    float fy = PApplet.parseFloat(parent.mouseY) / parent.height;
//
//	    // Extracting the bright regions from input texture.
//	    extractBloom.setParameterValue("bright_threshold", fx);
//	    extractBloom.apply(srcTex, tex0);
//	  
//	    // Downsampling with blur.
//	    tex0.filter(blur, tex2);
//	    tex2.filter(blur, tex4);    
//	    tex4.filter(blur, tex8);    
//	    tex8.filter(blur, tex16);     
//	    
//	    // Blending downsampled textures.
//	    blend4.apply(new GLTexture[]{tex2, tex4, tex8, tex16}, new GLTexture[]{bloomMask});
//	    
//	    // Final tone mapping into destination texture.
//	    toneMap.setParameterValue("exposure", fy);
//	    toneMap.setParameterValue("bright", fx);
//	    toneMap.apply(new GLTexture[]{srcTex, bloomMask}, new GLTexture[]{destTex});
//
//	    parent.image(srcTex, 0, 0, 320, 240);
//	    	parent.image(tex16, 320, 0, 320, 240);
//	    	parent.image(bloomMask, 0, 240, 320, 240);
//	    	parent.image(destTex, 320, 240, 320, 240);      
//	        
//	    	parent.fill(220, 20, 20);
//	    	parent.text("source texture", 10, 230);
//	    	parent. text("downsampled texture", 330, 230);
//	    	parent. text("bloom mask", 10, 470);        
//	    	parent. text("final texture", 330, 470);        
//	   

		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.demodrama.cv.blobDetector.BlobDetector#drawHighPassImg(int, int,
	 * float)
	 */
	@Override
	public void drawHighPassImg(int x, int y, float scale) {
		parent.noFill();
		parent.stroke(255);
		parent.rect(x, y, (int) (captureWidth * scale),
				(int) (captureHeight * scale));
		parent.fill(255);
		parent.textAlign(PApplet.CENTER);
		parent.text("HighPass image\nnot available",
				(int) (x + (captureWidth * scale) / 2),
				(int) (y + (captureHeight * scale) / 2));
		parent.textAlign(PApplet.LEFT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.demodrama.cv.blobDetector.BlobDetector#drawAmplifiedImg(int,
	 * int, float)
	 */
	@Override
	public void drawAmplifiedImg(int x, int y, float scale) {
		parent.noFill();
		parent.stroke(255);
		parent.rect(x, y, (int) (captureWidth * scale),
				(int) (captureHeight * scale));
		parent.fill(255);
		parent.textAlign(PApplet.CENTER);
		parent.text("Amplified image\nnot available",
				(int) (x + (captureWidth * scale) / 2),
				(int) (y + (captureHeight * scale) / 2));
		parent.textAlign(PApplet.LEFT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.demodrama.cv.blobDetector.BlobDetector#drawThresholdImg(int,
	 * int, float)
	 */
	@Override
	public void drawThresholdImg(int x, int y, float scale) {
		parent.noFill();
		parent.stroke(255);
		parent.rect(x, y, (int) (captureWidth * scale),
				(int) (captureHeight * scale));
		parent.fill(255);
		parent.textAlign(PApplet.CENTER);
		parent.text("Threshold image\nnot available",
				(int) (x + (captureWidth * scale) / 2),
				(int) (y + (captureHeight * scale) / 2));
		parent.textAlign(PApplet.LEFT);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.demodrama.cv.blobDetector.BlobDetector#drawBackground(int, int,
	 * float)
	 */
	@Override
	public void drawBackground(int x, int y, float scale) {
		// PImage imgTemp = opencv.getMemory();
		// if (imgTemp != null) {
		// imgTemp.resize((int)(captureWidth*scale),
		// (int)(captureHeight*scale));
		// parent.image(imgTemp, x, y);
		// }
		// else {
		parent.noFill();
		parent.stroke(255);
		parent.rect(x, y, (int) (captureWidth * scale),
				(int) (captureHeight * scale));
		parent.fill(255);
		parent.textAlign(PApplet.CENTER);
		parent.text("Background image\nnot available",
				(int) (x + (captureWidth * scale) / 2),
				(int) (y + (captureHeight * scale) / 2));
		parent.textAlign(PApplet.LEFT);
		// }
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.demodrama.cv.blobDetector.BlobDetector#drawBlobs(int, int,
	 * float)
	 */
	@Override
	public void drawBlobs(int x, int y, float scale) {

		// OpenCV Scale dont work :(

		// opencv.drawCentroidBlobs(blobs, x, y, scale); // trace
		// opencv.drawRectBlobs(blobs, x, y, scale); // trace
		// opencv.drawBlobs(blobs, x, y, scale); // trace
		parent.pushMatrix();
		parent.translate(x, y);
		float lastX;
		float lastY;
		for (int i = 0; i < blobs.length; i++) {
			BBlob b = blobs[i];
			parent.stroke(0, 100, 255);
			// for (int j = 1; j < b.points.length; j++) {
			// parent.line(b.points[j].x * scale, b.points[j].y * scale,
			// b.points[j - 1].x * scale, b.points[j - 1].y * scale);
			// }
			parent.stroke(0, 255, 255);
			parent.noFill();
			// parent.rect(b.rectangle.x * scale, b.rectangle.y * scale,
			// b.rectangle.width * scale, b.rectangle.height * scale);
			parent.stroke(255, 0, 0);
			parent.ellipse(b.centroid.x * scale, b.centroid.y * scale, 5, 5);
			if(drawIds == 1)
			 parent.text(""+b.id, b.centroid.x * scale +3, b.centroid.y * scale + 3);
		}
		parent.popMatrix();

	}

	public void setBlur(float blur) {
		// TODO Auto-generated method stub
		this.blurFactor = (int) blur;
	}

	public void setThreshold(float threshold) {
		// TODO Auto-generated method stub
		bloBable.threshold = threshold;
		this.threshold = threshold;
	}

}
