package com.base.engine.rendering.opengl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import com.base.engine.core.util.Util;
import com.base.engine.rendering.MaterialMap;
import com.base.math.Transform;
import math.Vector3f;

import math.MathUtilOLD;

public class Terrain {
	private static final float SIZE = 80;
	private static final float MAX_HEIGHT = 10f;
	private static final float MIN_HEIGHT = -10f;
	private static final float MAX_PIXEL_COLOR = 256 * 256 * 256;
	
	Transform transform;
	float world_size = 80;
	float max_height = 10f;
	float x, y;
	int mesh, indices;
	float[][] heights;
	int[] textures;
	
	public Terrain(float world_size, float max_height, float x, float y, int[] textures){
		this.max_height = max_height;
		this.world_size = world_size;
		this.x = x * world_size;
		this.y = y * world_size;
		
		transform = new Transform();
		transform.translate(this.x, 0, this.y);
		this.textures = textures;
		
		generateTerrain();
	}
	
	public float getHeight(float x, float y){
		x -= this.x;
		y -= this.y;
		float gridsize = world_size / (float)(heights.length - 1);
		int gridX = (int)Math.floor(x / gridsize);
		int gridY = (int)Math.floor(y / gridsize);
		if(gridX >= heights.length - 1 || gridY >= heights.length - 1 || gridX < 0 || gridY < 0) return 0;
		float xcoord = (x % gridsize)/gridsize;
		float ycoord = (y % gridsize)/gridsize;
//		if(xcoord <= 1 - ycoord) 
//			return MathUtil.barrycentricLerp(new Vector3f(0, heights[gridX][gridY], 0), new Vector3f(1, heights[gridX + 1][gridY], 0), new Vector3f(0, heights[gridX][gridY + 1], 1), xcoord, ycoord);
//		return MathUtil.barrycentricLerp(new Vector3f(1, heights[gridX + 1][gridY], 0), new Vector3f(1, heights[gridX + 1][gridY + 1], 1), new Vector3f(0, heights[gridX][gridY + 1], 1), xcoord, ycoord);
		if(xcoord <= 1 - ycoord) return MathUtilOLD.topLeftBarrycentricLerp(heights[gridX][gridY], heights[gridX + 1][gridY], heights[gridX][gridY + 1], xcoord, ycoord);
		return MathUtilOLD.botRightBarrycentricLerp(heights[gridX + 1][gridY], heights[gridX + 1][gridY + 1], heights[gridX][gridY + 1], xcoord, ycoord);
	}
	
	@Override
	protected void finalize(){
		GLVertexArray.deleteVertexArray(mesh);
	}
	
	public int getIndices(){return this.indices;}
	
	private int generateTerrain(){
		BufferedImage heightMap = null;
		try{ heightMap = ImageIO.read(new File("res/heightmap.png"));
		} catch(IOException e){ e.printStackTrace();}
		
		int VERTEX_COUNT = heightMap.getHeight();
		heights = new float[VERTEX_COUNT][VERTEX_COUNT];
		this.indices = 6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1);
		int count = VERTEX_COUNT * VERTEX_COUNT;
		FloatBuffer vertices = Util.createFloatBuffer(count * 3); 
		FloatBuffer normals = Util.createFloatBuffer(count * 3);
		FloatBuffer textureCoords = Util.createFloatBuffer(count * 2);
		IntBuffer indices = Util.createIntBuffer(6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1));
		//float[] vertices = new float[count * 3];
		//float[] normals = new float[count * 3];
		//float[] textureCoords = new float[count*2];
		//int[] indices = new int[6*(VERTEX_COUNT-1)*(VERTEX_COUNT-1)];
		for(int i=0;i<VERTEX_COUNT;i++){
			for(int j=0;j<VERTEX_COUNT;j++){
				float jdiv = (float)j/((float)VERTEX_COUNT - 1);
				float idiv = (float)i/((float)VERTEX_COUNT - 1);
				heights[j][i] = getHeight(j,i, heightMap);
				vertices.put(jdiv * world_size);
				vertices.put(heights[j][i]);
				vertices.put(idiv * world_size);
				
				float heightl = getHeight(j - 1, i, heightMap);
				float heightr = getHeight(j + 1, i, heightMap);
				float heightd = getHeight(j, i - 1, heightMap);
				float heightu = getHeight(j, i + 1, heightMap);
				
				float normalx = heightl - heightr;
				float normalz = heightd - heightu;
				float length = 1f / (float) Math.sqrt(normalx * normalx + 4f + normalz * normalz);
				
				normals.put(normalx * length);
				normals.put(2f * length);
				normals.put(normalz * length);
				textureCoords.put(jdiv);
				textureCoords.put(idiv);
			}
		}
		for(int gz=0;gz<VERTEX_COUNT-1;gz++){
			for(int gx=0;gx<VERTEX_COUNT-1;gx++){
				int topLeft = (gz*VERTEX_COUNT)+gx;
				int topRight = topLeft + 1;
				int bottomLeft = ((gz+1)*VERTEX_COUNT)+gx;
				int bottomRight = bottomLeft + 1;
				indices.put(topLeft);
				indices.put(bottomLeft);
				indices.put(topRight);
				indices.put(topRight);
				indices.put(bottomLeft);
				indices.put(bottomRight);
			}
		}
		vertices.flip();
		normals.flip();
		textureCoords.flip();
		indices.flip();
		FloatBuffer[] all = new FloatBuffer[3];
		all[0] = vertices;
		all[1] = normals;
		all[2] = textureCoords;
		this.mesh = GLVertexArray.setUpVertexArray(all, indices, 3,3,2) ;
		Util.free(vertices);
		Util.free(normals);
		Util.free(textureCoords);
		Util.free(indices);
		return this.mesh;
	}
	
	public float getHeight(int x, int y, BufferedImage image){
		if(x < 0 || x >= image.getWidth() || y < 0 || y >= image.getHeight()) return 0;
		float height = image.getRGB(x, y);
		height += MAX_PIXEL_COLOR/2f;
		height /= MAX_PIXEL_COLOR/2f;
		height *= max_height;
		return height;
	}
}
