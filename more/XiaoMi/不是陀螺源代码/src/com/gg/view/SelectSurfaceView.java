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

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.widget.Toast;

import com.gg.game.ClassicGame;
import com.gg.game.GameFrame;
import com.gg.module.*;
import com.gg.top.*;
import com.gg.util.*;


public class SelectSurfaceView extends GLSurfaceView {

	private MainActivity mainActivity; // 使用此界面的Activity
	private SceneRenderer sceneRender; // 场景渲染器
	
	private int nextConeTextureId;
	private int nextCylinderTextureId;
	private int nextCircleTextureId;
	private int prevConeTextureId;
	private int prevCylinderTextureId;
	private int prevCircleTextureId;
	
	private int currentConeTextureId; // 圆锥的纹理Id
	private int currentCylinderTextureId; // 圆柱的纹理Id
	private int currentCircleTextureId; // 圆的纹理Id
	private int hiddenConeTextureId;
	private int hiddenCylinderTextureId;
	private int hiddenCircleTextureId;

	private int[] initConeTextureIdArray; // 不能动态initTexture图片，只能在这里逐个初始化
	private int[] initCylinderTextureIdArray;
	private int[] initCircleTextureIdArray;

	private int backgroundTextureId; // 背景的纹理Id

	private DrawTop nextDrawTop;
	private DrawTop prevDrawTop;
	
	private DrawTop currentDrawTop;
	private DrawTop hiddenDrawTop;
	
	private int index;
	private int numberOfTop;

	private boolean moveLeftFlag;
	private boolean moveRightFlag;
	
	private Toast noLeftToast;
	private Toast noRightToast;
	
	private boolean moveHiddenToLeftFlag = true; // 默认hiddenDrawTop是在当前陀螺的右侧，此标记让hiddenDrawTop移到左侧方便右移

	private DrawTrack drawTrack; // 表示划屏轨迹的对象

	private DrawBackground drawBackground; // 表示背景的对象
	
	private double downX;
	private double downY;
	private double upX;
	private double upY;

	
	public SelectSurfaceView(Context context) {
		super(context);
		mainActivity = (MainActivity) context; // 获得使用此界面的Activity
		sceneRender = new SceneRenderer(); // 创建场景渲染器
		setRenderer(sceneRender); // 设置渲染器
		setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);// 设置渲染模式为主动渲染
		
		index = SelectControl.getIndex();
		numberOfTop = SelectControl.getNumberOfTop();
		
