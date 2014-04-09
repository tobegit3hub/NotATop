package com.gg.module;

import java.util.ArrayList;
import android.app.Activity;
import android.content.Intent;
import android.speech.RecognizerIntent;

/*		表示声控操作的类		*/
public class VoiceControl {

	private Activity activity; // 表示调用声控功能的Activity
	private boolean flag; // 标记是否启用声控功能

	public VoiceControl(Activity activity) {// 初始化数据成员
		this.activity = activity;
		flag = false;
	}

	public boolean isFlag() {
		return flag;
	}

	public void setFlag(boolean flag) {
		this.flag = flag;
	}

	public void start() {// 使用声控功能，即启动Google Voice的Activity
		if (flag == true) {
			Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
			// intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
			// RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
			intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
					RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH); // 必须联网才能实现其功能
			intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "语音录入");

			activity.startActivityForResult(intent, 1);
		}
	}

	public ArrayList<String> result(int requestCode, int resultCode, Intent data) {// 获得Google
																					// Voice返回的匹配字符串链表

		if (flag == true) {
			if (requestCode == 1) {
				if (resultCode == Activity.RESULT_OK) {
					ArrayList<String> matches = data
							.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

					// for(int i=0;i<matches.size();++i){ // 输出所有匹配的字符串
					// System.out.println(matches.get(i));
					// }
					return matches;
				}
			}
		}
		return null;
	}

}
