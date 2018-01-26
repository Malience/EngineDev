package primitives;

import math.Vector3f;

public abstract class PrimitivesUtil {
	
	public static float sqDistPointSegment(Vector3f p, Vector3f a, Vector3f b) {return sqDistPointSegment(p.x, p.y, p.z, a.x, a.y, a.z, b.x, b.y, b.z);}
	public static float sqDistPointSegment(float px, float py, float pz, float ax, float ay, float az, float bx, float by, float bz) {
		float mx = bx - ax, my = by - ay, mz = bz - az;
		float pax = px - ax, pay = py - ay, paz = pz - az; 
		float m_pa = mx * pax + my * pay + mz * paz;
		
		if(m_pa <= 0) return pax * pax + pay * pay + paz * paz;
		
		float mm = mx * mx + my * my + mz * mz;
		if(m_pa >= mm) {
			float pamx = pax - mx, pamy = pay - my, pamz = paz - mz;
			return pamx * pamx + pamy * pamy + pamz * pamz;
		}
		
		float t0 = m_pa / mm;
		float pamx = pax - mx * t0, pamy = pay - my * t0, pamz = paz - mz * t0;
		
		return pamx * pamx + pamy * pamy + pamz * pamz;
	}
	
	public static float sqDistPointRay(Vector3f p, Vector3f a, Vector3f b) {return sqDistPointRay(p.x, p.y, p.z, a.x, a.y, a.z, b.x, b.y, b.z);}
	public static float sqDistPointRay(float px, float py, float pz, float ax, float ay, float az, float bx, float by, float bz) {
		float mx = bx - ax, my = by - ay, mz = bz - az;
		float pax = px - ax, pay = py - ay, paz = pz - az; 
		float m_pa = mx * pax + my * pay + mz * paz;
		
		if(m_pa <= 0) return pax * pax + pay * pay + paz * paz;
		
		float t0 = m_pa / (mx * mx + my * my + mz * mz);
		float pamx = pax - mx * t0, pamy = pay - my * t0, pamz = paz - mz * t0;
		
		return pamx * pamx + pamy * pamy + pamz * pamz;
	}
	
	public static float sqDistPointLine(Vector3f p, Vector3f a, Vector3f b) {return sqDistPointLine(p.x, p.y, p.z, a.x, a.y, a.z, b.x, b.y, b.z);}
	public static float sqDistPointLine(float px, float py, float pz, float ax, float ay, float az, float bx, float by, float bz) {
		float mx = bx - ax, my = by - ay, mz = bz - az;
		float pax = px - ax, pay = py - ay, paz = pz - az; 
		float t0 = (mx * pax + my * pay + mz * paz) / (mx * mx + my * my + mz * mz);
		float pamx = pax - mx * t0, pamy = pay - my * t0, pamz = paz - mz * t0;
		return pamx * pamx + pamy * pamy + pamz * pamz;
	}
	