		noLeftToast = Toast.makeText(context, "已经是第一个了", Toast.LENGTH_SHORT);
		noRightToast = Toast.makeText(context, "已经是最后一个了", Toast.LENGTH_SHORT);
		
		
		initConeTextureIdArray = new int[numberOfTop]; // 不能动态initTexture图片，只能在这里逐个初始化
		initCylinderTextureIdArray = new int[numberOfTop];
		initCircleTextureIdArray = new int[numberOfTop];
		
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) { // 应该是多点触控有bug
		switch(e.getAction()){
		case MotionEvent.ACTION_DOWN:
			downX = Constant.convertX(e.getX());
			downY = Constant.convertY(e.getY());
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			upX = Constant.convertX(e.getX());
			upY = Constant.convertY(e.getY());		
			
			if(moveLeftFlag==false && moveRightFlag==false){ // 如果还在移动则不响应了

				if(downX<-1.0 && downY>0.3 && upX<-1.0 && upY>0.3){
					mainActivity.getHandler().sendEmptyMessage(SurfaceViewFactory.MAIN_MENU);
				}else if(downX>1.0 && downY>0.3 && upX>1.0 && upY>0.3){
					mainActivity.getHandler().sendEmptyMessage(SurfaceViewFactory.CLASSIC_GAME);
				}				
				
				
				
				
				if(downX>-1.5 && downX<-0.9 && downY>-0.7 && downY<0.0){ // 左下角的“上一个”按钮，陀螺右移
					if(upX>-1.5 && upX<-0.9 && upY>-0.7 && upY<0.0){ // 同时匹配手指移开时的位置，防止误触
						if(index>0){ // 如果还有上一个陀螺可选
							moveRightFlag = true; // 有则设置平移标志以实行陀螺的平移
						}else{
							noLeftToast.show(); // 没有则显示提示信息
						}
					}
				}else if(downX<1.5 && downX>0.9 && downY>-0.7 && downY<0.0){ // 右下角的“下一个”按钮，陀螺左移
					if(upX<1.5 && upX>0.9 && upY>-0.7 && upY<0.0){
						if(index<numberOfTop-1){
							moveLeftFlag = true;
						}else{
							noRightToast.show();
						}
					}
				}
				
				if(downX>-0.9 && downX<0.9){ // 手指右划，陀螺右移
					if(upX-downX>0.5){
						if(index>0){
							moveRightFlag = true;
						}else{
							noLeftToast.show();
						}
					}else if(downX-upX>0.5){ // 手指左划，陀螺左移
						if(index<numberOfTop-1){
							moveLeftFlag = true;
						}else{
							noRightToast.show();
						}
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
		
		private int trackTextureId;

		public SceneRenderer() { // 渲染器的构造函数

		}

		public void onSurfaceCreated(final GL10 gl, EGLConfig config) { // 界面生成时调用，用于初始化各种资源

			gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f); // 设置清除背景颜色
			gl.glClear(GL10.GL_COLOR_BUFFER_BIT); // 清除颜色缓冲

			nextConeTextureId = initTexture(gl, R.drawable.next_cone);
			nextCylinderTextureId = initTexture(gl, R.drawable.next_cylinder);
			nextCircleTextureId = initTexture(gl, R.drawable.next_circle);
			prevConeTextureId = initTexture(gl, R.drawable.prev_cone);
			prevCylinderTextureId = initTexture(gl, R.drawable.prev_cylinder);
			prevCircleTextureId = initTexture(gl, R.drawable.prev_circle);

			
			
			initConeTextureIdArray[0] = initTexture(gl, R.drawable.cone0);
			initConeTextureIdArray[1] = initTexture(gl, R.drawable.cone1);
			initConeTextureIdArray[2] = initTexture(gl, R.drawable.cone2);
			initConeTextureIdArray[3] = initTexture(gl, R.drawable.cone3);
			initConeTextureIdArray[4] = initTexture(gl, R.drawable.cone4);
			
			initCylinderTextureIdArray[0] = initTexture(gl, R.drawable.cylinder0);
			initCylinderTextureIdArray[1] = initTexture(gl, R.drawable.cylinder1);
			initCylinderTextureIdArray[2] = initTexture(gl, R.drawable.cylinder2);
			initCylinderTextureIdArray[3] = initTexture(gl, R.drawable.cylinder3);
			initCylinderTextureIdArray[4] = initTexture(gl, R.drawable.cylinder4);
			
			initCircleTextureIdArray[0] = initTexture(gl, R.drawable.circle0);
			initCircleTextureIdArray[1] = initTexture(gl, R.drawable.circle1);
			initCircleTextureIdArray[2] = initTexture(gl, R.drawable.circle2);
			initCircleTextureIdArray[3] = initTexture(gl, R.drawable.circle3);
			initCircleTextureIdArray[4] = initTexture(gl, R.drawable.circle4);
			
			
			
			currentConeTextureId = initTexture(gl, SelectControl.getConeTextureId());
			currentCylinderTextureId = initTexture(gl, SelectControl.getCylinderTextureId());
			currentCircleTextureId = initTexture(gl, SelectControl.getCircleTextureId());
			
			SelectControl.next();
			hiddenConeTextureId = initTexture(gl, SelectControl.getConeTextureId());
			hiddenCylinderTextureId = initTexture(gl, SelectControl.getCylinderTextureId());
			hiddenCircleTextureId = initTexture(gl, SelectControl.getCircleTextureId());
			SelectControl.prev();
			
			

			backgroundTextureId = initTexture(gl, R.drawable.select_bg); // 初始化背景的纹理
			
			trackTextureId = initTexture(gl, R.drawable.track);


			prevDrawTop = new DrawTop(prevConeTextureId,prevCylinderTextureId,prevCircleTextureId);
			prevDrawTop.setRadius(0.2f);
			prevDrawTop.setBasicPoint(new Point(-1.2f, -2.0f));
			prevDrawTop.setAngleSpeed(5);
			prevDrawTop.generateData();
			
			
			nextDrawTop = new DrawTop(nextConeTextureId,nextCylinderTextureId,nextCircleTextureId);
			nextDrawTop.setRadius(0.2f);
			nextDrawTop.setBasicPoint(new Point(1.2f, -2.0f));
			nextDrawTop.setAngleSpeed(5);
			nextDrawTop.generateData();

			
			
			currentDrawTop = new DrawTop(currentConeTextureId,currentCylinderTextureId,currentCircleTextureId);
			currentDrawTop.setRadius(0.55f);
			currentDrawTop.setBasicPoint(new Point(0.0f, -1.65f));
			currentDrawTop.setAngleSpeed(3);
			currentDrawTop.generateData();
			
			hiddenDrawTop = new DrawTop(hiddenConeTextureId,hiddenCylinderTextureId,hiddenCircleTextureId);
			hiddenDrawTop.setRadius(0.55f);
			hiddenDrawTop.setBasicPoint(new Point(2.5f, -1.65f));
			hiddenDrawTop.setAngleSpeed(3);
			hiddenDrawTop.generateData();
	

			drawTrack = new DrawTrack(trackTextureId); // 创建画轨迹的对象

			drawBackground = new DrawBackground(backgroundTextureId); // 创建画背景的对象

			gl.glDisable(GL10.GL_DITHER); // 关闭抗抖动
			gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST); // 设置特定Hint项目的模式，这里为设置为使用快速模式
			gl.glClearColor(0, 0, 0, 0); // 设置屏幕背景色黑色RGBA
			gl.glShadeModel(GL10.GL_SMOOTH); // 设置着色模型为平滑着色
			gl.glEnable(GL10.GL_DEPTH_TEST); // 启用深度测试


			new Thread()
			{
				public void run() {
					while (true) {
						nextDrawTop.rotate();
						prevDrawTop.rotate();
							
						currentDrawTop.rotate();
						hiddenDrawTop.rotate();
						
						if(moveLeftFlag){
							if(hiddenDrawTop.getBasicPoint().x>=0f){
								currentDrawTop.setBasicPoint(new Point(currentDrawTop.getBasicPoint().x-0.1f, currentDrawTop.getBasicPoint().y));
								hiddenDrawTop.setBasicPoint(new Point(hiddenDrawTop.getBasicPoint().x-0.1f, hiddenDrawTop.getBasicPoint().y));
							}else{
								moveLeftFlag = false;
								
								SelectControl.next();
								index++;
								
								currentDrawTop.setConeTextureId(initConeTextureIdArray[index]);
								currentDrawTop.setCylinderTextureId(initCylinderTextureIdArray[index]);
								currentDrawTop.setCircleTextureId(initCircleTextureIdArray[index]);
								//currentDrawTop.generateData();
								currentDrawTop.setBasicPoint(new Point(0f, currentDrawTop.getBasicPoint().y));
								
								if(index<numberOfTop-1){ // 防止已经到最右边了还为hiddenDrawTop赋溢出的值
									index++;
									hiddenDrawTop.setConeTextureId(initConeTextureIdArray[index]);
									hiddenDrawTop.setCylinderTextureId(initCylinderTextureIdArray[index]);
									hiddenDrawTop.setCircleTextureId(initCircleTextureIdArray[index]);
									//hiddenDrawTop.generateData();
									index--;
								}
								hiddenDrawTop.setBasicPoint(new Point(2.5f, hiddenDrawTop.getBasicPoint().y));
								
							}
							
						}
						
						if(moveRightFlag){
							if(moveHiddenToLeftFlag==true){

								if(index>0){ // 防止溢出
									index--;
									hiddenDrawTop.setConeTextureId(initConeTextureIdArray[index]);
									hiddenDrawTop.setCylinderTextureId(initCylinderTextureIdArray[index]);
									hiddenDrawTop.setCircleTextureId(initCircleTextureIdArray[index]);
									//hiddenDrawTop.generateData();
									index++;
								}
								hiddenDrawTop.setBasicPoint(new Point(-2.5f, hiddenDrawTop.getBasicPoint().y));		
								
								moveHiddenToLeftFlag = false;
							}
							
							if(hiddenDrawTop.getBasicPoint().x<=0f){
								currentDrawTop.setBasicPoint(new Point(currentDrawTop.getBasicPoint().x+0.1f, currentDrawTop.getBasicPoint().y));
								hiddenDrawTop.setBasicPoint(new Point(hiddenDrawTop.getBasicPoint().x+0.1f, hiddenDrawTop.getBasicPoint().y));
							}else{
								moveRightFlag = false;
								moveHiddenToLeftFlag = true;
								
								SelectControl.prev();
								index--;
								
								currentDrawTop.setConeTextureId(initConeTextureIdArray[index]);
								currentDrawTop.setCylinderTextureId(initCylinderTextureIdArray[index]);
								currentDrawTop.setCircleTextureId(initCircleTextureIdArray[index]);
								//currentDrawTop.generateData();
								currentDrawTop.setBasicPoint(new Point(0f, currentDrawTop.getBasicPoint().y));
								
								if(index<numberOfTop-1){
									index++;
									hiddenDrawTop.setConeTextureId(initConeTextureIdArray[index]);
									hiddenDrawTop.setCylinderTextureId(initCylinderTextureIdArray[index]);
									hiddenDrawTop.setCircleTextureId(initCircleTextureIdArray[index]);
									//hiddenDrawTop.generateData();
									index--;
								}
								hiddenDrawTop.setBasicPoint(new Point(2.5f, hiddenDrawTop.getBasicPoint().y));
							}							
						}
						
						
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

			float lx = 0; // 设定光源的位置
			float ly = (float) (7 * Math.cos(Math.toRadians(lightAngle)));
			float lz = (float) (7 * Math.sin(Math.toRadians(lightAngle)));
			float[] positionParamsRed = { lx, ly, lz, 0 };
			gl.glLightfv(GL10.GL_LIGHT1, GL10.GL_POSITION, positionParamsRed, 0);

			initMaterial(gl);// 初始化材质

			initLight(gl);// 开灯

			gl.glTranslatef(0f, 0f, -100.0f); // 将整体视图下移100（这个值没什么意义），保证可以看到陀螺顶部

			
			gl.glPushMatrix();
			gl.glTranslatef(0, 0, -1);
			gl.glRotatef(-70, 1, 0, 0);
			nextDrawTop.drawSelf(gl);
			gl.glPopMatrix();
			
			gl.glPushMatrix();
			gl.glTranslatef(0, 0, -1);
			gl.glRotatef(-70, 1, 0, 0);
			prevDrawTop.drawSelf(gl);
			gl.glPopMatrix();

			gl.glPushMatrix();
			gl.glRotatef(-60, 1, 0, 0);
			currentDrawTop.drawSelf(gl);
			gl.glPopMatrix();
			
			gl.glPushMatrix();
			gl.glRotatef(-60, 1, 0, 0);
			hiddenDrawTop.drawSelf(gl);
			gl.glPopMatrix();
			

			
			

			gl.glPushMatrix();
			drawTrack.drawSelf(gl);
			gl.glPopMatrix();

			gl.glPushMatrix(); // 保护当前矩阵
//			gl.glTranslatef(0, 0, -1);
//			if(showFlag){
//				gl.glRotatef(showAngle, 0, 0, 1);		
//			}
			drawBackground.drawSelf(gl); // 画背景
			gl.glPopMatrix(); // 回复之前变换矩阵

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
