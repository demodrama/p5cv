package org.demodrama.cv.calibrator;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.OutputStream;

import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;
import processing.xml.XMLElement;
import processing.xml.XMLWriter;

public class QuadCalibrator {

	int cameraWidth, cameraHeight;
	public int screenWidth, screenHeight;

	int res; // mesh resolution

	CalibPoint[] mesh = null;
	PApplet parent;
	QuadWarper warper;
	CalibPoint[] warperPoints = null;

	PVector unwrappedCameraPos;
	PVector defaultSrcMargin = new PVector(0, 0); // default margins for the
													// calibration point in the
													// source
	PVector defaultDestMargin = new PVector(0, 0);
	int warperPointColor = 0xFFAAAA00;

	int destPointHandlerRadio = 10;
	int srcPointHandlerRadio = 5;
	CalibPoint draggedPoint = null;

	public boolean warpedMode = true;

	public QuadCalibrator(PApplet parent, int cameraWidth, int cameraHeight,
			int screenWidth, int screenHeight, int res) {
		this.parent = parent;
		this.cameraWidth = cameraWidth;
		this.cameraHeight = cameraHeight;
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;

		unwrappedCameraPos = new PVector((screenWidth - cameraWidth) / 2,
				(screenHeight - cameraHeight) / 2);
		res++;
		this.res = res;
		// initialize the point array
		mesh = new CalibPoint[res * res];
		for (int i = 0; i < mesh.length; i++) {
			float x = (i % res) / (float) (res - 1);
			float y = (i / res) / (float) (res - 1);
			mesh[i] = new CalibPoint(x * screenWidth, y * screenHeight, x
					* cameraWidth, y * cameraHeight);
		}
	}

	/**
	 * Loads a saved layout from a given XML file
	 */
	public void load(String filename) {
		
		float xSrcTL, ySrcTL, xDestTL, yDestTL, xSrcTR, ySrcTR, xDestTR, yDestTR, 
		      xSrcDR, ySrcDR, xDestDR, yDestDR, xSrcDL, ySrcDL, xDestDL, yDestDL; 
		
		File f = new File(parent.dataPath(filename));
		if(f.exists()) {

			XMLElement root = new XMLElement(parent, parent.dataPath(filename));
			XMLElement quadWarper = root.getChild("quadWarper");
			XMLElement topLeft = quadWarper.getChild("topLeft");
			xSrcTL = topLeft.getFloat("srcX", 0 + defaultSrcMargin.x);
			ySrcTL = topLeft.getFloat("srcY", 0 + defaultSrcMargin.y);
			xDestTL = topLeft.getFloat("destX", 0 + defaultDestMargin.x);
			yDestTL = topLeft.getFloat("destY", 0 + defaultDestMargin.y);
			XMLElement topRight = quadWarper.getChild("topRight");
			xSrcTR = topRight.getFloat("srcX", cameraWidth
					- defaultSrcMargin.x);
			ySrcTR = topRight.getFloat("srcY", 0 + defaultSrcMargin.y);
			xDestTR = topRight.getFloat("destX", screenWidth
					- defaultDestMargin.x);
			yDestTR = topRight.getFloat("destY", 0 + defaultDestMargin.y);
			XMLElement downRight = quadWarper.getChild("downRight");
			xSrcDR = downRight.getFloat("srcX", cameraWidth
					- defaultSrcMargin.x);
			ySrcDR = downRight.getFloat("srcY", cameraHeight
					- defaultSrcMargin.y);
			xDestDR = downRight.getFloat("destX", screenWidth
					- defaultDestMargin.x);
			yDestDR = downRight.getFloat("destY", screenHeight
					- defaultDestMargin.y);
			XMLElement downLeft = quadWarper.getChild("downLeft");
			xSrcDL = downLeft.getFloat("srcX", 0 + defaultSrcMargin.x);
			ySrcDL = downLeft.getFloat("srcY", cameraHeight
					- defaultSrcMargin.y);
			xDestDL = downLeft.getFloat("destX", 0 + defaultDestMargin.x);
			yDestDL = downLeft.getFloat("destY", 0 + defaultDestMargin.y);

		} else {
			xSrcTL = defaultSrcMargin.x;
			ySrcTL = defaultSrcMargin.y;
			xDestTL = defaultDestMargin.x;
			yDestTL = defaultDestMargin.y;
			xSrcTR =  cameraWidth- defaultSrcMargin.x;
			ySrcTR = defaultSrcMargin.y;
			xDestTR = screenWidth - defaultDestMargin.x;
			yDestTR = defaultDestMargin.y;
			xSrcDR = cameraWidth - defaultSrcMargin.x;
			ySrcDR = cameraHeight - defaultSrcMargin.y;
			xDestDR = screenWidth - defaultDestMargin.x;
			yDestDR = screenHeight - defaultDestMargin.y;
			xSrcDL = defaultSrcMargin.x;
			ySrcDL = cameraHeight - defaultSrcMargin.y;
			xDestDL = defaultDestMargin.x;
			yDestDL = screenHeight - defaultDestMargin.y;				
		}
		
		
		warper = new QuadWarper(xSrcTL, ySrcTL, xSrcTR, ySrcTR, xSrcDR, ySrcDR,
				xSrcDL, ySrcDL, xDestTL, yDestTL, xDestTR, yDestTR, xDestDR,
				yDestDR, xDestDL, yDestDL);
		PApplet.println("QuadCalibrator: layout loaded from " + filename);
		updateMesh();
		warperPoints = warper.getPoints();
	}

