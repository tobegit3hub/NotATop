package com.gg.view;

import android.app.Activity;
import android.content.Context;
import android.view.SurfaceView;
import android.view.View;

/*		界面工厂类（工厂设计模式），不同的界面类只要在这里注册就可以很方便地使用		*/
public class SurfaceViewFactory {

	public final static int WELCOME = 0; // 表示欢迎界面的常量
	public final static int MAIN_MENU = 1; // 表示主菜单界面的常量
	public final static int CLASSIC_GAME = 2; // 表示传统模式界面的常量
	public final static int SELECT = 3; // 表示选择界面的常量
	public final static int SCORE = 4; // 表示高分榜界面的常量
	public final static int HELP = 5; // 表示帮助界面的常量
	public final static int END = 6;
	public final static int FIRST_TIME = 7;
	public final static int GAME_MODE = 8;
	public final static int TIME_GAME = 9;
	

	private static SurfaceView surfaceView; // 界面类的基类对象引用，用于获取不同的界面类对象
	

	private SurfaceViewFactory(){ // 限制创建工厂类对象
		
	}
	
	public static SurfaceView getView(MainActivity mainActivity, int index){ // 通过外界传来的界面类索引返回不同的界面类对象
		switch(index){
		case WELCOME:
			surfaceView = new WelcomeSurfaceView(mainActivity);
			break;
		case MAIN_MENU:
			surfaceView = new MainMenuSurfaceView(mainActivity);
			break;
		case CLASSIC_GAME:
			surfaceView = new ClassicGameSurfaceView(mainActivity);
			break;
		case SELECT:
			surfaceView = new SelectSurfaceView(mainActivity);
			break;	
		case SCORE:
			surfaceView = new ScoreSurfaceView(mainActivity);
			break;
		case HELP:
			surfaceView = new HelpSurfaceView(mainActivity);
			break;
		case END:
			surfaceView = new EndSurfaceView(mainActivity);
			break;
		case FIRST_TIME:
			surfaceView = new FirstTimeSurfaceView(mainActivity);
			break;
		case GAME_MODE:
			surfaceView = new GameModeSurfaceView(mainActivity);
			break;
		case TIME_GAME:
			surfaceView = new TimeGameSurfaceView(mainActivity);
			break;
		default:
			break;
		}
		return surfaceView;
	}
	
	
}
