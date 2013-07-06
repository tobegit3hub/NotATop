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
import com.gg.module.DrawBackground;
import com.gg.module.DrawTrack;
import com.gg.top.*;
import com.gg.util.*;


public class MainMenuSurfaceView extends GLSurfaceView {

	private MainActivity mainActivity; // 使用此界面的Activity
	private SceneRenderer sceneRender; // 场景渲染器


	private int onBackgroundTextureId; // 背景的纹理Id
	private int offBackgroundTextureId;
	
	
	private DrawTop selectDrawTop;
	private DrawTop startDrawTop;
	private DrawTop scoreDrawTop;
	private DrawTop helpDrawTop;

	private DrawTrack drawTrack; // 表示划屏轨迹的对象

	private DrawBackground drawBackground; // 表示背景的对象
	
	private Point downPoint;
	private Point upPoint;
	
	private double downX;
	private double downY;
	private double upX;
	private double upY;


	private boolean crackFlag;
	private long crackTime;
	
	
	public MainMenuSurfaceView(Context context) {
		super(context);
		mainActivity = (MainActivity) context; // 获得使用此界面的Activity
		sceneRender = new SceneRenderer(); // 创建场景渲染器
		setRenderer(sceneRender); // 设置渲染器
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);// 设置渲染模式为主动渲染
		
