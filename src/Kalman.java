import processing.core.*;
import processing.opengl.*;

import java.applet.*;
import java.awt.Dimension;
import java.awt.Frame;
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

import Jama.Matrix;

/**
 * 
 * @author Kike_Monster
 * 
 * 
 *         Referencias: Teoría del Kalman Filter
 *         http://bilgin.esme.org/BitsBytes/KalmanFilterforDummies.aspx
 * 
 * 
 * 
 */

public class Kalman extends PApplet {

	//
	// KModel model;

	KalmanFilter kfilter;

	double x;
	double vx;
	double ax;
	double processNoiseStdev;
	double measurementNoiseStdev;
	double m;
	double dt;

	Random jerk;
	Random sensorNoise;

	private boolean debug = true;
	KalmanFilter KF;

	public void setup() {
		size(400, 400);
		// model = new KModel();

		x = Math.random();
		vx = Math.random();
		ax = Math.random();

		// process parameters
		dt = 1.0 / 100.0;
		processNoiseStdev = 3;
		measurementNoiseStdev = 5;
		m = 0;

		// noise generators
		jerk = new Random();
		sensorNoise = new Random();

		// init filter
		KF = buildKF(dt, pow((float) processNoiseStdev, 2) / 2,
				pow((float) measurementNoiseStdev, 2));
		KF.setX(new Matrix(new double[][] { { x }, { vx }, { ax } }));

	}

	// To use this class you will need to create a new instance, set the
	// system matrices (F, B, U, Q, H, R)

	public static KalmanFilter buildKF(double dt, double processNoisePSD,
			double measurementNoiseVariance) {

		KalmanFilter KF = new KalmanFilter();

		// state vector
		KF.setX(new Matrix(new double[][] { { 0, 0, 0 } }).transpose());

		// error covariance matrix
		KF.setP(Matrix.identity(3, 3));

		// transition matrix
		KF.setF(new Matrix(new double[][] { { 1, dt, pow((float) dt, 2) / 2 },
				{ 0, 1, dt }, { 0, 0, 1 } }));

		// input gain matrix
		KF.setB(new Matrix(new double[][] { { 0, 0, 0 } }).transpose());

		// input vector
		KF.setU(new Matrix(new double[][] { { 0 } }));

		// process noise covariance matrix
		KF.setQ(new Matrix(new double[][] {
				{ pow((float) dt, 5) / 4, pow((float) dt, 4) / 2,
						pow((float) dt, 3) / 2 },
				{ pow((float) dt, 4) / 2, pow((float) dt, 3) / 1,
						pow((float) dt, 2) / 1 },
				{ pow((float) dt, 3) / 1, pow((float) dt, 2) / 1,
						pow((float) dt, 1) / 1 } }).times(processNoisePSD));

		// measurement matrix
		KF.setH(new Matrix(new double[][] { { 1, 0, 0 } }));

		// measurement noise covariance matrix
		KF.setR(Matrix.identity(1, 1).times(measurementNoiseVariance));

		return KF;
	}

	public void draw() {
		background(255);
		updateKalman();
	
		stroke(200);
		line(width/2,0,width/2,height);

		pushMatrix();
		translate(width/2f - (float)x,height/2f);
		stroke(0);
		noFill();
		ellipse((float)KF.getX().get(0, 0),0,10f,10f);
		popMatrix();
		
		pushMatrix();
		translate(width/2f - (float)x,height/2f);
		stroke(255,0,0);
		noFill();
		ellipse((float)m,0,10f,10f);		
		popMatrix();

		
	}

	public void updateKalman() {

		ax += jerk.nextGaussian() * processNoiseStdev;
		vx += dt * ax;
		x += dt * vx + 0.5 * pow((float) dt, 2) * ax;

		// measurement realization
		m = x + sensorNoise.nextGaussian() * measurementNoiseStdev;

		if (debug) {
			// results
			System.out.println("True:");
			new Matrix(new double[][] { { x }, { vx }, { ax } }).print(3, 1);
			System.out.println("Last measurement:\n\n " + m + "\n");
			System.out.println("Estimate:");
			KF.getX().print(3, 1);
			System.out.println("Estimate Error Cov:");
			KF.getP().print(3, 3);

		}

		// filter update
		KF.predict();
		KF.correct(new Matrix(new double[][] { { m } }));
		
		
	}

	static public void main(String[] passedArgs) {
		String[] appletArgs = new String[] { "Kalman" };
		if (passedArgs != null) {
			PApplet.main(concat(appletArgs, passedArgs));
		} else {
			PApplet.main(appletArgs);
		}
	}
}
