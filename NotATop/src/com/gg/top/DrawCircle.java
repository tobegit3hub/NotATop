package com.gg.top;

import java.nio.*;
import java.util.ArrayList;
import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import com.gg.util.Constant;

/*		用于绘制三维的陀螺（其中的圆部分）		*/
public class DrawCircle extends BasicTop {
	int textureId; // 纹理id
	int vertexNumber; // 所有顶点数量总和
	private FloatBuffer vertexBuffer; // 顶点坐标缓冲
	private FloatBuffer normalBuffer; // 法向量缓冲
	private FloatBuffer textureBuffer; // 纹理缓冲
	ArrayList<Float> val = new ArrayList<Float>(); // 顶点存放列表
	ArrayList<Float> ial = new ArrayList<Float>(); // 法向量存放列表
	ArrayList<Float> tal = new ArrayList<Float>();
	

	float degreeSpan = 12f; // 圆截环每一份的度数大小
	int degreeSpanNumber = (int) (360.0f / degreeSpan); // 圆截环数量
	int coneCol = 2; // 圆锥平均切分的块数
	float spanConeHeight = coneHeight / coneCol; // 圆锥每块所占的高度
	float spanConeRadius = radius / coneCol;// 半径单位长度
	float normalconeHeight = (coneHeight * radius * radius)
			/ (coneHeight * coneHeight + radius * radius); // 法向量点所在圆截面的高度
	float normalR = (coneHeight * coneHeight * radius)
			/ (coneHeight * coneHeight + radius * radius); // 法向量点所在圆截面的半径

	int cylinderCol = 1; // 圆柱平均分的块数
	float spanCylinderHeight = cylinderHeight / cylinderCol; // 圆柱每块所占的高度

//	public float xAngle; // 沿x轴旋转的角度
//	public float yAngle; // 沿y轴旋转的角度
//	public float zAngle; // 沿z轴旋转的角度
	//public float zAutoAngle; // 自动沿z轴旋转的角度

	public DrawCircle(int textureId) {
		this.textureId = textureId;

		generateData();
	}
	
