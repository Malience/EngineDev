package com.base.engine.physics;

import math.Matrix3f;
import math.Vector3f;

public class Contact {
	public Vector3f normal = new Vector3f();
	public Vector3f pos = new Vector3f();
	public float penetration;
	public RigidBody a, b;
	public float friction, static_friction, kinetic_friction, restitution = 0f;
	
	public Contact() {
		static_friction = kinetic_friction = 0f;
		friction = (static_friction + kinetic_friction) * 0.5f;
	}
	
	protected Matrix3f contactToWorld = new Matrix3f();
	protected Vector3f contactVelocity = new Vector3f();
	protected float desiredDeltaVelocity = 0;
	protected Vector3f relativeContactPositionA = new Vector3f();
	protected Vector3f relativeContactPositionB = new Vector3f();
	
	public void swap() { normal.negative(); a = b; b = null;}
	
	protected void calculateInternals(float delta) {
		if(a == null) swap();
		
		
		
		contactToWorld.calculateContactBasis(normal);
		//contactToWorld.m11 = -1;
		
		pos.sub(a.transform.pos, relativeContactPositionA);
		if(b != null) pos.sub(b.transform.pos, relativeContactPositionB);
		
		calculateLocalVelocity(delta);
		
		calculateDesiredDeltaVelocity(delta);
	}
	
	protected void matchAwakeState()
	{
		if(b == null) return;
		
//		boolean body0awake = body[0].getAwake();
//		boolean body1awake = body[1].getAwake();
//		
//		if(body0awake ^ body1awake)
//		{
//			if(body0awake) body[1].setAwake();
//			else body[0].setAwake();
//		}
	}
	
	protected void calculateDesiredDeltaVelocity(float delta)
	{
		final float velocityLimit = .25f;
		
		float velocityFromAcc = 0;
		
		velocityFromAcc += a.acceleration.dot(normal) * delta;
		if(b != null) velocityFromAcc -= a.acceleration.dot(normal) * delta;
		
		float thisRestitution = restitution;
		if(Math.abs(contactVelocity.x) < velocityLimit) {
			thisRestitution = 0;
		}
		
		desiredDeltaVelocity = -contactVelocity.x - thisRestitution * (contactVelocity.x - velocityFromAcc);//* (1 + restitution);// - thisRestitution * (contactVelocity.x - velocityFromAcc);
	}
	
	private Vector3f velocity = new Vector3f();
	
	protected Vector3f calculateLocalVelocity(float delta) {
		a.rotation.cross(relativeContactPositionA, velocity).add(a.velocity);
		contactToWorld.transformTranspose(velocity, contactVelocity);
		
		a.acceleration.mul(delta, velocity);
		
		contactToWorld.transform(velocity);
		velocity.x = 0;
		
		contactVelocity = contactVelocity.add(velocity);
		
		if(b != null) {
			b.acceleration.mul(delta, velocity);
			
			contactToWorld.transform(velocity);
			
			float y = velocity.y; float z = velocity.z;
			
			b.rotation.cross(relativeContactPositionB, velocity).add(b.velocity);
			contactToWorld.transformTranspose(velocity);
			
			velocity.y += y; velocity.z += z;
			
			contactVelocity.sub(velocity);
		}
		
		return contactVelocity;
	}
	
	protected void applyImpulse(Vector3f impulse, RigidBody body, Vector3f velocityChange, Vector3f rotationChange) {
		
	}
	boolean done2 = true;
	protected void applyVelocityChange(Vector3f linearChangeA, Vector3f linearChangeB, Vector3f angularChangeA, Vector3f angularChangeB) {
		if(friction == 0) calculateFrictionlessImpulse();
		else calculateFrictionImpulse();
		//impulse.z = -impulse.z;//The z is what needs to be negative //TODO: This shouldn't be here
		contactToWorld.transform(impulse);
		
		a.addVelocity(impulse.mul(a.inverseMass, linearChangeA));
		impulse.cross(relativeContactPositionA, angularChangeA);
		a.addRotation(angularChangeA.mul(a.inverseInertiaTensor));
		
		if(b != null) {
			impulse.negative();
			b.addVelocity(impulse.mul(-b.inverseMass, linearChangeB));
			b.addRotation(impulse.cross(relativeContactPositionB, angularChangeB).mul(b.inverseInertiaTensor));
		}
	}
	
