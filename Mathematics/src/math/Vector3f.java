package math;
import java.io.Serializable;
import java.util.Random;

public class Vector3f implements Serializable
{
	private transient static final long serialVersionUID = -2270634509087295007L;
	
	public static final Vector3f ZERO = new Vector3f(0,0,0);
	public static final Vector3f ONE = new Vector3f(1,1,1);
	public static final Vector3f UP = new Vector3f(0,1,0);
	public static final Vector3f DOWN = new Vector3f(0,1,0);
	public static final Vector3f LEFT = new Vector3f(-1,0,0);
	public static final Vector3f RIGHT = new Vector3f(1,0,0);
	public static final Vector3f FORWARD = new Vector3f(0,0,1);
	public static final Vector3f BACK = new Vector3f(0,0,-1);
	
	public float x, y, z;
	//Constructor
	public Vector3f(){}
	public Vector3f(float x, float y, float z){this.x = x; this.y = y; this.z = z;}
	public Vector3f(Vector3f r) {x = r.x; y = r.y; z = r.z;}
	
	
	//TODO: Stop instantiating
	//Basic Vector Functions
	public Vector3f normal(){float length = length();return new Vector3f(x / length, y / length, z / length);}
	public Vector3f normalize(){float length = 1.0f/length(); x *= length; y *= length; z *= length; return this;}
	public float distance(Vector3f r){return (float) Math.sqrt((x - r.x) * (x - r.x) + (y - r.y) * (y - r.y) + (z - r.z) * (z - r.z));}
	public float distanceSquared(Vector3f r){return (x - r.x) * (x - r.x) + (y - r.y) * (y - r.y) + (z - r.z) * (z - r.z);}
	public float length(){return (float)Math.sqrt(x * x + y * y + z * z);} //Also known as the magnitude
	public float lengthSquared(){return x * x + y * y + z * z;}
	public float magnitude(){return (float)Math.sqrt(x * x + y * y + z * z);} //Also known as the length
	public float magnitudeSquared(){return x * x + y * y + z * z;}
	public float max(){return x > y ? (x > z ? x : z) : (y > z ? y : z);}
	public Vector3f max(Vector3f r){return new Vector3f(x > r.x ? x : r.x, y > r.y ? y : r.y, z > r.z ? z : r.z);}
	public float min(){return x < y ? (x < z ? x : z) : (y < z ? y : z);}
	public Vector3f min(Vector3f r){return new Vector3f(x < r.x ? x : r.x, y < r.y ? y : r.y, z < r.z ? z : r.z);}
	public Vector3f cross(Vector3f r){return this.cross(r, this);}
	public Vector3f cross(Vector3f r, Vector3f out){float nx = y * r.z - z * r.y; float ny =  z * r.x - x * r.z; out.z = x * r.y - y * r.x; out.x = nx; out.y = ny; return out;}
	public Vector3f projection(Vector3f onto){return onto.mul(onto.dot(this)/onto.length());}

	//Basic Arithmetic Operations (Addition, Subtraction, Multiplication, Division)
	public Vector3f add(float r){x += r; y += r; z += r; return this;}
	public Vector3f addX(float r){x += r; return this;}
	public Vector3f addY(float r){y += r; return this;}
	public Vector3f addZ(float r){z += r; return this;}
	public Vector3f add(Vector3f r){x += r.x; y += r.y; z += r.z; return this;}
	public Vector3f add(float x, float y, float z){this.x += x; this.y += y; this.z += z; return this;}
	
	public Vector3f add(Vector3f r, Vector3f out){out.x = x + r.x; out.y = y + r.y; out.z = z + r.z; return out;}
	
	public Vector3f sub(float r){x -= r; y -= r; z -= r; return this;}
	public Vector3f subX(float r){x -= r; return this;}
	public Vector3f subY(float r){y -= r; return this;}
	public Vector3f subZ(float r){z -= r; return this;}
	public Vector3f sub(Vector3f r){x -= r.x; y -= r.y; z -= r.z; return this;}
	public Vector3f sub(Vector3f r, Vector3f out){out.x = x- r.x; out.y = y - r.y; out.z = z - r.z; return out;}
	public Vector3f sub(float x, float y, float z){this.x -= x; this.y -= y; this.z -= z; return this;}
	
	//~~~~~~~~~~~Something Else~~~~~~~~~~~\\
	
