package com.gg.view;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.MotionEvent;
import android.widget.Toast;

import com.gg.game.ClassicGame;
import com.gg.game.GameFrame;
import com.gg.game.TimeGame;
import com.gg.module.*;
import com.gg.top.*;
import com.gg.util.*;

/*		经典游戏模式的3D界面类，包含相应的渲染器类		*/
public class TimeGameSurfaceView extends GLSurfaceView {

	private MainActivity mainActivity; // 使用此界面的Activity

	private SceneRenderer sceneRender; // 场景渲染器

	private TimeGame timeGame; // 表示完整游戏的对象

	private DrawTrack drawTrack; // 表示划屏轨迹的对象

	private DrawBackground drawBackground; // 表示背景的对象
	
	private DrawScore drawScore;
	
	private DrawScore drawTime;
	
	private Toast nextLevelToast;
	private Toast notNextLevelToast;
	
	
	

	public TimeGameSurfaceView(Context context) {
		super(context);
		mainActivity = (MainActivity) context; // 获得使用此界面的Activity

		nextLevelToast = Toast.makeText(context, "恭喜进入下一关", Toast.LENGTH_SHORT);
		notNextLevelToast = Toast.makeText(context, "很遗憾挑战失败", Toast.LENGTH_SHORT);
		
		sceneRender = new SceneRenderer(); // 创建场景渲染器
		setRenderer(sceneRender); // 设置渲染器
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);// 设置渲染模式为主动渲染
		
		Toast.makeText(context, "关卡"+mainActivity.getSettings().getInt("currentLevel", 1)+"   本关目标："+mainActivity.getSettings().getInt("currentLevel", 1)*2000, Toast.LENGTH_LONG).show();
	

	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {

		timeGame.onTouch(e); // 调用游戏的onTouch函数来处理触屏消息
		
		//mainActivity.getSoundControl().paisound();


		drawTrack.onTouchEvent(e); // 响应触屏消息来画轨迹

		requestRender(); // 强制重绘画面

		return true;
	}

	private class SceneRenderer implements GLSurfaceView.Renderer { // 表示当前3D界面的渲染器

		private int lightAngle = 90;// 灯的当前角度

		private int coneTextureId; // 圆锥的纹理Id
		private int cylinderTextureId; // 圆柱的纹理Id
		private int circleTextureId; // 圆的纹理Id
		
		private int pauseTextureId;
		
		private int trackTextureId;

		private int backgroundTextureId; // 背景的纹理Id
		
		private int backgroundImages[];
		
		private int scoreTextureId;
		
		private int timeTextureId;
		

		public SceneRenderer() { // 渲染器的构造函数

		}

		public void onSurfaceCreated(final GL10 gl, EGLConfig config) { // 界面生成时调用，用于初始化各种资源

			gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // 设置清除背景颜色
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT); // 清除颜色缓冲
			


			coneTextureId = initTexture(gl, SelectControl.getConeTextureId()); // 初始化圆锥的纹理
			cylinderTextureId = initTexture(gl, SelectControl.getCylinderTextureId()); // 初始化圆柱的纹理
			circleTextureId = initTexture(gl, SelectControl.getCircleTextureId()); // 初始化圆的纹理
			
			pauseTextureId = initTexture(gl, R.drawable.pause_bg);
			
			
			timeGame = new TimeGame(gl, coneTextureId, cylinderTextureId, // 创建游戏对象
					circleTextureId, pauseTextureId);
			timeGame.setCrackFlag(mainActivity.getSettings().getBoolean("crack", false));
			
			timeGame.setCurrentLevel(mainActivity.getSettings().getInt("currentLevel", 1));
			timeGame.calculateGoal();

			
			backgroundImages = new int[]{ 0, R.drawable.time_game_one_bg, R.drawable.time_game_two_bg, 
					R.drawable.time_game_three_bg, R.drawable.time_game_four_bg, R.drawable.time_game_five_bg,
					R.drawable.time_game_six_bg, R.drawable.time_game_seven_bg, R.drawable.time_game_eight_bg};		
			
			backgroundTextureId = initTexture(gl, backgroundImages[timeGame.getCurrentLevel()]); // 初始化背景的纹理
			
			trackTextureId = initTexture(gl, R.drawable.track);
			
			scoreTextureId = initTexture(gl, R.drawable.number);
			
			timeTextureId = initTexture(gl, R.drawable.number);
			
			
			

			

			drawTrack = new DrawTrack(trackTextureId); // 创建画轨迹的对象

