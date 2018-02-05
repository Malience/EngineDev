package com.base.engine.core.util;

import static org.lwjgl.system.MemoryStack.stackPush;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.assimp.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import com.base.engine.data.Mesh;

public abstract class AssimpUtil {
	public static FloatBuffer arrayToBuffer(AIVector3D.Buffer array){return arrayToBuffer(Util.createFloatBuffer(array.remaining()*3), array);}
	public static FloatBuffer arrayToBuffer(AIVector2D.Buffer array){return arrayToBuffer(Util.createFloatBuffer(array.remaining()*2), array);}
	public static IntBuffer arrayToBuffer(AIFace.Buffer array){return arrayToBuffer(Util.createIntBuffer(array.remaining()*3), array);}
	
	public static FloatBuffer arrayToBuffer(AIColor4D array){return arrayToBuffer(Util.createFloatBuffer(4), array);}
	
	public static FloatBuffer arrayToBuffer(FloatBuffer buffer, AIVector3D.Buffer array)
	{AIVector3D v; while(array.hasRemaining()){v = array.get(); buffer.put(v.x());buffer.put(v.y());buffer.put(v.z());}buffer.flip();return buffer;}
	public static FloatBuffer arrayToBuffer(FloatBuffer buffer, AIVector2D.Buffer array)
	{AIVector2D v; while(array.hasRemaining()){v = array.get(); buffer.put(v.x());buffer.put(v.y());}buffer.flip();return buffer;}
	public static IntBuffer arrayToBuffer(IntBuffer buffer, AIFace.Buffer array)
	{AIFace v; while(array.hasRemaining()){v = array.get(); buffer.put(v.mIndices());}buffer.flip();return buffer;}
	
	public static FloatBuffer arrayToBuffer2D(AIVector3D.Buffer array){return arrayToBuffer2D(Util.createFloatBuffer(array.remaining()*3), array);}
	
	public static FloatBuffer arrayToBuffer2D(FloatBuffer buffer, AIVector3D.Buffer array)
	{AIVector3D v; while(array.hasRemaining()){v = array.get(); buffer.put(v.x());buffer.put(v.y());}buffer.flip();return buffer;}
	
	public static FloatBuffer arrayToBuffer(FloatBuffer buffer, AIVector3D v)
	{buffer.put(v.x());buffer.put(v.y());buffer.put(v.z());buffer.flip();return buffer;}
	public static FloatBuffer arrayToBuffer(FloatBuffer buffer, AIColor4D v)
	{buffer.put(v.r());buffer.put(v.g());buffer.put(v.b());buffer.put(v.a());buffer.flip();return buffer;}
	
	
	public static FloatBuffer malloc(MemoryStack stack, AIVector3D.Buffer array) {return arrayToBuffer(stack.mallocFloat(array.remaining() * 3), array);}
	public static FloatBuffer malloc(MemoryStack stack, AIVector2D.Buffer array) {return arrayToBuffer(stack.mallocFloat(array.remaining() * 2), array);}
	public static IntBuffer malloc(MemoryStack stack, AIFace.Buffer array) {return arrayToBuffer(stack.mallocInt(array.remaining() * 3), array);}
	
	public static FloatBuffer malloc2D(MemoryStack stack, AIVector3D.Buffer array) {return arrayToBuffer2D(stack.mallocFloat(array.remaining() * 3), array);}
	
	public static void loadMesh(Mesh mesh) {
		AIScene scene = Assimp.aiImportFile(mesh.filename, Assimp.aiProcess_Triangulate | Assimp.aiProcess_FlipUVs);
		
		if(scene == null || scene.mFlags() == Assimp.AI_SCENE_FLAGS_INCOMPLETE || scene.mRootNode() == null) {
	        System.err.println("ERROR::ASSIMP::" + Assimp.aiGetErrorString()); return;
	    }
		
		AIMesh aimesh = AIMesh.create(scene.mMeshes().get());
		
		try(MemoryStack stack = stackPush()){
			int[] buffers = mesh.buffers = new int[4];
			GL15.glGenBuffers(buffers);
			
			mesh.vao = GL30.glGenVertexArrays();
			
			GL30.glBindVertexArray(mesh.vao);
			
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffers[0]);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, malloc(stack, aimesh.mVertices()), GL15.GL_STATIC_DRAW);
			
			GL20.glEnableVertexAttribArray(0);
			GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 3 * 4, 0);
			
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffers[1]);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, malloc(stack, aimesh.mNormals()), GL15.GL_STATIC_DRAW);
			
			GL20.glEnableVertexAttribArray(1);
			GL20.glVertexAttribPointer(1, 3, GL11.GL_FLOAT, false, 3 * 4, 0);
			
			GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, buffers[2]);
			GL15.glBufferData(GL15.GL_ARRAY_BUFFER, malloc2D(stack, aimesh.mTextureCoords(0)), GL15.GL_STATIC_DRAW);
			
			GL20.glEnableVertexAttribArray(2);
			GL20.glVertexAttribPointer(2, 2, GL11.GL_FLOAT, false, 2 * 4, 0);
			
			GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, buffers[3]);
			GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, malloc(stack, aimesh.mFaces()), GL15.GL_STATIC_DRAW);
			
			mesh.indices = aimesh.mNumFaces() * 3;
			
			GL30.glBindVertexArray(0);
		}
	}
}
