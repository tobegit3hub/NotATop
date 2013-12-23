package com.gg.view;

import com.gg.util.Constant;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


public class EndSurfaceView extends SurfaceView implements SurfaceHolder.Callback{
	MainActivity mainActivity;//mainActivity的引用
	Paint paint;//画笔引用
	DrawThread drawThread;//绘制线程引用		
	Bitmap bgBitmap;//背景图片	
	//Bitmap titleBitmap;//文字的图片
	Bitmap scoreBitmap;
	//Bitmap dataBitmap;
	Bitmap[] numberBitmaps;//数字图片	
	//Bitmap dashBitmap;//符号"-"对应的图片
	int bmpx;//文字位置	
	//String queryResultStr;//查询数据库的结果
	//String[] splitResultStrs;//将查询结果切分后的数组
	private int numberWidth;//数字图片的宽度
	private int posFrom=-1;//查询的开始位置
	private int length=6;//查询的最大记录个数
//	int downY=0;//按下和抬起的y坐标
//	int upY=0;
	
	private double downX;
	private double downY;
	private double upX;
	private double upY;
	
	public EndSurfaceView(MainActivity mainActivity) {
		super(mainActivity);
		this.mainActivity=mainActivity;
		//获得焦点并设置为可触控
		this.requestFocus();
        this.setFocusableInTouchMode(true);
		getHolder().addCallback(this);//注册回调接口	
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//绘制背景
		canvas.drawColor(Color.WHITE);
		
		//canvas.drawBitmap(bgBitmap, 0, 0, paint);
		canvas.drawBitmap(bgBitmap, new Rect(0, 0,
				(int) bgBitmap.getWidth(), (int) bgBitmap
						.getHeight()), new Rect(0, 0,
				(int) Constant.SCREEN_WIDTH, (int) Constant.SCREEN_HEIGHT), paint);
		
		//绘制文字
		//canvas.drawBitmap(titleBitmap, bmpx, Constant.SCREEN_HEIGHT/20, paint);
//		canvas.drawBitmap(scoreBitmap, Constant.SCREEN_WIDTH*2/3, Constant.SCREEN_HEIGHT/5, paint);
		//canvas.drawBitmap(dataBitmap, Constant.SCREEN_WIDTH*1/3, titleBitmap.getHeight()+Constant.SCREEN_HEIGHT/10, paint);
//		//绘制得分和时间
//		int x,y;
//		for(int i=0;i<splitResultStrs.length;i++)
//		{
//			if(i%2==0)//计算得分的位置
//			{
//				x=Constant.SCREEN_WIDTH*3/4;				
//			}
//			else//计算时间的位置
//			{
//				x=Constant.SCREEN_WIDTH/2;
//			}
//			y=Constant.SCREEN_HEIGHT/20+titleBitmap.getHeight()+scoreBitmap.getHeight()+(numberBitmaps[0].getHeight()+Constant.SCREEN_HEIGHT/45)*(i/2+1);
//			//绘制字符串
//			drawDateBitmap(splitResultStrs[i],x,y,canvas,paint);
//		}
		drawDateBitmap(mainActivity.getCurrScore()+"",Constant.SCREEN_WIDTH*3/4,Constant.SCREEN_HEIGHT/4,canvas,paint);
	}
	//画日期图片的方法
	public void drawDateBitmap(String numberStr,float endX,float endY,Canvas canvas,Paint paint)
	{
		for(int i=0;i<numberStr.length();i++)
		{
			char c=numberStr.charAt(i);
			if(c=='-')
			{
				//canvas.drawBitmap(dashBitmap,endX-numberWidth*(numberStr.length()-i), endY, paint);
			}
			else
			{
				canvas.drawBitmap
				(
						numberBitmaps[c-'0'], 
						endX-numberWidth*(numberStr.length()-i), 
						endY, 
						paint
				);
			}			
		}
	}
	@Override
	public boolean onTouchEvent(MotionEvent e) {
//		int y = (int) event.getY();		
//    	switch(event.getAction())
//    	{
//    	case MotionEvent.ACTION_DOWN:
//    		downY=y;
//    		break;
//    	case MotionEvent.ACTION_UP:
//    		upY=y;    		
//        	if(Math.abs(downY-upY)<20)//在域值范围内，不翻页
//        	{
//        		return true;
//        	}
//        	else if(downY<upY)//往下抹
//        	{	
//        		//如果抹到最前页，不可再抹
//        		if(this.posFrom-this.length>=-1)
//        		{
//        			this.posFrom-=this.length;        			
//        		}
//        	}
//        	else//往上抹
//        	{	
//        		//如果抹到最后页，不可再抹
//        		if(this.posFrom+this.length<mainActivity.getRowCount()-1)
//        		{
//        			this.posFrom+=this.length;        			
//        		}
//        	}
//        	queryResultStr=mainActivity.query(posFrom,length);//得到数据库中的数据
//			splitResultStrs=queryResultStr.split("/", 0);//用"/"切分，且舍掉空串
//    		break;    	
//    	}
		
		
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
			
			if(downX<-1 && downY<-0.4){
				if(upX<-1 && upY<-0.4){
					mainActivity.getHandler().sendEmptyMessage(SurfaceViewFactory.SELECT);
				}
			}else if(downX>-0.6 && downX<0.6 && downY<-0.4){
				if(upX>-0.6 && upX<0.6 && upY<-0.4){
					mainActivity.getHandler().sendEmptyMessage(SurfaceViewFactory.GAME_MODE);
				}
			}else if(downX>1 && downY<-0.4){
				if(upX>1 && upY<-0.4){
					mainActivity.getHandler().sendEmptyMessage(SurfaceViewFactory.MAIN_MENU);
				}
			}
			break;
		}
		return true;
	}	
	
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {		
	}

	public void surfaceCreated(SurfaceHolder holder){
		paint=new Paint();//创建画笔
		paint.setAntiAlias(true);//打开抗锯齿	
		createAllThreads();//创建所有线程
		initBitmap();//初始化位图资源	
		numberWidth=numberBitmaps[0].getWidth()+3*5;//得到数字图片的宽度
		//初始化图片的位置
		bmpx=(Constant.SCREEN_WIDTH-50)/2;
		posFrom=-1;//查询的开始位置置-1			
		//queryResultStr=mainActivity.query(posFrom,length);//得到数据库中的数据
		//splitResultStrs=queryResultStr.split("/", 0);//用"/"切分，且舍掉空串		
		startAllThreads();//开启所有线程
	}
	
	public void surfaceDestroyed(SurfaceHolder holder) {
		  boolean retry = true;
	        stopAllThreads();
	        while (retry) {
	            try {
	            	drawThread.join();
	                retry = false;
	            } 
	            catch (InterruptedException e) {e.printStackTrace();}//不断地循环，直到刷帧线程结束
	        }
	}
	//将图片加载
	public void initBitmap(){
		//titleBitmap=BitmapFactory.decodeResource(this.getResources(), R.drawable.high_score);	
		bgBitmap=BitmapFactory.decodeResource(this.getResources(), R.drawable.end_bg);
		numberBitmaps=new Bitmap[]{
				BitmapFactory.decodeResource(this.getResources(), R.drawable.end0),
				BitmapFactory.decodeResource(this.getResources(), R.drawable.end1),
				BitmapFactory.decodeResource(this.getResources(), R.drawable.end2),
				BitmapFactory.decodeResource(this.getResources(), R.drawable.end3),
				BitmapFactory.decodeResource(this.getResources(), R.drawable.end4),
				BitmapFactory.decodeResource(this.getResources(), R.drawable.end5),
				BitmapFactory.decodeResource(this.getResources(), R.drawable.end6),
				BitmapFactory.decodeResource(this.getResources(), R.drawable.end7),
				BitmapFactory.decodeResource(this.getResources(), R.drawable.end8),
				BitmapFactory.decodeResource(this.getResources(), R.drawable.end9),
				BitmapFactory.decodeResource(this.getResources(), R.drawable.end0),
		};
		//dashBitmap=BitmapFactory.decodeResource(this.getResources(), R.drawable.gang);
		scoreBitmap=BitmapFactory.decodeResource(this.getResources(), R.drawable.score);
		//dataBitmap=BitmapFactory.decodeResource(this.getResources(), R.drawable.data);
	}
	void createAllThreads()
	{
		drawThread=new DrawThread(this);//创建绘制线程		
	}
	void startAllThreads()
	{
		drawThread.setFlag(true);     
		drawThread.start();
	}
	void stopAllThreads()
	{
		drawThread.setFlag(false);       
	}
	private class DrawThread extends Thread{
		private boolean flag = true;	
		private int sleepSpan = 100;
		EndSurfaceView fatherView;
		SurfaceHolder surfaceHolder;
		public DrawThread(EndSurfaceView fatherView){
			this.fatherView = fatherView;
			this.surfaceHolder = fatherView.getHolder();
		}
		public void run(){
			Canvas c;
	        while (this.flag) {
	            c = null;
	            try {
	            	// 锁定整个画布，在内存要求比较高的情况下，建议参数不要为null
	                c = this.surfaceHolder.lockCanvas(null);
	                synchronized (this.surfaceHolder) {
	                	fatherView.onDraw(c);//绘制
	                }
	            } finally {
	                if (c != null) {
	                	//并释放锁
	                    this.surfaceHolder.unlockCanvasAndPost(c);
	                }
	            }
	            try{
	            	Thread.sleep(sleepSpan);//睡眠指定毫秒数
	            }
	            catch(Exception e){
	            	e.printStackTrace();//打印堆栈信息
	            }
	        }
		}
		public void setFlag(boolean flag) {
			this.flag = flag;
		}
	}
}
