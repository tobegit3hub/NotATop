package com.gg.module;

import java.nio.*;
import java.util.ArrayList;
import java.util.Random;
import javax.microedition.khronos.opengles.GL10;

import android.view.MotionEvent;

import com.gg.util.Constant;
import com.gg.util.TrackPoint;


public class DrawTrack{
	int textureId; // 纹理id
	int vertexNumber; // 所有顶点数量总和
	private FloatBuffer vertexBuffer; // 顶点坐标缓冲
	private FloatBuffer textureBuffer; // 纹理缓冲
	
	private float[] trackPoint = new float[1024*3];
	private float[] vertexs = new float[1024*3];
//	private long[] birth = new long[1024];
	private int count = 0;

	public DrawTrack(int textureId) {
		this.textureId = textureId;
		generateData();
	}
	
	public void onTouchEvent(MotionEvent e){
		switch(e.getAction()){
		case MotionEvent.ACTION_DOWN:
			for(int i = 0 ; i < 20 ; i ++) {
				vertexs[3*i] = 0;
				vertexs[3*i+1] = 0;
				vertexs[3*i+2] = 0;
			}
			count = 0;
			break;
			
		case MotionEvent.ACTION_MOVE:
			if(count < 10){
				trackPoint[3*count] = Constant.convertX(e.getX());
				trackPoint[3*count+1] = Constant.convertY(e.getY());
				trackPoint[3*count+2] = 3f;
				
				
				count ++;
			}else{
				for(int i = 0; i < count - 1; i++) {
					trackPoint[i*3] = trackPoint[(i+1)*3];
					trackPoint[i*3+1] = trackPoint[(i+1)*3+1];
					trackPoint[i*3+2] = trackPoint[(i+1)*3+2];
				}
				trackPoint[3*(count-1)] = Constant.convertX(e.getX());
				trackPoint[3*(count-1)+1] = Constant.convertY(e.getY());
				trackPoint[3*(count-1)+2] = 3f;
			}
			
			
			vertexs[0] = trackPoint[0];
			vertexs[1] = trackPoint[1];
			vertexs[2] = trackPoint[2];
			
			for(int i = 1 ; i < count - 1 ; i ++) {
				vertexs[i*6-3] = leftPointX(trackPoint[(i-1)*3] , trackPoint[(i-1)*3+1] , trackPoint[i*3] , trackPoint[i*3+1], i);
				vertexs[i*6-2] = leftPointY(trackPoint[(i-1)*3] , trackPoint[(i-1)*3+1] , trackPoint[i*3] , trackPoint[i*3+1], i);
				vertexs[i*6-1] = 3f;
				
				vertexs[i*6] = rightPointX(trackPoint[(i-1)*3] , trackPoint[(i-1)*3+1] , trackPoint[i*3] , trackPoint[i*3+1], i);
				vertexs[i*6+1] = rightPointY(trackPoint[(i-1)*3] , trackPoint[(i-1)*3+1] , trackPoint[i*3] , trackPoint[i*3+1], i);
				vertexs[i*6+2] = 3f;
			}
			vertexs[51] = trackPoint[3*(count-1)];
			vertexs[52] = trackPoint[3*(count-1)+1];
			vertexs[53] = trackPoint[3*(count-1)+2];
			
			break;
			
		case MotionEvent.ACTION_UP:
			for(int j = 0 ; j < count; j ++)
				for(int i = 0; i < count - 1; i++) {
					trackPoint[i*3] = trackPoint[(i+1)*3];
					trackPoint[i*3+1] = trackPoint[(i+1)*3+1];
					trackPoint[i*3+2] = trackPoint[(i+1)*3+2];
				}
			
			count = 0;
			break;
		}
	}
	
