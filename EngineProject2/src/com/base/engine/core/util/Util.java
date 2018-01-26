package com.base.engine.core.util;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;

import org.lwjgl.system.MemoryUtil;

import com.base.engine.rendering.Vertex;
import math.Matrix4f;
import math.Vector3f;

public abstract class Util
{
	public static FloatBuffer arrayToBuffer(float[] array){return arrayToBuffer(createFloatBuffer(array.length), array);}
	public static FloatBuffer arrayToBuffer(float[][] array){return arrayToBuffer(createFloatBuffer(array.length * array[0].length), array);}
	public static IntBuffer arrayToBuffer(int[] array) {return arrayToBuffer(createIntBuffer(array.length), array);}
	public static IntBuffer arrayToBuffer(int[][] array) {return arrayToBuffer(createIntBuffer(array.length * array[0].length), array);}
	public static ByteBuffer arrayToBuffer(byte[] array) {return arrayToBuffer(createByteBuffer(array.length), array);}
	public static ByteBuffer arrayToBuffer(byte[][] array) {return arrayToBuffer(createByteBuffer(array.length * array[0].length), array);}
	
	public static FloatBuffer arrayToBuffer(FloatBuffer buffer, float[] array){buffer.put(array);buffer.flip();return buffer;}
	public static FloatBuffer arrayToBuffer(FloatBuffer buffer, float[][] array){for(int i = 0; i < array.length; i++) buffer.put(array[i]); buffer.flip();return buffer;}
	public static IntBuffer arrayToBuffer(IntBuffer buffer, int[] array){buffer.put(array);buffer.flip();return buffer;}
	public static IntBuffer arrayToBuffer(IntBuffer buffer, int[][] array){for(int i = 0; i < array.length; i++) buffer.put(array[i]); buffer.flip();return buffer;}
	public static ByteBuffer arrayToBuffer(ByteBuffer buffer, byte[] array){buffer.put(array);buffer.flip();return buffer;}
	public static ByteBuffer arrayToBuffer(ByteBuffer buffer, byte[][] array){for(int i = 0; i < array.length; i++) buffer.put(array[i]); buffer.flip();return buffer;}
	
	public static FloatBuffer createFloatBuffer(int size){return MemoryUtil.memAllocFloat(size);}
	public static IntBuffer createIntBuffer(int size){return MemoryUtil.memAllocInt(size);}
	public static ByteBuffer createByteBuffer(int size){return MemoryUtil.memAlloc(size);}
	
	public static FloatBuffer realloc(FloatBuffer buffer, int size){return MemoryUtil.memRealloc(buffer, size);}
	public static IntBuffer realloc(IntBuffer buffer, int size){return MemoryUtil.memRealloc(buffer, size);}
	public static ByteBuffer realloc(ByteBuffer buffer, int size){return MemoryUtil.memRealloc(buffer, size);}
	
	public static void free(Buffer buffer){MemoryUtil.memFree(buffer);}
	
	public static void vectorToBuffer(FloatBuffer buffer, Vector3f v){buffer.put(v.x); buffer.put(v.y); buffer.put(v.z);}
	
	public static void matrixToBuffer(FloatBuffer buffer, Matrix4f m){
		buffer.put(m.m00); buffer.put(m.m01); buffer.put(m.m02); buffer.put(m.m03);
		buffer.put(m.m10); buffer.put(m.m11); buffer.put(m.m12); buffer.put(m.m13);
		buffer.put(m.m20); buffer.put(m.m21); buffer.put(m.m22); buffer.put(m.m23);
		buffer.put(m.m30); buffer.put(m.m31); buffer.put(m.m32); buffer.put(m.m33);
		
//		buffer.put(m.m00); buffer.put(m.m10); buffer.put(m.m20); buffer.put(m.m30);
//		buffer.put(m.m01); buffer.put(m.m11); buffer.put(m.m21); buffer.put(m.m31);
//		buffer.put(m.m02); buffer.put(m.m12); buffer.put(m.m22); buffer.put(m.m32);
//		buffer.put(m.m03); buffer.put(m.m13); buffer.put(m.m23); buffer.put(m.m33);
		//buffer.flip();
	}
	