	public Vector3f mul(float r){x *= r; y *= r; z *= r; return this;}
	public Vector3f mul(Vector3f r){x *= r.x; y *= r.y; z *= r.z; return this;}
	public Vector3f mul(Vector3f r, Vector3f out){out.x = x * r.x; out.y = y * r.y; out.z = z * r.z; return out;}
	public Vector3f mul(float x, float y, float z){this.x *= x; this.y *= y; this.z *= z; return this;}
	public Vector3f mul(float r, Vector3f out){out.x = r * x; out.y = r * y; out.z = r * z; return out;}
	
	
	public Matrix3f mul(Matrix3f m, Matrix3f out) {
		out.m00 = m.m00 * x; out.m01 = m.m01 * x; out.m02 = m.m02 * x;
		out.m10 = m.m10 * y; out.m11 = m.m11 * y; out.m12 = m.m12 * y;
		out.m20 = m.m20 * z; out.m21 = m.m21 * z; out.m22 = m.m22 * z;
		return out;
	}
	
	public Vector3f div(float r){x /= r; y /= r; z /= r; return this;}
	public Vector3f div(float r, Vector3f out){out.x = x / r; out.y = y / r; out.z = z / r; return out;}
	public Vector3f div(Vector3f r){x /= r.x; y /= r.y; z /= r.z; return this;}
	public Vector3f div(float x, float y, float z){this.x /= x; this.y /= y; this.z /= z; return this;}
	public Vector3f div(Vector3f r, Vector3f out){out.x = x / r.x; out.y = y / r.y; out.z = z / r.z; return out;}
	
	//Basic Algebra Operations
	public Vector3f abs(){return new Vector3f(Math.abs(x), Math.abs(y), Math.abs(z));}
	public Vector3f floor(){return new Vector3f((float) Math.floor(x), (float) Math.floor(y), (float) Math.floor(z));}
	public Vector3f ceil(){return new Vector3f((float) Math.ceil(x), (float) Math.ceil(y), (float) Math.ceil(z));}
	
	//Rotations
	@Deprecated
	public Vector3f rotate(Vector3f axis, float angle){
		float sinAngle = (float)Math.sin(-angle);
		float cosAngle = (float)Math.cos(-angle);
		return this.cross(axis.mul(sinAngle)).add(           //Rotation on local X
				(this.mul(cosAngle)).add(                     //Rotation on local Z
						axis.mul(this.dot(axis.mul(1 - cosAngle))))); //Rotation on local Y
	}

//	public Vector3f rotate(Quaternion rotation, Vector3f out){
//		Quaternion w = rotation.mul(this, new Quaternion()).mul(-rotation.x, -rotation.y, -rotation.z);
//		out.x = w.x; out.y = w.y; out.z = w.z; return out;
//	}
	
	public Vector3f rotate(float yaw, float pitch){
		yaw = (float)Math.toRadians(yaw); pitch = (float)Math.toRadians(pitch);;
		x = (float)(Math.cos(yaw) * Math.cos(pitch)) ;
		y = (float)(Math.sin(pitch));
		z = -(float)(Math.sin(yaw) * Math.cos(pitch));
		return this;
	}
	
	//Basic Game Operations
	public Vector3f inverse(){x = 1.0f/x; y = 1.0f/y; z = 1.0f/z;return this;}
	public Vector3f zero(){x = 0; y = 0; z = 0; return this;}
	public Vector3f lerp(Vector3f r, float lerpFactor){return this.lerp(r, lerpFactor, this);}
	public Vector3f lerp(Vector3f r, float lerpFactor, Vector3f out){
		out.x = this.x + lerpFactor * (r.x - this.x);
		out.y = this.y + lerpFactor * (r.y - this.y);
		out.z = this.z + lerpFactor * (r.z - this.z);
		return out;
	}
	
	//Conversion to Vector2f Methods //Also known as Swizzling, which sounds amazing
	public Vector2f xy() { return new Vector2f(x, y); }
	public Vector2f yz() { return new Vector2f(y, z); }
	public Vector2f zx() { return new Vector2f(z, x); }
	public Vector2f yx() { return new Vector2f(y, x); }
	public Vector2f zy() { return new Vector2f(z, y); }
	public Vector2f xz() { return new Vector2f(x, z); }

	//Getters/Setters for values
	public Vector3f set(float x, float y, float z) { this.x = x; this.y = y; this.z = z; return this; }
	public Vector3f set(Vector3f r) { x = r.x; y = r.y; z = r.z; return this; }
	
	public void set(int i, float value){switch(i){case 0: this.x = value; break;case 1: this.y = value; break;case 2: this.z = value; break;}}
	public float get(int i) {switch(i) {case 0: return this.x;case 1: return this.y;case 2: return this.z;} return 0;}
	
