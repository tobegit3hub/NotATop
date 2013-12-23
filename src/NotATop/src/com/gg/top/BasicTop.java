package com.gg.top;

import com.gg.util.*;

/*		表示陀螺的基本模型		*/
public class BasicTop {
	protected Point basicPoint = new Point(0f, 0f, 0f); // 圆锥基点坐标xyz		
	protected float radius = 0.5f; // 圆锥、圆柱、圆半径
	protected float coneHeight = radius; // 圆锥高
	protected float coneAngle = (float) Math.toRadians(90); // 圆锥角，默认为90°
	protected float cylinderHeight = radius*2; // 圆柱高

	protected float angleCount = 0f; // 自身旋转的累计总角度
	protected float angleSpeed = 20f; // 角速度，10较慢，15正常，25很快，30左右极限，40就开始反过来了
	protected float autoAngleAccelerate = -0.05f; // 0.05大概15秒有点慢，0.06大概12秒停下来，自身旋转减速度，默认为负值
	public final static float MAX_ANGLE_SPEED = 30f; // 最大角速度	
	
	protected float axleAngleCount = 0f; // 轴线旋转累计总角度	
	protected float axleAngleSpeed = 0f;// 轴线旋转角速度，0.1挺慢的容易玩，0.2则挺快了（比较合理），0.25绝对是极限
	public final static float MAX_AXLE_ANGLE_SPEED = 15f; //

	protected float axleAngle = 0f; // 轴线倾角,20度差不多就该输了,30度必须输
	public final float DEAD_AXLE_ANGLE = 13f; // 死亡轴线倾角（死亡后不再响应触屏消息），默认为30°,15还是有点太大了
	
	protected float xShakeDistance = 0.002f; //x方向陀螺抖动的平移距离，可正可负,其他不动时0.005太明显，0.002会动
	protected float yShakeDistance = 0.002f; //y方向陀螺抖动的平移距离，可正可负
	
	protected float xMoveSpeed = 0.0f; // x方向平移速度,0.005有点明显
	protected float yMoveSpeed = 0.0f; // y方向平移速度
	public final static float X_MAX_MOVE_SPEED = 0.015f; // x方向最大平移速度的绝对值，0.015从中间到边大概4秒
	public final static float Y_MAX_MOVE_SPEED = 0.015f; // y方向最大平移速度的绝对值,0.015的话2秒撞
	protected float xAutoMoveAccelerate = 0.00005f; // x方向自身平移减速度，值为正数，0.0001很快就停,0.00005则滑到一半过一点
	protected float yAutoMoveAccelerate = 0.00005f; // y方向自身平移减速度

	public final static int PREPARE = 0; // 开始的状态
	public final static int ROTATING = 1; // 正在转的状态
	public final static int FALLING = 2; // 正在倒的状态
	public final static int END = 3; // 倒下的状态
	protected int state = PREPARE; // 陀螺状态

	
	public BasicTop() { // 缺省状态下的构造函数

	}
	

	//转过角度的总量，直接由角速度叠加而成
	public void rotate(){
		angleCount += angleSpeed;		
		if(angleCount>360){
			angleCount -= 360;
		}
	}
	
	//角速度自动减少，线性减少
	public void autoAngleAccelerate(){
		if(angleSpeed+autoAngleAccelerate>0){
			angleSpeed += autoAngleAccelerate;
		}else{
			angleSpeed = 0f;
		}
	}
	
	
	//轴线绕z轴旋转的总角度，直接由该角速度叠加而成
	public void axleRotate(){
		axleAngleCount += axleAngleSpeed;	
		if(axleAngleCount>360){
			axleAngleCount -= 360;
		}
	}
	
	//通过轴倾角计算出转轴的角速度，用sin来模拟
	//先表示0到1,然后是0到PI/2,不用平移后是Sin第一段,然后不用下移,最后乘以最大值(得到的是正数)
	public void axleRotateAccelerate(){
		if(axleAngleSpeed<MAX_AXLE_ANGLE_SPEED){
			axleAngleSpeed = ((float)Math.sin(axleAngle/DEAD_AXLE_ANGLE*(Math.PI/2)))*MAX_AXLE_ANGLE_SPEED;
		}
	}
	

	
	//轴线倾斜角度由自转角速度决定
	//先表示0到1,然后是0到PI/2,向右平移PI后是Sin第三段,然后上移,最后乘以最大倒下角度(得到的是正数)，再乘以1.2保证最大值大于死亡角度
	public void calculateAxleAngle(){
		axleAngle = (float)(Math.sin(angleSpeed/MAX_ANGLE_SPEED*(Math.PI/2)+Math.PI)+1)*DEAD_AXLE_ANGLE*1.2f;
	}
	
	
	//直接震动到某个值
	public void shake(){
		if(basicPoint.x+xShakeDistance-radius>-Constant.LOGIC_WIDTH/2 && basicPoint.x+xShakeDistance+radius<Constant.LOGIC_WIDTH){
			basicPoint.x += xShakeDistance;
		}
		if(basicPoint.y+yShakeDistance-radius>-Constant.LOGIC_HEIGHT/2 && basicPoint.y+yShakeDistance+radius<Constant.LOGIC_HEIGHT){
			basicPoint.y += yShakeDistance;
		}		
	}
	