	Vector3f inertia = new Vector3f();
	protected void applyPositionChange(Vector3f linearChangeA, Vector3f linearChangeB, Vector3f angularChangeA, Vector3f angularChangeB, float penetration) {
		float angularLimit = .2f;	
		
		float linearInertiaA = a.inverseMass;
		float angularInertiaA = relativeContactPositionA.cross(normal, inertia).mul(a.inverseInertiaTensor).cross(relativeContactPositionA).dot(normal);
		float totalInertia = linearInertiaA + angularInertiaA;
		
		float linearInertiaB = 0f, angularInertiaB = 0f;
		
		if(b != null) {
			linearInertiaB = b.inverseMass;
			angularInertiaB = relativeContactPositionB.cross(normal, inertia).mul(b.inverseInertiaTensor).cross(relativeContactPositionB).dot(normal);
			totalInertia += linearInertiaB + angularInertiaB;
		}
		
		float inverseInertia = 1.0f / totalInertia;
		float angularMove = penetration * angularInertiaA * inverseInertia;
		float linearMove = penetration * linearInertiaA * inverseInertia;
		
		float maxMagnitude = relativeContactPositionA.add(normal.mul(-relativeContactPositionA.dot(normal), inertia), inertia).magnitude() * angularLimit;
		
		if(angularMove < -maxMagnitude) {
			linearMove = angularMove + linearMove + maxMagnitude;
			angularMove = -maxMagnitude;
		} else if(angularMove > maxMagnitude) {
			linearMove = angularMove + linearMove - maxMagnitude;
			angularMove = maxMagnitude;
		}
		
		if(angularMove != 0) 
			a.transform.rot.rotate(relativeContactPositionA.cross(normal, angularChangeA).mul(a.inverseInertiaTensor).mul(angularMove / angularInertiaA));
		a.transform.translate(normal.mul(linearMove, linearChangeA));
		
		if(b != null) {
			angularMove = -penetration * angularInertiaB * inverseInertia;
			linearMove = -penetration * linearInertiaB * inverseInertia;
			
			maxMagnitude = relativeContactPositionB.add(normal.mul(-relativeContactPositionB.dot(normal), inertia), inertia).magnitude() * angularLimit;
			
			if(angularMove < -maxMagnitude) {
				linearMove = angularMove + linearMove + maxMagnitude;
				angularMove = -maxMagnitude;
			} else if(angularMove > maxMagnitude) {
				linearMove = angularMove + linearMove - maxMagnitude;
				angularMove = maxMagnitude;
			}
			
			if(angularMove != 0) b.transform.rot.addScaledVector(relativeContactPositionB.cross(normal, angularChangeB).mul(b.inverseInertiaTensor).mul(angularMove / angularInertiaB), 1.0f);
			b.transform.translate(normal.mul(linearMove, linearChangeB));
		}
		
	}
	
	Vector3f impulse = new Vector3f();
	protected Vector3f calculateFrictionlessImpulse() {
		float deltaVelocity = relativeContactPositionA.cross(normal, impulse).mul(a.inverseInertiaTensor).cross(relativeContactPositionA, impulse).dot(normal) + a.inverseMass;
		if(b != null) deltaVelocity += relativeContactPositionB.cross(normal, impulse).mul(b.inverseInertiaTensor).cross(relativeContactPositionB, impulse).dot(normal) + b.inverseMass;
		impulse.set(desiredDeltaVelocity / deltaVelocity, 0f, 0f);
		return impulse;
	}
	
	Matrix3f impulseToTorque = new Matrix3f();
//	protected Vector3f calculateFrictionImpulse()
//	{
//		Vector3f impulseContact = new Vector3f(0,0,0);
//		float inverseMass = a.getInverseMass();
//		
//		impulseToTorque.setSkewSymmetric(relativeContactPositionA);
//		impulseToTorque.impulseToTorque(a.inverseInertiaTensor);
//		
//		if(body[1] != null)
//		{
//			impulseToTorque.setSkewSymmetric(relativeContactPosition[1]);
//			
//			Matrix3f deltaVelWorld2 = impulseToTorque;
//			deltaVelWorld2 = deltaVelWorld2.mul(inverseInertiaTensor[1]);
//			deltaVelWorld2 = deltaVelWorld2.mul(impulseToTorque).mul(-1);
//			
//			deltaVelWorld = deltaVelWorld.add(deltaVelWorld2);
//			
//			inverseMass += body[1].getInverseMass();
//		}
//		
//		Matrix3f deltaVelocity = contactToWorld.transpose();
//		deltaVelocity = deltaVelocity.mul(deltaVelWorld);
//		deltaVelocity = deltaVelocity.mul(contactToWorld);
//		
//		deltaVelocity.m[0][0] += inverseMass;
//		deltaVelocity.m[1][1] += inverseMass;
//		deltaVelocity.m[2][2] += inverseMass;
//		
//		Matrix3f impulseMatrix = deltaVelocity.inverse();
//		
//		Vector3f velKill = new Vector3f(desiredDeltaVelocity, -contactVelocity.y, -contactVelocity.z);
//		
//		impulseContact = impulseMatrix.transform(velKill);
//		
//		float planarImpulse = (float) Math.sqrt(impulseContact.y*impulseContact.y + impulseContact.z*impulseContact.z);
//		
//		if(planarImpulse > impulseContact.x * friction)
//		{
//			impulseContact.y /= planarImpulse;
//			impulseContact.z /= planarImpulse;
//			
//			impulseContact.x = deltaVelocity.m[0][0] + deltaVelocity.m[0][1]*friction*impulseContact.y + deltaVelocity.m[0][2]*friction*impulseContact.z;
//			impulseContact.x = desiredDeltaVelocity / impulseContact.x;
//			impulseContact.y *= friction * impulseContact.x;
//			impulseContact.z *= friction * impulseContact.x;
//		}
//		
//		return impulseContact;
//	}
	
