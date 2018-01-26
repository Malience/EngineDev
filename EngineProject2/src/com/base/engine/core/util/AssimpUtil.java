package com.base.engine.core.util;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.assimp.*;

import com.base.engine.data.Mesh;
import com.base.engine.rendering.opengl.GLVertexArray;

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
	
	public static void loadMesh(Mesh mesh) {
		AIScene scene = Assimp.aiImportFile(mesh.filename, Assimp.aiProcess_Triangulate | Assimp.aiProcess_FlipUVs);
		
		if(scene == null || scene.mFlags() == Assimp.AI_SCENE_FLAGS_INCOMPLETE || scene.mRootNode() == null) {
	        System.err.println("ERROR::ASSIMP::" + Assimp.aiGetErrorString()); return;
	    }
		
		AIMesh aimesh = AIMesh.create(scene.mMeshes().get());
		
		FloatBuffer[] vertices = new FloatBuffer[3];
		
		vertices[0] = AssimpUtil.arrayToBuffer(aimesh.mVertices());
		vertices[1] = AssimpUtil.arrayToBuffer(aimesh.mNormals());
		vertices[2] = AssimpUtil.arrayToBuffer2D(aimesh.mTextureCoords(0));
		
		IntBuffer indices = AssimpUtil.arrayToBuffer(aimesh.mFaces());
		
		mesh.buffers = GLVertexArray.genBuffers(4);
		mesh.vao = GLVertexArray.setUpVertexArray(mesh.buffers, vertices, indices, 3,3,2);
		mesh.indices = indices.capacity();
		
//		buffers[0] = AssimpUtil.arrayToBuffer(mesh.mVertices());
//		buffers[1] = AssimpUtil.arrayToBuffer(mesh.mNormals());
//		buffers[2] = AssimpUtil.arrayToBuffer(mesh.mTangents());
//		buffers[3] = AssimpUtil.arrayToBuffer(mesh.mBitangents());
//		for(int i = 2; i < buffers.length; i++) buffers[i] = AssimpUtil.arrayToBuffer(mesh.mTextureCoords(i - 2));
	}
}