	public void save(String filename) {
		XMLElement root = new XMLElement("quadCalibrator");
		XMLElement quadWarper = new XMLElement("quadWarper");
		XMLElement topLeft = new XMLElement("topLeft");
		topLeft.setFloat("srcX", warperPoints[QuadWarper.TL].u);
		topLeft.setFloat("srcY", warperPoints[QuadWarper.TL].v);
		topLeft.setFloat("destX", warperPoints[QuadWarper.TL].x);
		topLeft.setFloat("destY", warperPoints[QuadWarper.TL].y);
		quadWarper.addChild(topLeft);
		XMLElement topRight = new XMLElement("topRight");
		topRight.setFloat("srcX", warperPoints[QuadWarper.TR].u);
		topRight.setFloat("srcY", warperPoints[QuadWarper.TR].v);
		topRight.setFloat("destX", warperPoints[QuadWarper.TR].x);
		topRight.setFloat("destY", warperPoints[QuadWarper.TR].y);
		quadWarper.addChild(topRight);
		XMLElement downRight = new XMLElement("downRight");
		downRight.setFloat("srcX", warperPoints[QuadWarper.DR].u);
		downRight.setFloat("srcY", warperPoints[QuadWarper.DR].v);
		downRight.setFloat("destX", warperPoints[QuadWarper.DR].x);
		downRight.setFloat("destY", warperPoints[QuadWarper.DR].y);
		quadWarper.addChild(downRight);
		XMLElement downLeft = new XMLElement("downLeft");
		downLeft.setFloat("srcX", warperPoints[QuadWarper.DL].u);
		downLeft.setFloat("srcY", warperPoints[QuadWarper.DL].v);
		downLeft.setFloat("destX", warperPoints[QuadWarper.DL].x);
		downLeft.setFloat("destY", warperPoints[QuadWarper.DL].y);
		quadWarper.addChild(downLeft);
		root.addChild(quadWarper);

		try {
			OutputStream stream = parent
					.createOutput(parent.dataPath(filename));
			XMLWriter writer = new XMLWriter(stream);
			writer.write(root, true);
		} catch (Exception e) {
			PApplet.println(e.getStackTrace());
		}
		PApplet.println("QuadCalibrator: layout saved to " + filename);

	}

	public void updateMesh() {
		for (int i = 0; i < mesh.length; i++) {
			CalibPoint cp = mesh[i];
			PVector point = warper.getInvTransformedCursor((int) cp.u,
					(int) cp.v);
			mesh[i].x = (float) point.x;
			mesh[i].y = (float) point.y;
		}
	}

	public PVector getScreenCoords(int x, int y) {
		return warper.getInvTransformedCursor(x, y);
	}

	public void draw(PImage texture) {
		if (warpedMode) {
			drawWarped(texture);
		} else {
			drawUnwarped(texture);
		}
	}

	PVector targetCameraBlob;
	
	public void draw(PVector targetCameraBlob) {
		warpedMode = true;
		this.targetCameraBlob = targetCameraBlob;
		parent.background(0);
		renderSrcPoints(0xFFFF0000);
		renderDestPoints(0xFFFFFFFF);
	}

	public void drawWarped(PImage texture) {
		//parent.fill(255);
		parent.beginShape(PApplet.QUADS);
		parent.texture(texture);
		for (int x = 0; x < res - 1; x++) {
			for (int y = 0; y < res - 1; y++) {
				CalibPoint cp;
				cp = mesh[(x) + (y) * res];
				parent.vertex(cp.x, cp.y, cp.u, cp.v);
				cp = mesh[(x + 1) + (y) * res];
				parent.vertex(cp.x, cp.y, cp.u, cp.v);
				cp = mesh[(x + 1) + (y + 1) * res];
				parent.vertex(cp.x, cp.y, cp.u, cp.v);
				cp = mesh[(x) + (y + 1) * res];
				parent.vertex(cp.x, cp.y, cp.u, cp.v);
			}
		}
		parent.endShape(PApplet.CLOSE);
		renderDestPoints(0xFFAAAA00);
		parent.textAlign(PApplet.CENTER);
		parent.textSize(15);
		parent.fill(255);
		parent.text("Modo pantalla", screenWidth / 2, screenHeight - 80);
		parent.text(
				"Arrastra los puntos amarillos hasta delimitar area de interaccion",
				screenWidth / 2, screenHeight - 60);
		parent.text("Pulsa 'w' para pasar al modo camara", screenWidth / 2,
				screenHeight - 40);
		parent.textAlign(PApplet.LEFT);
	}

