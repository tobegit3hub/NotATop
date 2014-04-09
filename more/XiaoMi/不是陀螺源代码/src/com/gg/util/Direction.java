package com.gg.util;

/*		表示方向的逻辑类		*/
public class Direction {

	private int direction;
	
	public static final int UP = 1;
	public static final int DOWN = 2;
	public static final int LEFT = 3;
	public static final int RIGHT = 4;
	public static final int UP_LEFT = 5;
	public static final int UP_RIGHT = 6;
	public static final int DOWN_LEFT = 7;
	public static final int DOWN_RIGHT = 8;
	
	
	
	public Direction(){
		
	}
	
	public Direction(int direction){
		this.direction = direction;
	}
	
	

	public int getDirection() {
		return direction;
	}
	
	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	
	
}

