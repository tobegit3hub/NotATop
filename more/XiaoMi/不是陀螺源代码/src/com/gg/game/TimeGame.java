package com.gg.game;

import java.util.ArrayList;

import javax.microedition.khronos.opengles.GL10;
import android.view.MotionEvent;
import android.widget.Toast;

import com.gg.module.*;
import com.gg.top.BasicTop;
import com.gg.top.DrawTop;
import com.gg.util.*;
import com.gg.view.R;

/* 计时游戏模式类，在60秒内通过“抽打”陀螺维持旋转计算得分 */
public class TimeGame extends GameFrame implements Runnable { // 继承游戏框架类，实现游戏必须实现的函数

	/* 3D绘图所用到的变量 */
	private GL10 gl; // 划3D陀螺所需要的gl对象
	private int coneTextureId; // 陀螺底部圆锥的纹理
	private int cylinderTextureId; // 陀螺中间圆柱的纹理
	private int circleTextureId; // 陀螺顶部圆的纹理
	private int pauseTextureId; // 暂停时显示的图片的纹理

	/* 响应划屏消息所用的变量 */
	private long startTime; // 开始响应触屏消息的时刻，用于限制连续划屏的时间
	private long endTime; // 与startTime对应
	private long touchStart; // （类似startTime）开始响应触屏消息的时刻，用于控制在ACTION_MOVE时响应次数不要太多
	private boolean responseFlag; // 表示正在响应触屏消息的标志
	private Point firstPoint; // 响应划屏消息的第一个点，所有划屏消息最终都看作多段线段来处理
	private Point secondPoint; // 响应划屏消息的第二个点

	/* 游戏逻辑用到的变量 */
	private Thread thread; // 游戏逻辑进行的线程
	private int state; // 表示游戏状态的变量
	private double duration; // 表示游戏进行的时间
	private double score; // 表示游戏目前的得分，注意得分不是直接与时间线性相关的，而与陀螺倒下的角度有关（陀螺倒的角度越大得分也越高）
	private DrawTop drawTop; // 3D陀螺对象
	private Circle logicCircle; // 代替陀螺的逻辑圆，实际上划屏或碰撞的响应都是相对于逻辑圆的，从而再作用在真正的陀螺上
	private DrawBackground drawPause; // 暂停界面的背景对象

	/* 与外界交互的变量 */
	private boolean collideFlag; // 表示陀螺（实际上是逻辑圆）碰到边缘，要求外部的Activity使手机震动
	private boolean crackFlag; // 嘿嘿，表示开启Crack功能的标志

	/* 比较特别的变量 */
	private int currentLevel; // 表示当前关卡的等级，范围是1~8
	private int currentGoal; // 表示完成当前等级所需要的得分，公式是每关2000分

	public TimeGame(GL10 gl, int coneTextureId, int cylinderTextureId,
			int circleTextureId, int pauseTextureId) { // 初始化各种3D资源

		this.gl = gl;
		this.coneTextureId = coneTextureId;
		this.cylinderTextureId = cylinderTextureId;
		this.circleTextureId = circleTextureId;
		this.pauseTextureId = pauseTextureId;

		state = PREPARE; // 设置游戏为准备状态
		init(); // 初始化游戏的对象资源（注意这时还没开始游戏）
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub

		drawTop = new DrawTop(coneTextureId, cylinderTextureId, circleTextureId);// 创建陀螺对象，并传入相应的纹理贴图
		logicCircle = new Circle(drawTop.getBasicPoint().x,
				drawTop.getBasicPoint().y, drawTop.getRadius()); // 以陀螺为蓝本建立逻辑圆对象

		drawPause = new DrawBackground(pauseTextureId); // 创建背景对象，并传入相应的纹理贴图

		firstPoint = new Point(0f, 0f, 0f); // 创建触控点的对象（经常忘了这个就很难找到原因了）
		secondPoint = new Point(0f, 0f, 0f); // 同样创建触控点的对象
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

		state = RUN; // 将游戏状态改成RUN运行状态
		drawTop.setState(RUN); // 将陀螺状态改成RUN运行状态

		duration = 0; // 重设游戏时间为零
		score = 0; // 重设游戏分数为零

		thread = new Thread(this); // 建立游戏逻辑运行的线程对象
		thread.start(); // 随便开启游戏逻辑线程
	}