		downPoint = new Point(0,0,0);
		upPoint = new Point(0,0,0);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) { // 应该是多点触控有bug
		switch(e.getAction()){
		case MotionEvent.ACTION_DOWN:
			if(e.getX()<1.5/8.8*Constant.SCREEN_WIDTH && e.getY()<1.0/5*Constant.SCREEN_HEIGHT){
				crackFlag = true;
				crackTime = System.currentTimeMillis();
			}
					
			downX = Constant.convertX(e.getX());
			downY = Constant.convertY(e.getY());
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			if(e.getX()<1.5/8.8*Constant.SCREEN_WIDTH && e.getY()<1.0/5*Constant.SCREEN_HEIGHT){
				if(crackFlag==true && System.currentTimeMillis()-crackTime>=3000){
					SharedPreferences settings = mainActivity.getSettings();
					SharedPreferences.Editor editor = settings.edit();
					if(settings.getBoolean("crack", false)==false){
						Toast.makeText(mainActivity, "^_^ 开启隐藏功能 ^_^", Toast.LENGTH_SHORT).show();
						editor.putBoolean("crack", true);
					}else{
						Toast.makeText(mainActivity, "^o^ 关闭隐藏功能 ^o^", Toast.LENGTH_SHORT).show();
						editor.putBoolean("crack", false);
					}
				    editor.commit();
				}
			}
			crackFlag = false;
			
			upX = Constant.convertX(e.getX());
			upY = Constant.convertY(e.getY());
			
			if(downX>-1.4 && downX<-0.9 && downY<-0.25){
				if(upX>-1.4 && upX<-0.9 && upY<-0.25){
					mainActivity.getHandler().sendEmptyMessage(SurfaceViewFactory.HELP);
				}
			}else if(downX>-0.9 && downX<-0.1 && downY<0.2){
				if(upX>-0.9 && upX<-0.1 && upY<0.2){
					mainActivity.getHandler().sendEmptyMessage(SurfaceViewFactory.SCORE);
				}
			}else if(downX>-0.1 && downX<0.8 && downY<0.5){
				if(upX>-0.1 && upX<0.8 && upY<0.5){
					mainActivity.getHandler().sendEmptyMessage(SurfaceViewFactory.GAME_MODE);
				}
			}else if(downX>0.8 && downX<1.5 && downY<0.2){
				if(upX>0.8 && upX<1.5 && upY<0.2){
					mainActivity.getHandler().sendEmptyMessage(SurfaceViewFactory.SELECT);
				}
			}
			
			if(downX<-1.4 && downY<-0.7){
				if(upX<-1.4 && upY<-0.7){
					mainActivity.getHandler().sendEmptyMessage(MainActivity.VOICE_MESSAGE);
				}
			}else if(downX>1.3 && downY>0.6){
				if(upX>1.3 && upY>0.6){
					
					mainActivity.getSoundControl().setMusic();
					mainActivity.getSoundControl().choose();
					
					if(mainActivity.getSoundControl().isSoundOn()){
						drawBackground.setDrawableId(onBackgroundTextureId);
					}else{
						drawBackground.setDrawableId(offBackgroundTextureId);
					}

				}
			}

			break;
		}

		
		drawTrack.onTouchEvent(e); // 响应触屏消息来画轨迹

		requestRender(); // 强制重绘画面

		return true;
	}



	private class SceneRenderer implements GLSurfaceView.Renderer { // 表示当前3D界面的渲染器

		private int lightAngle = 90;// 灯的当前角度

		private int selectConeTextureId; // 圆锥的纹理Id
		private int selectCylinderTextureId; // 圆柱的纹理Id
		private int selectCircleTextureId; // 圆的纹理Id
		private int startConeTextureId;
		private int startCylinderTextureId;
		private int startCircleTextureId;
		private int scoreConeTextureId;
		private int scoreCylinderTextureId;
		private int scoreCircleTextureId;
		private int helpConeTextureId;
		private int helpCylinderTextureId;
		private int helpCircleTextureId;
		
		private int trackTextureId;


		public SceneRenderer() { // 渲染器的构造函数

		}

		public void onSurfaceCreated(final GL10 gl, EGLConfig config) { // 界面生成时调用，用于初始化各种资源

			if(mainActivity.isFirstTimeFlag()){
				mainActivity.getHandler().sendEmptyMessage(SurfaceViewFactory.FIRST_TIME);
				mainActivity.setFirstTimeFlag(false);
			}
			
			
			gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // 设置清除背景颜色
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT); // 清除颜色缓冲

			selectConeTextureId = initTexture(gl, R.drawable.select_cone); // 初始化圆锥的纹理
			selectCylinderTextureId = initTexture(gl, R.drawable.select_cylinder); // 初始化圆柱的纹理
			selectCircleTextureId = initTexture(gl, R.drawable.select_circle); // 初始化圆的纹理
			startConeTextureId = initTexture(gl, R.drawable.start_cone);
			startCylinderTextureId = initTexture(gl, R.drawable.start_cylinder);
			startCircleTextureId = initTexture(gl, R.drawable.start_circle);
			scoreConeTextureId = initTexture(gl, R.drawable.score_cone); 
			scoreCylinderTextureId = initTexture(gl, R.drawable.score_cylinder);
			scoreCircleTextureId = initTexture(gl, R.drawable.score_circle);
			helpConeTextureId = initTexture(gl, R.drawable.help_cone);
			helpCylinderTextureId = initTexture(gl, R.drawable.help_cylinder);
			helpCircleTextureId = initTexture(gl, R.drawable.help_circle);

			onBackgroundTextureId = initTexture(gl, R.drawable.main_menu_on_bg); // 初始化背景的纹理
			offBackgroundTextureId = initTexture(gl, R.drawable.main_menu_off_bg);
			
			trackTextureId = initTexture(gl, R.drawable.track);
	
			
			helpDrawTop = new DrawTop(helpConeTextureId,helpCylinderTextureId,helpCircleTextureId);
			helpDrawTop.setRadius(0.2f);
			helpDrawTop.setBasicPoint(new Point(-1.15f, -2.1f));
			helpDrawTop.setAngleSpeed(3f);
			helpDrawTop.generateData();
			
			
			
			scoreDrawTop = new DrawTop(scoreConeTextureId,scoreCylinderTextureId,scoreCircleTextureId);
			scoreDrawTop.setRadius(0.3f);
			scoreDrawTop.setBasicPoint(new Point(-0.5f, -2.3f));
			scoreDrawTop.setAngleSpeed(4);
			scoreDrawTop.generateData();
	
			

			
			startDrawTop = new DrawTop(startConeTextureId,startCylinderTextureId,startCircleTextureId);
			startDrawTop.setRadius(0.4f);
			startDrawTop.setBasicPoint(new Point(0.35f, -2.4f));
			startDrawTop.setAngleSpeed(5);
			startDrawTop.generateData();
			
			
			
			selectDrawTop = new DrawTop(selectConeTextureId,selectCylinderTextureId,selectCircleTextureId);
			selectDrawTop.setRadius(0.3f);
			selectDrawTop.setBasicPoint(new Point(1.2f, -2.2f));
			selectDrawTop.setAngleSpeed(4);
			selectDrawTop.generateData();

			
			
			drawTrack = new DrawTrack(trackTextureId); // 创建画轨迹的对象

			
			drawBackground = new DrawBackground(onBackgroundTextureId); // 创建画背景的对象
			
			if(mainActivity.getSoundControl().isSoundOn()){
				drawBackground.setDrawableId(onBackgroundTextureId);
			}else{
				drawBackground.setDrawableId(offBackgroundTextureId);
			}
			

			gl.glDisable(GL10.GL_DITHER); // 关闭抗抖动
			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST); // 设置特定Hint项目的模式，这里为设置为使用快速模式
			gl.glClearColor(0, 0, 0, 0); // 设置屏幕背景色黑色RGBA
			gl.glShadeModel(GL10.GL_SMOOTH); // 设置着色模型为平滑着色
			gl.glEnable(GL10.GL_DEPTH_TEST); // 启用深度测试


			new Thread() // 转动摄像机
			{
				public void run() {
					while (true) {
						selectDrawTop.rotate();
						startDrawTop.rotate();
						scoreDrawTop.rotate();
						helpDrawTop.rotate();
						
						try{
							sleep(Constant.INTERVAL);
						}catch(Exception e){
							e.printStackTrace();
						}
					}

				}
			}.start();
			
			
		}

		public void onDrawFrame(GL10 gl) { // 界面的绘图函数，不能控制刷新频率
			// gl.glClearColor(0.5f, 0.5f, 0.5f, 0.0f); // 使用特定的清除颜色
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT); // 清除颜色缓存

			gl.glMatrixMode(GL10.GL_MODELVIEW); // 设置当前矩阵为模式矩阵
			gl.glLoadIdentity(); // 设置当前矩阵为单位矩阵
			gl.glPushMatrix();// 保护变换矩阵现场

