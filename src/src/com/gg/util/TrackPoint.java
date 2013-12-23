package com.gg.util;


public class TrackPoint extends Point{
	public long birth;
	
	public TrackPoint(float x , float y , long birth) {
		super(x ,y);
		this.birth = birth;
	}
	
	public TrackPoint() {
		super(0 ,0);
		birth = 0;
	}
	
	public TrackPoint leftPoint(TrackPoint pre , TrackPoint cur , long birth , long lastBirth) {
		float angle = (float) (Math.atan2(cur.y - pre.y, cur.x - pre.x));
		float len = (1 - (lastBirth - birth) / 300) * 5;
		TrackPoint point = new TrackPoint((float) (cur.x + len * Math.cos(angle + Math.PI / 2)) ,
				(float) (cur.y + len * Math.sin(angle + Math.PI / 2)) , System.currentTimeMillis());
		return point;
	}
	
	public TrackPoint rightPoint(TrackPoint pre , TrackPoint cur , long birth, long lastBirth) {
		float angle = (float) (Math.atan2(cur.y - pre.y, cur.x - pre.x));
		float len = (1 - (lastBirth- birth) / 300) * 5;
		TrackPoint point = new TrackPoint((float) (cur.x - len * Math.cos(angle + Math.PI / 2)) ,
				(float) (cur.y - len * Math.sin(angle + Math.PI / 2)) , System.currentTimeMillis());
		return point;
	}
}