	//Randomizer
	public static Vector3f randomVector(Vector3f minv, Vector3f maxv)
	{
		Random r = new Random();
		return new Vector3f(r.nextFloat()%(maxv.x - minv.x) + minv.x,
							r.nextFloat()%(maxv.y - minv.y) + minv.y,
							r.nextFloat()%(maxv.z - minv.z) + minv.z);
	}
	

	//Compare Methods
	
	public float clockwise(Vector3f v1, Vector3f v2) {return (v1.x - this.x) * (v2.y - this.y) - (v1.y - this.y) * (v2.x - this.x);} //Returns <0 if clockwise
	public float time(Vector3f v1, Vector3f v2) {return ((v1.x - this.x) * (v2.z - this.z) - (v2.x-this.x) * (v2.z - this.z)) / this.clockwise(v1,v2);} //when turn changes or something like that
	public boolean equals(Vector3f r){if(r == null) return false; return x == r.x && y == r.y && z == r.z;}
	public boolean equalsOr(Vector3f r){return x == r.x || y == r.y || z == r.z; }
	public int compareTo(Vector3f r){
		if(x < r.x && y < r.y && z < r.z)return -1;
		else if(x > r.x && y > r.y && z > r.z)return 1;
		return 0; //CAREFUL, this does not mean they are equal!!!1!!
	}
	//Basic Outputs
	public String toString(){return "(" + x + " " + y + " " + z + ")";}
	
	////~~~~~~~~~~~~~~~~~~YOU DON'T NEED TO MESS WITH ANYTHING BELOW THIS LINE!!!!!~~~~~~~~~~~~~~~~~~\\\\\\\\\\\\\\\
	
	public Vector3f negative() {x = -x; y = -y; z = -z; return this;}
	
	//~~~~~~~~~~~DOT PRODUCT~~~~~~~~~~\\
	public float dot() {return x * x + y * y + z * z;}
	public float dot(Vector3f r){return x * r.x + y * r.y + z * r.z;}
	public float dot(float x, float y, float z){return this.x * x + this.y * y + this.z * z;}
	public float dot(float x, float y, float z, float w){return this.x * x + this.y * y + this.z * z + w;}
	
	public static float dot(Vector3f v0, Vector3f v1) {return v0.x * v1.x + v0.y * v1.y + v0.z * v1.z;}
	public static float dot(Vector3f v0, float x1, float y1, float z1) {return v0.x * x1 + v0.y * y1 + v0.z * z1;}
	public static float dot(float x0, float y0, float z0, float x1, float y1, float z1) {return x0 * x1 + y0 * y1 + z0 * z1;}
	
	//~~~~~~~~~~~SCALED ADDITION~~~~~~~~~~~\\
	//TODO: REMOVE THE TRASH
//	public Vector3f addScaled(float scalea, Vector3f b) {x = x * scalea + b.x; y = y * scalea + b.y; z = z * scalea + b.z; return this;}
	public Vector3f addScaled(Vector3f b, float scale) {x = x + b.x * scale; y = y + b.y * scale; z = z + b.z * scale; return this;}
//	public Vector3f addScaled(float scalea, Vector3f b, float scaleb){x = x * scalea + b.x * scaleb; y = y * scalea + b.y * scaleb; z = z * scalea + b.z * scaleb; return this;}
	
//	public Vector3f addScaledA(float scalea, float bx, float by, float bz) {x = x * scalea + bx; y = y * scalea + by; z = z * scalea + bz; return this;}
	public Vector3f addScaled(float bx, float by, float bz, float scale) {x = x + bx * scale; y = y + by * scale; z = z + bz * scale; return this;}
//	public Vector3f addScaled(float scalea, float bx, float by, float bz, float scaleb)
//	{x = x * scalea + bx * scaleb; y = y * scalea + by * scaleb; z = z * scalea + bz * scaleb; return this;}
	
//	public Vector3f addScaled(float scalea, Vector3f b, Vector3f out) {out.x = x * scalea + b.x; out.y = y * scalea + b.y; out.z = z * scalea + b.z; return out;}
	public Vector3f addScaled(Vector3f b, float scale, Vector3f out) {out.x = x + b.x * scale; out.y = y + b.y * scale; out.z = z + b.z * scale; return out;}
//	public Vector3f addScaled(float scalea, Vector3f b, float scaleb, Vector3f out){out.x = x * scalea + b.x * scaleb; out.y = y * scalea + b.y * scaleb; out.z = z * scalea + b.z * scaleb; return out;}
	
//	public Vector3f addScaledA(float scalea, float bx, float by, float bz, Vector3f out) {out.x = x * scalea + bx; out.y = y * scalea + by; out.z = z * scalea + bz; return out;}
	public Vector3f addScaled(float bx, float by, float bz, float scale, Vector3f out) {out.x = x + bx * scale; out.y = y + by * scale; out.z = z + bz * scale; return out;}
//	public Vector3f addScaled(float scalea, float bx, float by, float bz, float scaleb, Vector3f out)
//	{out.x = x * scalea + bx * scaleb; out.y = y * scalea + by * scaleb; out.z = z * scalea + bz * scaleb; return out;}
	
//	public static Vector3f addScaled(Vector3f a, float scale, Vector3f b, Vector3f out) {out.x = a.x * scale + b.x; out.y = a.y * scale + b.y; out.z = a.z * scale + b.z; return out;}
	public static Vector3f addScaled(Vector3f a, Vector3f b, float scale, Vector3f out) {out.x = a.x + b.x * scale; out.y = a.y + b.y * scale; out.z = a.z + b.z * scale; return out;}
//	public static Vector3f addScaled(Vector3f a, float scalea, Vector3f b, float scaleb, Vector3f out) 
//	{out.x = a.x * scalea + b.x * scaleb; out.y = a.y * scalea + b.y * scaleb; out.z = a.z * scalea + b.z * scaleb; return out;}
	
//	public static Vector3f addScaledA(Vector3f a, float scale, float bx, float by, float bz, Vector3f out) {out.x = a.x * scale + bx; out.y = a.y * scale + by; out.z = a.z * scale + bz; return out;}
//	public static Vector3f addScaledA(float ax, float ay, float az, float scale, Vector3f b, Vector3f out) {out.x = ax * scale + b.x; out.y = ay * scale + b.y; out.z = az * scale + b.z; return out;}
//	public static Vector3f addScaledA(float ax, float ay, float az, float scale, float bx, float by, float bz, Vector3f out) 
//	{out.x = ax * scale + bx; out.y = ay * scale + by; out.z = az * scale + bz; return out;}
	
