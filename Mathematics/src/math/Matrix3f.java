package math;

public class Matrix3f {
	public float 	
		m00, m01, m02,
		m10, m11, m12,
		m20, m21, m22;
	
	
	public Matrix3f() {m00 = 1f; m11 = 1f; m22 = 1f;}
	
	public Matrix3f transpose() {return transpose(this, this);}
	public Matrix3f transpose(Matrix3f out) {return transpose(this, out);}
	public static Matrix3f transpose(Matrix3f m, Matrix3f out) {
		float nm10 = m.m01, nm20 = m.m02, nm21 = m.m12;
		out.m01 = m.m10; out.m02 = m.m20; out.m12 = m.m21;
		out.m10 = nm10; out.m20 = nm20; out.m21 = nm21;
		out.m00 = m.m00; out.m11 = m.m11; out.m22 = m.m22;
		return out;
	}
	
	public Vector3f transform(Vector3f v){return transform(this, v, v);}
	public Vector3f transform(Vector3f v, Vector3f out) {return transform(this, v, out);}
	public static Vector3f transform(Matrix3f m, Vector3f v) {return transform(m, v, v);}
	public static Vector3f transform(Matrix3f m, Vector3f v, Vector3f out) {
		float nx = m.m00 * v.x + m.m01 * v.y + m.m02 * v.z;
		float ny = m.m10 * v.x + m.m11 * v.y + m.m12 * v.z;
		float nz = m.m20 * v.x + m.m21 * v.y + m.m22 * v.z;
		return out.set(nx, ny, nz);
	}
	
	public Vector3f transformTranspose(Vector3f v){return transformTranspose(this, v, v);}
	public Vector3f transformTranspose(Vector3f v, Vector3f out) {return transformTranspose(this, v, out);}
	public static Vector3f transformTranspose(Matrix3f m, Vector3f v) {return transformTranspose(m, v, v);}
	public static Vector3f transformTranspose(Matrix3f m, Vector3f v, Vector3f out) {
		float nx = m.m00 * v.x + m.m10 * v.y + m.m20 * v.z;
		float ny = m.m01 * v.x + m.m11 * v.y + m.m21 * v.z;
		float nz = m.m02 * v.x + m.m12 * v.y + m.m22 * v.z;
		return out.set(nx, ny, nz);
	}
	
	
	
	public Matrix3f calculateContactBasis(Vector3f normal) {
		m00 = normal.x; m01 = normal.y; m02 = normal.z;
		if(Math.abs(normal.x) > Math.abs(normal.y)) {
			float s = 1.0f / (float)Math.sqrt(normal.z * normal.z + normal.x * normal.x);
			m10 = normal.z * s; m11 = 0f; m12 = -normal.x * s;
			m20 = normal.y * m10; m21 = normal.z * m10 - normal.x * m12; m22 = -m20;
		} else {
			float s = 1.0f / (float)Math.sqrt(normal.z*normal.z + normal.y*normal.y);
			m10 = 0f; m11 = -normal.z * s; m12 = normal.x * s;
			m20 = normal.y * m12 - normal.z * m11; m21 = -normal.x * m12; m22 = normal.x * m11;
		}
		this.transpose();
		return this;
	}
	
	public Matrix3f mulInverseInertiaTensor(Vector3f iit) {
		m00 *= iit.x; m01 *= iit.y; m02 *= iit.z;
		m10 *= iit.x; m11 *= iit.y; m12 *= iit.z;
		m20 *= iit.x; m21 *= iit.y; m22 *= iit.z;
		return this;
	}
	
	public Matrix3f mul(float scale) {return mul(this, scale, this);}
	public Matrix3f mul(float scale, Matrix3f out) {return mul(this, scale, out);}
	public static Matrix3f mul(Matrix3f m, float scale, Matrix3f out) {
		out.m00 = m.m00 * scale; out.m01 = m.m01 * scale; out.m02 = m.m02 * scale;
		out.m10 = m.m10 * scale; out.m11 = m.m11 * scale; out.m12 = m.m02 * scale;
		out.m20 = m.m20 * scale; out.m21 = m.m21 * scale; out.m22 = m.m02 * scale;
		return out;
	}
	
