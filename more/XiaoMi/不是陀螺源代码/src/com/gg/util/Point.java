package com.gg.util;

/*		表示3D坐标点的逻辑类		*/
public class Point {

	public float x;
	public float y;
	public float z;
	
	public Point(float x, float y, float z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Point(float x, float y){
		this(x,y,0f);
	}
}