	public void generateData(){
		
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertexs.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(vertexs);
		vertexBuffer.position(0);

		// 纹理
		float[] textures = generateTexCoor(12, 1);
//		float[] textures = new float[]{0.0f, 0.0f,
//									   1.0f, 0.0f,
//									   0.0f, 1.0f,
//									   0.0f, 1.0f,
//									   1.0f, 0.0f,
//									   1.0f, 1.0f
//		};
		ByteBuffer tbb = ByteBuffer.allocateDirect(textures.length * 4);
		tbb.order(ByteOrder.nativeOrder());
		textureBuffer = tbb.asFloatBuffer();
		textureBuffer.put(textures);
		textureBuffer.position(0);
	}
	
//	public float leftPointX(float x1 ,float y1 , float x2 , float y2 , long birth , long lastBirth , long totalTime) {
//		float angle = (float) (Math.atan2(y2 - y1, x2 - x1));
//		float len = Constant.convert((1 - (lastBirth - birth) / totalTime) * 10);
//		float x = (float) (x2 + len * Math.cos(angle + Math.PI / 2));
//		return x;
//	}
//	
//	public float leftPointY(float x1 ,float y1 , float x2 , float y2 , long birth , long lastBirth , long totalTime) {
//		float angle = (float) (Math.atan2(y2 - y1, x2 - x1));
//		float len = Constant.convert((1 - (lastBirth - birth) / totalTime) * 10);
//		float y = (float) (y2 - len * Math.sin(angle + Math.PI / 2));
//		return y;
//	}
//	
//	public float rightPointX(float x1 ,float y1 , float x2 , float y2 , long birth , long lastBirth, long totalTime) {
//		float angle = (float) (Math.atan2(y2 - y1, x2 - x1));
//		float len = Constant.convert((1 - (lastBirth - birth) / totalTime) * 10) ;
//		float x = (float) (x2 - len * Math.cos(angle + Math.PI / 2));
//		return x;
//	}
//	
//	public float rightPointY(float x1 ,float y1 , float x2 , float y2 , long birth , long lastBirth, long totalTime) {
//		float angle = (float) (Math.atan2(y2 - y1, x2 - x1));
//		float len = Constant.convert((1 - (lastBirth - birth) / totalTime) * 10) ;
//		float y = (float) (y2 + len * Math.sin(angle + Math.PI / 2));
//		return y;
//	}


	public void drawSelf(GL10 gl) {
//		gl.glLineWidth(1.0f);
		int number;
		if(count == 0){
			number = 0;
		}else
		if(count == 1) {
			number = 1;
		}else if(count != 10) {
			number = 2*count - 4;
		} else number = 2*count - 2;
		
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertexs.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(vertexs);
		vertexBuffer.position(0);

		// 纹理
		float[] textures = generateTexCoor(12, 1);
		ByteBuffer tbb = ByteBuffer.allocateDirect(textures.length * 4);
		tbb.order(ByteOrder.nativeOrder());
		textureBuffer = tbb.asFloatBuffer();
		textureBuffer.put(textures);
		textureBuffer.position(0);


		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);// 打开顶点缓冲
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);// 指定顶点缓冲

		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);

		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, number);// 绘制图像

		gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);// 关闭缓冲
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
	}

	// 法向量规格化，求长度
	public float getVectorLength(float x, float y, float z) {
		float pingfang = x * x + y * y + z * z;
		float length = (float) Math.sqrt(pingfang);
		return length;
	}

	// 自动切分纹理产生纹理数组的方法
	public float[] generateTexCoor(int bw, int bh) {
		float[] result = new float[bw * bh * 6 * 2];
		float sizew = 1.0f / bw;// 列单位长度
		float sizeh = 1.0f / bh;// 行单位长度
		int c = 0;
		for (int j = 0; j < bw; j++) {
			for (int i = 0; i < bh; i++) {
				// 每行列一个矩形，由两个三角形构成，共六个点，12个纹理坐标
				float s = j * sizew;
				float t = i * sizeh;

				result[c++] = s;
				result[c++] = t;

				result[c++] = s;
				result[c++] = t + sizeh;

				result[c++] = s + sizew;
				result[c++] = t;

				result[c++] = s;
				result[c++] = t + sizeh;

				result[c++] = s + sizew;
				result[c++] = t + sizeh;

				result[c++] = s + sizew;
				result[c++] = t;
			}
		}
		return result;
	}
	
	
	public float leftPointX(float x1 ,float y1 , float x2 , float y2 , int i) {
		float angle = (float) (Math.atan2(y2 - y1, x2 - x1));
		float len = Constant.convert((float)(i) / 10 * 15);
		float x = (float) (x2 - len * Math.sin(angle));
		return x;
	}
	
	public float leftPointY(float x1 ,float y1 , float x2 , float y2 , int i) {
		float angle = (float) (Math.atan2(y2 - y1, x2 - x1));
		float len = Constant.convert( (float)(i) / 10 * 15);
		float y = (float) (y2 + len * Math.cos(angle));
		return y;
	}
	
	public float rightPointX(float x1 ,float y1 , float x2 , float y2 , int i) {
		float angle = (float) (Math.atan2(y2 - y1, x2 - x1));
		float len = Constant.convert( (float)(i) / 10 * 15) ;
		float x = (float) (x2 + len * Math.sin(angle));
		return x;
	}
	
	public float rightPointY(float x1 ,float y1 , float x2 , float y2 , int i) {
		float angle = (float) (Math.atan2(y2 - y1, x2 - x1));
		float len = Constant.convert( (float)(i)  / 10 * 15) ;
		float y = (float) (y2 - len * Math.cos(angle));
		return y;
	}
	
}