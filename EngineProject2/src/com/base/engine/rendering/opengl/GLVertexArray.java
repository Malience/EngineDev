package com.base.engine.rendering.opengl;

import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import com.base.engine.core.util.Util;

public abstract class GLVertexArray {
	private static FloatBuffer floatBuffer = Util.createFloatBuffer(1);
	private static IntBuffer intBuffer = Util.createIntBuffer(1);
	private static int current_float_size = 1;
	private static int current_int_size = 1;
	
	public static void dumpMemoryBuffer(){reallocFloatBuffer(1);}
	public static void reallocFloatBuffer(int size){floatBuffer = Util.realloc(floatBuffer, size); current_float_size = size;}
	public static void reallocIntBuffer(int size){intBuffer = Util.realloc(intBuffer, size); current_int_size = size;}
	public static void arrayAlloc(float[] farray, int[] iarray){arrayAlloc(farray); arrayAlloc(iarray);}
	public static void arrayAlloc(float[] array){
		floatBuffer.clear();
		if(current_float_size < array.length) {reallocFloatBuffer(array.length + 1);}
		Util.arrayToBuffer(floatBuffer, array);
	}
	
	public static void arrayAlloc(int[] array){
		intBuffer.clear();
		if(current_int_size < array.length) {reallocIntBuffer(array.length + 1);}
		Util.arrayToBuffer(intBuffer, array);
	}
	
	public static int genVertexArray(){return GL30.glGenVertexArrays();}
	public static int[] genVertexArrays(int num){return genVertexArrays(new int[num]);}
	public static int[] genVertexArrays(int[] arrays){GL30.glGenVertexArrays(arrays); return arrays;}
	public static int genBuffer(){return GL15.glGenBuffers();}
	public static int[] genBuffers(int num){return genBuffers(new int[num]);}
	public static int[] genBuffers(int[] buffers){GL15.glGenBuffers(buffers); return buffers;}
	
	public static void deleteVertexArray(int array){GL30.glDeleteVertexArrays(array);}
	public static void deleteVertexArrays(int[] arrays){GL30.glDeleteVertexArrays(arrays);}
	public static void deleteBuffer(int buffer){GL15.glDeleteBuffers(buffer);}
	public static void deleteBuffers(int[] buffers){GL15.glDeleteBuffers(buffers);}
	
	public static int setUpVertexArray(float[] vertices, int[] indices, boolean interweaved, int... attribute_sizes)
	{arrayAlloc(vertices, indices); return setUpVertexArray(GL30.glGenVertexArrays(), floatBuffer, intBuffer, interweaved, attribute_sizes);}
	public static int setUpVertexArray(int array, float[] vertices, int[] indices, boolean interweaved, int... attribute_sizes)
	{arrayAlloc(vertices, indices); return setUpVertexArray(array, floatBuffer, intBuffer, interweaved, attribute_sizes);}
	public static int setUpVertexArray(int[] buffers, float[] vertices, int[] indices, boolean interweaved, int... attribute_sizes)
	{arrayAlloc(vertices, indices); return setUpVertexArray(GL30.glGenVertexArrays(), buffers, floatBuffer, intBuffer, interweaved, attribute_sizes);}
	public static int setUpVertexArray(FloatBuffer vertices, int[] indices, boolean interweaved, int... attribute_sizes)
	{arrayAlloc(indices); return setUpVertexArray(GL30.glGenVertexArrays(), vertices, intBuffer, interweaved, attribute_sizes);}
	public static int setUpVertexArray(int array, FloatBuffer vertices, IntBuffer indices, boolean interweaved, int... attribute_sizes)
	{return setUpVertexArray(GL30.glGenVertexArrays(), genBuffers(2), vertices, intBuffer, interweaved, attribute_sizes);}
	public static int setUpVertexArray(int[] buffers, FloatBuffer vertices, IntBuffer indices, boolean interweaved, int... attribute_sizes)
	{return setUpVertexArray(GL30.glGenVertexArrays(), buffers, vertices, indices, interweaved, attribute_sizes);}
	public static int setUpVertexArray(int array, int[] buffers, FloatBuffer vertices, IntBuffer indices, boolean interweaved, int... attribute_sizes){
		GL30.glBindVertexArray(array);
		
		setUpBuffer(buffers[0], vertices);
		setUpVertexAttrib(attribute_sizes, interweaved);
		setUpElementBuffer(buffers[1], indices);
		
		GL30.glBindVertexArray(0);
		return array;
	}
	
