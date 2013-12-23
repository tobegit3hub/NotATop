package com.gg.module;

import java.util.HashMap;
import java.util.Map;
import com.gg.view.R;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class SoundControl {
	
	private MediaPlayer music ;  //music 变量
	public AudioManager am;  //用于获取系统声音的对象
	private SoundPool soundPool ; // short music

	private boolean soundOn ; //short music control
	
	private Context context ;  
	
	private Map<Integer , Integer> soundMap;   //音效资源Id和加载过后的音源Id的映射关系表
	
	
	public SoundControl(Context context){
		init(context);
	}
	
	//初始化音乐播发器
	private void initMusic(){
		music = MediaPlayer.create(context, R.raw.backmusic);
		music.setLooping(true);
	}
	
	
	//初始化音效播放器
	private void initSound(){
		soundPool = new SoundPool(10,AudioManager.STREAM_MUSIC,100);
		
		soundMap = new HashMap<Integer,Integer>();
		
		soundMap.put(R.raw.choose, soundPool.load(context, R.raw.choose,1));
		
		soundMap.put(R.raw.end, soundPool.load(context, R.raw.end,1));
		
		soundMap.put(R.raw.pai, soundPool.load(context, R.raw.pai,1));
		
		soundMap.put(R.raw.runing, soundPool.load(context, R.raw.runing,1));
		
	}			

	//载入音效和音乐资源
	public void init(Context c){
	
		context = c ;
	
		initMusic();
		
		initSound();
	}
	
	
	//获得音效开关状态
	public boolean isSoundOn(){
		return soundOn;
	}
	
	
	//播放音乐操作
	public void playSound(int resId){
		am=(AudioManager)context.getSystemService(context.AUDIO_SERVICE);
    	float audioMaxVolumn=am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);	//返回当前AudioManager对象的最大音量值
    	float audioCurrentVolumn=am.getStreamVolume(AudioManager.STREAM_MUSIC);//返回当前AudioManager对象的音量值
    	float volumnRatio=audioCurrentVolumn/audioMaxVolumn;
    	if(isSoundOn() == false)//控制音乐是否播放
			return ;
    	soundPool.play(
    			soundMap.get(resId), 					//播放的音乐id
    			volumnRatio, 						//左声道音量
    			volumnRatio, 						//右声道音量
    			1, 									//优先级，0为最低
    			0, 							//循环次数，0无不循环，-1无永远循环
    			1									//回放速度 ，该值在0.5-2.0之间，1为正常速度
		);
		
		Integer soundId = soundMap.get(resId);
		if(soundId != null)
			soundPool.play(soundId, 1, 1, 1, 0, 1);
	}
	
	
	//暂停音乐
	public void setMusic(){
		if(music.isPlaying()){
			music.pause();
			soundOn = false;
		}
		else{
			music.start();
			soundOn = true;
		}
	}
	
	
	//发出声音
	public void paisound(){
		playSound(R.raw.pai);
	}
	
	public void end(){
		playSound(R.raw.end);
	}
	
	public void choose(){
		playSound(R.raw.choose);
	}
	
	public void running(){
		playSound(R.raw.runing);
	}
	
	
	//释放音乐和音效资源
	public void releasemusic(){
		music.release();
		soundPool.release();
	}
	
}