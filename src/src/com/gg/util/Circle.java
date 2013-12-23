package com.gg.util;

/*		表示圆的逻辑类		*/
public class Circle {

	private Point center;
	private float radius;
	

	public Circle(Point center, float r){
		this.center = center;	
		radius = r;
	}
	
	public Circle(float x, float y, float r){
		//this(new Point(x, y), r);
		center = new Point(x, y, 0);
		radius = r;
	}

	public Point getCenter() {
		return center;
	}

	public void setCenter(Point center) {
		this.center = center;
	}

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}
	
	

	public boolean isCollideRectangle(float x1, float y1, float x2, float y2){
		if(center.y-radius <= y1){
			return true;
		}
		if(center.y+radius >= y2){
			return true;
		}
		if(center.x-radius <= x1){
			return true;
		}
		if(center.x+radius >= x2){
			return true;
		}
		return false;
	}
	
	public boolean isCollideCircle(Circle circle){
		float x1 = center.x;
		float y1 = center.y;
		float x2 = circle.center.x;
		float y2 = circle.center.y;	
		float distance = (float) Math.sqrt((y2-y1)*(y2-y1)+(x2-x1)*(x2-x1));
		
		if(distance<=radius+circle.radius){
			return true;
		}else{
			return false;
		}
	}
	
}

