/*
 * Copyright (C) 2012 The Project “不是陀螺”
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gg.view;

import java.util.ArrayList;
import com.gg.module.*;
import com.gg.util.Constant;
import com.gg.util.DateUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.*;
import android.widget.Toast;

/*		整个程序唯一的Activity，通过改变使用自定义界面类来切换不同的界面		*/
public class MainActivity extends Activity {
	public final static int EXIT_MESSAGE = 100;	// 表示退出消息的常量
	public final static int VOICE_MESSAGE = 101; // 表示声控消息的常量
	public final static int VIBRATE_MESSAGE = 102;
	
	private boolean pressMenu = false;
	
	private boolean firstTimeFlag;

	private Vibrator vibrator;
	private VoiceControl voiceControl; // 调用Google Voice进行声控操作
	
	SharedPreferences settings;
	
	private Toast highScoreToast;
	private Toast notHighScoreToast;

	private Handler handler; // 接收消息，用于切换不同的界面
	
	private int surfaceViewIndex; // 记录当前界面的索引
	
	private SoundControl soundControl;
	
	int currScore;//游戏结束后的得分
	int highestScore;
	SQLiteDatabase sld;//SQLiteDatabase数据库
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		requestWindowFeature(Window.FEATURE_NO_TITLE); // 隐藏标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, // 设置全屏
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // 设置横屏
		
		Display display = getWindowManager().getDefaultDisplay();
		Constant.SCREEN_WIDTH = display.getWidth();
		Constant.SCREEN_HEIGHT = display.getHeight();
		Constant.SCREEN_RATE = (float)(0.5*4/Constant.SCREEN_HEIGHT);
		
		
		
		settings = getPreferences(MODE_PRIVATE);
	    firstTimeFlag = settings.getBoolean("firstTime", true);
	    //System.out.println("flag = "+firstTimeFlag);
	    SharedPreferences.Editor editor = settings.edit();
	    editor.putBoolean("firstTime", false);
	    editor.commit();
	    
	    soundControl = new SoundControl(this);
	    soundControl.setMusic();
		

		voiceControl = new VoiceControl(this); // 创建声控功能的对象
		//voiceControl.setFlag(true); // 设置声控功能可用
		voiceControl.setFlag( settings.getBoolean("voiceControlFlag", false) );
		

