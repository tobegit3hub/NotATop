package com.gg.util;

import java.util.ArrayList;
import android.view.MotionEvent;

public class Track {
	ArrayList<TrackPoint> list;
	ArrayList<TrackPoint> drawList;
	private Boolean touchFlag;
	private long startTime;
	
	public Track() {
		touchFlag = false;

		list = new ArrayList<TrackPoint>();
		drawList = new ArrayList<TrackPoint>();
	}
	
	public ArrayList<TrackPoint> getDrawList() {
		return drawList;
	}
	
	public ArrayList<TrackPoint> getList(){
		return list;
	}
	
	public void onTouchDown(MotionEvent e) {
		list.clear();
		touchFlag = true;
		startTime = System.currentTimeMillis();
		TrackPoint point = new TrackPoint(e.getX() , e.getY() , System.currentTimeMillis());
		list.add(point);
	}
	
	public void onTouchMove(MotionEvent e) {
		if((System.currentTimeMillis() - startTime) > 300){
			touchFlag = false;
//			list.clear();
			return;
		}else{
			TrackPoint point = new TrackPoint(e.getX() , e.getY() , System.currentTimeMillis());
			list.add(point);
			setPoint(list, drawList);
		}
	}
	
	public void onTouchUp(MotionEvent e) {
		TrackPoint point = new TrackPoint(e.getX() , e.getY() , System.currentTimeMillis());
		list.add(point);
		touchFlag = false;
	}
	
	public void setPoint(ArrayList<TrackPoint> list, ArrayList<TrackPoint> drawList) {
		drawList.clear();
		long currentTime = System.currentTimeMillis();
		//这里把超过时间的点删除
		for(int i = 0; i < list.size(); i ++){            
			if((currentTime - list.get(i).birth) > 300){
				for(int j = list.size() -1 ; j > i ; j--){
					list.remove(j);
				}
				i = list.size();
			}
		}
		
		drawList.add(list.get(0));
		for(int i = 0; i < list.size() - 2; i ++){
			drawList.add(list.get(i).leftPoint(list.get(i), list.get(i + 1), list.get(i + 1).birth, list.get(list.size() - 1).birth));
			drawList.add(list.get(i).rightPoint(list.get(i), list.get(i + 1), list.get(i + 1).birth, list.get(list.size() - 1).birth));
		}
		drawList.add(list.get(list.size() - 1));
	}
}
