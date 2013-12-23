package com.gg.view;

import com.gg.util.Constant;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

public class FirstTimeSurfaceView extends SurfaceView 
implements SurfaceHolder.Callback  //实现生命周期回调接口
{
	MainActivity mainActivity;
	Paint paint;//画笔
	Bitmap background;//当前logo图片引用
	
	private Toast helpToast;

	
	public FirstTimeSurfaceView(MainActivity mainActivity) {
		super(mainActivity);
		this.mainActivity = mainActivity;
		this.getHolder().addCallback(this);//设置生命周期回调接口的实现者
		paint = new Paint();//创建画笔
		paint.setAntiAlias(true);//打开抗锯齿
		
		//加载图片
		background=BitmapFactory.decodeResource(mainActivity.getResources(), R.drawable.first_time_bg); 
		
		helpToast = Toast.makeText(mainActivity, "进入帮助界面", Toast.LENGTH_SHORT);

	}
	public void onDraw(Canvas canvas){	
		
		//绘制黑填充矩形清背景
		paint.setColor(Color.WHITE);//设置画笔颜色
		paint.setAlpha(255);
		canvas.drawRect(0, 0, Constant.SCREEN_WIDTH, Constant.SCREEN_WIDTH, paint);

		canvas.drawBitmap(background, new Rect(0, 0,
				(int) background.getWidth(), (int) background
						.getHeight()), new Rect(0, 0,
				(int) Constant.SCREEN_WIDTH, (int) Constant.SCREEN_HEIGHT), paint);


	}

	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		
	}
	public void surfaceCreated(SurfaceHolder holder) {//创建时被调用		

		SurfaceHolder myholder=FirstTimeSurfaceView.this.getHolder();
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
				
	}

	public void surfaceDestroyed(SurfaceHolder arg0) {//销毁时被调用

	}
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		
		double x = Constant.convertX(event.getX());
		double y = Constant.convertY(event.getY());
		

		SharedPreferences settings = mainActivity.getSettings();
		//voiceControlFlag = settings.getBoolean("voiceControlFlag", true);
	    SharedPreferences.Editor editor = settings.edit();
//	    editor.putBoolean("voiceControlFlag", false);
//	    editor.commit();
		
		if(x>-1 && x<-0.2 && y>-0.8 && y<-0.2){
		    editor.putBoolean("voiceControlFlag", true);
		    editor.commit();
		    mainActivity.getVoiceControl().setFlag(true);

		    helpToast.show();
			mainActivity.getHandler().sendEmptyMessage(SurfaceViewFactory.HELP);
		}else if(x>0.2 && x<1 && y>-0.8 && y<-0.2){
		    editor.putBoolean("voiceControlFlag", false);
		    editor.commit();

		    helpToast.show();
			mainActivity.getHandler().sendEmptyMessage(SurfaceViewFactory.HELP);
		}
		
		
		//return super.onTouchEvent(event);
		return false;
	}
	
	
}