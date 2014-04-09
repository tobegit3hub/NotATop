package com.gg.util;

public class Constant {

	public static int SCREEN_WIDTH;// = 854;
	public static int SCREEN_HEIGHT;// = 480;
	
	public static float LOGIC_WIDTH = 1.7f*2;
	public static float LOGIC_HEIGHT = 1.0f*2;
	
	
	public static float SCREEN_RATE = (float)(0.5*4/SCREEN_HEIGHT); // 屏幕坐标乘以此值得到游戏的逻辑坐标
	
	public static final int INTERVAL = 30;
	
	
	private Constant(){
		
	}
	
	public static float convertX(float x){
		x -= SCREEN_WIDTH/2;
		return (float)(x*SCREEN_RATE);
	}
	
	public static float convertY(float y){
		y -= SCREEN_HEIGHT/2;
		y = -y;
		return (float)(y*SCREEN_RATE);
	}
	
	public static float convert(float coor) {
		return (float)(coor*SCREEN_RATE);
	}
	
}