	static int done = 0;
	
	protected Vector3f calculateFrictionImpulse() {
		//Find impulse Contact
		
		//Matrix3f impulseToTorque = new Matrix3f().setSkewSymmetric(relativeContactPositionA);
		
		Matrix3f delVelWorld = Matrix3f.impulseToTorque(relativeContactPositionA, a.inverseInertiaTensor, new Matrix3f());
		//Matrix3f delVelWorld2 = impulseToTorque.mul(a.inverseInertiaTensor, new Matrix3f()).mul(impulseToTorque).mul(-1);
		
		//if(done == 0) {System.out.println("\n" + delVelWorld + "\n");System.out.println(delVelWorld2 + "\n");}
		
		//TODO: EFFICIENTIZE THIS
		
		if(done == 30) {
			System.out.println(impulse);
		}
		
		Matrix3f deltaVelocity = contactToWorld.mulTranspose(delVelWorld, delVelWorld).mul(contactToWorld);
		deltaVelocity.m00 += a.inverseMass; deltaVelocity.m11 += a.inverseMass; deltaVelocity.m22 += a.inverseMass;
		
		Matrix3f impulseMatrix = deltaVelocity.invert(new Matrix3f());
		Vector3f velKill = new Vector3f(desiredDeltaVelocity, -contactVelocity.y, -contactVelocity.z);
		
		if(done == 30) {System.out.println(velKill);}
		
		impulseMatrix.transform(velKill, impulse);
		
		if(done == 30) {System.out.println(impulse);}
		
		float planarImpulse = (float) Math.sqrt(impulse.y * impulse.y + impulse.z * impulse.z);
		
		if(planarImpulse > impulse.x * static_friction) {
			float invPlanarImpulse = 1.0f / planarImpulse;
			
			impulse.y *= invPlanarImpulse;
			impulse.z *= invPlanarImpulse;
			if(done == 30) {System.out.println(impulse);}
			impulse.x = desiredDeltaVelocity / (deltaVelocity.m00 + deltaVelocity.m01 * kinetic_friction * impulse.y + deltaVelocity.m02 * kinetic_friction * impulse.z);
			
			float fricx = kinetic_friction * impulse.x;
			impulse.y *= fricx;
			impulse.z *= fricx;
			if(done == 30) {System.out.println(impulse);}
			if(done == 30) {System.out.println("Kinetic");}
		}
		done++;
//		Matrix3f impulseToTorque = new Matrix3f().setSkewSymmetric(relativeContactPositionA);
//		
//		Matrix3f deltaVelWorld = impulseToTorque.impulseToTorque(a.inverseInertiaTensor, new Matrix3f());
//		
//		if(b != null)
//		{
//			impulseToTorque.setSkewSymmetric(relativeContactPositionB);
//			
//			Matrix3f deltaVelWorld2 = impulseToTorque.impulseToTorque(b.inverseInertiaTensor, new Matrix3f());
//			
//			deltaVelWorld.add(deltaVelWorld2);
//			
//			inverseMass += b.getInverseMass();
//		}
//		
//		Matrix3f deltaVelocity = contactToWorld.mulTranspose(deltaVelWorld, new Matrix3f()).mul(contactToWorld);
//		
//		deltaVelocity.m00 += inverseMass; deltaVelocity.m11 += inverseMass; deltaVelocity.m22 += inverseMass;
//		
//		Matrix3f impulseMatrix = deltaVelocity.invert(new Matrix3f());
//		
//		Vector3f velKill = new Vector3f(desiredDeltaVelocity, -contactVelocity.y, -contactVelocity.z);
//		
//		impulseMatrix.transform(velKill, impulse);
//		
//		float planarImpulse = (float) Math.sqrt(impulse.y * impulse.y + impulse.z * impulse.z);
//		
//		if(planarImpulse > impulse.x * friction)
//		{
//			impulse.y /= planarImpulse;
//			impulse.z /= planarImpulse;
//			
//			impulse.x = deltaVelocity.m00 + deltaVelocity.m01 * friction * impulse.y + deltaVelocity.m02 * friction * impulse.z;
//			impulse.x = desiredDeltaVelocity / impulse.x;
//			impulse.y *= friction * impulse.x;
//			impulse.z *= friction * impulse.x;
//		}
		
		return impulse;
	}
	
}
