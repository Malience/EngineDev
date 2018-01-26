package math;

import java.io.Serializable;

public class Quaternion implements Serializable {
	private transient static final long serialVersionUID = -6314861404774056705L;
	public float x, y, z, w;

	//~~~~~~~~~~~CONSTRUCTOR~~~~~~~~~~~\\
	
	public Quaternion(){w = 1.0f;}
	public Quaternion(float x, float y, float z){this.x = x; this.y = y; this.z = z; w = 1.0f;}
	//public Quaternion(float x, float y, float z, float w){this.x = x;this.y = y;this.z = z;this.w = w;}

	public Quaternion(Vector3f axis, float angle){this(axis.x, axis.y, axis.z, angle);}
	public Quaternion(float x, float y, float z, float angle){
		angle *= 0.5f;
		float sinHalfAngle = (float)Math.sin(angle);

		this.x = x * sinHalfAngle;
		this.y = y * sinHalfAngle;
		this.z = z * sinHalfAngle;
		this.w = (float)Math.cos(angle);
	}
	
	//~~~~~~~~~~~MULTIPLICATION~~~~~~~~~~~\\
	
	public Quaternion mul(Quaternion q) {return mul(this, q.x, q.y, q.z, q.w, this);}
	public Quaternion mul(Quaternion q, Quaternion out) {return mul(this, q.x, q.y, q.z, q.w, out);}
	public Quaternion mul(float x, float y, float z, float w) {return mul(this, x, y, z, w, this);}
	public Quaternion mul(float x, float y, float z, float w, Quaternion out) {return mul(this, x, y, z, w, out);}
	//public static Quaternion mul(Quaternion q, Quaternion q2) {return mul(q, q2.x, q2.y, q2.z, q2.w, q);}
	public static Quaternion mul(Quaternion q, Quaternion q2, Quaternion out) {return mul(q, q2.x, q2.y, q2.z, q2.w, out);}
	public static Quaternion mul(Quaternion q, float x, float y, float z, float w) {return mul(q, x, y, z, w, q);}
	public static Quaternion mul(Quaternion q, float x, float y, float z, float w, Quaternion out) {
		float nx = q.w * x + q.x * w + q.y * z - q.z * y;
		float ny = q.w * y - q.x * z + q.y * w + q.z * x;
		float nz = q.w * z + q.x * y - q.y * x + q.z * w;
		out.w =  q.w * w - q.x * x - q.y * y - q.z * z;
		out.x = nx; out.y = ny; out.z = nz;
		return out;
	}
	
	//~~~~~~~~~~~MULTIPLICATION~~~~~~~~~~~\\
	
	public Vector3f transform(Vector3f v) {return transform(this, v.x, v.y, v.z, v);}
	public Vector3f transform(Vector3f v, Vector3f out) {return transform(this, v.x, v.y, v.z, out);}
	public static Vector3f transform(Quaternion q, Vector3f v) {return transform(q, v.x, v.y, v.z, v);}
	public static Vector3f transform(Quaternion q, Vector3f v, Vector3f out) {return transform(q, v.x, v.y, v.z, out);}
	public static Vector3f transform(Quaternion q, float x, float y, float z, Vector3f out) {
        float w2 = q.w * q.w;
        float x2 = q.x * q.x;
        float y2 = q.y * q.y;
        float z2 = q.z * q.z;
        float zw = q.z * q.w;
        float xy = q.x * q.y;
        float xz = q.x * q.z;
        float yw = q.y * q.w;
        float yz = q.y * q.z;
        float xw = q.x * q.w;
        float m00 = w2 + x2 - z2 - y2;
        float m01 = xy + zw + zw + xy;
        float m02 = xz - yw + xz - yw;
        float m10 = -zw + xy - zw + xy;
        float m11 = y2 - z2 + w2 - x2;
        float m12 = yz + yz + xw + xw;
        float m20 = yw + xz + xz + yw;
        float m21 = yz + yz - xw - xw;
        float m22 = z2 - y2 - x2 + w2;
        out.x = m00 * x + m10 * y + m20 * z;
        out.y = m01 * x + m11 * y + m21 * z;
        out.z = m02 * x + m12 * y + m22 * z;
        return out;
    }
	
	//~~~~~~~~~~~CONJUGATE~~~~~~~~~~~\\
	
