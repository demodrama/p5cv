package org.demodrama.cv.blobDetector;

import diewald_CV_kit.blobdetection.BLOBable;
import diewald_CV_kit.utility.PixelColor;
import processing.core.PApplet;
import processing.core.PImage;

// new BLOBable class, that implements the BLOBable-interface.
public final class BLOBable_GRADIENT implements BLOBable {
	int width_, height_;
	private float hsb_[] = new float[3];
	private float mousex_val_, mousey_val_;
	private String name_;
	private PImage img_;
	public float threshold;
	
	public BLOBable_GRADIENT( PImage img) {
		img_ = img;
	}

	// @Override
	public final void init() {
		name_ = this.getClass().getSimpleName();
	}

	// @Override
	public final void updateOnFrame(int width, int height) {
		width_ = width;
		height_ = height;
		if (mousex_val_ > 98)
			mousex_val_ = 98;
		// System.out.println("MY NAME IS: "
		// +this.getClass().getSimpleName());
	}

	// @Override
	public final boolean isBLOBable(int pixel_index, int x, int y) {
		if (PixelColor.brighntess(img_.pixels[pixel_index]) > threshold) {
			return true;
		} else {
			return false;
		}
	}
}
