package com.base.engine.physics;

import static primitives.Primitive.*;

import math.Vector3f;
import primitives.*;

public class CollisionTest {
	static Vector3f v0 = new Vector3f();
	static Vector3f v1 = new Vector3f();
	
	public static void detectCollision(Primitive a, Primitive b, RigidBody rba, RigidBody rbb) {
		if(!ContactList.hasNext()) return;
		if(a.type > b.type) {Primitive swap = b; b = a; a = swap; RigidBody s = rbb; rbb = rba; rba = s;}
		
		switch(a.type) {
		case SPHERE:
			switch(b.type) {
			case SPHERE: sphereSphere((Sphere)a, (Sphere)b, rba, rbb); return;
			case PLANE: spherePlane((Sphere)a, (Plane)b, rba, rbb); return;
			case OBB:	sphereOBB((Sphere)a, (OBB)b, rba, rbb); return;
			}
		case PLANE:
			switch(b.type) {
			case CAPSULE: planeCapsule((Plane)a, (Capsule)b, rba, rbb); return;
			case OBB:	planeOBB((Plane)a, (OBB)b, rba, rbb); return;
			}
		}
		
		
	}
	
	public static void sphereSphere(Sphere a, Sphere b, RigidBody rba, RigidBody rbb) {
		if(rba != null) rba.transform.transform(a.pos, v0); else v0.set(a.pos);
		if(rbb != null) rbb.transform.transform(b.pos, v1); else v1.set(b.pos);
		v1.sub(v0, v0);
		//b.pos.sub(a.pos, contact.normal);
		//float abx = b.pos.x - a.pos.x, aby = b.pos.y - a.pos.y, abz = b.pos.z - a.pos.z;
		float size = v1.lengthSquared();
		if(size <= 0) return;
		float radius = a.r + b.r;
		if(size >= radius * radius) return;
		
		Contact contact = ContactList.getNext();
		v1.addScaled(v0, 0.5f, contact.pos);
		
		size = (float)Math.sqrt(size);
		v1.div(size, contact.normal);
		contact.penetration = radius - size;
		contact.a = rba; contact.b = rbb;
		
		//ContactList.iterate();
	}
	
	public static void spherePlane(Sphere a, Plane b, RigidBody rba, RigidBody rbb) {
		if(rba != null) rba.transform.transform(a.pos, v0); else v0.set(a.pos);
		float dist = v0.dot(b.normal) - a.r - b.d; //TODO: Fix Plane
		
		if(dist >= 0f) return;
		
		Contact contact = ContactList.getNext();
		contact.normal.set(b.normal);
		contact.penetration = -dist;
		dist += a.r;
		v0.subScaled(b.normal, dist, contact.pos);
		contact.a = rba; contact.b = rbb;
		//ContactList.iterate();
	}
	
	public static void sphereOBB(Sphere a, OBB b, RigidBody rba, RigidBody rbb) {
		
	}
	
	static Vector3f v2 = new Vector3f();
	
	//TODO: Fix capsule collision
	public static void planeCapsule(Plane a, Capsule b, RigidBody rba, RigidBody rbb) {
		if(rba != null) {rba.transform.transform(b.a, v0); rba.transform.transform(b.b, v1);} else {v0.set(b.a); v0.set(b.b);}
		
		v1.sub(v0, v2);
		float t = a.d - a.normal.dot(v0);
		float nab = a.normal.dot(v2);
		
		
		if(t <= 0f) v2.set(v0);
		else if(t >= nab) v2.set(v1);
		else v0.addScaled(v2, t / nab, v2); 
		
		//v2 is the closest point on the segment to the plane
		float dist = a.normal.dot(v2) - b.r + a.d;
		if(dist > 0f) return; 
		
		Contact contact = ContactList.getNext();
		
		a.normal.mul(dist, contact.pos);
		contact.normal.set(a.normal);
		contact.penetration = dist;
		contact.a = rba; contact.b = rbb;
	}
	
	static Vector3f v3 = new Vector3f();
	static Vector3f v4 = new Vector3f();
	
	static float EPSILON = 0.0001f;
	
	public static void planeOBB(Plane a, OBB b, RigidBody rba, RigidBody rbb) {
		if(rbb != null) {
			rbb.transform.transform(b.pos, v0); rbb.transform.transformRotation(b.xaxis, v1); rbb.transform.transformRotation(b.yaxis, v2);
		} else {v0.set(b.pos); v1.set(b.xaxis); v2.set(b.yaxis);}
		
		v1.cross(v2, v3);//TODO: Might need to swap v1 and v2
		
		for(byte i = 0; i < 8; i++) {
			v1.mul((i & 0b001) == 0 ? b.hx : -b.hx, v4);
			v4.addScaled(v2, (i & 0b010) == 0 ? b.hy : -b.hy);
			v4.addScaled(v3, (i & 0b100) == 0 ? b.hz : -b.hz);
			v4.add(v0);
			float dist = v4.dot(a.normal);
			
			if(dist <= a.d + EPSILON) {
				Contact contact = ContactList.getNext();
				a.normal.mul(dist - a.d, contact.pos);
				contact.pos.add(v4);
				contact.normal.set(a.normal);
				contact.penetration = a.d - dist;
				contact.a = rba; contact.b = rbb;
			}
			
		}
	}
}
