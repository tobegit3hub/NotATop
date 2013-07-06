package com.gg.top;

import java.nio.*;
import java.util.ArrayList;
import java.util.Random;
import javax.microedition.khronos.opengles.GL10;
import com.gg.util.Point;


/*		用于绘制三维的陀螺		*/
public class DrawTop extends BasicTop {
	
	private DrawCone drawCone;
	private DrawCylinder drawCylinder;
	private DrawCircle drawCircle;
	
	

	public DrawTop(int coneTextureId, int cylinderTextureId, int circleTextureId) {
		drawCone = new DrawCone(coneTextureId);
		drawCylinder = new DrawCylinder(cylinderTextureId);
		drawCircle = new DrawCircle(circleTextureId);
	}

	
	public void generateData(){
		drawCone.generateData();
		drawCylinder.generateData();
		drawCircle.generateData();
	}
	

	public void drawSelf(GL10 gl) {
		gl.glPushMatrix();
		drawCone.drawSelf(gl);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		drawCylinder.drawSelf(gl);
		gl.glPopMatrix();
		
		gl.glPushMatrix();
		drawCircle.drawSelf(gl);
		gl.glPopMatrix();
	}
	

	public void logic() {
		if(state==ROTATING){
			
			rotate(); // 绕轴自转
			autoAngleAccelerate(); // 角速度自动减少
			
			axleRotate(); // 轴绕z轴旋转
			axleRotateAccelerate(); // 轴绕z轴旋转的角速度与轴倾角大小有关
			
			//autoAxleDown();
			//autoAxleDownAccelerate();
			calculateAxleAngle();

			
			shake();
			
			move();
			autoMoveAccelerate();
		}
	}
	
	
	/* 重写一系列BasicTop的函数，以保证圆锥、圆柱、圆的属性与陀螺的一致 */
	public void rotate(){
		super.rotate();
		drawCone.rotate();
		drawCylinder.rotate();
		drawCircle.rotate();
	}
	
	public void autoAngleAccelerate(){
		super.autoAngleAccelerate();
		drawCone.autoAngleAccelerate();
		drawCylinder.autoAngleAccelerate();
		drawCircle.autoAngleAccelerate();
	}
	
	public void axleRotate(){
		super.axleRotate();
		drawCone.axleRotate();
		drawCylinder.axleRotate();
		drawCircle.axleRotate();
	}
	
	public void axleRotateAccelerate(){
		super.axleRotateAccelerate();
		drawCone.axleRotateAccelerate();
		drawCylinder.axleRotateAccelerate();
		drawCircle.axleRotateAccelerate();
	}
	
	
//	public void autoAxleDown(){
//		super.autoAxleDown();
//		drawCone.autoAxleDown();
//		drawCylinder.autoAxleDown();
//		drawCircle.autoAxleDown();	
//	}
//	
//	public void autoAxleDownAccelerate(){
//		super.autoAxleDownAccelerate();
//		drawCone.autoAxleDownAccelerate();
//		drawCylinder.autoAxleDownAccelerate();
//		drawCircle.autoAxleDownAccelerate();
//	}
	
	public void calculateAxleAngle(){
		super.calculateAxleAngle();
		drawCone.calculateAxleAngle();
		drawCylinder.calculateAxleAngle();
		drawCircle.calculateAxleAngle();
	}

	
	public void shake(){
		Random random = new Random();
		switch(random.nextInt()%4){
		case 0:
			xShakeDistance = xShakeDistance;
			yShakeDistance = yShakeDistance;
			break;
		case 1:
			xShakeDistance = -xShakeDistance;
			yShakeDistance = yShakeDistance;
			break;
		case 2:
			xShakeDistance = xShakeDistance;
			yShakeDistance = -yShakeDistance;
			break;
		case 3:
			xShakeDistance = -xShakeDistance;
			yShakeDistance = -yShakeDistance;
			break;
		}
		this.setxShakeDistance(xShakeDistance);
		this.setyShakeDistance(yShakeDistance);
		drawCone.setxShakeDistance(xShakeDistance);
		drawCone.setyShakeDistance(yShakeDistance);
		drawCylinder.setxShakeDistance(xShakeDistance);
		drawCylinder.setyShakeDistance(yShakeDistance);
		drawCircle.setxShakeDistance(xShakeDistance);
		drawCircle.setyShakeDistance(yShakeDistance);
		
		
		drawCone.shake();
		drawCylinder.shake();
		drawCircle.shake();
	}
	
	
	public void move(){
		super.move();
		drawCone.move();
		drawCylinder.move();
		drawCircle.move();
	}