//			float lx = 0; // 设定光源的位置
//			float ly = (float) (7 * Math.cos(Math.toRadians(lightAngle)));
//			float lz = (float) (7 * Math.sin(Math.toRadians(lightAngle)));
//			float[] positionParamsRed = { lx, ly, lz, 0 };
//			gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, positionParamsRed, 0);
//
//			initMaterial(gl);// 初始化材质
//
//			initLight(gl);// 开灯

			gl.glTranslatef(0f, 0f, -100.0f); // 将整体视图下移100（这个值没什么意义），保证可以看到陀螺顶部
			

			
			gl.glPushMatrix();
			gl.glRotatef(-70, 1, 0, 0);
			gl.glRotatef(-10, 0, 1, 0);
			helpDrawTop.drawSelf(gl);
			gl.glPopMatrix();
			
			
			gl.glPushMatrix();
			gl.glRotatef(-70, 1, 0, 0);
			gl.glRotatef(-5, 0, 1, 0);
			scoreDrawTop.drawSelf(gl);
			gl.glPopMatrix();
			
			
			gl.glPushMatrix();
			gl.glRotatef(-70, 1, 0, 0);
			startDrawTop.drawSelf(gl);
			gl.glPopMatrix();
			
			
			gl.glPushMatrix();
			gl.glRotatef(-70, 1, 0, 0);
			gl.glRotatef(5, 0, 1, 0);
			selectDrawTop.drawSelf(gl);
			gl.glPopMatrix();



			
			

			gl.glPushMatrix();
			gl.glTranslatef(0, 0, 1);
			drawTrack.drawSelf(gl);
			gl.glPopMatrix();

			gl.glPushMatrix(); // 保护当前矩阵
//			if(showFlag){
//				gl.glRotatef(showAngle, 0, 0, 1);		
//			}
			drawBackground.drawSelf(gl); // 画背景
			gl.glPopMatrix(); // 回复之前变换矩阵

//			closeLight(gl);// 关灯

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
