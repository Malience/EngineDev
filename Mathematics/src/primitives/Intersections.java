package primitives;

import math.Vector3f;

public abstract class Intersections {
	
	//~~~~~~~~~~~CONTAINS~~~~~~~~~~~\\
	//The right primitive is completely contained within the left primitive
	
	public static boolean contains(Sphere s, Vector3f v) {return intersects(s, v.x, v.y, v.z);}
	public static boolean contains(Plane p, Sphere s) {return s.pos.dot(p.normal) < p.d - s.r;}
	
	//~~~~~~~~~~~INTERSECTION~~~~~~~~~~~\\
	//The two primitives intersect
	
	//Sphere vs Point
	public static boolean intersects(Sphere s, Vector3f v) {return intersectsSpherePoint(s.pos.x, s.pos.y, s.pos.z, s.r, v.x, v.y, v.z);}
	public static boolean intersects(Sphere s, float x, float y, float z) {return intersectsSpherePoint(s.pos.x, s.pos.y, s.pos.z, s.r, x, y, z);}
	public static boolean intersectsSpherePoint(Vector3f pos, float r0, Vector3f v) {return intersectsSpherePoint(pos.x, pos.y, pos.z, r0, v.x, v.y, v.z);}
	public static boolean intersectsSpherePoint(Vector3f pos, float r0, float x1, float y1, float z1) {return intersectsSpherePoint(pos.x, pos.y, pos.z, r0, x1, y1, z1);}
	public static boolean intersectsSpherePoint(float x0, float y0, float z0, float r0, Vector3f v) {return intersectsSpherePoint(x0, y0, z0, r0, v.x, v.y, v.z);}
	public static boolean intersectsSpherePoint(float x0, float y0, float z0, float r0, float x1, float y1, float z1) 
	{x0 -= x1; y0 -= y1; z0 -= z1; return x0 * x0 + y0 * y0 + z0 * z0 <= r0 * r0;}
	
	
	//Sphere vs Sphere
	public static boolean intersects(Sphere a, Sphere b) {return intersectsSphereSphere(a.pos.x, a.pos.y, a.pos.z, a.r, b.pos.x, b.pos.y, b.pos.z, b.r);}
	public static boolean intersectsSphereSphere(Sphere a, Vector3f pos1, float r1) {return intersectsSphereSphere(a.pos.x, a.pos.y, a.pos.z, a.r, pos1.x, pos1.y, pos1.z, r1);}
	public static boolean intersectsSphereSphere(Vector3f pos0, float r0, Sphere b) {return intersectsSphereSphere(pos0.x, pos0.y, pos0.z, r0, b.pos.x, b.pos.y, b.pos.z, b.r);}
	public static boolean intersectsSphereSphere(Vector3f pos0, float r0, Vector3f pos1, float r1) {return intersectsSphereSphere(pos0.x, pos0.y, pos0.z, r0, pos1.x, pos1.y, pos1.z, r1);}
	public static boolean intersectsSphereSphere(Vector3f pos0, float r0, float x1, float y1, float z1, float r1) {return intersectsSphereSphere(pos0.x, pos0.y, pos0.z, r0, x1, y1, z1, r1);}
	public static boolean intersectsSphereSphere(float x0, float y0, float z0, float r0, Vector3f pos1, float r1) {return intersectsSphereSphere(x0, y0, z0, r0, pos1.x, pos1.y, pos1.z, r1);}
	public static boolean intersectsSphereSphere(	float x0, float y0, float z0, float r0,
													float x1, float y1, float z1, float r1) {
		x0 -= x1; y0 -= y1; z0 -= z1; r0 += r1;
		return x0 * x0 + y0 * y0 + z0 * z0 <= r0 * r0;
	}

	public static boolean intersects(Sphere a, Capsule b) {
		float radius = a.r + b.r;
		return PrimitivesUtil.sqDistPointSegment(a.pos, b.a, b.b) <= radius * radius;
	}
	
	public static boolean intersects(Capsule a, Capsule b) {
		float radius = a.r + b.r;
		return PrimitivesUtil.sqDistSegmentSegment(a.a, a.b, b.a, b.b) <= radius * radius;
	}
	
	//public static boolean intersects(Sphere a, SSAABB b) {
	//	float radius = a.r + b.r;
	//	return PrimitivesUtil.sqDistPointAABB(b, a.pos) <= radius * radius;
	//}
	
	public static boolean intersects(OBB a, OBB b) {return intersectsOBBOBB(a.pos, a.xaxis, a.yaxis, a.hx, a.hy, a.hz, b.pos, b.xaxis, b.yaxis, b.hx, b.hy, b.hz);}
	public static boolean intersectsOBBOBB(Vector3f apos, Vector3f ax, Vector3f ay, float ahx, float ahy, float ahz, Vector3f bpos, Vector3f bx, Vector3f by, float bhx, float bhy, float bhz) {
		//TODO: OBB v OBB intersection
		return true;
	}
	
