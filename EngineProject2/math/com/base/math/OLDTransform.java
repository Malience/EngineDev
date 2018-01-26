package com.base.math;

import java.io.Serializable;

import math.Matrix4f;
import math.Quaternion;
import math.Vector3f;

/**
 * Holds Position, Scale, and Rotation
 *
 */
public class OLDTransform implements Serializable
{
	private transient static final long serialVersionUID = 1435942764555714013L;
	private transient static final Matrix4f defaultParentMatrix = new Matrix4f();
	private OLDTransform parent; //Parent transform
	private Matrix4f parentMatrix; //This

	private Vector3f pos;//Position
	private Quaternion rot;//Rotation
	private Vector3f scale;//Oh I wonder what this could be smart guy!

	private Vector3f oldPos;//In case any of these changed
	private Quaternion oldRot;
	private Vector3f oldScale;
	
	/**
	 * Default Constructor
	 */
	public OLDTransform()
	{
		pos = new Vector3f(0,0,0);
		rot = new Quaternion(0,0,0,1);
		scale = new Vector3f(1,1,1);

		parentMatrix = defaultParentMatrix;
	}
	
	/**
	 * Sets fields 
	 * @param pos Position
	 * @param rot Rotation
	 * @param scale <---
	 */
	public OLDTransform(Vector3f pos, Quaternion rot, Vector3f scale)
	{
		this();
		this.setPos(pos);
		this.setRot(rot);
		this.setScale(scale);
	}
	
	/**
	 * Don't call this
	 */
	public void update()
	{
		if(oldPos != null)
		{
			oldPos.set(pos);
			oldRot.set(rot);
			oldScale.set(scale);
		}
		else
		{
			oldPos = new Vector3f(0,0,0).set(pos).add(1.0f);
			//oldRot = new Quaternion(0,0,0,0).set(rot).mul(0.5f);
			oldScale = new Vector3f(0,0,0).set(scale).add(1.0f);
		}
	}
	
	public Vector3f getLastPos()
	{
		return oldPos;
	}
	
	/**
	 * Rotates the transform about an axis
	 * @param axis The axis to be rotated about ( (0,0,1), (0,1,0), and (1,0,0) are all popular choices!)
	 * @param angle Angle in degrees (citation needed)
	 */
	public void rotate(Vector3f axis, float angle)
	{
		//rot = new Quaternion(axis, angle).mul(rot).normalized();
	}
	
	/**
	 * Use this to look at a specific location
	 * @param point Location to be looked at
	 * @param up The direction currently considered to be "up" (0,1,0) is a popular choice
	 */
	public void lookAt(Vector3f point, Vector3f up)
	{
		rot = getLookAtRotation(point, up);
	}
	
	/**
	 * Use this if you need to know the rotation you would get from looking at a point
	 * @param point The point you would theoretically be looking at
	 * @param up The direction currently considered to be "up" (0,1,0) is a popular choice
	 * @return The Rotation as a Quaternion
	 */
	public Quaternion getLookAtRotation(Vector3f point, Vector3f up)
	{
		return null;//new Quaternion(new Matrix4f().initRotation(point.sub(pos).normal(), up));
	}
	
	/**
	 * If the Transform has moved, rotated, or scaled since last frame
	 * @return I just told you >.<
	 */
	public boolean hasChanged()
	{
		if(parent != null && parent.hasChanged())
			return true;

		if(!pos.equals(oldPos))
			return true;

		if(!rot.equals(oldRot))
			return true;

		if(!scale.equals(oldScale))
			return true;

		return false;
	}
	
	/**
	 * The transformation of the Transform
	 * For example when applied to an object this is how the camera
	 * calculates how you should see it.
	 * Places something into "world space" from "local space"
	 * @return The transformation
	 */
	public Matrix4f getTransformation()
	{
		//Matrix4fOLD translationMatrix = new Matrix4fOLD().initTranslation(pos.getX(), pos.getY(), pos.getZ());
		//Matrix4fOLD rotationMatrix = rot.toRotationMatrix();
		//Matrix4fOLD scaleMatrix = new Matrix4fOLD().initScale(scale.getX(), scale.getY(), scale.getZ());

		return null;//getParentMatrix().mul(translationMatrix.mul(rotationMatrix.mul(scaleMatrix)));
	}
	
	private Matrix4f getParentMatrix()
	{
		if(parent != null && parent.hasChanged())
			parentMatrix = parent.getTransformation();

		return parentMatrix;
	}
	
	/**
	 * Used when an object is made the child of another object
	 * @param parent The parent
	 */
	public void setParent(OLDTransform parent)
	{
		this.parent = parent;
	}
	
	public void removeParent()
	{
		this.parent = null;
		this.parentMatrix = defaultParentMatrix;
	}
	
	/**
	 * Gets the position in world space
	 * Say you had two objects with said positions
	 * 1 (0,0,0)
	 * 2 (1,1,1)
	 * Currently they are in local space
	 * Making 2 the child of 1 would mean that (1,1,1) is both
	 * the local space and world space locations of 2
	 * However, if we change 1 to the location (1,1,1) this will happen
	 * Local Space:
	 * 1 (1,1,1)
	 * 2 (1,1,1)
	 * World Space:
	 * 1 (1,1,1)
	 * 2 (2,2,2)
	 * Basically, it makes a child objects position an offset from its parents position
	 * @return The transformed position
	 */
	public Vector3f getTransformedPos()
	{
		return null;//getParentMatrix().transform(pos);
	}
	
	/**
	 * Read getTransformedPos()
	 * The same thing except for rotation
	 * @return The transformed rotation
	 */
	public Quaternion getTransformedRot()
	{
		Quaternion parentRotation = new Quaternion(0,0,0,1);

		if(parent != null)
			parentRotation = parent.getTransformedRot();

		return parentRotation.mul(rot);
	}
	
	/**
	 * Returns the objects position
	 * @return The position
	 */
	public Vector3f getPos()
	{
		return pos;
	}
	
//	public Vector3f getOverallPos(Vector3f pos)
//	{
//		pos.add(this.pos);
//		if(parent!=null)
//			pos = parent.getOverallPos(pos);
//		return pos;
//	}
	
	/**
	 * Sets the position of an object
	 * @param x x coordinate
	 * @param y y coordinate
	 * @param z z coordinate
	 */
	public void setPos(float x, float y, float z){this.setPos(new Vector3f(x,y,z));}
	
	/**
	 * Vector3f form of setPos
	 * @param pos The position
	 */
	public void setPos(Vector3f pos)
	{
		this.pos = pos;
	}
	
	/**
	 * Returns the rotation of an object as a Quaternion
	 * @return The Rotation
	 */
	public Quaternion getRot()
	{
		return rot;
	}
	
	/**
	 * Sets the rotation of the object
	 * @param rotation The rotation
	 */
	public void setRot(Quaternion rotation)
	{
		this.rot = rotation;
	}
	
	/**
	 * Returns the scale of an object as a Quaternion
	 * @return The scale
	 */
	public Vector3f getScale()
	{
		return scale;
	}
	
	/**
	 * Sets the scale of the object 
	 * (and yes this is the scale in each respective direction)
	 * @param scale The rotation
	 */
	public void setScale(Vector3f scale)
	{
		this.scale = scale;
	}
	
	/**
	 * Scales the object uniformly
	 * Same thing as setting the scale to a vector where x=y=z
	 * @param scale The scale
	 */
	public void setScale(float scale)
	{
		this.scale = new Vector3f(1,1,1).mul(scale);
	}
}
