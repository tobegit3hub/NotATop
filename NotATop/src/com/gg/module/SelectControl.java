package com.gg.module;

import com.gg.view.R;

public class SelectControl {

	private static int index = 0;
	private static int numberOfTop = 5;
	
	private static int[] coneTextureIdArray = new int[]{
			R.drawable.cone0,
			R.drawable.cone1,
			R.drawable.cone2,
			R.drawable.cone3,
			R.drawable.cone4}; // 圆锥的纹理Id数组
	private static int[] cylinderTextureIdArray = new int[]{
			R.drawable.cylinder0,
			R.drawable.cylinder1,
			R.drawable.cylinder2,
			R.drawable.cylinder3,
			R.drawable.cylinder4}; // 圆柱的纹理Id数组
	private static int[] circleTextureIdArray = new int[]{
			R.drawable.circle0,
			R.drawable.circle1,
			R.drawable.circle2,
			R.drawable.circle3,
			R.drawable.circle4}; // 圆的纹理Id数组
	
	
	private SelectControl(){

	}
	
	
	public static int getIndex(){
		return index;
	}
	
	public static void setIndex(int indexOfTop){
		index = indexOfTop;
	}
	
	public static int getNumberOfTop(){
		return numberOfTop;
	}
	
	
	public static int getConeTextureId(){
		return coneTextureIdArray[index];
	}
	
	public static int getCylinderTextureId(){
		return cylinderTextureIdArray[index];
	}
	
	public static int getCircleTextureId(){
		return circleTextureIdArray[index];
	}
	
	
	
	public static void next(){
		if(index<numberOfTop-1){
			index++;
		}
	}
	
	public static void prev(){
		if(index>0){
			index--;
		}
	}
	
}
