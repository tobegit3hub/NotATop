package com.gg.game;

import javax.microedition.khronos.opengles.GL10;
import android.view.MotionEvent;

/*		游戏的框架，定义一个游戏所必须实现的函数		*/
abstract public class GameFrame {
	
	public final static int PREPARE = 0;
	public final static int RUN = 1;
	public final static int PAUSE = 2;
	public final static int END = 3;

	abstract public void init();// 初始化游戏资源

	abstract public void start();// 游戏开始，初始化游戏元素状态

	abstract public void pause();// 游戏暂停，停止一切动作

	abstract public void resume();// 游戏恢复，启动一切动作

	abstract public void end();// 游戏结束，释放资源

	abstract public void save();// 保存游戏，记录游戏元素状态

	abstract public void load();// 载入游戏，恢复游戏元素状态

	abstract public void onTouch(MotionEvent e);// 响应触屏信息

	abstract public void logic();// 游戏逻辑部分，每隔固定时间调用一次

	abstract public void draw(GL10 gl);// 利用OpenGL绘图
}
