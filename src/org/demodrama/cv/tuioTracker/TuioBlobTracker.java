package org.demodrama.cv.tuioTracker;

import org.demodrama.cv.blobDetector.BBlob;
import org.demodrama.cv.calibrator.QuadCalibrator;

import processing.core.PVector;

import monclubelec.javacvPro.Blob;
import TUIO.TuioContainer;
import TUIO.TuioCursor;
import processing.core.PApplet;

public class TuioBlobTracker {
	TrackedBlob[] trackedBlobs = new TrackedBlob[0];
	int globalTouchPointIndex = 0;

	public TuioCursor[] process(BBlob[] blobs, QuadCalibrator calibrator, float cameraWidth, float cameraHeight) {
		int rawBlobsNumber = blobs.length;
		int goodBlobsNumber = 0;

		// Create the new tracked blobs array
		TrackedBlob[] newTrackedBlobs = new TrackedBlob[rawBlobsNumber];
		for (int i = 0; i < rawBlobsNumber; i++) {
			//PVector tmpVec = calibrator.getScreenCoords((int)blobs[i].centroid.x,
			//		(int)blobs[i].centroid.y);
			//tmpVec.x /= calibrator.screenWidth;
			//tmpVec.y /= calibrator.screenHeight;
			//newTrackedBlobs[goodBlobsNumber] = new TrackedBlob(0, tmpVec.x,
			//		tmpVec.y, false);
			newTrackedBlobs[goodBlobsNumber] = new TrackedBlob(0, blobs[i].centroid.x/cameraWidth,
					blobs[i].centroid.y/cameraHeight, false);
			goodBlobsNumber++;
		}
		while (newTrackedBlobs.length > goodBlobsNumber)
			newTrackedBlobs = (TrackedBlob[]) PApplet.shorten(newTrackedBlobs);

		// Compare with last tracked array
		int pLen = trackedBlobs.length;

		float minDist = 0;
		int minIndex = -1;
		float curDist = 0;

		// If we are getting more blobs
		if (goodBlobsNumber >= pLen) {

			for (int i = 0; i < pLen; i++) {
				minIndex = -1;
				curDist = 0;
				minDist = 0;

				for (int j = 0; j < goodBlobsNumber; j++) {
					if (!newTrackedBlobs[j].isLinked()) {
						curDist = PApplet.dist(trackedBlobs[i].getX(),
								trackedBlobs[i].getY(),
								newTrackedBlobs[j].getX(),
								newTrackedBlobs[j].getY());
						if (minIndex == -1 || curDist < minDist) {
							minDist = curDist;
							minIndex = j;
						}
					}
				}

				trackedBlobs[i].update(newTrackedBlobs[minIndex].getX(),
						newTrackedBlobs[minIndex].getY()); // Update tuioCursor
				trackedBlobs[i].clearTuioPath();
				newTrackedBlobs[minIndex].setTuioCursor(new TuioCursor(
						trackedBlobs[i].getTuioCursor())); // Transfer
															// tuioCursor to new
															// blob
				newTrackedBlobs[minIndex].setLinked(true);

				// Min Dist filtering
				// ///////////////////////////////////////////////////////
				// if(PVector.dist(blobPoints[minIndex],
				// blobPoints[minIndex].lastPoint) < .005) {
				// blobPoints[minIndex] = blobPoints[minIndex].lastPoint; }

				// Low Pass Filtering
				// //////////////////////////////////////////////////////
				// blobPoints[minIndex].x = blobPoints[minIndex].lastPoint.x +
				// (blobPoints[minIndex].x - blobPoints[minIndex].lastPoint.x) *
				// .5;
				// blobPoints[minIndex].y = blobPoints[minIndex].lastPoint.y +
				// (blobPoints[minIndex].y - blobPoints[minIndex].lastPoint.y) *
				// .5;

			}

			for (int i = 0; i < goodBlobsNumber; i++) {
				if (!newTrackedBlobs[i].isLinked()) { // not linked means new
														// tuio
					globalTouchPointIndex++;
					newTrackedBlobs[i].setId(globalTouchPointIndex);
					newTrackedBlobs[i].setState(TuioContainer.TUIO_ADDED);
				}
			}

		} else { // if we are getting less blobs than last frame

			for (int i = 0; i < pLen; i++) {
				trackedBlobs[i].setLinked(false);
			}

			for (int i = 0; i < goodBlobsNumber; i++) {
				minIndex = -1;
				curDist = 0;
				minDist = 0;
				for (int j = 0; j < pLen; j++) {
					if (trackedBlobs[j].isLinked()) {
					} else {
						curDist = PApplet.dist(newTrackedBlobs[i].getX(),
								newTrackedBlobs[i].getY(),
								trackedBlobs[j].getX(), trackedBlobs[j].getY());
						if (minIndex == -1 || curDist < minDist) {
							minDist = curDist;
							minIndex = j;
						}
					}
				}

				if (minIndex != -1) {
					trackedBlobs[minIndex].update(newTrackedBlobs[i].getX(),
							newTrackedBlobs[i].getY()); // Update tuioCursor
					trackedBlobs[minIndex].clearTuioPath();
					newTrackedBlobs[i].setTuioCursor(new TuioCursor(
							trackedBlobs[minIndex].getTuioCursor())); // Transfer
																		// tuioCursor
																		// to
																		// new
																		// blob
					newTrackedBlobs[i].setId(trackedBlobs[minIndex].getId());
					trackedBlobs[minIndex].setLinked(true);
				}
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