	public static int setUpVertexArray(FloatBuffer[] vertices, int[] indices, int... attribute_sizes)
	{arrayAlloc(indices); return setUpVertexArray(GL30.glGenVertexArrays(), vertices, intBuffer, attribute_sizes);}
	public static int setUpVertexArray(FloatBuffer[] vertices, IntBuffer indices, int... attribute_sizes)
	{return setUpVertexArray(GL30.glGenVertexArrays(), vertices, indices, attribute_sizes);}
	public static int setUpVertexArray(int array, FloatBuffer[] vertices, IntBuffer indices,  int... attribute_sizes)
	{return setUpVertexArray(GL30.glGenVertexArrays(), genBuffers(vertices.length + 1), vertices, indices, attribute_sizes);}
	public static int setUpVertexArray(int[] buffers, FloatBuffer[] vertices, IntBuffer indices,  int... attribute_sizes)
	{return setUpVertexArray(GL30.glGenVertexArrays(), buffers, vertices, indices, attribute_sizes);}
	public static int setUpVertexArray(int array, int[] buffers, FloatBuffer[] vertices, IntBuffer indices,  int... attribute_sizes){
		GL30.glBindVertexArray(array);
		
		for(int i = 0; i < vertices.length; i++){
			if(vertices[i] == null) continue;
			setUpBuffer(buffers[i], vertices[i]);
			setUpVertexAttrib(i, attribute_sizes[i]);
		}
		setUpElementBuffer(buffers[vertices.length], indices);
		
		GL30.glBindVertexArray(0);
		return array;
	}
	
	public static int setUpVertexArray(int array, float[] vertices){
		GL30.glBindVertexArray(array);
		int buffer = GL15.glGenBuffers();
		setUpBuffer(buffer, vertices);
		setUpVertexAttrib(0, 3);
		
		GL30.glBindVertexArray(0);
		return array;
	}
	public static int setUpVertexArray(int array, float[] vertices, int[] indices){
		GL30.glBindVertexArray(array);
		int[] buffers = new int[2];
		GL15.glGenBuffers(buffers);
		setUpBuffer(buffers[0], vertices);
		setUpVertexAttrib(0, 3);
		setUpElementBuffer(buffers[1], indices);
		
		GL30.glBindVertexArray(0);
		return array;
	}
	
	
	
