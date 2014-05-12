package org.demodrama.cv.blobDetector;

import processing.core.PImage;

public interface BlobDetector {

	abstract BBlob[] update(PImage captureImg);

	abstract void captureBackground();

	abstract void drawBlurImg(int x, int y, float scale);

	abstract void drawHighPassImg(int x, int y, float scale);

	abstract void drawAmplifiedImg(int x, int y, float scale);

	abstract void drawThresholdImg(int x, int y, float scale);

	abstract void drawBackground(int x, int y, float scale);

	abstract void drawBlobs(int x, int y, float scale);

	void setBlur(float blur);
	
	void setThreshold(float threshold);
}