	public static Vector3f addScaled(Vector3f a, float bx, float by, float bz, float scale, Vector3f out) {out.x = a.x + bx * scale; out.y = a.y + by * scale; out.z = a.z + bz * scale; return out;}
	public static Vector3f addScaled(float ax, float ay, float az, Vector3f b, float scale, Vector3f out) {out.x = ax + b.x * scale; out.y = ay + b.y * scale; out.z = az + b.z * scale; return out;}
	public static Vector3f addScaled(float ax, float ay, float az, float bx, float by, float bz, float scale, Vector3f out) 
	{out.x = ax + bx * scale; out.y = ay + by * scale; out.z = az + bz * scale; return out;}
	
//	public static Vector3f addScaled(Vector3f a, float scalea, float bx, float by, float bz, float scaleb, Vector3f out) 
//	{out.x = a.x * scalea + bx * scaleb; out.y = a.y * scalea + by * scaleb; out.z = a.z * scalea + bz * scaleb; return out;}
//	public static Vector3f addScaled(float ax, float ay, float az, float scalea, Vector3f b, float scaleb, Vector3f out) 
//	{out.x = ax * scalea + b.x * scaleb; out.y = ay * scalea + b.y * scaleb; out.z = az * scalea + b.z * scaleb; return out;}
//	public static Vector3f addScaled(float ax, float ay, float az, float scalea, float bx, float by, float bz, float scaleb, Vector3f out) 
//	{out.x = ax * scalea + bx * scaleb; out.y = ay * scalea + by * scaleb; out.z = az * scalea + bz * scaleb; return out;}
		