	public static float sqDistSegmentSegment(Vector3f a0, Vector3f b0, Vector3f a1, Vector3f b1) {return sqDistSegmentSegment(a0.x, a0.y, a0.z, b0.x, b0.y, b0.z, a1.x, a1.y, a1.z, b1.x, b1.y, b1.z);}
	public static float sqDistSegmentSegment(float a0x, float a0y, float a0z, float b0x, float b0y, float b0z, float a1x, float a1y, float a1z, float b1x, float b1y, float b1z) {
		float b0b1x = b0x - b1x, b0b1y = b0y - b1y, b0b1z = b0z - b1z;
		
		float m0x = b0x - a0x, m0y = b0y - a0y, m0z = b0z - a0z;
		float m1x = b1x - a1x, m1y = b1y - a1y, m1z = b1z - a1z;
		
		float a = m0x * m0x + m0y * m0y + m0z * m0z;
		float b = -(m0x * m1x + m0y * m1y + m0z * m1z);
		float c = m1x * m1x + m1y * m1y + m1z * m1z;
		float d = m0x * b0b1x + m0y * b0b1y + m0z * b0b1z;
		float e = -(m1x * b0b1x + m1y * b0b1y + m1z * b0b1z);
		float f = b0b1x * b0b1x + b0b1y * b0b1y + b0b1z * b0b1z;
		
		float det = a * c - b * b;
		float s = 0, t = 0;
		//Not Parallel
		if(det > 0.00000001f) {
			s = b * e - c * d;
			t = b * d - a * e;
			
			if(s >= 0) {
				if(s <= det) {
					if(t >= 0) {
						if(t <= det) {
							//region 0
							float invdet = 1f / det;
							s *= invdet; t *= invdet;
						} else {
							//region 3
							s = 1; float tmp = b+e; t = tmp > 0 ? 0 : (-tmp > c ? 1 : -tmp / c);
						}
					} else {
						//region 7
						s = 1; float tmp = b+e; t = tmp > 0 ? 0 : (-tmp > c ? 1 : -tmp / c);
					}
				} else {
					if(t >= 0) {
						if(t <= det) {
							//region 1
							s = 1; float tmp = b+e; t = tmp > 0 ? 0 : (-tmp > c ? 1 : -tmp / c);
						} else {
							//region 2
							float tmp = b + d;
							if(-tmp < a) { t = 1; s = tmp > 0 ? 0 : -tmp / a; }
							else { s = 1; tmp = b + e; t = -tmp < c ? (tmp > 0 ? 0f : -tmp / c) : 1f; }
						}
					} else {
						//region 8
						float tmp = b + d;
						if(-tmp < a) { t = 1; s = tmp > 0 ? 0 : -tmp / a; }
						else { s = 1; tmp = b + e; t = -tmp < c ? (tmp > 0 ? 0f : -tmp / c) : 1f; }
					}
				}
			} else {
				if(t >= 0) {
					if(t <= det) {
						//region 5
						s = 1; float tmp = b+e; t = tmp > 0 ? 0 : (-tmp > c ? 1 : -tmp / c);
					} else {
						//region 4
						float tmp = b + d;
						if(-tmp < a) { t = 1; s = tmp > 0 ? 0 : -tmp / a; }
						else { s = 1; tmp = b + e; t = -tmp < c ? (tmp > 0 ? 0f : -tmp / c) : 1f; }
					}
				} else {
					//region 6
					float tmp = b + d;
					if(-tmp < a) { t = 1; s = tmp > 0 ? 0 : -tmp / a; }
					else { s = 1; tmp = b + e; t = -tmp < c ? (tmp > 0 ? 0f : -tmp / c) : 1f; }
				}
			}
		} else { //Parallel
			if(b > 0) {
				if(d >= 0) { s = t = 0; }
				else if(-d <= a) { s = -d / a; t = 0; }
				else {
					float tmp = a + d;
					t = -tmp >= b ? 1 : -tmp / b;
				}
			} else {
				if(-d >= a) { s = 1; t = 0; }
				else if(d <= 0) { s = -d / a; t = 0; }
				else t = d >= -b ? 1 : -d / b;
			}
		}
		
		return a * s * s + c * t * t + 2 * (s * (b + d) + e * t) + f;
		
		//return a * s * s + 2 * b * s * t + c * t * t + 2 * d * s + 2 * e * t + f;
	}
	
	public static float distPointPlane(Vector3f a, Plane b) {return distPointPlane(a.x, a.y, a.z, b.normal.x, b.normal.y, b.normal.z, b.d);}
	public static float distPointPlane(Vector3f p, Vector3f n, float d) {return distPointPlane(p.x, p.y ,p.z, n.x, n.y, n.z, d);}
	public static float distPointPlane(float px, float py, float pz, float nx, float ny, float nz, float d) {return px * nx + py * ny + pz * nz - d;}
	
	public static float sqDistPointAABB(AABB a, Vector3f b) {return sqDistPointAABB(a, b.x, b.y, b.z);}
	public static float sqDistPointAABB(AABB a, float x, float y, float z) {
		float out = 0f;
		if(x < a.pos.x - a.hx) {float tmp = a.pos.x - a.hx - x; out += tmp * tmp;}
		else if(x > a.pos.x + a.hx) {float tmp = x - a.pos.x - a.hx; out += tmp * tmp;}
		
		if(y < a.pos.y - a.hy) {float tmp = a.pos.y - a.hy - y; out += tmp * tmp;}
		else if(y > a.pos.y + a.hy) {float tmp = y - a.pos.y - a.hy; out += tmp * tmp;}
		
		if(z < a.pos.z - a.hz) {float tmp = a.pos.z - a.hz - z; out += tmp * tmp;}
		else if(z > a.pos.z + a.hz) {float tmp = z - a.pos.z - a.hz; out += tmp * tmp;}
		
		return out;
	}
	