			drawBackground = new DrawBackground(backgroundTextureId); // 创建画背景的对象
			
			drawScore = new DrawScore(scoreTextureId, TimeGameSurfaceView.this);
			
			drawTime = new DrawScore(timeTextureId, TimeGameSurfaceView.this);
			

			gl.glDisable(GL10.GL_DITHER); // 关闭抗抖动
			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST); // 设置特定Hint项目的模式，这里为设置为使用快速模式
			gl.glClearColor(0, 0, 0, 0); // 设置屏幕背景色黑色RGBA
			gl.glShadeModel(GL10.GL_SMOOTH); // 设置着色模型为平滑着色
			gl.glEnable(GL10.GL_DEPTH_TEST); // 启用深度测试
			
		    gl.glEnable(GL10.GL_ALPHA_TEST);  // Enable Alpha Testing (To Make BlackTansparent)  
		    
		    gl.glAlphaFunc(GL10.GL_GREATER,0.1f);  // Set Alpha Testing (To Make Black Transparent)

			
		}

		public void onDrawFrame(GL10 gl) { // 界面的绘图函数，不能控制刷新频率
			// gl.glClearColor(0.5f, 0.5f, 0.5f, 0.0f); // 使用特定的清除颜色
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT); // 清除颜色缓存

			gl.glMatrixMode(GL10.GL_MODELVIEW); // 设置当前矩阵为模式矩阵
			gl.glLoadIdentity(); // 设置当前矩阵为单位矩阵
			gl.glPushMatrix();// 保护变换矩阵现场

			float lx = 0; // 设定光源的位置
			float ly = (float) (7 * Math.cos(Math.toRadians(lightAngle)));
			float lz = (float) (7 * Math.sin(Math.toRadians(lightAngle)));
			float[] positionParamsRed = { lx, ly, lz, 0 };
			gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, positionParamsRed, 0);

			initMaterial(gl);// 初始化材质

			initLight(gl);// 开灯
			
			
			if(mainActivity.isPressMenu()){
				timeGame.setState(GameFrame.PAUSE);
				mainActivity.setPressMenu(false);
			}
			
			if(timeGame.isCollideFlag()){
				mainActivity.getHandler().sendEmptyMessage(MainActivity.VIBRATE_MESSAGE);
				timeGame.setCollideFlag(false);
			}
			
			if(timeGame.getState()==GameFrame.RUN){
				gl.glTranslatef(0f, 0f, -100.0f); // 将整体视图下移100（这个值没什么意义），保证可以看到陀螺顶部
		
				
				gl.glPushMatrix(); // 保护当前矩阵
	//			if(showFlag){
	//				gl.glRotatef(showAngle, 0, 0, 1);		
	//			}
				drawBackground.drawSelf(gl); // 画背景
				gl.glPopMatrix(); // 回复之前变换矩阵
				
				
				gl.glPushMatrix(); // 画图前保存矩阵						
				timeGame.draw(gl); // 调用游戏的绘图函数
				gl.glPopMatrix(); // 画图后恢复矩阵
	
				
				gl.glPushMatrix();
				drawTrack.drawSelf(gl);
				gl.glPopMatrix();			

				
				gl.glPushMatrix();
				gl.glTranslatef(-1.5f, 0.6f, 3);
				drawScore.setScore((int)timeGame.getScore());
				drawScore.drawSelf(gl);
				gl.glPopMatrix();
				
				
				gl.glPushMatrix();
				gl.glTranslatef(1.3f, 0.6f, 3);
				drawTime.setScore(60-(int)timeGame.getDuration());
				drawTime.drawSelf(gl);
				gl.glPopMatrix();


			}else if(timeGame.getState()==GameFrame.END){
				//Toast.makeText(MainActivity.this, "游戏结束，感谢参与", Toast.LENGTH_SHORT);

				
				SharedPreferences settings = mainActivity.getSettings();
				SharedPreferences.Editor editor = settings.edit();
				
				if(timeGame.getScore()>timeGame.getCurrentGoal() ){ // 如果当前得分比目标分数要高
					
				    if(settings.getInt("currentLevel", 1)>=settings.getInt("level", 1) && settings.getInt("level", 1)<8){ // 如果当前level比最高level还大且最高值小于8
					    editor.putInt("level", settings.getInt("currentLevel", 1)+1);
					    editor.commit();							 
				    }
				    
				    nextLevelToast.show();
				    
				}else{
					notNextLevelToast.show();
				}
				

				//mainActivity.updateScore((int)timeGame.getScore());
				mainActivity.setCurrScore((int)timeGame.getScore());

				
				mainActivity.getHandler().sendEmptyMessage(SurfaceViewFactory.END);

//				try {
//					Thread.sleep(500);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
//				mainActivity.getHandler().sendEmptyMessage(SurfaceViewFactory.END);
			}else if(timeGame.getState()==GameFrame.PAUSE){
				gl.glTranslatef(0f, 0f, -100.0f); // 将整体视图下移100（这个值没什么意义），保证可以看到陀螺顶部		
				gl.glPushMatrix(); // 画图前保存矩阵					
				timeGame.draw(gl); // 调用游戏的绘图函数
				gl.glPopMatrix(); // 画图后恢复矩阵
			}
			
			
			

			closeLight(gl);// 关灯

			gl.glPopMatrix();// 恢复变换矩阵现场
		}

		public void onSurfaceChanged(GL10 gl, int width, int height) { // 窗口发生改变时调用，一般只可能会调用一次
			gl.glViewport(0, 0, width, height); // 设置视窗大小及位置
			gl.glMatrixMode(GL10.GL_PROJECTION); // 设置当前矩阵为投影矩阵
			gl.glLoadIdentity(); // 设置当前矩阵为单位矩阵
			float ratio = (float) width / height; // 计算透视投影的比例
			// gl.glFrustumf(-ratio, ratio, -1, 1, 1, 100); // 调用此方法计算产生透视投影矩阵
			gl.glOrthof(-ratio, ratio, -1, 1, 1, 100); // 使用平行投影避免了夸张的3D效果，参数100也是没什么意义的

			Constant.SCREEN_WIDTH = width; // 屏幕改变时设置Constant的变量，此函数一般只会调用一次，而且是延后调用
			Constant.SCREEN_HEIGHT = height;
			Constant.SCREEN_RATE = (float) (0.5 * 4 / Constant.SCREEN_HEIGHT);

			timeGame.start();
		}

	}

	private void initLight(GL10 gl) { // 初始化白色灯
		gl.glEnable(GL10.GL_LIGHTING);// 允许光照
		gl.glEnable(GL10.GL_LIGHT1);// 打开1号灯

		// 环境光设置
		float[] ambientParams = { 0.2f, 0.2f, 0.2f, 1.0f };// 光参数 RGBA
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_AMBIENT, ambientParams, 0);

		// 散射光设置
		float[] diffuseParams = { 1f, 1f, 1f, 1.0f };// 光参数 RGBA
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_DIFFUSE, diffuseParams, 0);

		// 反射光设置
		float[] specularParams = { 1f, 1f, 1f, 1.0f };// 光参数 RGBA
		gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_SPECULAR, specularParams, 0);
	}

	private void closeLight(GL10 gl) {// 关闭灯
		gl.glDisable(GL10.GL_LIGHT1);
		gl.glDisable(GL10.GL_LIGHTING);
	}

	private void initMaterial(GL10 gl) { // 初始化材质
		// 环境光
		float ambientMaterial[] = { 248f / 255f, 242f / 255f, 144f / 255f, 1.0f };
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT,
				ambientMaterial, 0);
		// 散射光
		float diffuseMaterial[] = { 248f / 255f, 242f / 255f, 144f / 255f, 1.0f };
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE,
				diffuseMaterial, 0);
		// 高光材质
		float specularMaterial[] = { 248f / 255f, 242f / 255f, 144f / 255f,
				1.0f };
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR,
				specularMaterial, 0);
		gl.glMaterialf(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, 100.0f);
	}

	public int initTexture(GL10 gl, int drawableId) // 初始化纹理
	{
		// 生成纹理ID
		int[] textures = new int[1];
		gl.glGenTextures(1, textures, 0);
		int currTextureId = textures[0];
		gl.glBindTexture(GL10.GL_TEXTURE_2D, currTextureId);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER,
				GL10.GL_LINEAR_MIPMAP_NEAREST);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER,
				GL10.GL_LINEAR_MIPMAP_LINEAR);
		((GL11) gl).glTexParameterf(GL10.GL_TEXTURE_2D,
				GL11.GL_GENERATE_MIPMAP, GL10.GL_TRUE);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,
				GL10.GL_REPEAT);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,
				GL10.GL_REPEAT);

		InputStream is = this.getResources().openRawResource(drawableId);
		Bitmap bitmapTmp;
		try {
			bitmapTmp = BitmapFactory.decodeStream(is);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmapTmp, 0);
		bitmapTmp.recycle();

		return currTextureId;
	}

}