	public Quaternion conjugate() {x = -x; y = -y; z = -z; return this;}
	public Quaternion conjugate(Quaternion out) {out.x = -x; out.y = -y; out.z = -z; out.w = w; return out;}
	
	public Quaternion normalize() {
		float length = (float) (1.0 / Math.sqrt(x * x + y * y + z * z + w * w));
        x *= length; y *= length; z *= length; w *= length;
        return this;
	}
	
	public Quaternion rotate(Vector3f r) {return rotate(r.x, r.y, r.z);}
	
	public Quaternion rotate(float angleX, float angleY, float angleZ) {
        float sx = (float) Math.sin(angleX * 0.5f);
        float cx = (float) Math.cos(angleX * 0.5f);
        float sy = (float) Math.sin(angleY * 0.5f);
        float cy = (float) Math.cos(angleY * 0.5f);
        float sz = (float) Math.sin(angleZ * 0.5f);
        float cz = (float) Math.cos(angleZ * 0.5f);

        float cycz = cy * cz;
        float sysz = sy * sz;
        float sycz = sy * cz;
        float cysz = cy * sz;
        float w = cx*cycz - sx*sysz;
        float x = sx*cycz + cx*sysz;
        float y = cx*sycz - sx*cysz;
        float z = cx*cysz + sx*sycz;
        // right-multiply
        float nx = this.w * x + this.x * w + this.y * z - this.z * y;
        float ny = this.w * y - this.x * z + this.y * w + this.z * x;
        float nz = this.w * z + this.x * y - this.y * x + this.z * w;
        float nw = this.w * w - this.x * x - this.y * y - this.z * z;
        
        this.x = nx; this.y = ny; this.z = nz; this.w = nw;
        return this;
    }
	
	public Quaternion rotate(float angleX, float angleY, float angleZ, float scale) {
        float sx = (float) Math.sin(angleX * scale * 0.5f);
        float cx = (float) Math.cos(angleX * scale * 0.5f);
        float sy = (float) Math.sin(angleY * scale * 0.5f);
        float cy = (float) Math.cos(angleY * scale * 0.5f);
        float sz = (float) Math.sin(angleZ * scale * 0.5f);
        float cz = (float) Math.cos(angleZ * scale * 0.5f);

        float cycz = cy * cz;
        float sysz = sy * sz;
        float sycz = sy * cz;
        float cysz = cy * sz;
        float w = cx*cycz - sx*sysz;
        float x = sx*cycz + cx*sysz;
        float y = cx*sycz - sx*cysz;
        float z = cx*cysz + sx*sycz;
        // right-multiply
        float nx = this.w * x + this.x * w + this.y * z - this.z * y;
        float ny = this.w * y - this.x * z + this.y * w + this.z * x;
        float nz = this.w * z + this.x * y - this.y * x + this.z * w;
        float nw = this.w * w - this.x * x - this.y * y - this.z * z;
        
        this.x = nx; this.y = ny; this.z = nz; this.w = nw;
        return this;
    }
	
	public Quaternion rotateAngularVelocity(Vector3f r, float scale) {
		float nx = w * r.x + x * 0f + y * r.z - z * r.y;
		float ny = w * r.y + y * 0f + z * r.x - x * r.z;
		float nz = w * r.z + z * 0f + x * r.y - y * r.x;
		float nw = w * 0f - x * r.x - y * r.y - z * r.z;
		scale *= 0.5f;
		x += scale * nx;
		y += scale * ny;
		z += scale * nz;
		w += scale * nw;
		return this;
	}
	
	//public Quaternion addScaledVector(Vector3f r, float scale){return addScaledVector(r.x, r.y, r.z, scale);}
//	public Quaternion addScaledVector(float x, float y, float z, float scale){
//		float w_ = - x * scale * this.x - y * scale * this.y -  z * scale * this.z * scale;
//		float x_ = x * scale * this.w + y * scale * this.z * scale -  z * scale * this.y;
//		float y_ = y * scale * this.w + z * scale * this.x - x * scale * this.z * scale;
//		float z_ =  z * scale * this.w + x * scale * this.y - y * scale * this.x;
//		
//		this.w += w_ * 0.5f; this.x += x_ * 0.5f; this.y += y_ * 0.5f; this.z += z_ * 0.5f;
//		return this;
//	}
	
