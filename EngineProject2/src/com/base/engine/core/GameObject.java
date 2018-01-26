package com.base.engine.core;

import com.base.engine.physics.RigidBody;
import com.base.math.Transform;

import primitives.AABB;
import primitives.Primitive;

public class GameObject {
	public Transform transform;
	public Primitive collider;
	public RigidBody rigidbody;
	public Component rb;
	
	public GameObject(float x, float y, float z){
		transform = new Transform(x,y,z);
		collider = new AABB(0.5f,0.5f,0.5f,-0.5f,-0.5f,-0.5f);
	}
	
	public boolean update(float delta){
		if(rigidbody != null){
			rigidbody.integrate(delta);
			rigidbody.simulate(delta);
		}
		return transform.hasChanged;
	}
	
	public String toString(){return transform.pos.toString();}
}