	public static FloatBuffer createFlippedBuffer(Vertex[] vertices)
	{
		FloatBuffer buffer = createFloatBuffer(vertices.length * Vertex.SIZE);
		
		for(int i = 0; i < vertices.length; i++)
		{
			buffer.put((float) vertices[i].getPos().x);
			buffer.put((float) vertices[i].getPos().y);
			buffer.put((float) vertices[i].getPos().z);
			buffer.put((float) vertices[i].getTexCoord().x);
			buffer.put((float) vertices[i].getTexCoord().y);
			buffer.put((float) vertices[i].getNormal().x);
			buffer.put((float) vertices[i].getNormal().y);
			buffer.put((float) vertices[i].getNormal().z);
		}
		
		buffer.flip();
		
		return buffer;
	}
	
	
	public static FloatBuffer matricesToFloatBuffer(Matrix4f[] matrices) 
	{
		FloatBuffer buffer = createFloatBuffer(4 * 4 * matrices.length);
		
		for(int k = 0; k < matrices.length; k++)
		{
			buffer.put(matrices[k].m00); buffer.put(matrices[k].m01); buffer.put(matrices[k].m02); buffer.put(matrices[k].m03);
			buffer.put(matrices[k].m10); buffer.put(matrices[k].m11); buffer.put(matrices[k].m12); buffer.put(matrices[k].m13);
			buffer.put(matrices[k].m20); buffer.put(matrices[k].m21); buffer.put(matrices[k].m22); buffer.put(matrices[k].m23);
			buffer.put(matrices[k].m30); buffer.put(matrices[k].m31); buffer.put(matrices[k].m32); buffer.put(matrices[k].m33);
		}
		
		buffer.flip();
		
		return buffer;
	}
	public static FloatBuffer createFlippedBuffer(Matrix4f matrix)
	{
		FloatBuffer buffer = createFloatBuffer(4 * 4);
		
		buffer.put(matrix.m00); buffer.put(matrix.m01); buffer.put(matrix.m02); buffer.put(matrix.m03);
		buffer.put(matrix.m10); buffer.put(matrix.m11); buffer.put(matrix.m12); buffer.put(matrix.m13);
		buffer.put(matrix.m20); buffer.put(matrix.m21); buffer.put(matrix.m22); buffer.put(matrix.m23);
		buffer.put(matrix.m30); buffer.put(matrix.m31); buffer.put(matrix.m32); buffer.put(matrix.m33);
		
		buffer.flip();
		
		return buffer;
	}
	
	public static String[] removeEmptyStrings(String[] data)
	{
		ArrayList<String> result = new ArrayList<String>();
		
		for(int i = 0; i < data.length; i++)
			if(!data[i].equals(""))
				result.add(data[i]);
		
		String[] res = new String[result.size()];
		result.toArray(res);
		
		return res;
	}
	
	public static int[] toIntArray(Integer[] data)
	{
		int[] result = new int[data.length];
		
		for(int i = 0; i < data.length; i++)
			result[i] = data[i].intValue();
		
		return result;
	}
	
	public static int[] createNArray(int n){int[] array = new int[n]; for(int i = 1; i < n; i++) array[i] = i; return array;}
	
	@SuppressWarnings("unchecked")
	public static <E> E[] expandArray(E[] array, int size_increase){E[] copy =(E[]) new Object[array.length + size_increase];System.arraycopy(array, 0, copy, 0, array.length);return copy;}
	public static int[] expandIntArray(int[] array, int size_increase){int[] copy = new int[array.length + size_increase];System.arraycopy(array, 0, copy, 0, array.length);return copy;}
	public static int[][] expandDoubleIntArray(int[][] array, int size_increase){int[][] copy = new int[array.length + size_increase][];System.arraycopy(array, 0, copy, 0, array.length);return copy;}
	public static String[] expandStringArray(String[] array, int size_increase){String[] copy = new String[array.length + size_increase];System.arraycopy(array, 0, copy, 0, array.length);return copy;}
}