	public void generateData(){
		val.clear();
		ial.clear();
		
		for (float circle_degree = 360.0f; circle_degree > 0.0f; circle_degree -= degreeSpan)// 循环行
		{

//			// 求圆锥的点和法向量
//			for (int j = 0; j < coneCol; j++) // 循环列
//			{
//				float currentR = j * spanConeRadius;// 当前截面的圆半径
//				float currentconeHeight = j * spanConeHeight;// 当前截面的高度
//
//				float x1 = (float) (currentR * Math.cos(Math
//						.toRadians(circle_degree)));
//				float y1 = (float) (currentR * Math.sin(Math
//						.toRadians(circle_degree)));
//				float z1 = currentconeHeight;
//
//				float a1 = (float) (normalR * Math.cos(Math
//						.toRadians(circle_degree)));
//				float b1 = (float) (normalR * Math.sin(Math
//						.toRadians(circle_degree)));
//				float c1 = normalconeHeight;
//				float l1 = getVectorLength(a1, b1, c1);// 模长
//				a1 = a1 / l1;// 法向量规格化
//				b1 = b1 / l1;
//				c1 = c1 / l1;
//
//				float x2 = (float) ((currentR + spanConeRadius) * Math.cos(Math
//						.toRadians(circle_degree)));
//				float y2 = (float) ((currentR + spanConeRadius) * Math.sin(Math
//						.toRadians(circle_degree)));
//				float z2 = currentconeHeight + spanConeHeight;
//
//				float a2 = (float) (normalR * Math.cos(Math
//						.toRadians(circle_degree)));
//				float b2 = (float) (normalR * Math.sin(Math
//						.toRadians(circle_degree)));
//				float c2 = normalconeHeight;
//
//				float l2 = getVectorLength(a2, b2, c2);// 模长
//				a2 = a2 / l2;// 法向量规格化
//				b2 = b2 / l2;
//				c2 = c2 / l2;
//
//				float x3 = (float) ((currentR + spanConeRadius) * Math.cos(Math
//						.toRadians(circle_degree - degreeSpan)));
//				float y3 = (float) ((currentR + spanConeRadius) * Math.sin(Math
//						.toRadians(circle_degree - degreeSpan)));
//				float z3 = currentconeHeight + spanConeHeight;
//
//				float a3 = (float) (normalR * Math.cos(Math
//						.toRadians(circle_degree - degreeSpan)));
//				float b3 = (float) (normalR * Math.sin(Math
//						.toRadians(circle_degree - degreeSpan)));
//				float c3 = normalconeHeight;
//
//				float l3 = getVectorLength(a3, b3, c3);// 模长
//				a3 = a3 / l3;// 法向量规格化
//				b3 = b3 / l3;
//				c3 = c3 / l3;
//
//				float x4 = (float) ((currentR) * Math.cos(Math
//						.toRadians(circle_degree - degreeSpan)));
//				float y4 = (float) ((currentR) * Math.sin(Math
//						.toRadians(circle_degree - degreeSpan)));
//				float z4 = currentconeHeight;
//
//				float a4 = (float) (normalR * Math.cos(Math
//						.toRadians(circle_degree - degreeSpan)));
//				float b4 = (float) (normalR * Math.sin(Math
//						.toRadians(circle_degree - degreeSpan)));
//				float c4 = normalconeHeight;
//
//				float l4 = getVectorLength(a4, b4, c4);// 模长
//				a4 = a4 / l4;// 法向量规格化
//				b4 = b4 / l4;
//				c4 = c4 / l4;
//
//				val.add(x1);
//				val.add(y1);
//				val.add(z1);// 两个三角形，共6个顶点的坐标
//				val.add(x2);
//				val.add(y2);
//				val.add(z2);
//				val.add(x4);
//				val.add(y4);
//				val.add(z4);
//
//				val.add(x2);
//				val.add(y2);
//				val.add(z2);
//				val.add(x3);
//				val.add(y3);
//				val.add(z3);
//				val.add(x4);
//				val.add(y4);
//				val.add(z4);
//
//				ial.add(a1);
//				ial.add(b1);
//				ial.add(c1);// 顶点对应的法向量
//				ial.add(a2);
//				ial.add(b2);
//				ial.add(c2);
//				ial.add(a4);
//				ial.add(b4);
//				ial.add(c4);
//
//				ial.add(a2);
//				ial.add(b2);
//				ial.add(c2);
//				ial.add(a3);
//				ial.add(b3);
//				ial.add(c3);
//				ial.add(a4);
//				ial.add(b4);
//				ial.add(c4);
//			}
//
//			// 求圆柱的点和法向量
//			for (int j = 0; j < cylinderCol; j++)// 循环列
//			{
//				float x1 = (float) (radius * Math.sin(Math
//						.toRadians(circle_degree)));
//				float y1 = (float) (radius * Math.cos(Math
//						.toRadians(circle_degree)));
//				float z1 = (float) (j * spanCylinderHeight + coneHeight);
//
//				float a1 = (float) (normalR * Math.sin(Math
//						.toRadians(circle_degree)));
//				float b1 = (float) (normalR * Math.cos(Math
//						.toRadians(circle_degree)));
//				float c1 = 0;
//				float l1 = getVectorLength(a1, b1, c1);// 模长
//				a1 = a1 / l1;// 法向量规格化
//				b1 = b1 / l1;
//				c1 = c1 / l1;
//
//				float x2 = (float) (radius * Math.sin(Math
//						.toRadians(circle_degree - degreeSpan)));
//				float y2 = (float) (radius * Math.cos(Math
//						.toRadians(circle_degree - degreeSpan)));// 化为弧度制
//				float z2 = (float) (j * spanCylinderHeight + coneHeight);
//
//				float a2 = (float) (normalR * Math.sin(Math
//						.toRadians(circle_degree - degreeSpan)));
//				float b2 = (float) (normalR * Math.cos(Math
//						.toRadians(circle_degree - degreeSpan)));
//				float c2 = 0;
//				float l2 = getVectorLength(a1, b1, c1);// 模长
//				a2 = a2 / l2;// 法向量规格化
//				b2 = b2 / l2;
//				c2 = c2 / l2;
//
//				float x3 = (float) (radius * Math.sin(Math
//						.toRadians(circle_degree - degreeSpan)));
//				float y3 = (float) (radius * Math.cos(Math
//						.toRadians(circle_degree - degreeSpan)));
//				float z3 = (float) ((j + 1) * spanCylinderHeight + coneHeight);
//
//				float a3 = (float) (normalR * Math.sin(Math
//						.toRadians(circle_degree - degreeSpan)));
//				float b3 = (float) (normalR * Math.cos(Math
//						.toRadians(circle_degree - degreeSpan)));
//				float c3 = 0;
//				float l3 = getVectorLength(a1, b1, c1);// 模长
//				a3 = a3 / l3;// 法向量规格化
//				b3 = b3 / l3;
//				c3 = c3 / l3;
//
//				float x4 = (float) (radius * Math.sin(Math
//						.toRadians(circle_degree)));
//				float y4 = (float) (radius * Math.cos(Math
//						.toRadians(circle_degree)));
//				float z4 = (float) ((j + 1) * spanCylinderHeight + coneHeight);
//
//				float a4 = (float) (normalR * Math.sin(Math
//						.toRadians(circle_degree)));
//				float b4 = (float) (normalR * Math.cos(Math
//						.toRadians(circle_degree)));
//				float c4 = 0;
//				float l4 = getVectorLength(a1, b1, c1);// 模长
//				a4 = a4 / l4;// 法向量规格化
//				b4 = b4 / l4;
//				c4 = c4 / l4;
//
//				val.add(x1);
//				val.add(y1);
//				val.add(z1);// 画两个三角形，共六个顶点
//				val.add(x2);
//				val.add(y2);
//				val.add(z2);
//				val.add(x4);
//				val.add(y4);
//				val.add(z4);
//
//				val.add(x2);
//				val.add(y2);
//				val.add(z2);
//				val.add(x3);
//				val.add(y3);
//				val.add(z3);
//				val.add(x4);
//				val.add(y4);
//				val.add(z4);
//
//				ial.add(x1);
//				ial.add(y1);
//				ial.add(z1);
//				ial.add(x2);
//				ial.add(y2);
//				ial.add(z2);
//				ial.add(x4);
//				ial.add(y4);
//				ial.add(z4);
//
//				ial.add(x2);
//				ial.add(y2);
//				ial.add(z2);
//				ial.add(x3);
//				ial.add(y3);
//				ial.add(z3);
//				ial.add(x4);
//				ial.add(y4);
//				ial.add(z4);
//			}

			// 求圆的点和法向量
			float x1 = 0f;
			float y1 = 0f;
			float z1 = cylinderHeight + coneHeight;
			
			float a1 = 0;
			float b1 = 0;
			float c1 = 1;
			float l1 = getVectorLength(a1, b1, c1);// 模长
			a1 = a1 / l1;// 法向量规格化
			b1 = b1 / l1;
			c1 = c1 / l1;
			
			float x2 = (float) ((radius) * Math.cos(Math
					.toRadians(circle_degree)));
			float y2 = (float) ((radius) * Math.sin(Math
					.toRadians(circle_degree)));
			float z2 = (float) (cylinderHeight + coneHeight);

			float a2 = 0;
			float b2 = 0;
			float c2 = 1;
			float l2 = getVectorLength(a2, b2, c2);// 模长
			a2 = a2 / l2;// 法向量规格化
			b2 = b2 / l2;
			c2 = c2 / l2;

			float x3 = (float) ((radius) * Math.cos(Math
					.toRadians(circle_degree + degreeSpan)));
			float y3 = (float) ((radius) * Math.sin(Math
					.toRadians(circle_degree + degreeSpan)));
			float z3 = (float) (cylinderHeight + coneHeight);

			float a3 = 0;
			float b3 = 0;
			float c3 = 1;
			float l3 = getVectorLength(a3, b3, c3);// 模长
			a3 = a3 / l3;// 法向量规格化
			b3 = b3 / l3;
			c3 = c3 / l3;

			
			val.add(x1);
			val.add(y1);
			val.add(z1);
			val.add(x2);
			val.add(y2);
			val.add(z2);
			val.add(x3);
			val.add(y3);
			val.add(z3);

			ial.add(x1);
			ial.add(y1);
			ial.add(z1);
			ial.add(x2);
			ial.add(y2);
			ial.add(z2);
			ial.add(x3);
			ial.add(y3);
			ial.add(z3);
			
			
			float tx1=(float) (0.5f*Math.cos(Math.toRadians(circle_degree)));
			float ty1=(float) (0.5f*Math.sin(Math.toRadians(circle_degree)));
			float tx2=(float) (0.5f*Math.cos(Math.toRadians(circle_degree+degreeSpan)));
			float ty2=(float) (0.5f*Math.sin(Math.toRadians(circle_degree+degreeSpan)));
			tal.add(0.5f);
			tal.add(0.5f);
			tal.add(tx1+0.5f);
			tal.add(ty1+0.5f);
			tal.add(tx2+0.5f);
			tal.add(ty2+0.5f);

		}

		vertexNumber = val.size() / 3;// 确定顶点数量

		// 顶点
		float[] vertexs = new float[vertexNumber * 3];
		for (int i = 0; i < vertexNumber * 3; i++) {
			vertexs[i] = val.get(i);
		}
		ByteBuffer vbb = ByteBuffer.allocateDirect(vertexs.length * 4);
		vbb.order(ByteOrder.nativeOrder());
		vertexBuffer = vbb.asFloatBuffer();
		vertexBuffer.put(vertexs);
		vertexBuffer.position(0);

		// 法向量
		float[] normals = new float[vertexNumber * 3];
		for (int i = 0; i < vertexNumber * 3; i++) {
			normals[i] = ial.get(i);
		}
		ByteBuffer ibb = ByteBuffer.allocateDirect(normals.length * 4);
		ibb.order(ByteOrder.nativeOrder());
		normalBuffer = ibb.asFloatBuffer();
		normalBuffer.put(normals);
		normalBuffer.position(0);

//		// 纹理
//		float[] textures = generateTexCoor(degreeSpanNumber, coneCol);
//		ByteBuffer tbb = ByteBuffer.allocateDirect(textures.length * 4);
//		tbb.order(ByteOrder.nativeOrder());
//		textureBuffer = tbb.asFloatBuffer();
//		textureBuffer.put(textures);
//		textureBuffer.position(0);
		
		float[] textures=new float[tal.size()];
		for(int i=0;i<tal.size();i++)
		{
			textures[i]=tal.get(i);
		}
		ByteBuffer tbb=ByteBuffer.allocateDirect(textures.length*4);
		tbb.order(ByteOrder.nativeOrder());
		textureBuffer=tbb.asFloatBuffer();
		textureBuffer.put(textures);
		textureBuffer.position(0);
	}
	


