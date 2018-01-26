package com.base.engine.physics;

import com.base.engine.rendering.Camera;
import com.base.engine.rendering.Projection;

import math.Matrix4f;
import math.Vector3f;
import primitives.Plane;

public class Frustum {
	public Plane far;
	public Plane near;
	public Plane top;
	public Plane right;
	public Plane bot;
	public Plane left;
	
	public Frustum(Camera c, Projection proj){this(proj.calculateInverse(c.getViewMatrix()), c.pos, c.forward, proj.near, proj.far);}
	public Frustum(Matrix4f ivp, Vector3f pos, Vector3f forward, float near, float far){
		Vector3f tr = ivp.transform(new Vector3f(1f,1f,1f));
		Vector3f br = ivp.transform(new Vector3f(1f,-1f,1f));
		Vector3f tl = ivp.transform(new Vector3f(-1f,1f,1f));
		Vector3f bl = ivp.transform(new Vector3f(-1f,-1f,1f));
		//Vector3f forward = ivp.transform(new Vector3f(0f,0f,1f)).normalize();
		this.far = new Plane(forward, far);
		this.near = new Plane(-forward.x, -forward.y, -forward.z, -near);
		top = new Plane(pos, tr, tl);
		right = new Plane(pos, br, tr);
		bot = new Plane(pos, bl, br);
		left = new Plane(pos, tl, bl);
	}
	
	//1 = contains, 0 = intersects, -1 = doesn't intersect
//	public int contains(BoundingBox b){
//		if(!far.contains(b)) return -1;
//		if(!near.contains(b)) return -1;
//		if(!top.contains(b)) return -1;
//		if(!bot.contains(b)) return -1;
//		if(!left.contains(b)) return -1;
//		if(!right.contains(b)) return -1;
//		return 0;
//	}
//	
//	//It at least intersects
//	public boolean intersects(BoundingBox b){
//		if(!far.contains(b)) return false;
//		if(!near.contains(b)) return false;
//		if(!top.contains(b)) return false;
//		if(!bot.contains(b)) return false;
//		if(!left.contains(b)) return false;
//		if(!right.contains(b)) return false;
//		return true;
//	}
	
	//true = behind or on plane (inside) false = outside of plane
	public boolean pointPlane(Vector3f pdir, float poff, float x, float y, float z){return pdir.x * x + pdir.y * y + pdir.z * z <= poff;}
}
