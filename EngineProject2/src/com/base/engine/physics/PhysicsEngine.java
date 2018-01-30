package com.base.engine.physics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import com.base.engine.core.Engine;
import com.base.engine.core.Time;
import com.base.math.Transform;

import math.Vector3f;
import primitives.Capsule;
import primitives.OBB;
import primitives.Plane;
import primitives.Sphere;

public class PhysicsEngine extends Thread implements Engine
{
	//World world;
	//ArrayList<Physical> physicalComponents;
	//ArrayList<Collidable> collidableComponents;
	//ArrayList<BroadCollidable> broadphase;
	public static RigidBody[] bodies;
	//CollisionDetector detector;
	int iterations;
	boolean calculateIterations;
	
	public PhysicsEngine()
	{
		//world = World.world;
		//detector = new CollisionDetector();
		iterations = 40;
		ContactResolver.setIterations(iterations);
		calculateIterations = (iterations == 0);
		bodies = new RigidBody[20];
		bodies[0] = new RigidBody(1f, 1f, 0.05f);
		bodies[0].acceleration.set(0f, -10f, 0f);
		bodies[0].transform = new Transform(0f, 20f, 0f);
		//bodies[0].transform.rotate(0, 45, 45);
		bodies[0].inverseInertiaTensor = InertiaTensors.rigidBoxTensor(1, 1, 1);//InertiaTensors.rigidSphereTensor(1f);//InertiaTensors.rigidCapsuleTensor(c).inverse();//InertiaTensors.rigidSphereTensor(1f);
		ContactList.generate(10);
		p.normalize();
	}
	
	Capsule c = new Capsule(new Vector3f(0,-1,0), new Vector3f(0, 1,0), 1);
	Sphere s = new Sphere(0f, 0f, 0f, 1f);
	Plane p = new Plane(1f, 1f, 0f, 0f);
	OBB o = new OBB(1,1,1);
	
	public void start() {
		
	}
	
	public void run() {
		float delta = Time.getDelta();
		integrate(delta);
		simulate(delta);
		generateContacts(delta);
		resolve(delta);
		
		//System.out.println(bodies[0].velocity);
	}
	
	public void integrate(float delta){ for(int i = 0; i < 1; i++) bodies[i].integrate(delta);}
	public void simulate(float delta){ for(int i = 0; i < 1; i++) bodies[i].simulate(delta);}
	
	public void generateContacts(float delta)
	{
		CollisionTest.detectCollision(o, p, bodies[0], null);
//		if(ThreadPool.multiThreading)
//		{
//			ThreadPool.physicsCollisionDetect(collidableComponents);
//			return;
//		}
//		for(int i = 0; i < collidableComponents.size(); i++)
//		{
//			for(int j = i + 1; j < collidableComponents.size(); j++)
//			{
//				CollisionDetector.checkCollision(collidableComponents.get(i), collidableComponents.get(j));
//			}
//		}
		
//		for(int i = 0; i < broadphase.size(); i++)
//		{
//			for(int j = i + 1; j < broadphase.size(); j++)
//			{
//				broadphase(broadphase.get(i).getCollider(), broadphase.get(j).getCollider());
//			}
//		}
	}
	
//	public void broadphase(BoundingCollider one, BoundingCollider two)
//	{
//		if(broadDetector.checkCollision(one, two))
//		{
//			ArrayList<Collider> colliders1 = one.getColliders();
//			ArrayList<Collider> colliders2 = two.getColliders();
//			for(int i = 0; i < colliders1.size(); i++)
//			{
//				for(int j = 0; j < colliders2.size(); j++)
//				{
//					CollisionDetector.checkCollision(colliders1.get(i).getPrimitive(),colliders2.get(j).getPrimitive(), data);
//				}
//			}
//			
//			for(BoundingCollider second : two.getSubColliders())
//			{
//				broadphase(one, second);
//			}
//			
//			for(BoundingCollider first : one.getSubColliders())
//			{
//				broadphase(two, first);
//			}
//		}
//	}
	
	public void resolve(float delta){
		if(ContactList.next == 0) return;
		ContactResolver.resolveContacts(ContactList.contacts, 0, ContactList.next, delta);
		ContactList.reset();
	}
	
	public void dispose() {
		
	}
	
	//TODO: ForceGenerators
//	public static void addForce(RigidBody body, String forceName)
//	{
//		ForceGenerator force = forces.get(forceName);
//		if(force != null) registry.add(body, force);
//	}
//	
//	public static void addForce(RigidBody body, ForceGenerator force)
//	{
//		String forceName = addForce(force);
//		force = forces.get(forceName);
//		if(force != null) registry.add(body, force);
//	}
//	
//	public static String addForce(ForceGenerator force)
//	{
//		String forceName = force.getClass().getSimpleName();
//		ForceGenerator test = forces.get(forceName);
//		int i = -1;
//		while(test != null)
//		{
//			test = forces.get(forceName + ++i);
//		}
//		forces.put(forceName + i, force);
//		return forceName + i;
//	}
//	
//	public static void addForce(ForceGenerator force, String forceName)
//	{
//		forces.put(forceName, force);
//	}
}