	//~~~~~~~~~~~SCALED SUBTRACTION~~~~~~~~~~~\\
	
//	public Vector3f subScaled(float scalea, Vector3f b) {x = x * scalea - b.x; y = y * scalea - b.y; z = z * scalea - b.z; return this;}
	public Vector3f subScaled(Vector3f b, float scaleb) {x = x - b.x * scaleb; y = y - b.y * scaleb; z = z - b.z * scaleb; return this;}
//	public Vector3f subScaled(float scalea, Vector3f b, float scaleb){x = x * scalea - b.x * scaleb; y = y * scalea - b.y * scaleb; z = z * scalea - b.z * scaleb; return this;}
	
//	public Vector3f subScaledA(float scalea, float bx, float by, float bz) {x = x * scalea - bx; y = y * scalea - by; z = z * scalea - bz; return this;}
	public Vector3f subScaled(float bx, float by, float bz, float scale) {x = x - bx * scale; y = y - by * scale; z = z - bz * scale; return this;}
//	public Vector3f subScaled(float scalea, float bx, float by, float bz, float scaleb)
//	{x = x * scalea - bx * scaleb; y = y * scalea - by * scaleb; z = z * scalea - bz * scaleb; return this;}
	
//	public Vector3f subScaled(float scalea, Vector3f b, Vector3f out) {out.x = x * scalea - b.x; out.y = y * scalea - b.y; out.z = z * scalea - b.z; return out;}
	public Vector3f subScaled(Vector3f b, float scale, Vector3f out) {out.x = x - b.x * scale; out.y = y - b.y * scale; out.z = z - b.z * scale; return out;}
//	public Vector3f subScaled(float scalea, Vector3f b, float scaleb, Vector3f out){out.x = x * scalea - b.x * scaleb; out.y = y * scalea - b.y * scaleb; out.z = z * scalea - b.z * scaleb; return out;}
	
//	public Vector3f subScaledA(float scalea, float bx, float by, float bz, Vector3f out) {out.x = x * scalea - bx; out.y = y * scalea - by; out.z = z * scalea - bz; return out;}
	public Vector3f subScaled(float bx, float by, float bz, float scale, Vector3f out) {out.x = x - bx * scale; out.y = y - by * scale; out.z = z - bz * scale; return out;}
//	public Vector3f subScaled(float scalea, float bx, float by, float bz, float scaleb, Vector3f out)
//	{out.x = x * scalea - bx * scaleb; out.y = y * scalea - by * scaleb; out.z = z * scalea - bz * scaleb; return out;}
	
//	public static Vector3f subScaled(Vector3f a, float scale, Vector3f b, Vector3f out) {out.x = a.x * scale - b.x; out.y = a.y * scale - b.y; out.z = a.z * scale - b.z; return out;}
	public static Vector3f subScaled(Vector3f a, Vector3f b, float scale, Vector3f out) {out.x = a.x - b.x * scale; out.y = a.y - b.y * scale; out.z = a.z - b.z * scale; return out;}
//	public static Vector3f subScaled(Vector3f a, float scalea, Vector3f b, float scaleb, Vector3f out) 
//	{out.x = a.x * scalea - b.x * scaleb; out.y = a.y * scalea - b.y * scaleb; out.z = a.z * scalea - b.z * scaleb; return out;}
	
//	public static Vector3f subScaledA(Vector3f a, float scale, float bx, float by, float bz, Vector3f out) {out.x = a.x * scale - bx; out.y = a.y * scale - by; out.z = a.z * scale - bz; return out;}
//	public static Vector3f subScaledA(float ax, float ay, float az, float scale, Vector3f b, Vector3f out) {out.x = ax * scale - b.x; out.y = ay * scale - b.y; out.z = az * scale - b.z; return out;}
//	public static Vector3f subScaledA(float ax, float ay, float az, float scale, float bx, float by, float bz, Vector3f out) 
//	{out.x = ax * scale - bx; out.y = ay * scale - by; out.z = az * scale - bz; return out;}
	
	public static Vector3f subScaled(Vector3f a, float bx, float by, float bz, float scale, Vector3f out) {out.x = a.x - bx * scale; out.y = a.y - by * scale; out.z = a.z - bz * scale; return out;}
	public static Vector3f subScaled(float ax, float ay, float az, Vector3f b, float scale, Vector3f out) {out.x = ax - b.x * scale; out.y = ay - b.y * scale; out.z = az - b.z * scale; return out;}
	public static Vector3f subScaled(float ax, float ay, float az, float bx, float by, float bz, float scale, Vector3f out) 
	{out.x = ax - bx * scale; out.y = ay - by * scale; out.z = az - bz * scale; return out;}
	
//	public static Vector3f subScaled(Vector3f a, float scalea, float bx, float by, float bz, float scaleb, Vector3f out) 
//	{out.x = a.x * scalea - bx * scaleb; out.y = a.y * scalea - by * scaleb; out.z = a.z * scalea - bz * scaleb; return out;}
//	public static Vector3f subScaled(float ax, float ay, float az, float scalea, Vector3f b, float scaleb, Vector3f out) 
//	{out.x = ax * scalea - b.x * scaleb; out.y = ay * scalea - b.y * scaleb; out.z = az * scalea - b.z * scaleb; return out;}
//	public static Vector3f subScaled(float ax, float ay, float az, float scalea, float bx, float by, float bz, float scaleb, Vector3f out) 
//	{out.x = ax * scalea - bx * scaleb; out.y = ay * scalea - by * scaleb; out.z = az * scalea - bz * scaleb; return out;}
}