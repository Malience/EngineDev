package com.base.math;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import com.base.engine.core.util.Util;

import math.Matrix4f;
import math.Quaternion;
import math.Vector3f;

//TODO: Optimizable
public class Transform {
	private Transform parent; //Parent transform
	private ArrayList<Transform> children;
	
	private Matrix4f transformation;
	private FloatBuffer buffer;

	public Vector3f pos;//Position
	public Quaternion rot;//Rotation
	private Vector3f scale;//Oh I wonder what this could be smart guy!
	
	//bit 0 = position, bit 1 = rotation, bit 2 = scale, bit 3 = buffer
	//TODO: Optimize into byte for each changable vector
	public boolean hasChanged;
	private boolean bufferHasChanged = true;
	public Runnable changeCallback;
	
	/**
	 * Default Constructor
	 */
	public Transform(){
		pos = new Vector3f();
		rot = new Quaternion();
		scale = new Vector3f(1f,1f,1f);
		transformation = new Matrix4f();
	}
	
	public Transform(float x, float y, float z){
		pos = new Vector3f(x,y,z);
		rot = new Quaternion();
		scale = new Vector3f(1f,1f,1f);
		transformation = new Matrix4f();
		remakeTransformation();
	}
	
	public Transform(float x, float y, float z, float rotx, float roty, float rotz, float scale) {
		pos = new Vector3f(x,y,z);
		rot = new Quaternion().rotate(rotx, roty, rotz);
		this.scale = new Vector3f(scale, scale, scale);
		transformation = new Matrix4f();
		remakeTransformation();
	}
	
	/**
	 * Sets fields 
	 * @param pos Position
	 * @param rot Rotation
	 * @param scale <---
	 */
	public Transform(Vector3f pos, Quaternion rot, Vector3f scale)
	{
		this.pos = pos;
		this.rot = rot;
		this.scale = scale;
		remakeTransformation();
	}
	
	public void applyChanges()
	{
		this.hasChanged = false;
		this.remakeTransformation();
		
		if(changeCallback != null) changeCallback.run();
	}
	
	public void hasChanged(){
		if(!hasChanged){
			hasChanged = true;
			bufferHasChanged = true;
			if(children != null){
				for(Transform t : children) t.hasChanged();
			}
		}
	}
	
	/**
	 * The transformation of the Transform
	 * For example when applied to an object this is how the camera
	 * calculates how you should see it.
	 * Places something into "world space" from "local space"
	 * @return The transformation
	 */
	public Matrix4f getTransformation(){
		if(hasChanged) applyChanges();
		return transformation;
	}
	
	public void remakeTransformation(){
		if(parent == null) transformation.translationRotateScale(pos, rot, scale); 
		else transformation.translationRotateScale(pos, rot, scale).mul(parent.getTransformation());
	}
	
	public Matrix4f getParentTransformation(){return parent == null ? null : parent.getTransformation();}
	
	public Matrix4f getRootTransformation(){
		if(parent == null) return this.getTransformation();
		Transform current = parent;
		while(true) {if(current == this) return null; if(current.parent == null) return current.parent.getTransformation(); current = current.parent;}
	}
	
	public FloatBuffer getBuffer(){if(bufferHasChanged) applyBuffer(); return buffer;}
	
	private void applyBuffer(){
		if(buffer == null) buffer = Util.createFloatBuffer(16);
		buffer.clear(); Util.matrixToBuffer(buffer, this.getTransformation()); buffer.flip();
		bufferHasChanged = false;
	}
	
	@Override
	public void finalize(){if(this.parent != null) this.parent.children.remove(this); if(buffer != null) Util.free(buffer);}
	
	/**
	 * Used when an object is made the child of another object
	 * @param parent The parent
	 */
	public void setParent(Transform parent)
	{
		this.parent = parent;
		if(loopCheck()) {this.parent = null; return;}
		if(this.parent.children == null) this.parent.children = new ArrayList<Transform>();
		this.parent.children.add(this);
		hasChanged();
	}
	