	public void move(){
		if(basicPoint.x+xMoveSpeed-radius>-Constant.LOGIC_WIDTH/2 && basicPoint.x+xMoveSpeed+radius<Constant.LOGIC_WIDTH/2){
			basicPoint.x += xMoveSpeed;
		}
//		else{
//			xMoveSpeed = -xMoveSpeed*2/3;
//			angleSpeed = angleSpeed*4/5;
//			
//			collideFlag = true;
//		}
		
		if(basicPoint.y+yMoveSpeed-radius>-Constant.LOGIC_HEIGHT/2 && basicPoint.y+yMoveSpeed+radius<Constant.LOGIC_HEIGHT/2){
			basicPoint.y += yMoveSpeed;
		}
//		else{
//			yMoveSpeed = -yMoveSpeed*2/3;
//			angleSpeed = angleSpeed*4/5;
//			
//			collideFlag = true;
//		}
	}

	
	
	public void autoMoveAccelerate(){
		if(xMoveSpeed>0 && xMoveSpeed-xAutoMoveAccelerate>0){
			xMoveSpeed -= xAutoMoveAccelerate;
		}else if(xMoveSpeed<0 && xMoveSpeed+xAutoMoveAccelerate<0){
			xMoveSpeed += xAutoMoveAccelerate;
		}
		
		if(yMoveSpeed>0 && yMoveSpeed-yAutoMoveAccelerate>0){
			yMoveSpeed -= yAutoMoveAccelerate;
		}else if(yMoveSpeed<0 && yMoveSpeed+yAutoMoveAccelerate<0){
			yMoveSpeed += yAutoMoveAccelerate;
		}
	}
	

	
	/* 一系列get、set函数 */
	public Point getBasicPoint() {
		return basicPoint;
	}


	public void setBasicPoint(Point basicPoint) {
		this.basicPoint = basicPoint;
	}


	public float getRadius() {
		return radius;
	}


	public void setRadius(float radius) {
		this.radius = radius;
		this.coneHeight = radius;
		this.cylinderHeight = radius*2;
	}


	public float getConeHeight() {
		return coneHeight;
	}


	public void setConeHeight(float coneHeight) {
		this.coneHeight = coneHeight;
	}


	public float getConeAngle() {
		return coneAngle;
	}


	public void setConeAngle(float coneAngle) {
		this.coneAngle = coneAngle;
	}


	public float getCylinderHeight() {
		return cylinderHeight;
	}


	public void setCylinderHeight(float cylinderHeight) {
		this.cylinderHeight = cylinderHeight;
	}



	public float getAxleAngle() {
		return axleAngle;
	}


	public void setAxleAngle(float axleAngle) {
		this.axleAngle = axleAngle;
	}


	public float getAxleAngleSpeed() {
		return axleAngleSpeed;
	}


	public void setAxleAngleSpeed(float axleAngleSpeed) {
		this.axleAngleSpeed = axleAngleSpeed;
	}


	public float getAxleAngleCount() {
		return axleAngleCount;
	}


	public void setAxleAngleCount(float axleAngleCount) {
		this.axleAngleCount = axleAngleCount;
	}


//	public float getAxleAngleDownSpeed() {
//		return axleAngleDownSpeed;
//	}
//
//
//	public void setAxleAngleDownSpeed(float axleAngleDownSpeed) {
//		this.axleAngleDownSpeed = axleAngleDownSpeed;
//	}



	public float getAngleSpeed() {
		return angleSpeed;
	}


	public void setAngleSpeed(float angleSpeed) {
		this.angleSpeed = angleSpeed;
	}


	public float getAngleCount() {
		return angleCount;
	}


	public void setAngleCount(float angleCount) {
		this.angleCount = angleCount;
	}


	public float getAutoAngleAccelerate() {
		return autoAngleAccelerate;
	}


	public void setAutoAngleAccelerate(float autoAngleAccelerate) {
		this.autoAngleAccelerate = autoAngleAccelerate;
	}


	public float getxMoveSpeed() {
		return xMoveSpeed;
	}


	public void setxMoveSpeed(float xMoveSpeed) {
		this.xMoveSpeed = xMoveSpeed;
	}


	public float getyMoveSpeed() {
		return yMoveSpeed;
	}


	public void setyMoveSpeed(float yMoveSpeed) {
		this.yMoveSpeed = yMoveSpeed;
	}





	public float getxAutoMoveAccelerate() {
		return xAutoMoveAccelerate;
	}


	public void setxAutoMoveAccelerate(float xAutoMoveAccelerate) {
		this.xAutoMoveAccelerate = xAutoMoveAccelerate;
	}


	public float getyAutoMoveAccelerate() {
		return yAutoMoveAccelerate;
	}


	public void setyAutoMoveAccelerate(float yAutoMoveAccelerate) {
		this.yAutoMoveAccelerate = yAutoMoveAccelerate;
	}


	public float getxShakeDistance() {
		return xShakeDistance;
	}


	public void setxShakeDistance(float xShakeDistance) {
		this.xShakeDistance = xShakeDistance;
	}


	public float getyShakeDistance() {
		return yShakeDistance;
	}


	public void setyShakeDistance(float yShakeDistance) {
		this.yShakeDistance = yShakeDistance;
	}


	public int getState() {
		return state;
	}


	public void setState(int state) {
		this.state = state;
	}




	public float getDEAD_AXLE_ANGLE() {
		return DEAD_AXLE_ANGLE;
	}





	public float getMAX_ANGLE_SPEED() {
		return MAX_ANGLE_SPEED;
	}




	public float getX_MAX_MOVE_SPEED() {
		return X_MAX_MOVE_SPEED;
	}




	public float getY_MAX_MOVE_SPEED() {
		return Y_MAX_MOVE_SPEED;
	}




	public float getMAX_AXLE_ANGLE_SPEED() {
		return MAX_AXLE_ANGLE_SPEED;
	}









	public int getPREPARE() {
		return PREPARE;
	}




	public int getROTATING() {
		return ROTATING;
	}




	public int getFALLING() {
		return FALLING;
	}




	public int getEND() {
		return END;
	}



}