	public Quaternion addScaledVector(Vector3f r, float scale) {
		Quaternion q = new Quaternion(r.x * scale, r.y * scale, r.z * scale, 0);
		
		q.mul(this);
		
		w += q.w * .5f;
		x += q.x * .5f;
		y += q.y * .5f;
		z += q.z * .5f;
		return this;
	}
	
	/*
	public Quaternion setEuler(Vector3f e){return euler(e.x,e.y,e.z, this);}
	public Quaternion setEuler(float x, float y, float z){return euler(x,y,z,this);}
	public Quaternion setEulerDegrees(float x, float y, float z){return euler(x*DEG_TO_RAD,y*DEG_TO_RAD,z*DEG_TO_RAD,this);}
	public static Quaternion euler(Vector3f e){return euler(e.x,e.y,e.z, new Quaternion());}
	public static Quaternion euler(Vector3f e, Quaternion out){return euler(e.x,e.y,e.z, out);}
	public static Quaternion euler(float x, float y, float z){return euler(x,y,z, new Quaternion());}
	public static Quaternion eulerDegrees(float x, float y, float z){return euler(x*DEG_TO_RAD,y*DEG_TO_RAD,z*DEG_TO_RAD, new Quaternion());}
	public static Quaternion eulerDegrees(float x, float y, float z, Quaternion out){return euler(x*DEG_TO_RAD,y*DEG_TO_RAD,z*DEG_TO_RAD, out);}
	public static Quaternion euler(float x, float y, float z, Quaternion out){
		x *= 0.5f; y *= 0.5f; z *= 0.5f;
		float c1 = (float)Math.cos(y), c2 = (float)Math.cos(z), c3 = (float)Math.cos(x);
		float s1 = (float)Math.sin(y), s2 = (float)Math.sin(z), s3 = (float)Math.sin(x);
		out.w = c1 * c2 * c3 - s1 * s2 * s3; out.x = c1 * c2 * s3 + s1 * s2 * c3;
		out.y = s1 * c2 * c3 + c1 * s2 * s3; out.z = c1 * s2 * c3 - s1 * c2 * s3;
		return out;
	}
	
	@Deprecated
	public Quaternion(Matrix4f rot)
	{
		float trace = rot.get(0, 0) + rot.get(1, 1) + rot.get(2, 2);

		if(trace > 0)
		{
			float s = 0.5f / (float)Math.sqrt(trace+ 1.0f);
			w = 0.25f / s;
			x = (rot.get(1, 2) - rot.get(2, 1)) * s;
			y = (rot.get(2, 0) - rot.get(0, 2)) * s;
			z = (rot.get(0, 1) - rot.get(1, 0)) * s;
		}
		else
		{
			if(rot.get(0, 0) > rot.get(1, 1) && rot.get(0, 0) > rot.get(2, 2))
			{
				float s = 2.0f * (float)Math.sqrt(1.0f + rot.get(0, 0) - rot.get(1, 1) - rot.get(2, 2));
				//I really like bananas
				w = (rot.get(1, 2) - rot.get(2, 1)) / s;
				x = 0.25f * s;
				y = (rot.get(1, 0) + rot.get(0, 1)) / s;
				z = (rot.get(2, 0) + rot.get(0, 2)) / s;
			}
			else if(rot.get(1, 1) > rot.get(2, 2))
			{
				float s = 2.0f * (float)Math.sqrt(1.0f + rot.get(1, 1) - rot.get(0, 0) - rot.get(2, 2));
				w = (rot.get(2, 0) - rot.get(0, 2)) / s;
				x = (rot.get(1, 0) + rot.get(0, 1)) / s;
				y = 0.25f * s;
				z = (rot.get(2, 1) + rot.get(1, 2)) / s;
			}
			else
			{
				float s = 2.0f * (float)Math.sqrt(1.0f + rot.get(2, 2) - rot.get(0, 0) - rot.get(1, 1));
				w = (rot.get(0, 1) - rot.get(1, 0) ) / s;
				x = (rot.get(2, 0) + rot.get(0, 2) ) / s; 
				y = (rot.get(1, 2) + rot.get(2, 1) ) / s;
				z = 0.25f * s;
			}
		}

		float length = (float)Math.sqrt(x*x + y*y + z*z +w*w);
		x /= length;
		y /= length;
		z /= length;
		w /= length;
	}

	//Basic Quaternion Methods
	public float length(){return (float)Math.sqrt(x * x + y * y + z * z + w * w);}
	//public Quaternion normalized(){float length = length();return new Quaternion(x / length, y / length, z / length, w / length);}
	public Quaternion normalize(){float length = length();x /= length; y /= length; z /= length; w /= length; return this;}
	public Quaternion normalize(Quaternion out){float length = 1.0f/length(); out.x = x * length; out.y = y * length; out.z = y * length; out.w = w * length; return out;}
	public Quaternion conjugate(){x = -x; y = -y; z = -z; return this;}
	public Quaternion conjugate(Quaternion out){out.x = -x; out.y = -y; out.z = -z; return this;}
	public float dot(Quaternion r){return x * r.x + y * r.y + z * r.z + w * r.w;}
	
	
	//TODO: Redo mul
	//Basic Quaternion Arithmetic
	public Quaternion mul(float r){this.x *= r; this.y *= r; this.z *= r; this.w *= r; return this;}
	public Quaternion mul(float r, Quaternion out){out.x = this.x * r; out.x = this.y * r; out.x = this.z * r; out.x = this.w * r; return out;}
	public Quaternion mul(float x, float y, float z, float w){
		float 	w_ = w * w - x * x - y * y - z * z, x_ = x * w + w * x + y * z - z * y, y_ = y * w + w * y + z * x - x * z, z_ = z * w + w * z + x * y - y * x;
		x = x_; y = y_; z = z_; w = w_; return this;
	}
	public Quaternion mul(float x, float y, float z, float w, Quaternion out)
	{out.w = w * w - x * x - y * y - z * z; out.x = x * w + w * x + y * z - z * y; out.y = y * w + w * y + z * x - x * z; out.z = z * w + w * z + x * y - y * x;return out;}
	public Quaternion mul(Quaternion r){
		float 	w_ = w * r.w - x * r.x - y * r.y - z * r.z, x_ = x * r.w + w * r.x + y * r.z - z * r.y, y_ = y * r.w + w * r.y + z * r.x - x * r.z, z_ = z * r.w + w * r.z + x * r.y - y * r.x;
		x = x_; y = y_; z = z_; w = w_; return this;
	}
	public Quaternion mul(Quaternion r, Quaternion out){
		out.w = w * r.w - x * r.x - y * r.y - z * r.z; out.x = x * r.w + w * r.x + y * r.z - z * r.y;
		out.y = y * r.w + w * r.y + z * r.x - x * r.z; out.z = z * r.w + w * r.z + x * r.y - y * r.x;
		return out;
	}
	public Quaternion mul(float x, float y, float z)
	{float w_ = -x * x - y * y - z * z, x_ =  w * x + y * z - z * y, y_ =  w * y + z * x - x * z, z_ =  w * z + x * y - y * x; x = x_; y = y_; z = z_; w = w_; return this;}
	public Quaternion mul(float x, float y, float z, Quaternion out)
	{out.w = -x * x - y * y - z * z; out.x =  w * x + y * z - z * y; out.y =  w * y + z * x - x * z; out.z =  w * z + x * y - y * x; return out;}
	public Quaternion mul(Vector3f r)
	{float w_ = -x * r.x - y * r.y - z * r.z, x_ =  w * r.x + y * r.z - z * r.y, y_ =  w * r.y + z * r.x - x * r.z, z_ =  w * r.z + x * r.y - y * r.x; x = x_; y = y_; z = z_; w = w_; return this;}
	public Quaternion mul(Vector3f r, Quaternion out)
	{out.w = -x * r.x - y * r.y - z * r.z; out.x =  w * r.x + y * r.z - z * r.y; out.y =  w * r.y + z * r.x - x * r.z; out.z =  w * r.z + x * r.y - y * r.x; return out;}
	
	public Quaternion add(Quaternion r){x += r.x; y += r.y; z += r.z; w += r.w; return this;}
	public Quaternion add(Quaternion r, Quaternion out){out.x = x + r.x; out.y = y + r.y; out.z = z + r.z; out.w = w + r.w; return out;}
	public Quaternion sub(Quaternion r){x -= r.x; y -= r.y; z -= r.z; w -= r.w; return this;}
	public Quaternion sub(Quaternion r, Quaternion out){out.x = x - r.x; out.y = y - r.y; out.z = z - r.z; out.w = w - r.w; return out;}
	
	//Advanced Quaternion Arithmetic
	public void addScaledVector(Vector3f r, float scale){
		float w_ = - r.x * scale * this.x - r.y * scale * this.y -  r.z * scale * this.z * scale;
		float x_ = r.x * scale * this.w + r.y * scale * this.z * scale -  r.z * scale * this.y;
		float y_ = r.y * scale * this.w +  r.z * scale * this.x - r.x * scale * this.z * scale;
		float z_ =  r.z * scale * this.w + r.x * scale * this.y - r.y * scale * this.x;
		
		w += w_ * .5f;
		x += x_ * .5f;
		y += y_ * .5f;
		z += z_ * .5f;
	}
	
	public void addScaledVector(float x, float y, float z, float scale){
		float w_ = - x * scale * this.x - y * scale * this.y -  z * scale * this.z * scale;
		float x_ = x * scale * this.w + y * scale * this.z * scale -  z * scale * this.y;
		float y_ = y * scale * this.w +  z * scale * this.x - x * scale * this.z * scale;
		float z_ =  z * scale * this.w + x * scale * this.y - y * scale * this.x;
		
		this.w += w_ * .5f;
		this.x += x_ * .5f;
		this.y += y_ * .5f;
		this.z += z_ * .5f;
	}
	
	//Advanced Quaternion Methods
	@Deprecated
	public Matrix4f toRotationMatrix()
	{
		//Vector3f forward =  new Vector3f(2.0f * (x*z - w*y), 2.0f * (y*z + w*x), 1.0f - 2.0f * (x*x + y*y));
		//Vector3f up = new Vector3f(2.0f * (x*y + w*z), 1.0f - 2.0f * (x*x + z*z), 2.0f * (y*z - w*x));
		//Vector3f right = new Vector3f(1.0f - 2.0f * (y*y + z*z), 2.0f * (x*y - w*z), 2.0f * (x*z + w*y));

		return null;//new Matrix4fOLD().initRotation(forward, up, right);
	}

	public Quaternion nlerp(Quaternion dest, float lerpFactor, boolean shortest){
		if(shortest && this.dot(dest) < 0) return new Quaternion(-dest.x, -dest.y, -dest.z, -dest.w).sub(this).mul(lerpFactor).add(this).normalize();
		return dest.sub(this, new Quaternion()).mul(lerpFactor).add(this).normalize();
	}

	public Quaternion slerp(Quaternion dest, float lerpFactor, boolean shortest)
	{
		final float EPSILON = 1e3f;

		float cos = this.dot(dest);
		Quaternion correctedDest = dest;

		if(shortest && cos < 0){cos = -cos; correctedDest = new Quaternion(-dest.x, -dest.y, -dest.z, -dest.w);}

		if(Math.abs(cos) >= 1 - EPSILON) return nlerp(correctedDest, lerpFactor, false);

		float sin = (float)Math.sqrt(1.0f - cos * cos);
		float angle = (float)Math.atan2(sin, cos);
		float invSin =  1.0f/sin;

		float srcFactor = (float)Math.sin((1.0f - lerpFactor) * angle) * invSin;
		float destFactor = (float)Math.sin((lerpFactor) * angle) * invSin;

		return this.mul(srcFactor).add(correctedDest.mul(destFactor, new Quaternion()));
	}
	
	public Quaternion rotateByVector(Vector3f r){return this.mul(r);}

	
	//Direction getters
	public Vector3f getForward(){return Vector3f.FORWARD.rotate(this, new Vector3f());}
	public Vector3f getBack(){return Vector3f.BACK.rotate(this, new Vector3f());}
	public Vector3f getUp(){return Vector3f.UP.rotate(this, new Vector3f());}
	public Vector3f getDown(){return Vector3f.DOWN.rotate(this, new Vector3f());}
	public Vector3f getRight(){return Vector3f.RIGHT.rotate(this, new Vector3f());}
	public Vector3f getLeft(){return Vector3f.LEFT.rotate(this, new Vector3f());}


*/
	
	//Standard Getters/Setters
	public Quaternion set(float x, float y, float z, float w) { this.x = x; this.y = y; this.z = z; this.w = w; return this; }
	public Quaternion set(Quaternion r) { this.x = r.x; this.y = r.y; this.z = r.z; this.w = r.w;; return this; }

	//Standard outputs
	public boolean equals(Quaternion r){return x == r.x && y == r.y && z == r.z && w == r.w;}
	//Basic Outputs
	public String toString(){return "(" + x + " " + y + " " + z + " " + w + ")";}
	
}