	public static void setUpVertexAttrib(int[] attribute_sizes, boolean interweaved){
		if(interweaved){
			int stride = 0; for(int i = 0; i < attribute_sizes.length; i++) stride += attribute_sizes[i];
			for(int i = 0; i < attribute_sizes.length; i++){
				GL20.glEnableVertexAttribArray(i);
				GL20.glVertexAttribPointer(i, attribute_sizes[i], GL11.GL_FLOAT, false, stride, attribute_sizes[i] * 4);
			}
		} else {
			
		}
	}
	
	
	public static int setUpBuffer(int buffer, float[] data) {return setUpBuffer(buffer, data, GL15.GL_STATIC_DRAW);}
	public static int setUpBuffer(int buffer, float[] data, int usage){
		try(MemoryStack stack = stackPush()) {return setUpBuffer(buffer, Util.malloc(data, stack), usage);}
	}
	public static int setUpBuffer(int buffer, int usage){return setUpBuffer(buffer, floatBuffer, usage);}
	public static int setUpBuffer(int buffer){return setUpBuffer(buffer, floatBuffer, GL15.GL_STATIC_DRAW);}
	public static int setUpBuffer(int buffer, FloatBuffer data){return setUpBuffer(buffer, data, GL15.GL_STATIC_DRAW);}
	public static int setUpBuffer(int buffer, FloatBuffer data, int usage){
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, data, usage);
		return buffer;
	}
	
	
	public static void setUpVertexAttrib(int index, int attribute_size){setUpVertexAttrib(index, attribute_size, 0);}
	public static void setUpVertexAttrib(int index, int attribute_size, int stride){
		GL20.glEnableVertexAttribArray(index);
		GL20.glVertexAttribPointer(index, attribute_size, GL11.GL_FLOAT, false, attribute_size * 4, stride);
	}
	
	
	
	public static void bindArrayBuffer(int buffer){GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffer);}
	public static void bindVertexArray(int vertexArray){GL30.glBindVertexArray(vertexArray);}
	public static void unbindVertexArray(){GL30.glBindVertexArray(0);}
	
//	public static int setUpElementBuffer(int... indices){arrayAlloc(indices); return setUpElementBuffer(GL15.glGenBuffers(), intBuffer);}
//	public static int setUpElementBuffer(int buffer, int... indices){arrayAlloc(indices); return setUpElementBuffer(buffer, intBuffer);}
//	public static int setUpElementBuffer(int buffer, IntBuffer indices){
//		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer);
//		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, GL15.GL_STATIC_DRAW);
//		return buffer;
//	}
	
	public static int setUpElementBuffer(int[] indices) {return setUpElementBuffer(GL15.glGenBuffers(), GL15.GL_STATIC_DRAW, indices);}
	public static int setUpElementBuffer(int buffer, int[] indices) {return setUpElementBuffer(buffer, GL15.GL_STATIC_DRAW, indices);}
	public static int setUpElementBuffer(int buffer, int buffer_type, int[] indices){
		try(MemoryStack stack = stackPush()) {return setUpElementBuffer(buffer, buffer_type, Util.malloc(indices, stack));}
	}
	public static int setUpElementBuffer(int buffer, IntBuffer indices){return setUpElementBuffer(buffer, GL15.GL_STATIC_DRAW, indices);}
	public static int setUpElementBuffer(int buffer, int buffer_type, IntBuffer indices) {
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indices, buffer_type);
		return buffer;
	}
	
	
	public static void setUpVertexAttribMat4(int index){setUpVertexAttribMat4(index, 0);}
	public static void setUpVertexAttribMat4(int index, int stride){
		GL20.glEnableVertexAttribArray(index);
		GL20.glVertexAttribPointer(index, 4, GL11.GL_FLOAT, false, 64, stride);
		GL20.glEnableVertexAttribArray(index + 1);
		GL20.glVertexAttribPointer(index + 1, 4, GL11.GL_FLOAT, false, 64, stride + 16);
		GL20.glEnableVertexAttribArray(index + 2);
		GL20.glVertexAttribPointer(index + 2, 4, GL11.GL_FLOAT, false, 64, stride + 32);
		GL20.glEnableVertexAttribArray(index + 3);
		GL20.glVertexAttribPointer(index + 3, 4, GL11.GL_FLOAT, false, 64, stride + 48);
	}
	
	
	public static int datatypeSize(int datatype){
		switch(datatype){
		case GL11.GL_BYTE: case GL11.GL_UNSIGNED_BYTE: return 1;
		case GL11.GL_SHORT: case GL11.GL_UNSIGNED_SHORT: case GL11.GL_2_BYTES: return 2;
		case GL11.GL_3_BYTES: return 3;
		case GL11.GL_FLOAT: case GL11.GL_INT: case GL11.GL_UNSIGNED_INT: case GL11.GL_4_BYTES: return 4;
		case GL11.GL_DOUBLE: return 8;
		}return -1;
	}
}