	public void drawSelf(GL10 gl) {


		gl.glTranslatef(basicPoint.x, basicPoint.y, 0);
		

		gl.glRotatef(-axleAngle, (float)Math.sin(axleAngleCount*Math.PI/180), -(float)Math.cos(axleAngleCount*Math.PI/180), 0);

		gl.glRotatef(-angleCount, 0, 0, 1);
		


		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);// 打开顶点缓冲
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);// 指定顶点缓冲

		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);// 打开法向量缓冲
		gl.glNormalPointer(GL10.GL_FLOAT, 0, normalBuffer);// 指定法向量缓冲

		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, textureBuffer);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureId);

		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, vertexNumber);// 绘制图像

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
//		float[] result = new float[bw * bh * 6 * 2];
//		float sizew = 1.0f / bw;// 列单位长度
//		float sizeh = 1.0f / bh;// 行单位长度
//		int c = 0;
//		for (int j = 0; j < bw; j++) {
//			for (int i = 0; i < bh; i++) {
//				// 每行列一个矩形，由两个三角形构成，共六个点，12个纹理坐标
//				float s = j * sizew;
//				float t = i * sizeh;
//
//				result[c++] = s;
//				result[c++] = t;
//
//				result[c++] = s;
//				result[c++] = t + sizeh;
//
//				result[c++] = s + sizew;
//				result[c++] = t;
//
//				result[c++] = s;
//				result[c++] = t + sizeh;
//
//				result[c++] = s + sizew;
//				result[c++] = t + sizeh;
//
//				result[c++] = s + sizew;
//				result[c++] = t;
//			}
//		}
//		return result;
		float[] result = new float[bw * 3 * 2];

		int c = 0;
		for (int i = 0; i < bw; i++) {
			float angle = 12f*i;
			
			result[c++] = (float)(Math.cos(angle)+1)/2;
			result[c++] = (float)(Math.sin(angle)+1)/2;;

			result[c++] = (float)(Math.cos(angle+12f)+1)/2;;
			result[c++] = (float)(Math.sin(angle+12f)+1)/2;;

			result[c++] = 0.5f;
			result[c++] = 0.5f;


		}
		return result;
	}
	
	
	public int getTextureId() {
		return textureId;
	}

	public void setTextureId(int textureId) {
		this.textureId = textureId;
	}
	
	
}