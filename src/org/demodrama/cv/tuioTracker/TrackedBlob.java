package org.demodrama.cv.tuioTracker;

import java.util.Vector;
import TUIO.*;


public class TrackedBlob {
	TuioCursor tuioCursor;
	boolean linked;

	public TrackedBlob(long id, float tx, float ty, boolean tLinked) {
		tuioCursor = new TuioCursor(id, (int)id, tx, ty);
		linked = tLinked;
	}
	
	public void update(float tx, float ty)
	{
		tuioCursor.update(tx,ty);
	}
	
	public void setLinked(boolean linked){
		this.linked = linked;
	}
	
	public boolean isLinked(){
		return linked;
	}
	
	public float getX(){
		return tuioCursor.getX();
	}
	
	public float getY() {
		return tuioCursor.getY();
	}
	
	public long getId() {
		return tuioCursor.getSessionID();
	}
	
	/** 
	 * Set new sesion id, reset tuio point 
	 * 
	 * @param id
	 */
	
	public void setId(long id){
		tuioCursor = new TuioCursor((int)id,(int)id,tuioCursor.getX(),tuioCursor.getY());
	}
	
	public void setState(int tuioState){
		tuioCursor.setTuioState(tuioState);
	}
	
	public void clearTuioPath(){
		Vector<TuioPoint> path = tuioCursor.getPath();
		path.clear();
	}
	
	public TuioCursor getTuioCursor(){
		return tuioCursor;
	}
	
	public void setTuioCursor(TuioCursor tuioCursor){
		this.tuioCursor = tuioCursor;
	}
}