	public void run() {
		// TODO Auto-generated method stub

		while (true) { // 死循环，保证游戏在什么游戏状态下都能按需要正常运行，而且调用此函数的时间间隔是固定的
			long start = System.currentTimeMillis(); // 首先纪录这一个小循环的开始时间

			logic(); // 游戏的逻辑运行

			long end = System.currentTimeMillis(); // 纪录这一个小循环的结束时间

			try {
				if (end - start < Constant.INTERVAL) { // 如果逻辑运行的时间比规定的时间间隔短
					thread.sleep(Constant.INTERVAL - (end - start)); // 则补上它们的差值，从而保证了每隔规定时间间隔运行一次小循环
				}
			} catch (Exception e) { // 因为sleep了所以要处理异常
				e.printStackTrace();
			}
		}
	}

	@Override
	public void onTouch(MotionEvent e) {
		// TODO Auto-generated method stub
		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (state == RUN) {
				responseFlag = false;
			} else if (state == PAUSE) {
				double x = Constant.convertX(e.getX());
				double y = Constant.convertY(e.getY());
				
				if(x>-0.8 && x<-0.2 && y>0.-6 && y<0.0){
					resume();
				}else if(x<0.8 && x>0.2 && y>0.-6 && y<0.0){
					end();
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (state == RUN) {
				if (responseFlag == false) {
					if (isInLogicCircle(Constant.convertX(e.getX()),
							Constant.convertY(e.getY()))) {
						// lastTime = System.currentTimeMillis();
						startTime = System.currentTimeMillis();

						touchStart = System.currentTimeMillis();

						firstPoint.x = Constant.convertX(e.getX());
						firstPoint.y = Constant.convertY(e.getY());

						responseFlag = true;
					}
				} else {
					endTime = System.currentTimeMillis();

					if (crackFlag == true) {
						endTime = 0;
					}

					if (endTime - startTime > 90) {
						return;
					}

					if (System.currentTimeMillis() - touchStart > Constant.INTERVAL) {
						if (isInLogicCircle(Constant.convertX(e.getX()),
								Constant.convertY(e.getY()))) {
							secondPoint.x = Constant.convertX(e.getX());
							secondPoint.y = Constant.convertY(e.getY());

							responseTouch(firstPoint, secondPoint);
							// System.out.println("response");

							touchStart = System.currentTimeMillis();
							firstPoint.x = Constant.convertX(e.getX());
							firstPoint.y = Constant.convertY(e.getY());
						}

					}
				}
			}

			break;
		case MotionEvent.ACTION_UP:
			if (state == RUN) {
				firstPoint.x = 0;
				firstPoint.y = 0;
				secondPoint.x = 0;
				secondPoint.y = 0;

				responseFlag = false;
			}

			break;
		}
	}

	@Override
	public void logic() {
		// TODO Auto-generated method stub

		if (state == RUN) {
			drawTop.logic();

			updateLogicCircle();

			duration += (double) Constant.INTERVAL / 1000;
			score += (double) Constant.INTERVAL / 1000 * drawTop.getAxleAngle()
					* 10 + duration * 120 / 1000;// 大概三分之一的是由坚持时间贡献的

		} else if (state == PAUSE) {

		}

		if (drawTop.getAxleAngle() > drawTop.getDEAD_AXLE_ANGLE()) {
			end();
		}

		if (duration >= 60) {
			end();
		}

		if (logicCircle.getCenter().x - logicCircle.getRadius() <= -Constant.LOGIC_WIDTH / 2) { // 撞到左墙
			drawTop.getBasicPoint().x += 0.1f; // 首先要平移防止再次撞（0.05的话撞完还会再撞，0.1就不会了）
			drawTop.setxMoveSpeed(-drawTop.getxMoveSpeed() * 1 / 2); // 将速度反向并减速
			drawTop.setAngleSpeed(drawTop.getAngleSpeed() * 9 / 10); // 角速度减少
			collideFlag = true; // 设置碰撞标志，以发信息响应震动
		} else if (logicCircle.getCenter().x + logicCircle.getRadius() >= Constant.LOGIC_WIDTH / 2) { // 撞到右墙
			drawTop.getBasicPoint().x -= 0.1f;
			drawTop.setxMoveSpeed(-drawTop.getxMoveSpeed() * 1 / 2);
			drawTop.setAngleSpeed(drawTop.getAngleSpeed() * 9 / 10);
			collideFlag = true;
		}

		if (logicCircle.getCenter().y + logicCircle.getRadius() >= Constant.LOGIC_HEIGHT / 2) { // 撞到上墙
			drawTop.getBasicPoint().y -= 0.1f;
			drawTop.setyMoveSpeed(-drawTop.getyMoveSpeed() * 1 / 2);
			drawTop.setAngleSpeed(drawTop.getAngleSpeed() * 9 / 10);
			collideFlag = true;
		} else if (logicCircle.getCenter().y - logicCircle.getRadius() <= -Constant.LOGIC_HEIGHT / 2) { // 撞到下墙
			drawTop.getBasicPoint().y += 0.1f;
			drawTop.setyMoveSpeed(-drawTop.getyMoveSpeed() * 1 / 2);
			drawTop.setAngleSpeed(drawTop.getAngleSpeed() * 9 / 10);
			collideFlag = true;
		}

	}

	@Override
	public void draw(GL10 gl) {
		// TODO Auto-generated method stub
		if (state == RUN) {
			drawTop.drawSelf(gl);
		} else if (state == PAUSE) {
			drawPause.drawSelf(gl);
		}
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

		state = PAUSE;
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

		state = RUN;
	}

	@Override
	public void end() {
		// TODO Auto-generated method stub

		try {
			Thread.sleep(500);
		} catch (Exception e) {
			e.printStackTrace();
		}

		state = END;
		drawTop.setState(END);

	}

	@Override
	public void save() {
		// TODO Auto-generated method stub

	}

	@Override
	public void load() {
		// TODO Auto-generated method stub

	}

	public void calculateGoal() {
		currentGoal = 2000 * currentLevel;
	}

	public DrawTop getDrawTop() {
		return drawTop;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public void updateLogicCircle() {
		// logicCircle.getCenter().x = drawTop.getBasicPoint().x +
		// 3*drawTop.getRadius()*drawTop.getAxleVector().x;
		// logicCircle.getCenter().y = drawTop.getBasicPoint().y +
		// 3*drawTop.getRadius()*drawTop.getAxleVector().y;
		logicCircle.getCenter().x = drawTop.getBasicPoint().x + 3
				* drawTop.getRadius()
				* (float) Math.sin(drawTop.getAxleAngle() * Math.PI / 180)
				* (float) Math.cos(drawTop.getAxleAngleCount() * Math.PI / 180);
		logicCircle.getCenter().y = drawTop.getBasicPoint().y + 3
				* drawTop.getRadius()
				* (float) Math.sin(drawTop.getAxleAngle() * Math.PI / 180)
				* (float) Math.sin(drawTop.getAxleAngleCount() * Math.PI / 180);
		logicCircle.setRadius(drawTop.getRadius());

	}

	public boolean isInLogicCircle(float x, float y) {
		float distance = (float) Math.sqrt((x - logicCircle.getCenter().x)
				* (x - logicCircle.getCenter().x)
				+ (y - logicCircle.getCenter().y)
				* (y - logicCircle.getCenter().y));
		if (distance < logicCircle.getRadius() * 1.2
				&& distance > logicCircle.getRadius() / 3) {
			return true;
		} else {
			return false;
		}

	}

	public void responseTouch(Point firstPoint, Point secondPoint) {

		Line touchLine = new Line(firstPoint, secondPoint);
		Line axleLine = new Line(drawTop.getBasicPoint(),
				logicCircle.getCenter());

		float angleSpeedOffset = 0.7f; // 按转动方向鞭打一次增加的角速度（1.0就可以瞬间加速，0.8会玩很容易，0.5很难维持,0.6也挺难,0.7挺好的）
		float downAngleSpeedOffset = 0.5f; // 拍倒或拍起来增减的角速度，配合上者会玩的可以很容易

		float xMoveOffset = 0.04f; // 拍打一次往x轴方向的弹动距离（0.06有点夸张,0.04能挡住了而且不跃进）
		float yMoveOffset = 0.04f; // 拍打一次往y轴方向的弹动距离
		float xMoveSpeedOffset = 0.0015f; // 拍打一次x轴方向速度的改变量（0.001可以了，划两三次就有效果了,0.02难控制，0.0015可以了）
		float yMoveSpeedOffset = 0.0015f; // 拍打一次y轴方向速度的改变量

		switch (touchLine.getDirection().getDirection()) { // 首先判断手划线的方向
		case Direction.UP_RIGHT: // 如果手是往右上方滑动
			if (touchLine.directionCircle(logicCircle).getDirection() == Direction.DOWN) { // 再判断如果划线在逻辑圆的下方
				angleSpeedOffset = -angleSpeedOffset; // 角速度减少
				xMoveSpeedOffset = -xMoveSpeedOffset; // 左移一点
				yMoveSpeedOffset = yMoveSpeedOffset; // 上移一点
				xMoveOffset = -xMoveOffset; // 右移速度减少或左移速度增加
				yMoveOffset = yMoveOffset; // 上移速度增加或下移速度减少
				if (axleLine.getDirection().getDirection() == Direction.UP_LEFT) { // 如果陀螺正往左上方倒
					downAngleSpeedOffset = -downAngleSpeedOffset; // 把陀螺拍倒（角速度减少）
				} else if (axleLine.getDirection().getDirection() == Direction.DOWN_RIGHT) { // 如果陀螺正往左上方倒
					downAngleSpeedOffset = downAngleSpeedOffset; // 把陀螺拍起来（角速度增加）
				}
			} else if (touchLine.directionCircle(logicCircle).getDirection() == Direction.UP) {
				angleSpeedOffset = angleSpeedOffset;
				xMoveSpeedOffset = xMoveSpeedOffset;
				yMoveSpeedOffset = -yMoveSpeedOffset;
				xMoveOffset = xMoveOffset;
				yMoveOffset = -yMoveOffset;
				if (axleLine.getDirection().getDirection() == Direction.DOWN_RIGHT) {
					downAngleSpeedOffset = -downAngleSpeedOffset;
				} else if (axleLine.getDirection().getDirection() == Direction.UP_LEFT) {
					downAngleSpeedOffset = downAngleSpeedOffset;
				}
			}
			break;
		case Direction.DOWN_RIGHT:
			if (touchLine.directionCircle(logicCircle).getDirection() == Direction.DOWN) {
				angleSpeedOffset = -angleSpeedOffset;
				xMoveSpeedOffset = xMoveSpeedOffset;
				yMoveSpeedOffset = yMoveSpeedOffset;
				xMoveOffset = xMoveOffset;
				yMoveOffset = yMoveOffset;
				if (axleLine.getDirection().getDirection() == Direction.UP_RIGHT) {
					downAngleSpeedOffset = -downAngleSpeedOffset;
				} else if (axleLine.getDirection().getDirection() == Direction.DOWN_LEFT) {
					downAngleSpeedOffset = downAngleSpeedOffset;
				}
			} else if (touchLine.directionCircle(logicCircle).getDirection() == Direction.UP) {
				angleSpeedOffset = angleSpeedOffset;
				xMoveSpeedOffset = -xMoveSpeedOffset;
				yMoveSpeedOffset = -yMoveSpeedOffset;
				xMoveOffset = -xMoveOffset;
				yMoveOffset = -yMoveOffset;
				if (axleLine.getDirection().getDirection() == Direction.DOWN_LEFT) {
					downAngleSpeedOffset = -downAngleSpeedOffset;
				} else if (axleLine.getDirection().getDirection() == Direction.UP_RIGHT) {
					downAngleSpeedOffset = downAngleSpeedOffset;
				}
			}
			break;
		case Direction.UP_LEFT:
			if (touchLine.directionCircle(logicCircle).getDirection() == Direction.DOWN) {
				angleSpeedOffset = angleSpeedOffset;
				xMoveSpeedOffset = xMoveSpeedOffset;
				yMoveSpeedOffset = yMoveSpeedOffset;
				xMoveOffset = xMoveOffset;
				yMoveOffset = yMoveOffset;
				if (axleLine.getDirection().getDirection() == Direction.UP_RIGHT) {
					downAngleSpeedOffset = -downAngleSpeedOffset;
				} else if (axleLine.getDirection().getDirection() == Direction.DOWN_LEFT) {
					downAngleSpeedOffset = downAngleSpeedOffset;
				}
			} else if (touchLine.directionCircle(logicCircle).getDirection() == Direction.UP) {
				angleSpeedOffset = -angleSpeedOffset;
				xMoveSpeedOffset = -xMoveSpeedOffset;
				yMoveSpeedOffset = -yMoveSpeedOffset;
				xMoveOffset = -xMoveOffset;
				yMoveOffset = -yMoveOffset;
				if (axleLine.getDirection().getDirection() == Direction.DOWN_LEFT) {
					downAngleSpeedOffset = -downAngleSpeedOffset;
				} else if (axleLine.getDirection().getDirection() == Direction.UP_RIGHT) {
					downAngleSpeedOffset = downAngleSpeedOffset;
				}
			}
			break;
		case Direction.DOWN_LEFT:
			if (touchLine.directionCircle(logicCircle).getDirection() == Direction.DOWN) {
				angleSpeedOffset = angleSpeedOffset;
				xMoveSpeedOffset = -xMoveSpeedOffset;
				yMoveSpeedOffset = yMoveSpeedOffset;
				xMoveOffset = -xMoveOffset;
				yMoveOffset = yMoveOffset;
				if (axleLine.getDirection().getDirection() == Direction.UP_LEFT) {
					downAngleSpeedOffset = -downAngleSpeedOffset;
				} else if (axleLine.getDirection().getDirection() == Direction.DOWN_RIGHT) {
					downAngleSpeedOffset = downAngleSpeedOffset;
				}
			} else if (touchLine.directionCircle(logicCircle).getDirection() == Direction.UP) {
				angleSpeedOffset = -angleSpeedOffset;
				xMoveSpeedOffset = xMoveSpeedOffset;
				yMoveSpeedOffset = -yMoveSpeedOffset;
				xMoveOffset = xMoveOffset;
				yMoveOffset = -yMoveOffset;
				if (axleLine.getDirection().getDirection() == Direction.DOWN_RIGHT) {
					downAngleSpeedOffset = -downAngleSpeedOffset;
				} else if (axleLine.getDirection().getDirection() == Direction.UP_LEFT) {
					downAngleSpeedOffset = downAngleSpeedOffset;
				}
			}
			break;
		}

		if (drawTop.getAngleSpeed() + angleSpeedOffset < BasicTop.MAX_ANGLE_SPEED) { // 根据前面算的偏移量进行增减
			drawTop.setAngleSpeed(drawTop.getAngleSpeed() + angleSpeedOffset);
		}

		if (drawTop.getAngleSpeed() + downAngleSpeedOffset < BasicTop.MAX_ANGLE_SPEED) {
			drawTop.setAngleSpeed(drawTop.getAngleSpeed()
					+ downAngleSpeedOffset);
		}

		if (Math.abs(drawTop.getxMoveSpeed() + xMoveSpeedOffset) < BasicTop.X_MAX_MOVE_SPEED) {
			drawTop.setxMoveSpeed(drawTop.getxMoveSpeed() + xMoveSpeedOffset);
		}

		if (Math.abs(drawTop.getyMoveSpeed() + yMoveSpeedOffset) < BasicTop.Y_MAX_MOVE_SPEED) {
			drawTop.setyMoveSpeed(drawTop.getyMoveSpeed() + yMoveSpeedOffset);
		}

		if (drawTop.getBasicPoint().x + xMoveOffset > -Constant.LOGIC_WIDTH / 2
				&& drawTop.getBasicPoint().x + xMoveOffset < Constant.LOGIC_WIDTH / 2) {
			drawTop.setBasicPoint(new Point(drawTop.getBasicPoint().x
					+ xMoveOffset, drawTop.getBasicPoint().y));
		}

		if (drawTop.getBasicPoint().y + yMoveOffset > -Constant.LOGIC_HEIGHT / 2
				&& drawTop.getBasicPoint().y + yMoveOffset < Constant.LOGIC_HEIGHT / 2) {
			drawTop.setBasicPoint(new Point(drawTop.getBasicPoint().x, drawTop
					.getBasicPoint().y + yMoveOffset));
		}

	}

	public double getDuration() {
		return duration;
	}

	public void setDuration(double duration) {
		this.duration = duration;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public boolean isCollideFlag() {
		return collideFlag;
	}

	public void setCollideFlag(boolean collideFlag) {
		this.collideFlag = collideFlag;
	}

	public int getCurrentLevel() {
		return currentLevel;
	}

	public void setCurrentLevel(int currentLevel) {
		this.currentLevel = currentLevel;
	}

	public int getCurrentGoal() {
		return currentGoal;
	}

	public void setCurrentGoal(int currentGoal) {
		this.currentGoal = currentGoal;
	}

	public boolean isCrackFlag() {
		return crackFlag;
	}

	public void setCrackFlag(boolean crackFlag) {
		this.crackFlag = crackFlag;
	}

}