	public Matrix3f mul(Vector3f r) {return mul(this, r, this);}
	public Matrix3f mul(Vector3f r, Matrix3f out) {return mul(this, r, out);}
	public static Matrix3f mul(Matrix3f m, Vector3f r, Matrix3f out) {
		out.m00 = m.m00 * r.x; out.m01 = m.m01 * r.y; out.m02 = m.m02 * r.z;
		out.m10 = m.m10 * r.x; out.m11 = m.m11 * r.y; out.m12 = m.m02 * r.z;
		out.m20 = m.m20 * r.x; out.m21 = m.m21 * r.y; out.m22 = m.m02 * r.z;
		return out;
	}
	
	public Matrix3f mul(Matrix3f m) {return mul(this, m, this);}
	public Matrix3f mul(Matrix3f m, Matrix3f out) {return mul(this, m, out);}
	public static Matrix3f mul(Matrix3f left, Matrix3f right, Matrix3f out) {		
		float nm00 = left.m00 * right.m00 + left.m01 * right.m10 + left.m02 * right.m20;
		float nm10 = left.m10 * right.m00 + left.m11 * right.m10 + left.m12 * right.m20;
		float nm20 = left.m20 * right.m00 + left.m21 * right.m10 + left.m22 * right.m20;
		float nm01 = left.m00 * right.m01 + left.m01 * right.m11 + left.m02 * right.m21;
		float nm11 = left.m10 * right.m01 + left.m11 * right.m11 + left.m12 * right.m21;
		float nm21 = left.m20 * right.m01 + left.m21 * right.m11 + left.m22 * right.m21;
		float nm02 = left.m00 * right.m02 + left.m01 * right.m12 + left.m02 * right.m22;
		float nm12 = left.m10 * right.m02 + left.m11 * right.m12 + left.m12 * right.m22;
		float nm22 = left.m20 * right.m02 + left.m21 * right.m12 + left.m22 * right.m22;
		out.m00 = nm00; out.m01 = nm01; out.m02 = nm02;
        out.m10 = nm10; out.m11 = nm11; out.m12 = nm12;
        out.m20 = nm20; out.m21 = nm21; out.m22 = nm22;
        return out;
	}
	
	public Matrix3f mulTranspose(Matrix3f m, Matrix3f out) {return mulTranspose(this, m, out);}
	public static Matrix3f mulTranspose(Matrix3f left, Matrix3f right, Matrix3f out) {
		float nm00 = left.m00 * right.m00 + left.m10 * right.m10 + left.m20 * right.m20;
		float nm10 = left.m01 * right.m00 + left.m11 * right.m10 + left.m21 * right.m20;
		float nm20 = left.m02 * right.m00 + left.m12 * right.m10 + left.m22 * right.m20;
		float nm01 = left.m00 * right.m01 + left.m10 * right.m11 + left.m20 * right.m21;
		float nm11 = left.m01 * right.m01 + left.m11 * right.m11 + left.m21 * right.m21;
		float nm21 = left.m02 * right.m01 + left.m12 * right.m11 + left.m22 * right.m21;
		float nm02 = left.m00 * right.m02 + left.m10 * right.m12 + left.m20 * right.m22;
		float nm12 = left.m01 * right.m02 + left.m11 * right.m12 + left.m21 * right.m22;
		float nm22 = left.m02 * right.m02 + left.m12 * right.m12 + left.m22 * right.m22;
		out.m00 = nm00; out.m01 = nm01; out.m02 = nm02;
		out.m10 = nm10; out.m11 = nm11; out.m12 = nm12;
		out.m20 = nm20; out.m21 = nm21; out.m22 = nm22;
		return out;
	}
	
