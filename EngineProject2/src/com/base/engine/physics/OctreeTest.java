package com.base.engine.physics;

import com.base.engine.core.GameObject;
import com.base.math.Transform;

import primitives.AABB;

public class OctreeTest {
	static float delta = 1.0f;
	
	public static void main(String [] args){
		GameObject o = new GameObject(0,0,0);
		o.transform = new Transform();
		o.collider = new AABB(1,1,1,-1,-1,-1);
		o.rigidbody = new RigidBody(1,0,0);
		o.rigidbody.transform = o.transform;
//		o.rigidbody.velocityx = -2; o.rigidbody.velocityy = -2; o.rigidbody.velocityz = -2;
		
		Octree octree = new Octree(10,10,10, new GameObject(1,1,1), new GameObject(-1,1,1), new GameObject(1,2,1), new GameObject(1,3,3), new GameObject(1,-1,-1));
		octree.update(delta);
		
		octree.add(o);
		
		octree.update(delta);
		octree.update(delta);
	}
}