	public static Vector3f closestPtPointSegment(Vector3f p, Vector3f a, Vector3f b, Vector3f out) {return closestPtPointSegment(p.x, p.y, p.z, a.x, a.y, a.z, b.x, b.y, b.z, out);}
	public static Vector3f closestPtPointSegment(float px, float py, float pz, float ax, float ay, float az, float bx, float by, float bz, Vector3f out) {
		float bax = bx - ax, bay = by - ay, baz = bz - az;
		float cax = px - ax, cay = py - ay, caz = pz - az;
		float t = cax * bax + cay * bay + caz * baz;
		if(t <= 0f) return out.set(ax, ay, az);
		float denom = bax * bax + bay * bay + baz * baz;
		if(t >= denom) return out.set(bx, by, bz);
		return Vector3f.addScaled(ax, ay, az, bax, bay, baz, t, out);
	}
	
	public static final float EPSILON = 0.0000001f;
	
	public static float closestPtPointSegment(Vector3f p0, Vector3f q0, Vector3f p1, Vector3f q1, Vector3f out0, Vector3f out1) {
		
		float d0x = q0.x - p0.x, d0y = q0.y - p0.y, d0z = q0.z - p0.z;
		float d1x = q1.x - p1.x, d1y = q1.y - p1.y, d1z = q1.z - p1.z;
		float rx = p0.x - p1.x, ry = p0.y - p1.y, rz = p0.z - p1.z;
		
		float a = d0x * d0x + d0y * d0y + d0z * d0z;
		float e = d1x * d1x + d1y * d1y + d1z * d1z;
		float f = d1x * rx + d1y * ry + d1z * rz;
		
		float s = 0f, t = 0f;
		if(a <= EPSILON && e <= EPSILON) {out0 = p0; out1 = p1; return out0.distanceSquared(out1);}
		if(a <= EPSILON) t = f <= 0f ? 0f : (f >= e ? 1.0f : f / e);
		else {
			float c = d1x * rx + d1y * ry + d1z * rz;
			if(e <= EPSILON) s = -c <= 0f ? 0f : (-c >= a ? 1f : -c / a);
			else {
				float b = d0x * d1x + d0y * d1y + d0z + d1z;
				float denom = a * e - b * b;
				if(denom != 0f) {s = b * f - c * e; s = s <= 0f ? 0f : (s >= denom ? 1f : s / denom);}
				else s = 0f;
				
				t = b * s + f;
				
				if(t <= 0f) {
					t = 0f;
					s = -c <= 0f ? 0f : (-c >= a ? 1f : -c / a);
				} else if(t >= e) {
					t = 1f;
					s = b - c;
					s = s <= 0f ? 0f : (s >= a ? 1.0f : s / a);
				} else t /= e;
			}
		}
		
		Vector3f.addScaled(p0, d0x, d0y, d0z, s, out0);
		Vector3f.addScaled(p1, d1x, d1y, d1z, t, out1);
		return out0.distanceSquared(out1);
	}
	
	public static Vector3f closestPtPointPlane(Vector3f a, Plane b, Vector3f out) {return closestPtPointPlane(a.x, a.y, a.z, b.normal.x, b.normal.y, b.normal.z, b.d, out);}
	public static Vector3f closestPtPointPlane(Vector3f p, Vector3f n, float d, Vector3f out) {return closestPtPointPlane(p.x, p.y ,p.z, n.x, n.y, n.z, d, out);}
	public static Vector3f closestPtPointPlane(float px, float py, float pz, float nx, float ny, float nz, float d, Vector3f out) {
		float t = px * nx + py * ny + pz * nz - d;
		return Vector3f.subScaled(px, py, pz, nx, ny, nz, t, out);
	}
	
	public static Vector3f closestPtPointAABB(AABB a, Vector3f b, Vector3f out) {return closestPtPointAABB(a, b.x, b.y, b.z, out);}
	public static Vector3f closestPtPointAABB(AABB a, float x, float y, float z, Vector3f out) {
		out.x = Math.min(Math.max(x, a.pos.x - a.hx), a.pos.x + a.hx);
		out.y = Math.min(Math.max(y, a.pos.y - a.hy), a.pos.y + a.hy);
		out.z = Math.min(Math.max(z, a.pos.z - a.hz), a.pos.z + a.hz);
		return out;
	}
	
	//dot product 	0x * 1x + 0y * 1y + 0z * 1z
	//dot product 	0x * 0x + 0y * 0y + 0z * 0z
	//sub         	namex = 0x - 1x, namey = 0y - 1y, namez = 0z - 1z
	
	public static void main(String [] args) {
		Vector3f point = new Vector3f(0,5,80f);
		Vector3f sega = new Vector3f(0,0,0), segb = new Vector3f(0,0,2);
		
		System.out.println(sqDistPointLine(point, sega, segb));
	}
}