	public Matrix3f add(Matrix3f m) {return add(this, m, this);}
	public Matrix3f add(Matrix3f m, Matrix3f out) {return add(this, m, out);}
	public static Matrix3f add(Matrix3f m0, Matrix3f m1, Matrix3f out) {
		out.m00 = m0.m00 + m1.m00; out.m01 = m0.m01 + m1.m01; out.m02 = m0.m02 + m1.m02;
		out.m10 = m0.m10 + m1.m10; out.m11 = m0.m11 + m1.m11; out.m12 = m0.m12 + m1.m12;
		out.m20 = m0.m20 + m1.m20; out.m21 = m0.m21 + m1.m21; out.m22 = m0.m22 + m1.m22;
		return out;
	}
	
	
	public Matrix3f impulseToTorque(Vector3f iit) {return impulseToTorque(this, iit, this);}
	public Matrix3f impulseToTorque(Vector3f iit, Matrix3f out) {return impulseToTorque(this, iit, out);}
	public static Matrix3f impulseToTorque(Matrix3f m, Vector3f iit) {return impulseToTorque(m, iit, m);}
	public static Matrix3f impulseToTorque(Matrix3f m, Vector3f iit, Matrix3f out) {//TODO: IF THERE IS AN ERROR IT IS PROBABLY HERE!!!!!!
		float nm00 = m.m00 * iit.x, nm01 = m.m01 * iit.y, nm02 = m.m02 * iit.z;
		float nm10 = m.m10 * iit.x, nm11 = m.m11 * iit.y, nm12 = m.m12 * iit.z;
		float nm20 = m.m20 * iit.x, nm21 = m.m21 * iit.y, nm22 = m.m22 * iit.z;
		
		float nnm00 = nm00 * m.m00 + nm01 * m.m10 + nm02 * m.m20, nnm01 = nm00 * m.m01 + nm01 * m.m11 + nm02 * m.m21, nnm02 = nm00 * m.m02 + nm01 * m.m12 + nm02 * m.m22;
		float nnm10 = nm10 * m.m00 + nm11 * m.m10 + nm12 * m.m20, nnm11 = nm10 * m.m01 + nm11 * m.m11 + nm12 * m.m21, nnm12 = nm10 * m.m02 + nm11 * m.m12 + nm12 * m.m22;
		float nnm20 = nm20 * m.m00 + nm21 * m.m10 + nm22 * m.m20, nnm21 = nm20 * m.m01 + nm21 * m.m11 + nm22 * m.m21, nnm22 = nm20 * m.m02 + nm21 * m.m12 + nm22 * m.m22;
		
		out.m00 = -nnm00; out.m01 = -nnm01; out.m02 = -nnm02;
		out.m10 = -nnm10; out.m11 = -nnm11; out.m12 = -nnm12;
		out.m20 = -nnm20; out.m21 = -nnm21; out.m22 = -nnm22;
		return out;
	}
	
	//skew = x y z, iit = u v w
	public static Matrix3f impulseToTorque(Vector3f skew, Vector3f iit, Matrix3f out) {
		float wxy = -iit.z * skew.x * skew.y;
		float vxz = -iit.y * skew.x * skew.z;
		float uyz = -iit.x * skew.y * skew.z;
		float x2 = skew.x * skew.x, y2 = skew.y * skew.y, z2 = skew.z * skew.z;
		
		out.m00 = iit.y * z2 + iit.z * y2; out.m11 = iit.x * z2 + iit.z * x2; out.m22 = iit.x * y2 + iit.y * x2;
		out.m01 = out.m10 = wxy;
		out.m02 = out.m20 = vxz;
		out.m21 = out.m12 = uyz;
		return out;
	}
	
	public Matrix3f setSkewSymmetric(Vector3f v) {
		m00 = m11 = m22 = 0f;
		m01 = -v.z;
		m02 = v.y;
		m10 = v.z;
		m12 = -v.x;
		m20 = -v.y;
		m21 = v.x;
		return this;
	}
	
	public float determinant() { return (m01 * m12 - m02 * m11) * m20 + (m02 * m10 - m00 * m12) * m21 + (m00 * m11 - m01 * m10) * m22; }
	
	public Matrix3f invert() {return invert(this);}
	public Matrix3f invert(Matrix3f out) {
		float s = 1.0f / determinant();
		float nm00 = (m11 * m22 - m21 * m12) * s, nm01 = (m21 * m02 - m01 * m22) * s, nm02 = (m01 * m12 - m11 * m02) * s;
		float nm10 = (m20 * m12 - m10 * m22) * s, nm11 = (m00 * m22 - m20 * m02) * s, nm12 = (m10 * m02 - m00 * m12) * s;
		float nm20 = (m10 * m21 - m20 * m11) * s, nm21 = (m20 * m01 - m00 * m21) * s, nm22 = (m00 * m11 - m10 * m01) * s;
		out.m00 = nm00; out.m01 = nm01; out.m02 = nm02;
		out.m10 = nm10; out.m11 = nm11; out.m12 = nm12;
		out.m20 = nm20; out.m21 = nm21; out.m22 = nm22;
		return out;
    }
	
	public String toString() {
		return 	m00 + ", " + m01 + ", " + m02 + "\n" +
				m10 + ", " + m11 + ", " + m12 + "\n" +
				m20 + ", " + m21 + ", " + m22;
	}
}
