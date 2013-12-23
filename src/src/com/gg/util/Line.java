package com.gg.util;

/*		表示直线的逻辑类		*/
public class Line {

	private Point firstPoint;
	private Point secondPoint;
	
	private Direction direction;
	private float k;
	private float b;

	public Line(){
		
	}
	
	public Line(Point first, Point second){
		firstPoint = first;
		secondPoint = second;
		
		direction = calculateDirection(firstPoint, secondPoint);
		
		k = (secondPoint.y-firstPoint.y) / (secondPoint.x-firstPoint.x);
		b = firstPoint.y - k*firstPoint.x;
	}
	
	public Line(float x1, float y1, float x2, float y2){
		this(new Point(x1, y1, 0f), new Point(x2, y2, 0f));
	}

	
	/*		鍚勭get(),set()		*/
	public Point getFirstPoint() {
		return firstPoint;
	}

	public void setFirstPoint(Point firstPoint) {
		this.firstPoint = firstPoint;
	}

	public Point getSecondPoint() {	
		return secondPoint;	
	}

	public void setSecondPoint(Point secondPoint) {
		this.secondPoint = secondPoint;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	
	public float getB() {
		return b;
	}

	public void setB(float b) {
		this.b = b;
	}

	public float getK() {
		return k;
	}

	public void setK(float k) {
		this.k = k;
	}

	

	public Direction calculateDirection(Point firstPoint, Point secondPoint){
		if(secondPoint.x > firstPoint.x){
			if(secondPoint.y > firstPoint.y){
				return new Direction(Direction.UP_RIGHT);
			}else if(secondPoint.y < firstPoint.y){
				return new Direction(Direction.DOWN_RIGHT);
			}else{
				return new Direction(Direction.RIGHT);
			}
		}else if(secondPoint.x < firstPoint.x){
			if(secondPoint.y > firstPoint.y){
				return new Direction(Direction.UP_LEFT);
			}else if(secondPoint.y < firstPoint.y){
				return new Direction(Direction.DOWN_LEFT);
			}else{
				return new Direction(Direction.LEFT);
			}
		}else{
			if(secondPoint.y > firstPoint.y){
				return new Direction(Direction.UP);
			}else{
				return new Direction(Direction.DOWN);
			}
		}
	}

	
	public boolean isCollideCircle(Circle circle){
		float r = circle.getRadius();
		float distance = distanceCircle(circle);
		
		if(distance > r){
			return false;
		}else if(distance == r){
			return true;
		}else{	
			return true;

		}
	}
	
	
	public float distanceCircle(Circle circle){
		float x0 = circle.getCenter().x;
		float y0 = circle.getCenter().y;
		return (float) (Math.abs(y0-k*x0-b) / Math.sqrt(1+k*k));
	}
	
	
	public Direction directionCircle(Circle circle){
		float x0 = circle.getCenter().x;
		float y0 = circle.getCenter().y;
		float circleB = y0 - k*x0;
		
		if(circleB>b){
			return new Direction(Direction.DOWN);
		}else if(circleB<b){
			return new Direction(Direction.UP);
		}else{
			return new Direction(Direction.RIGHT);
		}
	}
	
}
