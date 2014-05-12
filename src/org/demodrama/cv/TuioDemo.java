package org.demodrama.cv;

import processing.core.*;

import TUIO.*;

import java.applet.*;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.FocusEvent;
import java.awt.Image;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;
import java.util.zip.*;
import java.util.regex.*;

import org.demodrama.cv.calibrator.QuadCalibrator;

public class TuioDemo extends PApplet {

	/*
	 * TUIO processing demo - part of the reacTIVision project
	 * http://reactivision.sourceforge.net/
	 * 
	 * Copyright (c) 2005-2009 Martin Kaltenbrunner <mkalten@iua.upf.edu>
	 * 
	 * This program is free software; you can redistribute it and/or modify it
	 * under the terms of the GNU General Public License as published by the
	 * Free Software Foundation; either version 2 of the License, or (at your
	 * option) any later version.
	 * 
	 * This program is distributed in the hope that it will be useful, but
	 * WITHOUT ANY WARRANTY; without even the implied warranty of
	 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
	 * Public License for more details.
	 * 
	 * You should have received a copy of the GNU General Public License along
	 * with this program; if not, write to the Free Software Foundation, Inc.,
	 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
	 */

	// we need to import the TUIO library
	// and declare a TuioProcessing client variable

	TuioProcessing tuioClient;

	// these are some helper variables which are used
	// to create scalable graphical feedback
	float cursor_size = 15;
	float object_size = 60;
	float table_size = 760;
	float scale_factor = 1;
	PFont font;

	QuadCalibrator calibrator;
	boolean calibMode = false;

	public void setup() {
		size(1024, 768);
		// size(640, 480);
		noStroke();
		fill(0);

		loop();
		frameRate(30);
		// noLoop();

		hint(ENABLE_NATIVE_FONTS);
		font = createFont("Arial", 18);
		scale_factor = height / table_size;

		// we create an instance of the TuioProcessing client
		// since we add "this" class as an argument the TuioProcessing class
		// expects
		// an implementation of the TUIO callback methods (see below)
		tuioClient = new TuioProcessing(this);

		calibrator = new QuadCalibrator(this, 1024, 768, 1024, 768, 20);
		calibrator.load("calibrator.xml");
	}

	// within the draw method we retrieve a Vector (List) of TuioObject and
	// TuioCursor (polling)
	// from the TuioProcessing client and then loop over both lists to draw the
	// graphical feedback.
	public void draw() {
		if (calibMode) {
			// calibrator.draw(tuioClient.getTuioCursors(), topRightCameraBlob,
			// downRightCameraBlob, downLeftCameraBlob)
			Vector tuioCursors = tuioClient.getTuioCursors();
			if (tuioCursors.size() == 1) {
				TuioCursor tcur = (TuioCursor) tuioCursors.elementAt(0);
				calibrator.draw(new PVector(tcur.getScreenX(width), tcur
						.getScreenY(height)));
			} else
				background(0);
		} else
			drawNormal();
		
	//	TuioCursor tCurr = new TuioCursor(si, ci, xp, yp);
	}

	private void drawNormal() {
		
		background(255);
		text(frameRate, 10, 10);
		textFont(font, 18 * scale_factor);
		float cur_size = cursor_size * scale_factor;

		Vector ccvTuioCursorList = tuioClient.getTuioCursors();
		Vector tuioCursorList = new Vector();
		for (int i = 0; i < ccvTuioCursorList.size(); i++) {
			TuioCursor tcur = (TuioCursor) ccvTuioCursorList.get(i);
			PVector posTcur = calibrator.getScreenCoords(
					tcur.getScreenX(width), tcur.getScreenY(height));
			TuioCursor newTCur = new TuioCursor(tcur.getSessionID(),
					tcur.getCursorID(), posTcur.x / width, posTcur.y / height);
			tuioCursorList.add(newTCur);
		}

		
		for (int i = 0; i < tuioCursorList.size(); i++) {
			TuioCursor tcur = (TuioCursor) tuioCursorList.elementAt(i);
			fill(255, 0, 0);
			ellipse(tcur.getScreenX(width), tcur.getScreenY(height), 10, 10);
			fill(0);
			text("" + tcur.getCursorID(), tcur.getScreenX(width) - 5, tcur.getScreenY(height) + 5);
		}
	}

	// these callback methods are called whenever a TUIO event occurs

	// called when an object is added to the scene
	public void addTuioObject(TuioObject tobj) {
		// println("add object " + tobj.getSymbolID() + " (" +
		// tobj.getSessionID()
		// + ") " + tobj.getX() + " " + tobj.getY() + " "
		// + tobj.getAngle());
	}

	// called when an object is removed from the scene
	public void removeTuioObject(TuioObject tobj) {
		// println("remove object " + tobj.getSymbolID() + " ("
		// + tobj.getSessionID() + ")");
	}

	// called when an object is moved
	public void updateTuioObject(TuioObject tobj) {
		// println("update object " + tobj.getSymbolID() + " ("
		// + tobj.getSessionID() + ") " + tobj.getX() + " " + tobj.getY()
		// + " " + tobj.getAngle() + " " + tobj.getMotionSpeed() + " "
		// + tobj.getRotationSpeed() + " " + tobj.getMotionAccel() + " "
		// + tobj.getRotationAccel());
	}

	// called when a cursor is added to the scene
	public void addTuioCursor(TuioCursor tcur) {
		println("add cursor " + tcur.getCursorID() + " (" + tcur.getSessionID()
				+ ") " + tcur.getX() + " " + tcur.getY());
	}

	// called when a cursor is moved
	public void updateTuioCursor(TuioCursor tcur) {
		println("update cursor " + tcur.getCursorID() + " ("
				+ tcur.getSessionID() + ") " + tcur.getX() + " " + tcur.getY()
				+ " " + tcur.getMotionSpeed() + " " + tcur.getMotionAccel());
	}

	// called when a cursor is removed from the scene
	public void removeTuioCursor(TuioCursor tcur) {
		println("remove cursor " + tcur.getCursorID() + " ("
				+ tcur.getSessionID() + ")");
	}

	// called after each message bundle
	// representing the end of an image frame
	public void refresh(TuioTime bundleTime) {
		// redraw();
	}

	public void keyPressed() {

		if (key == 'c')
			calibMode = !calibMode;
		else if (key == 's') {
			calibrator.save("calibrator.xml");
		}

	}

	public void mousePressed() {
		if (calibMode) {
			calibrator.mousePressed();
		}
	}

	public void mouseDragged() {
		if (calibMode) {
			calibrator.mouseDragged();
		}
	}

	@Override
	public void mouseReleased() {
		if (calibMode) {
			calibrator.mouseReleased();
		}
	}

	static public void main(String args[]) {
		PApplet.main(new String[] { "--bgcolor=#ECE9D8",
				"org.demodrama.cv.TuioDemo" });
	}
}