	private void renderDestPoints(int color) {
		parent.fill(color);
		parent.noStroke();
		for (int i = 0; i < warperPoints.length; i++) {
			parent.ellipse(warperPoints[i].x, warperPoints[i].y,
					destPointHandlerRadio * 2, destPointHandlerRadio * 2);
		}
		parent.noFill();
	}

	public void drawUnwarped(PImage texture) {
		parent.pushMatrix();
		parent.translate(unwrappedCameraPos.x, unwrappedCameraPos.y);
		parent.image(texture, 0, 0);
		renderSrcPoints(0xFFAAAA00);
		parent.popMatrix();
		renderDestPoints(0xFFFFFFFF);
		parent.textAlign(PApplet.CENTER);
		parent.textSize(15);
		parent.fill(255);
		parent.text("Modo camara", screenWidth / 2, screenHeight - 80);
		parent.text(
				"Arrastra los puntos amarillos sobre la posición en camara de los puntos blancos",
				screenWidth / 2, screenHeight - 60);
		parent.text("Pulsa 'w' para pasar al modo pantalla", screenWidth / 2,
				screenHeight - 40);
		parent.textAlign(PApplet.LEFT);
	}

	private void renderSrcPoints(int color) {
		parent.fill(255, 255, 0);
		parent.noStroke();
		for (int i = 0; i < warperPoints.length; i++) {
			parent.ellipse(warperPoints[i].u, warperPoints[i].v,
					srcPointHandlerRadio * 2, srcPointHandlerRadio * 2);
			if(i == QuadWarper.TL) {
				parent.text("TL", warperPoints[i].u + 5, warperPoints[i].v + 5);
			} else if(i == QuadWarper.TR) {
				parent.text("TR", warperPoints[i].u + 5, warperPoints[i].v + 5);
			} else if(i == QuadWarper.DR) {
				parent.text("DR", warperPoints[i].u + 5, warperPoints[i].v + 5);
			} else if(i == QuadWarper.DL) {
				parent.text("DL", warperPoints[i].u + 5, warperPoints[i].v + 5);				
			}
		}
		parent.noFill();
	}

	public void mousePressed() {
		if (warpedMode) {
			for (int i = 0; i < warperPoints.length; i++) {
				if (PApplet.dist(warperPoints[i].x, warperPoints[i].y,
						parent.mouseX, parent.mouseY) < destPointHandlerRadio) {
					draggedPoint = warperPoints[i];
					if(targetCameraBlob != null) {
						draggedPoint.u = targetCameraBlob.x;
						draggedPoint.v = targetCameraBlob.y;
					}
					break;
				}
			}
		} else {
			for (int i = 0; i < warperPoints.length; i++) {
				float x = parent.mouseX - unwrappedCameraPos.x;
				float y = parent.mouseY - unwrappedCameraPos.y;
				if (PApplet.dist(warperPoints[i].u, warperPoints[i].v, x, y) < destPointHandlerRadio) {
					draggedPoint = warperPoints[i];
					break;
				}
			}
		}
	}

	public void mouseDragged() {
		if (warpedMode) {
			if (parent.mouseButton == PApplet.LEFT) {
				if (draggedPoint != null) {
					draggedPoint.x += parent.mouseX - parent.pmouseX;
					draggedPoint.y += parent.mouseY - parent.pmouseY;
					
					if(targetCameraBlob != null) {
						draggedPoint.u = targetCameraBlob.x;
						draggedPoint.v = targetCameraBlob.y;
					}
					
				}
			}
//			else if (parent.mouseButton == PApplet.RIGHT) {
//				for (int i = 0; i < warperPoints.length; i++) {
//					warperPoints[i].x += parent.mouseX - parent.pmouseX;
//					warperPoints[i].y += parent.mouseY - parent.pmouseY;
//				}
//			}
		} else {
			if (draggedPoint != null) {
				draggedPoint.u += parent.mouseX - parent.pmouseX;
				draggedPoint.v += parent.mouseY - parent.pmouseY;
			}
		}
		warper.calculateMesh();
		updateMesh();
	}

	public void mouseReleased() {
		draggedPoint = null;
	}

}