	public static boolean intersects(KDOP a, KDOP b) {return intersectsKDOPKDOP(a.max, a.min, b.max, b.min, Math.min(a.max.length, b.max.length));}
	public static boolean intersects(KDOP a, KDOP b, int hk) {return intersectsKDOPKDOP(a.max, a.min, b.max, b.min, hk);}
	public static boolean intersectsKDOPKDOP(float[] amax, float[] amin, float[] bmax, float[] bmin) {return intersectsKDOPKDOP(amax, amin, bmax, bmin, Math.min(amax.length, bmax.length));}
	public static boolean intersectsKDOPKDOP(float[] amax, float[] amin, float[] bmax, float[] bmin, int hk) {
		for(int i = 0; i < hk; i++) if (amin[i] > bmax[i] || amax[i] < bmin[i]) return false;
		return true;
	}
	
	
	//AABB vs AABB
	public static boolean intersects(AABB a, AABB b) {return intersectsAABBAABB(a.pos.x, a.pos.y, a.pos.z, a.hx, a.hy, a.hz, b.pos.x, b.pos.y, b.pos.z, b.hx, b.hy, b.hz);}
	public static boolean intersectsAABBAABB(AABB a, Vector3f p1, float hx1, float hy1, float hz1) 
	{return intersectsAABBAABB(a.pos.x, a.pos.y, a.pos.z, a.hx, a.hy, a.hz, p1.x, p1.y, p1.z, hx1, hy1, hz1);}
	public static boolean intersectsAABBAABB(AABB a, float x1, float y1, float z1, float hx1, float hy1, float hz1) 
	{return intersectsAABBAABB(a.pos.x, a.pos.y, a.pos.z, a.hx, a.hy, a.hz, x1, y1, z1, hx1, hy1, hz1);}
	public static boolean intersectsAABBAABB(Vector3f p0, float hx0, float hy0, float hz0, AABB b) 
	{return intersectsAABBAABB(p0.x, p0.y, p0.z, hx0, hy0, hz0, b.pos.x, b.pos.y, b.pos.z, b.hx, b.hy, b.hz);}
	public static boolean intersectsAABBAABB(float x0, float y0, float z0, float hx0, float hy0, float hz0, AABB b) 
	{return intersectsAABBAABB(x0, y0, z0, hx0, hy0, hz0, b.pos.x, b.pos.y, b.pos.z, b.hx, b.hy, b.hz);}
	public static boolean intersectsAABBAABB(Vector3f p0, float hx0, float hy0, float hz0, Vector3f p1, float hx1, float hy1, float hz1)
	{return intersectsAABBAABB(p0.x, p0.y, p0.z, hx0, hy0, hz0, p1.x, p1.y, p1.z, hx1, hy1, hz1);}
	public static boolean intersectsAABBAABB(Vector3f p0, float hx0, float hy0, float hz0, float x1, float y1, float z1, float hx1, float hy1, float hz1)
	{return intersectsAABBAABB(p0.x, p0.y, p0.z, hx0, hy0, hz0, x1, y1, z1, hx1, hy1, hz1);}
	public static boolean intersectsAABBAABB(float x0, float y0, float z0, float hx0, float hy0, float hz0, Vector3f p1, float hx1, float hy1, float hz1)
	{return intersectsAABBAABB(x0, y0, z0, hx0, hy0, hz0, p1.x, p1.y, p1.z, hx1, hy1, hz1);}
	public static boolean intersectsAABBAABB(	float x0, float y0, float z0, float hx0, float hy0, float hz0, 
												float x1, float y1, float z1, float hx1, float hy1, float hz1) {
		if(Math.abs(x0 - x1) > hx0 + hx1) return false;
		if(Math.abs(y0 - y1) > hy0 + hy1) return false;
		return Math.abs(z0 - z1) <= hz0 + hz1;
	}
	
	public static boolean intersects(Sphere s, Plane p) {return Math.abs(s.pos.dot(p.normal) - p.d) <= s.r;}
	public static boolean intersectsSphereHalfspace(Sphere s, Plane p) {return s.pos.dot(p.normal) <= p.d - s.r;}
	public static boolean intersectsSphereAABB(Sphere s, AABB a) {return PrimitivesUtil.sqDistPointAABB(a, s.pos) <= s.r * s.r;}
	
	public static boolean intersectsSphereAABB(Sphere s, AABB a, Vector3f out) {
		PrimitivesUtil.closestPtPointAABB(a, s.pos, out);
		float vx = out.x - s.pos.x, vy = out.y - s.pos.y, vz = out.z - s.pos.z;
		return vx * vx + vy * vy + vz * vz <= s.r * s.r;
	}
	
	public static boolean intersectsSegmentPlane(Vector3f a, Vector3f b, Plane p) {
		float bax = b.x - a.x, bay = b.y - a.y, baz = b.z - a.z;
		float t = p.d - p.normal.dot(a);
		
		return t >= 0f && t <= p.normal.dot(bax, bay, baz);
	}
	
	public static boolean intersectsSegmentPlane(Vector3f a, Vector3f b, Plane p, Vector3f out) {
		float bax = b.x - a.x, bay = b.y - a.y, baz = b.z - a.z;
		float na = p.normal.dot(a);
		float nba = p.normal.dot(bax, bay, baz);
		float t = p.d - na;
		
		return t >= 0f && t <= nba;
	}
}
