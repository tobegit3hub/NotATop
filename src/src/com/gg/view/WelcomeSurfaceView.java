package com.gg.view;

import com.gg.util.Constant;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class WelcomeSurfaceView extends SurfaceView 
implements SurfaceHolder.Callback  //实现生命周期回调接口
{
	MainActivity mainActivity;
	Paint paint;//画笔
	int currentAlpha=0;//当前的不透明值
	
	int screenWidth=Constant.SCREEN_WIDTH;//屏幕宽度
	int screenHeight=Constant.SCREEN_HEIGHT;//屏幕高度
	int sleepSpan=50;//动画的时延ms
	
	Bitmap[] images=new Bitmap[2];//logo图片数组
	Bitmap currentImage;//当前logo图片引用
	int currentX;
	int currentY;
	
	public WelcomeSurfaceView(MainActivity mainActivity) {
		super(mainActivity);
		this.mainActivity = mainActivity;
		this.getHolder().addCallback(this);//设置生命周期回调接口的实现者
		paint = new Paint();//创建画笔
		paint.setAntiAlias(true);//打开抗锯齿
		
		//加载图片
		images[0]=BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.welcome_top); 
		images[1]=BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.welcome_gg);
	}
	public void onDraw(Canvas canvas){	
		//绘制黑填充矩形清背景
		paint.setColor(Color.BLACK);//设置画笔颜色
		paint.setAlpha(255);
		canvas.drawRect(0, 0, screenWidth, screenHeight, paint);
		
		//进行平面贴图
		if(currentImage==null)return;
		paint.setAlpha(currentAlpha);		
		//canvas.drawBitmap(currentImage, currentX, currentY, paint);	
		
		canvas.drawBitmap(currentImage, new Rect(0, 0,
				(int) currentImage.getWidth(), (int) currentImage
						.getHeight()), new Rect(0, 0,
				(int) Constant.SCREEN_WIDTH, (int) Constant.SCREEN_HEIGHT), paint);

	}

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		
	}
	public void surfaceCreated(SurfaceHolder holder) {//创建时被调用		
		new Thread()
		{
			public void run()
			{
				for(Bitmap bm:images)
				{
					currentImage=bm;
					//计算图片位置
					currentX=screenWidth/2-bm.getWidth()/2;
					currentY=screenHeight/2-bm.getHeight()/2;
					
					for(int i=255;i>-100;i=i-15)
					{//动态更改图片的透明度值并不断重绘	
						currentAlpha=i;
						if(currentAlpha<0)
						{
							currentAlpha=0;
						}
						SurfaceHolder myholder=WelcomeSurfaceView.this.getHolder();
						Canvas canvas = myholder.lockCanvas();//获取画布
						try{
							synchronized(myholder){
								onDraw(canvas);//绘制
							}
						}
						catch(Exception e){
							e.printStackTrace();
						}
						finally{
							if(canvas != null){
								myholder.unlockCanvasAndPost(canvas);
							}
						}						
						try
						{
							if(i==255)
							{//若是新图片，多等待一会
								Thread.sleep(1000);
							}
							Thread.sleep(sleepSpan);
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}
				//动画播放完毕后，去主菜单界面
				//activity.sendMessage(WhatMessage.GOTO_MAIN_MENU_VIEW);
				mainActivity.getHandler().sendEmptyMessage(SurfaceViewFactory.MAIN_MENU);
			}
		}.start();
	}

	public void surfaceDestroyed(SurfaceHolder arg0) {//销毁时被调用

	}
}