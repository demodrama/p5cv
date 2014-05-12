package org.demodrama.cv.tuioTracker;

import org.demodrama.cv.blobDetector.BBlob;
import org.demodrama.cv.calibrator.QuadCalibrator;

import processing.core.PVector;

import monclubelec.javacvPro.Blob;
import TUIO.TuioContainer;
import TUIO.TuioCursor;
import processing.core.PApplet;

public class TuioBlobTrackerModIDs {
	TrackedBlob[] trackedBlobs = new TrackedBlob[0];
	int globalTouchPointIndex = 0;

	public TuioCursor[] process(BBlob[] blobs, QuadCalibrator calibrator) {
		int rawBlobsNumber = blobs.length;
		//int goodBlobsNumber = 0;

		// Create the new tracked blobs array
		TrackedBlob[] newTrackedBlobs = new TrackedBlob[rawBlobsNumber];
		for (int i = 0; i < rawBlobsNumber; i++) {
			PVector tmpVec = calibrator.getScreenCoords((int)blobs[i].centroid.x,
					(int)blobs[i].centroid.y);
			tmpVec.x /= calibrator.screenWidth;
			tmpVec.y /= calibrator.screenHeight;
			newTrackedBlobs[i] = new TrackedBlob(blobs[i].id, tmpVec.x,
					tmpVec.y, false);
		}



		// If we are getting more blobs
		if (newTrackedBlobs.length >= trackedBlobs.length) {

			for (int nt = 0; nt < newTrackedBlobs.length; nt++) {
				int oldIndex = -1;

				for (int t = 0; t < trackedBlobs.length; t++) {
					
					if(newTrackedBlobs[nt].tuioCursor.getCursorID() == trackedBlobs[t].tuioCursor.getCursorID()){ 
						oldIndex = nt;
						newTrackedBlobs[nt].setTuioCursor(new TuioCursor(
								trackedBlobs[t].getTuioCursor()));
						trackedBlobs[t].update(newTrackedBlobs[oldIndex].getX(),
								newTrackedBlobs[oldIndex].getY()); // Update tuioCursor
						trackedBlobs[t].clearTuioPath();
						trackedBlobs[t].linked = true;
						break;
					}
				}

				if(oldIndex == -1) {
					newTrackedBlobs[nt].setState(TuioContainer.TUIO_ADDED);
				}

			}
			
		} else { // if we are getting less blobs than last frame

			for (int i = 0; i < trackedBlobs.length; i++) {
				trackedBlobs[i].setLinked(false);
			}

			for (int t = 0; t < trackedBlobs.length; t++) {
				int newIndex = -1;
				for (int nt = 0; nt < newTrackedBlobs.length; nt++) {
					if(newTrackedBlobs[nt].tuioCursor.getCursorID() == trackedBlobs[t].tuioCursor.getCursorID()){ 
						newIndex = t;
						trackedBlobs[t].update(newTrackedBlobs[nt].getX(),
								newTrackedBlobs[nt].getY()); // Update tuioCursor
						trackedBlobs[t].clearTuioPath();
						newTrackedBlobs[nt].setTuioCursor(new TuioCursor(
								trackedBlobs[t].getTuioCursor())); // Transfer
																			// tuioCursor
																			// to
																			// new
																			// blob
						newTrackedBlobs[nt].setId(trackedBlobs[t].getId());
						break;
					}
				}
			
//				if (newIndex == -1) {
//			
//				}
			}
		}

		trackedBlobs = newTrackedBlobs;

		// Create the tuio cursor array
		TuioCursor[] tuioCursors = new TuioCursor[trackedBlobs.length];
		for (int i = 0; i < tuioCursors.length; i++) {
			tuioCursors[i] = new TuioCursor(trackedBlobs[i].getTuioCursor());
		}

		return tuioCursors;
	}

}
