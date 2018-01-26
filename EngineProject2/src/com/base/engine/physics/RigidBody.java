package com.base.engine.physics;

import math.Quaternion;

import com.base.engine.core.Component;
import com.base.math.Transform;
import math.Vector3f;

public class RigidBody extends Component
{
	private static final float sleepEpsilon = .01f;
	
	public Transform transform;
	
	float inverseMass;
	
	float linearDamping;
	float angularDamping;
	
	//TODO: OPTIMIZE
	Vector3f velocity;
	Vector3f rotation;
	
	Vector3f acceleration;
	Vector3f torque;
	
	Vector3f inverseInertiaTensor;
	
	public float motion;
	boolean sleep;
	
	public RigidBody(float mass, float ldamping, float adamping)
	{
		inverseMass = 1.0f/mass;
		
		linearDamping = ldamping;
		angularDamping = adamping;
		
		velocity = new Vector3f();
		rotation = new Vector3f();
		acceleration = new Vector3f();
		torque = new Vector3f();
		
		inverseInertiaTensor = new Vector3f(1.0f,1.0f,1.0f);
	}
	
	public void attach(Transform transform){this.transform = transform;}
	
	public void setInertiaTensor(Vector3f inertiaTensor){inverseInertiaTensor = inertiaTensor.inverse();}
	public void setInverseInertiaTensor(Vector3f inverseInertiaTensor){this.inverseInertiaTensor = inverseInertiaTensor;}

	public float getInverseMass() {return inverseMass;}
	public float getMass() {return 1.0f/inverseMass;}
	public void setMass(float mass) {this.inverseMass = 1.0f/mass;}
	
	public void addForce(Vector3f force){acceleration.add(force); sleep = false;}
	public void addForce(float x, float y, float z){acceleration.add(x, y, z); sleep = false;}
	public void removeForce(Vector3f force){acceleration.sub(force); sleep = false;}
	public void removeForce(float x, float y, float z){acceleration.sub(x, y, z); sleep = false;}
	public void addTorque(Vector3f force){torque.add(force); sleep = false;}
	public void addTorque(float x, float y, float z){torque.sub(x, y, z); sleep = false;}
	public void subTorque(Vector3f force){torque.add(force); sleep = false;}
	public void subTorque(float x, float y, float z){torque.sub(x, y, z); sleep = false;}
	public void addVelocity(Vector3f v){velocity.add(v); sleep = false;}
	public void addVelocity(Vector3f v, float scale){velocity.addScaled(v, scale); sleep = false;}
	public void addRotation(Vector3f v){rotation.add(v); sleep = false;}
	
	public void addForceAtBodyPoint(Vector3f force, Vector3f point)
	{
		Vector3f pt = getPointInWorldSpace(point);
		addForceAtPoint(force, pt);
	}
	
	public void addForceAtPoint(Vector3f force, Vector3f point)
	{
		addForce(force);
		addTorque(point.sub(transform.pos).cross(force));
		sleep = true;
	}
	
//	public Vector3f getPointInLocalSpace(Vector3f point)
//	{
//		return transformMatrix.transformInverse(point);
//	}
//	
	public Vector3f getPointInWorldSpace(Vector3f point){return transform.transform(point);}
//	
//	public Vector3f getDirectionInLocalSpace(Vector3f direction)
//	{
//		return transformMatrix.transformInverseDirection(direction);
//	}
//	
//	public Vector3f getDirectionInWorldSpace(Vector3f direction)
//	{
//		return transformMatrix.transformDirection(direction);
//	}
	
	public void integrate(float delta){	
//		Quaternion rot = transform.rot;
//	    float xx      = rot.x * rot.x;
//	    float xy      = rot.x * rot.y;
//	    float xz      = rot.x * rot.z;
//	    float xw      = rot.x * rot.w;
//
//	    float yy      = rot.y * rot.y;
//	    float yz      = rot.y * rot.z;
//	    float yw      = rot.y * rot.w;
//
//	    float zz      = rot.z * rot.z;
//	    float zw      = rot.z * rot.w;
//
//	    float m00  = 1 -(yy + zz) - (yy + zz);
//	    float m01  =    (xy - zw) + (xy - zw);
//	    float m02  =    (xz + yw) + (xz + yw);
//
//	    float m10  =    (xy + zw) + (xy + zw);
//	    float m11  = 1 -(xx + zz) - (xx + zz);
//	    float m12  =    (yz - xw) + (yz - xw);
//
//	    float m20  =    (xz - yw) + (xz - yw);
//	    float m21  =    (yz + xw) + (yz + xw);
//	    float m22  = 1 -(xx + yy) - (xx + yy);
//		
//		float l1 = inverseInertiaTensor.x * (torque.x * m00 + torque.y * m10 + torque.z * m20);
//		float l2 = inverseInertiaTensor.y * (torque.x * m01 + torque.y * m11 + torque.z * m21);
//		float l3 = inverseInertiaTensor.z * (torque.x * m02 + torque.y * m12 + torque.z * m22);
//		
//		float angularx = l1 * m00 + l2 * m01 + l3 * m02;
//		float angulary = l1 * m10 + l2 * m11 + l3 * m12;
//		float angularz = l1 * m20 + l2 * m21 + l3 * m22;
//		
//		float imassdelta = inverseMass * delta;
//		float lindamp = imassdelta * (float)Math.pow(linearDamping, delta);
//		float angdamp = imassdelta * (float)Math.pow(angularDamping, delta);
//		
//		velocity.x += acceleration.x * lindamp;
//		velocity.y += acceleration.y * lindamp;
//		velocity.z += acceleration.z * lindamp;
//		
//		rotation.x += angularx * angdamp;
//		rotation.y += angulary * angdamp;
//		rotation.z += angularz * angdamp;
		
		velocity.addScaled(acceleration, delta);
		velocity.mul((float)Math.pow(linearDamping, delta));
		
		//TODO: Torque calculation
		rotation.mul((float)Math.pow(angularDamping, delta));
	}
		
	public void simulate(float delta){
		if (sleep) return;
		
		//System.out.println(parent.getTransform().getRot());
		
		transform.translateScaled(velocity, delta);
		//parent.getTransform().setPos(parent.getTransform().getPos().add(velocity.mul(delta)));
		
		//TODO: FIX THISSSSS
		//transform.rotate(rotation.x * delta, rotation.y * delta, rotation.z * delta);
		//transform.rotateScaled(rotation, delta);
		transform.rot.rotateAngularVelocity(rotation, delta);
		transform.normalizeRot();
		transform.hasChanged();
//		parent.getTransform().getRot().addScaledVector(rotationVelocity, delta);
//		
//		parent.getTransform().getRot().normalize();
		
		//TODO: SLEEPING STUFF
//		if(canSleep)
//		{
//			float currentMotion = velocity.dot(velocity) + rotationVelocity.dot(rotationVelocity);
//			
//			float bias = (float) Math.pow(0.5f, delta);
//			
//			motion = bias*motion + (1-bias)*currentMotion;
//			
//			if(motion < sleepEpsilon) setAwake(false);
//			else if(motion > 10 * sleepEpsilon) motion = 10 * sleepEpsilon;
//		}
	}
	
	public void wake(){sleep = false; motion = sleepEpsilon + sleepEpsilon;}
	public void sleep(){sleep = true; velocity.x = 0; velocity.y = 0; velocity.z = 0; rotation.x = 0; rotation.y = 0; rotation.z = 0; motion = 0;}
}