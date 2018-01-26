package com.base.engine.physics;

import java.util.Stack;

import math.Vector3f;

public class ContactResolver {
	protected static int velocityIterations;
	protected static int positionIterations;
	protected static float velocityEpsilon = 0.001f;
	protected static float positionEpsilon = 0.001f;;
	public static int velocityIterationsUsed;
	public static int positionIterationsUsed;
	//TODO valid Settings
	//private boolean validSettings;
	
	
	public static boolean isValid() { return (velocityIterations > 0) && (positionEpsilon >= 0.0f) && (positionEpsilon >= 0.0f); }
	
	public static void setIterations(int iterations){ setIterations(iterations, iterations); }
	public static void setIterations(int velocityIterations, int positionIterations) {
		ContactResolver.velocityIterations = velocityIterations;
		ContactResolver.positionIterations = positionIterations;
	}
	
	public static void setEpsilon(float velocityEpsilon, float positionEpsilon) {
		ContactResolver.velocityEpsilon = velocityEpsilon;
		ContactResolver.positionEpsilon = positionEpsilon;
	}
	
	public static void resolveContacts(Contact[] contacts, int start, int end, float delta) {
		if(!isValid()) return;
			
		prepareContacts(contacts, start, end, delta);
		
		adjustPositions(contacts, start, end, delta);
		
		adjustVelocities(contacts, start, end, delta);
	}
	
	public static void prepareContacts(Contact[] contacts, int start, int end, float delta) { for(int i = start; i < end; i++) contacts[i].calculateInternals(delta); }
	
	static Vector3f linearChangeA = new Vector3f(), linearChangeB = new Vector3f();
	static Vector3f angularChangeA = new Vector3f(), angularChangeB = new Vector3f();
	static Vector3f deltaValue = new Vector3f();
	
	protected static void adjustPositions(Contact[] contacts, int start, int end, float delta) {	
		int i, index;
		float max;
		
		positionIterationsUsed = 0;
		while(positionIterationsUsed < positionIterations) {
			max = positionEpsilon;
			index = end;
			for(i = start; i < end; i++) {
				if(contacts[i].penetration > max) {
					max = contacts[i].penetration;
					index = i;
				}
			}
			if(index == end) break;
			
			contacts[index].matchAwakeState();
			
			contacts[index].applyPositionChange(linearChangeA, linearChangeB, angularChangeA, angularChangeB, max);
			
			for(i = start; i < end; i++) {
				if(contacts[i].a == contacts[index].a)
					contacts[i].penetration -= angularChangeA.cross(contacts[i].relativeContactPositionA, deltaValue).add(linearChangeA).dot(contacts[i].normal);
				if(contacts[i].b == contacts[index].a)
					contacts[i].penetration += angularChangeA.cross(contacts[i].relativeContactPositionB, deltaValue).add(linearChangeA).dot(contacts[i].normal);
				if(contacts[i].a == contacts[index].b)
					contacts[i].penetration -= angularChangeB.cross(contacts[i].relativeContactPositionA, deltaValue).add(linearChangeB).dot(contacts[i].normal);
				if(contacts[index].b != null && contacts[i].b == contacts[index].b)
					contacts[i].penetration += angularChangeB.cross(contacts[i].relativeContactPositionB, deltaValue).add(linearChangeB).dot(contacts[i].normal);
			}
			positionIterationsUsed++;
		}
		if(positionIterationsUsed == positionIterations) System.out.println("Maxing Position Iterations");
	}
	
	protected static void adjustVelocities(Contact contacts[], int start, int end, float delta) {
		velocityIterationsUsed = 0;
		while(velocityIterationsUsed < velocityIterations) {
			float max = velocityEpsilon;
			int index = end;
			for(int i = start; i < end; i++) {
				if(contacts[i].desiredDeltaVelocity > max) {
					max = contacts[i].desiredDeltaVelocity;
					index = i;
				}
			}
			if(index == end) break;
			
			contacts[index].matchAwakeState();
			
			contacts[index].applyVelocityChange(linearChangeA, linearChangeB, angularChangeA, angularChangeB);
			
			for(int i = start; i < end; i++) {
				if(contacts[i].a == contacts[index].a) {	
					contacts[i].contactVelocity.add(contacts[i].contactToWorld.transformTranspose(angularChangeA.cross(contacts[i].relativeContactPositionA, deltaValue).add(linearChangeA)));
					contacts[i].calculateDesiredDeltaVelocity(delta);
				}
				if(contacts[i].b == contacts[index].a) {
					contacts[i].contactVelocity.sub(contacts[i].contactToWorld.transformTranspose(angularChangeA.cross(contacts[i].relativeContactPositionB, deltaValue).add(linearChangeA)));
					contacts[i].calculateDesiredDeltaVelocity(delta);
				}
				if(contacts[i].a == contacts[index].b) {
					contacts[i].contactVelocity.add(contacts[i].contactToWorld.transformTranspose(angularChangeB.cross(contacts[i].relativeContactPositionA, deltaValue).add(linearChangeB)));
					contacts[i].calculateDesiredDeltaVelocity(delta);
				}
				if(contacts[index].b != null && contacts[i].b == contacts[index].b) {
					contacts[i].contactVelocity.sub(contacts[i].contactToWorld.transformTranspose(angularChangeB.cross(contacts[i].relativeContactPositionB, deltaValue).add(linearChangeB)));
					contacts[i].calculateDesiredDeltaVelocity(delta);
				}
			}
			velocityIterationsUsed++;
		}
		if(velocityIterationsUsed == velocityIterations) System.out.println("Maxing Velocity Iterations");
		
	}
}