		vibrator = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);  
		
		highScoreToast = Toast.makeText(MainActivity.this, "恭喜获得打破最高纪录", Toast.LENGTH_SHORT);
		notHighScoreToast = Toast.makeText(MainActivity.this, "很遗憾没能打破最高纪录", Toast.LENGTH_SHORT);


		surfaceViewIndex = SurfaceViewFactory.WELCOME; // 一开始设置界面为欢迎界面
		//surfaceViewIndex = SurfaceViewFactory.GAME_MODE; // 只是方便调试而已
		setContentView(SurfaceViewFactory.getView(this, surfaceViewIndex)); // 使用自定义的欢迎界面

		handler = new Handler() { // 创建消息处理对象，用于响应从不同界面传来的消息并切换界面
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);

				if (msg.what == VOICE_MESSAGE) {
					
					if(voiceControl.isFlag()==true){
						Toast.makeText(MainActivity.this, "请选择合适的语言", Toast.LENGTH_SHORT).show();
						voiceControl.start(); // 启用声控功能
					}else{
						Toast.makeText(MainActivity.this, "未安装Google Voice，无法启用声控功能", Toast.LENGTH_SHORT).show();
					}
				} else if (msg.what == EXIT_MESSAGE) {
					exit(); // 提示是否退出程序
				} else if(msg.what == VIBRATE_MESSAGE){
					vibrator.vibrate(50);
				} else {
					soundControl.choose();
					surfaceViewIndex = msg.what; // 根据传进来不同的消息设置当前界面
					setContentView(SurfaceViewFactory.getView( // 使用工厂模式获得不同的界面对象
							MainActivity.this, surfaceViewIndex));
				}
			}
		};
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}


	@Override
	public boolean onPrepareOptionsMenu(Menu menu) { // 按下菜单键时调用
		// TODO Auto-generated method stub
		
		switch(surfaceViewIndex){ // 根据当前不同的界面予以不同的操作
		case SurfaceViewFactory.WELCOME:
			break;
		case SurfaceViewFactory.MAIN_MENU:
			break;
		case SurfaceViewFactory.CLASSIC_GAME:
		case SurfaceViewFactory.TIME_GAME:
			if(pressMenu==false){
				pressMenu = true;
			}
			break;
		case SurfaceViewFactory.SELECT:
			break;
		case SurfaceViewFactory.SCORE:
			break;
		case SurfaceViewFactory.HELP:
			break;
		case SurfaceViewFactory.END:
			break;
		case SurfaceViewFactory.GAME_MODE:
//			surfaceViewIndex = SurfaceViewFactory.MAIN_MENU;
//			setContentView(SurfaceViewFactory.getView(MainActivity.this, surfaceViewIndex));
			break;
		case SurfaceViewFactory.FIRST_TIME:
			break;
		}
		

		return super.onPrepareOptionsMenu(menu);
	}
	

	@Override
	public void onBackPressed() { // 按下返回键时调用
		// TODO Auto-generated method stub
		// super.onBackPressed(); // 必须注释掉否则不执行下面的代码
		
		switch(surfaceViewIndex){ // 根据当前不同的界面予以不同的操作
		case SurfaceViewFactory.WELCOME:
			break;
		case SurfaceViewFactory.MAIN_MENU:
			exit();
			break;
		case SurfaceViewFactory.CLASSIC_GAME:
		case SurfaceViewFactory.TIME_GAME:
			if(pressMenu==false){ // 设置暂停标志
				pressMenu = true;
			}
			break;
		case SurfaceViewFactory.SELECT:
		case SurfaceViewFactory.SCORE:
		case SurfaceViewFactory.HELP:
		case SurfaceViewFactory.END:
		case SurfaceViewFactory.GAME_MODE:
			surfaceViewIndex = SurfaceViewFactory.MAIN_MENU;
			setContentView(SurfaceViewFactory.getView(MainActivity.this, surfaceViewIndex));
			break;
		case SurfaceViewFactory.FIRST_TIME:
			vibrator.vibrate(50);
			new AlertDialog.Builder(MainActivity.this) // 创建对话框
			.setTitle("为保证程序正常运行") // 设置标题图标等等
			.setIcon(R.drawable.ic_launcher)
			.setMessage("是否放弃语音控制功能")
			.setPositiveButton("确认", // 如果选择了“确认”
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							// TODO Auto-generated method stub

						    SharedPreferences.Editor editor = settings.edit();
						    editor.putBoolean("voiceControlFlag", false);
						    editor.commit();
						    
							handler.sendEmptyMessage(SurfaceViewFactory.MAIN_MENU);
							//System.exit(0);
						}
					})
			.setNegativeButton("取消", // 如果选择了“取消”
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int which) {
							// TODO Auto-generated method stub

						}
					}).show();	
			break;
		}
	}
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { // 响应声控功能传进来的各类消息
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		
		boolean recognizeFlag = false;

		if (voiceControl.isFlag()) { // 如果开启了声控功能则进行下去
			ArrayList<String> words = voiceControl.result(requestCode, // 获得所有匹配的字符串
					resultCode, data);
			if (words != null) { // 如果存在匹配的字符串则进行下去
				for (int i = 0; i < words.size(); ++i) { // 遍历所有可能匹配的字符串
					//System.out.println(words.get(i));
					if (words.get(i).equals("start") // 响应英文命令，并跳转到各种界面
							|| words.get(i).equals("开始") // 响应普通话命令
							|| words.get(i).equals("開始")) { // 响应粤语命令
						recognizeFlag = true;
						Toast.makeText(this, "进入游戏界面", Toast.LENGTH_SHORT).show();
						handler.sendEmptyMessage(SurfaceViewFactory.CLASSIC_GAME);
					} else if (words.get(i).equals("select")
							|| words.get(i).equals("选择")
							|| words.get(i).equals("選擇")) {
						recognizeFlag = true;
						Toast.makeText(this, "进入陀螺选择界面", Toast.LENGTH_SHORT).show();
						handler.sendEmptyMessage(SurfaceViewFactory.SELECT);
					} else if (words.get(i).equals("score")
							|| words.get(i).equals("高分榜")
							|| words.get(i).equals("高分榜")) {
						recognizeFlag = true;
						Toast.makeText(this, "进入高分榜界面", Toast.LENGTH_SHORT).show();
						handler.sendEmptyMessage(SurfaceViewFactory.SCORE);
					} else if (words.get(i).equals("help")
							|| words.get(i).equals("帮助")
							|| words.get(i).equals("幫助")) {
						recognizeFlag = true;
						Toast.makeText(this, "进入帮助界面", Toast.LENGTH_SHORT).show();
						handler.sendEmptyMessage(SurfaceViewFactory.HELP);
					} else if (words.get(i).equals("exit")
							|| words.get(i).equals("退出")
							|| words.get(i).equals("退出")) {
						recognizeFlag = true;
						handler.sendEmptyMessage(EXIT_MESSAGE);
					}
				}
				if(recognizeFlag==false){
					Toast.makeText(this, "语音无法识别，请重试", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	
	public Handler getHandler() { // 让外部类能够访问消息处理对象，并往此Activity传入不同消息来切换界面
		return handler;
	}
	
	
	public boolean isPressMenu() {
		return pressMenu;
	}
	

	public boolean isFirstTimeFlag() {
		return firstTimeFlag;
	}

	public void setFirstTimeFlag(boolean firstTimeFlag) {
		this.firstTimeFlag = firstTimeFlag;
	}

	public void setPressMenu(boolean pressMenu) {
		this.pressMenu = pressMenu;
	}

	public void exit(){ // 自定义的退出函数，会弹出对话框让用户选择是否真的要退出程序
		
		vibrator.vibrate(50);
		//vibrator.vibrate(new long[]{100,400,100,400}, 0);
		
		new AlertDialog.Builder(MainActivity.this) // 创建对话框
		.setTitle("提醒") // 设置标题图标等等
		.setIcon(R.drawable.ic_launcher)
		.setMessage("确认退出游戏")
		.setPositiveButton("确认", // 如果选择了“确认”
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						// TODO Auto-generated method stub

					    SharedPreferences.Editor editor = settings.edit();
					    editor.putBoolean("crack", false);
					    editor.commit();
					    
						System.exit(0);
					}
				})
		.setNegativeButton("取消", // 如果选择了“取消”
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						// TODO Auto-generated method stub

					}
				}).show();	
	}

	
	public void setVoiceControl(){
		new AlertDialog.Builder(MainActivity.this) // 创建对话框
		.setTitle("第一次运行程序") // 设置标题图标等等
		.setIcon(R.drawable.ic_launcher)
		.setMessage("为实现语音操控功能，请确认是否安装Google Voice")
		.setPositiveButton("已安装",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						// TODO Auto-generated method stub

						voiceControl.setFlag(true);
					}
				})
		.setNegativeButton("未安装",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
						// TODO Auto-generated method stub

						voiceControl.setFlag(false);
					}
				}).show();		
	}
	
	
	
	
	
	
	
	
	
	
    //打开或创建数据库的方法
    public void openOrCreateDatabase()
    {
    	try
    	{
	    	sld=SQLiteDatabase.openDatabase
	    	(
	    			"/data/data/com.gg.view/mydb", //数据库所在路径
	    			null, 								//CursorFactory
	    			SQLiteDatabase.OPEN_READWRITE|SQLiteDatabase.CREATE_IF_NECESSARY //读写、若不存在则创建
	    	);
	    	String sql="create table if not exists highScore" +
	    			"( " +
	    			"score integer," +
	    			"date varchar(20)" +
	    			");";
	    	sld.execSQL(sql);
    	}
    	catch(Exception e)
    	{
    		Toast.makeText(this, "数据库错误："+e.toString(), Toast.LENGTH_SHORT).show();
    	}
    }
    //关闭数据库的方法
    public void closeDatabase()
    {
    	try
    	{
	    	sld.close();
    	}
		catch(Exception e)
		{
			Toast.makeText(this, "数据库错误："+e.toString(), Toast.LENGTH_SHORT).show();;
		}
    }
    //插入记录的方法
    public void insert(int score,String date)
    {
    	try
    	{
        	String sql="insert into highScore values("+score+",'"+date+"');";
        	sld.execSQL(sql);
        	sld.close();
    	}
		catch(Exception e)
		{
			Toast.makeText(this, "数据库错误："+e.toString(), Toast.LENGTH_SHORT).show();;
		}
    }
    //查询的方法
    public String query(int posFrom,int length)//开始的位置，要查寻的记录条数
    {
    	StringBuilder sb=new StringBuilder();//要返回的结果
    	Cursor cur=null;
    	openOrCreateDatabase();
        String sql="select score,date from highScore order by score desc;";    	
        cur=sld.rawQuery(sql, null);
    	try
    	{
    		
        	cur.moveToPosition(posFrom);//将游标移动到指定的开始位置
        	int count=0;//当前查询记录条数
        	while(cur.moveToNext()&&count<length)
        	{
        		int score=cur.getInt(0);
        		String date=cur.getString(1);        		
        		sb.append(score);
        		sb.append("/");
        		sb.append(date);
        		sb.append("/");//将记录用"/"分隔开
        		count++;
        	}        			
    	}
		catch(Exception e)
		{
			Toast.makeText(this, "数据库错误："+e.toString(), Toast.LENGTH_SHORT).show();
		}
		finally
		{
			cur.close();
			closeDatabase();
		}
		//转换成字符，并返回
		return sb.toString();
    }
    //得到数据库中记录条数的方法
    public int getRowCount()
    {
    	int result=0;
    	Cursor cur=null;
    	openOrCreateDatabase();
    	try
    	{
    		String sql="select count(score) from highScore;";    	
            cur=sld.rawQuery(sql, null);
        	if(cur.moveToNext())
        	{
        		result=cur.getInt(0);
        	}
    	}
    	catch(Exception e)
		{
			Toast.makeText(this, "数据库错误："+e.toString(), Toast.LENGTH_SHORT).show();
		}
		finally
		{
			cur.close();
			closeDatabase();
		}
        
    	return result;
    }
    
    
    
    
    
    public int getCurrScore() {
		return currScore;
	}

	public void setCurrScore(int currScore) {
		this.currScore = currScore;
	}
	

    //将得分和时间插入数据库，并跳转到相应的结束界面
    public void goToOverView()
    {
    	Cursor cur=null;
    	openOrCreateDatabase();//打开或创建数据库
    	try
    	{	
    		//从数据库中选出最高分
        	String sql="select max(score) from highScore;";   	
        	cur=sld.rawQuery(sql, null);
        	if(cur.moveToNext())//如果结果集不为空，移到下一行
        	{
        		this.highestScore=cur.getInt(0);
        	}
        	insert(currScore,DateUtil.getCurrentDate());//获得当前分数和日期并插入数据库        	
    	}
		catch(Exception e)
		{
			Toast.makeText(this, "数据库错误："+e.toString(), Toast.LENGTH_SHORT).show();
		}
		finally
		{
			cur.close();
			closeDatabase();
		}
		
		if(currScore>=highestScore)//如果当前得分大于积分榜中最高分
		{    	
	    	//this.gotoWinView();//进入胜利的界面
			highScoreToast.show();
			handler.sendEmptyMessage(SurfaceViewFactory.END);
		}
		else//如果当前得分不大于积分榜中最高分
		{
			//this.gotoFailView();//进入失败的界面
			notHighScoreToast.show();
			handler.sendEmptyMessage(SurfaceViewFactory.END);
		}
    	
    }
    

	public VoiceControl getVoiceControl() {
		return voiceControl;
	}

	public SharedPreferences getSettings() {
		return settings;
	}

	public SoundControl getSoundControl() {
		return soundControl;
	}

	public void setSoundControl(SoundControl soundControl) {
		this.soundControl = soundControl;
	}

    
    
    
}