	public void autoMoveAccelerate(){
		super.autoMoveAccelerate();
		drawCone.autoMoveAccelerate();
		drawCylinder.autoMoveAccelerate();
		drawCircle.autoMoveAccelerate();
	}

	
	

	public Point getBasicPoint() {
		return basicPoint;
	}


	public void setBasicPoint(Point basicPoint) {
		this.basicPoint = basicPoint;
		drawCone.basicPoint = basicPoint;
		drawCylinder.basicPoint = basicPoint;
		drawCircle.basicPoint = basicPoint;
	}


	public float getRadius() {
		return radius;
	}


	public void setRadius(float radius) {
		this.radius = radius;
		drawCone.setRadius(radius);
		drawCylinder.setRadius(radius);
		drawCircle.setRadius(radius);
	}


	public float getConeHeight() {
		return coneHeight;
	}


	public void setConeHeight(float coneHeight) {
		this.coneHeight = coneHeight;
		drawCone.coneHeight = coneHeight;
		drawCylinder.coneHeight = coneHeight;
		drawCircle.coneHeight = coneHeight;
	}


	public float getConeAngle() {
		return coneAngle;
	}


	public void setConeAngle(float coneAngle) {
		this.coneAngle = coneAngle;
		drawCone.coneAngle = coneAngle;
		drawCylinder.coneAngle = coneAngle;
		drawCircle.coneAngle = coneAngle;
	}


	public float getCylinderHeight() {
		return cylinderHeight;
	}


	public void setCylinderHeight(float cylinderHeight) {
		this.cylinderHeight = cylinderHeight;
		drawCone.cylinderHeight = cylinderHeight;
		drawCylinder.cylinderHeight = cylinderHeight;
		drawCircle.cylinderHeight = cylinderHeight;
	}


	public float getAxleAngle() {
		return axleAngle;
	}


	public void setAxleAngle(float axleAngle) {
		this.axleAngle = axleAngle;
		drawCone.axleAngle = axleAngle;
		drawCylinder.axleAngle = axleAngle;
		drawCircle.axleAngle = axleAngle;
	}


	public float getAxleAngleSpeed() {
		return axleAngleSpeed;
	}


	public void setAxleAngleSpeed(float axleAngleSpeed) {
		this.axleAngleSpeed = axleAngleSpeed;
		drawCone.axleAngleSpeed = axleAngleSpeed;
		drawCylinder.axleAngleSpeed = axleAngleSpeed;
		drawCircle.axleAngleSpeed = axleAngleSpeed;
	}


	public float getAxleAngleCount() {
		return axleAngleCount;
	}


	public void setAxleAngleCount(float axleAngleCount) {
		this.axleAngleCount = axleAngleCount;
		drawCone.axleAngleCount = axleAngleCount;
		drawCylinder.axleAngleCount = axleAngleCount;
		drawCircle.axleAngleCount = axleAngleCount;
	}


//	public float getAxleAngleDownSpeed() {
//		return axleAngleDownSpeed;
//	}
//
//
//	public void setAxleAngleDownSpeed(float axleAngleDownSpeed) {
//		this.axleAngleDownSpeed = axleAngleDownSpeed;
//		drawCone.axleAngleDownSpeed = axleAngleDownSpeed;
//		drawCylinder.axleAngleDownSpeed = axleAngleDownSpeed;
//		drawCircle.axleAngleDownSpeed = axleAngleDownSpeed;
//	}




	public float getAngleSpeed() {
		return angleSpeed;
	}


	public void setAngleSpeed(float angleSpeed) {
		this.angleSpeed = angleSpeed;
		drawCone.angleSpeed = angleSpeed;
		drawCylinder.angleSpeed = angleSpeed;
		drawCircle.angleSpeed = angleSpeed;
	}


	public float getAngleCount() {
		return angleCount;
	}


	public void setAngleCount(float angleCount) {
		this.angleCount = angleCount;
		drawCone.angleCount = angleCount;
		drawCylinder.angleCount = angleCount;
		drawCircle.angleCount = angleCount;
	}