	public void removeParent()
	{
		this.parent.children.remove(this);
		this.parent = null;
		hasChanged();
	}
	
	@Deprecated
	public void setPosition(float... pos){
		//this.pos = pos;
		hasChanged();
	}
	
	@Deprecated
	public void setRotation(float... v){
		//this.rot = QuaternionMath.createQuaternionFromEuler(v);
		hasChanged();
	}
	
	@Deprecated
	public void setScale(float... scale){
		//this.scale = scale;
		hasChanged();
	}
	
	@Deprecated
	public void setScale(float scale){
		//this.scale[0] = scale; this.scale[1] = scale; this.scale[2] = scale;
		hasChanged();
	}
	
	public void translate(Vector3f v){pos.add(v);hasChanged();}
	public void translate(float x, float y, float z){pos.add(x, y, z);hasChanged();}
	public void translateScaled(Vector3f v, float scale){pos.addScaled(v, scale);hasChanged();}
	public void translateScaled(float x, float y, float z, float scale){pos.addScaled(x, y, z, scale);hasChanged();}
	
	public void rotateScaled(Vector3f v, float scale){rot.addScaledVector(v, scale);hasChanged();}
	//public void rotateScaled(float x, float y, float z, float scale){rot.addScaledVector(x, y, z, scale);hasChanged();}
	public void normalizeRot(){rot.normalize();}
	
	public Vector3f transform(Vector3f v){
		if(hasChanged) applyChanges();
		return transformation.transform(v);
	}
	
	public Vector3f transform(Vector3f v, Vector3f out){
		if(hasChanged) applyChanges();
		return transformation.transform(v, out);
	}
	
	public Vector3f transformRotation(Vector3f v) {return rot.transform(v);}
	public Vector3f transformRotation(Vector3f v, Vector3f out) {return rot.transform(v, out);}
	
	@Deprecated
	public void lookAt(float... point){
		//QuaternionMath.lookAt(rot, pos, point);
		hasChanged();
	}
	
	@Deprecated
	public void lookAt(float x, float y, float z){
		//QuaternionMath.lookAt(rot, pos, x, y, z);
		hasChanged();
	}
	
	@Deprecated
	public void rotateX(float angle){
		//QuaternionMath.rotate(this.rot, VectorMath.RIGHT, angle);
		hasChanged();
	}
	
	@Deprecated
	public void rotateY(float angle){
		//QuaternionMath.rotate(this.rot, VectorMath.UP, angle);
		hasChanged();
	}
	
	@Deprecated
	public void rotateZ(float angle){
		//QuaternionMath.rotate(this.rot, VectorMath.FORWARD, angle);
		hasChanged();
	}
	
	@Deprecated
	public void rotate(float[] axis, float angle){
		//QuaternionMath.rotate(this.rot, axis, angle);
		hasChanged();
	}
	
	@Deprecated
	public void rotate(float angle, float... axis){
		//QuaternionMath.rotate(this.rot, axis, angle);
		hasChanged();
	}
	
	public void rotate(float x, float y, float z) {
		rot.rotate(x, y, z);
		hasChanged();
	}
	
	@Deprecated
	public void rotate(float x, float y, float z, float angle){
		//QuaternionMath.rotate(this.rot, x, y, z, angle);
		hasChanged();
	}
	
	@Deprecated
	public void rotateXYZ(float x, float y, float z){
		rotateX(x);
		rotateY(y);
		rotateZ(z);
		hasChanged();
	}
	public void scale(float scale){this.scale.mul(scale);hasChanged();}
	public void scale(float x, float y, float z){this.scale.mul(x, y, z);hasChanged();}
	
	private boolean loopCheck(){
		Transform current = parent;
		try{while(true) {if(current.parent == this) return true; current = current.parent;}}
		catch(NullPointerException e){return false;}
	}
	
	public String toString(){return pos.toString();}
}