	public float getAutoAngleAccelerate() {
		return autoAngleAccelerate;
	}


	public void setAutoAngleAccelerate(float autoAngleAccelerate) {
		this.autoAngleAccelerate = autoAngleAccelerate;
		drawCone.autoAngleAccelerate = autoAngleAccelerate;
		drawCylinder.autoAngleAccelerate = autoAngleAccelerate;
		drawCircle.autoAngleAccelerate = autoAngleAccelerate;
	}


	public float getxMoveSpeed() {
		return xMoveSpeed;
	}


	public void setxMoveSpeed(float xMoveSpeed) {
		this.xMoveSpeed = xMoveSpeed;
		drawCone.xMoveSpeed = xMoveSpeed;
		drawCylinder.xMoveSpeed = xMoveSpeed;
		drawCircle.xMoveSpeed = xMoveSpeed;
	}


	public float getyMoveSpeed() {
		return yMoveSpeed;
	}


	public void setyMoveSpeed(float yMoveSpeed) {
		this.yMoveSpeed = yMoveSpeed;
		drawCone.yMoveSpeed = yMoveSpeed;
		drawCylinder.yMoveSpeed = yMoveSpeed;
		drawCircle.yMoveSpeed = yMoveSpeed;
	}




	public float getxAutoMoveAccelerate() {
		return xAutoMoveAccelerate;
	}


	public void setxAutoMoveAccelerate(float xAutoMoveAccelerate) {
		this.xAutoMoveAccelerate = xAutoMoveAccelerate;
		drawCone.xAutoMoveAccelerate = xAutoMoveAccelerate;
		drawCylinder.xAutoMoveAccelerate = xAutoMoveAccelerate;
		drawCircle.xAutoMoveAccelerate = xAutoMoveAccelerate;
	}


	public float getyAutoMoveAccelerate() {
		return yAutoMoveAccelerate;
	}


	public void setyAutoMoveAccelerate(float yAutoMoveAccelerate) {
		this.yAutoMoveAccelerate = yAutoMoveAccelerate;
		drawCone.yAutoMoveAccelerate = yAutoMoveAccelerate;
		drawCylinder.yAutoMoveAccelerate = yAutoMoveAccelerate;
		drawCircle.yAutoMoveAccelerate = yAutoMoveAccelerate;
	}


	public float getxShakeDistance() {
		return xShakeDistance;
	}


	public void setxShakeDistance(float xShakeDistance) {
		this.xShakeDistance = xShakeDistance;
		drawCone.xShakeDistance = xShakeDistance;
		drawCylinder.xShakeDistance = xShakeDistance;
		drawCircle.xShakeDistance = xShakeDistance;
	}


	public float getyShakeDistance() {
		return yShakeDistance;
	}


	public void setyShakeDistance(float yShakeDistance) {
		this.yShakeDistance = yShakeDistance;
		drawCone.yShakeDistance = yShakeDistance;
		drawCylinder.yShakeDistance = yShakeDistance;
		drawCircle.yShakeDistance = yShakeDistance;
	}


	public int getState() {
		return state;
	}


	public void setState(int state) {
		this.state = state;
		drawCone.state = state;
		drawCylinder.state = state;
		drawCircle.state = state;
	}


	public DrawCone getDrawCone() {
		return drawCone;
	}


	public void setDrawCone(DrawCone drawCone) {
		this.drawCone = drawCone;
	}


	public DrawCylinder getDrawCylinder() {
		return drawCylinder;
	}


	public void setDrawCylinder(DrawCylinder drawCylinder) {
		this.drawCylinder = drawCylinder;
	}


	public DrawCircle getDrawCircle() {
		return drawCircle;
	}


	public void setDrawCircle(DrawCircle drawCircle) {
		this.drawCircle = drawCircle;
	}
	
	
	public void setConeTextureId(int coneTextureId){
		drawCone.setTextureId(coneTextureId);
	}
	
	public void setCylinderTextureId(int cylinderTextureId){
		drawCylinder.setTextureId(cylinderTextureId);
	}
	
	public void setCircleTextureId(int circleTextureId){
		drawCircle.setTextureId(circleTextureId);
	}

	
}