package math;

public class Matrix4f {
	public static final byte IDENTITY = 0b1;
	public static final byte AFFINE = 0b10;
	public static final byte TRANSLATION = 0b100;
	public static final byte PERSPECTIVE = 0b1000;
	public static final byte ORTHOGRAPHIC = 0b10000;
	
	public byte prop;
	public float 	
		m00, m01, m02, m03,
		m10, m11, m12, m13,
		m20, m21, m22, m23,
		m30, m31, m32, m33;
	
	//~~~~~~~~~~~CONSTRUCTORS~~~~~~~~~~~\\
	
	public Matrix4f() {
		m00 = 1f; m11 = 1f; m22 = 1f; m33 = 1f; 
		prop = IDENTITY | AFFINE | TRANSLATION;
	}
	public Matrix4f(Matrix3f m) {
		m00 = m.m00; m01 = m.m01; m02 = m.m02;
		m10 = m.m10; m11 = m.m11; m12 = m.m12;
		m20 = m.m20; m21 = m.m21; m22 = m.m22;
		m33 =1f; prop = AFFINE;
	}
	public Matrix4f(Matrix4f m) {
		m00 = m.m00; m01 = m.m01; m02 = m.m02; m03 = m.m03;
		m10 = m.m10; m11 = m.m11; m12 = m.m12; m13 = m.m13;
		m20 = m.m20; m21 = m.m21; m22 = m.m22; m23 = m.m23;
		m30 = m.m30; m31 = m.m31; m32 = m.m32; m33 = m.m33;
		prop = m.prop;
	}
	public Matrix4f(float nm00, float nm01, float nm02, float nm03,
					float nm10, float nm11, float nm12, float nm13,
					float nm20, float nm21, float nm22, float nm23,
					float nm30, float nm31, float nm32, float nm33) {
		m00 = nm00; m01 = nm01; m02 = nm02; m03 = nm03;
		m10 = nm10; m11 = nm11; m12 = nm12; m13 = nm13;
		m20 = nm20; m21 = nm21; m22 = nm22; m23 = nm23;
		m30 = nm30; m31 = nm31; m32 = nm32; m33 = nm33;
	}
	
	
	//~~~~~~~~~~~SETTERS~~~~~~~~~~~\\
	
	public Matrix4f identity() {
		if((prop & IDENTITY) != 0) return this;
		m00 = 1f; m01 = 0f; m02 = 0f; m03 = 0f;
		m10 = 0f; m11 = 1f; m12 = 0f; m13 = 0f;
		m20 = 0f; m21 = 0f; m22 = 1f; m23 = 0f;
		m30 = 0f; m31 = 0f; m32 = 0f; m33 = 1f;
		prop = IDENTITY | AFFINE | TRANSLATION;
		return this;
	}
	
	public Matrix4f set(Matrix3f m) {
		m00 = m.m00; m01 = m.m01; m02 = m.m02; m03 = 0f;
		m10 = m.m10; m11 = m.m11; m12 = m.m12; m13 = 0f;
		m20 = m.m20; m21 = m.m21; m22 = m.m22; m23 = 0f;
		m30 = 0f; m31 = 0f; m32 = 0f; m33 = 1f;
		prop = AFFINE;
		return this;
	}
	public Matrix4f set(Matrix4f m) {
		m00 = m.m00; m01 = m.m01; m02 = m.m02; m03 = m.m03;
		m10 = m.m10; m11 = m.m11; m12 = m.m12; m13 = m.m13;
		m20 = m.m20; m21 = m.m21; m22 = m.m22; m23 = m.m23;
		m30 = m.m30; m31 = m.m31; m32 = m.m32; m33 = m.m33;
		prop = m.prop;
		return this;
	}
	public Matrix4f set(float nm00, float nm01, float nm02, float nm03,
						float nm10, float nm11, float nm12, float nm13,
						float nm20, float nm21, float nm22, float nm23,
						float nm30, float nm31, float nm32, float nm33) {
		m00 = nm00; m01 = nm01; m02 = nm02; m03 = nm03;
		m10 = nm10; m11 = nm11; m12 = nm12; m13 = nm13;
		m20 = nm20; m21 = nm21; m22 = nm22; m23 = nm23;
		m30 = nm30; m31 = nm31; m32 = nm32; m33 = nm33;
		prop = 0;
		return this;
	}
	
	//~~~~~~~~~~~TRANSLATION~~~~~~~~~~~\\
	
	public Matrix4f translation(Vector3f v) {return translation(v.x, v.y, v.z);}
	public Matrix4f translation(float x, float y, float z) {
		m00 = 1f; m01 = 0f; m02 = 0f; m03 = x;
		m10 = 0f; m11 = 1f; m12 = 0f; m13 = y;
		m20 = 0f; m21 = 0f; m22 = 1f; m23 = z;
		m30 = 0f; m31 = 0f; m32 = 0f; m33 = 1f;
		prop = AFFINE | TRANSLATION;
		return this;
	}
	public Matrix4f setTranslation(Vector3f v) {return setTranslation(v.x, v.y, v.z);}
	public Matrix4f setTranslation(float x, float y, float z) {
        m03 = x; m13 = y; m23 = z;
        prop &= ~(PERSPECTIVE | IDENTITY);
        return this;
    }
	
	//~~~~~~~~~~~ROTATION~~~~~~~~~~~\\
	
	public Matrix4f rotation(Vector3f axis, float angle) {return rotation(axis.x, axis.y, axis.z, angle);}
	public Matrix4f rotation(float x, float y, float z, float angle) {
		float sin = (float) Math.sin(angle);
        float cos = (float) Math.cos(angle);
        float C = 1.0f - cos;
        float xy = x * y, xz = x * z, yz = y * z;
        m00 = cos + x * x * C; 	m01 = xy * C - z * sin; m02 = xz * C + y * sin; m03 = 0f;
		m10 = xy * C + z * sin; m11 = cos + y * y * C; 	m12 = yz * C - x * sin; m13 = 0f;
		m20 = xz * C - y * sin; m21 = yz * C + x * sin; m22 = cos + z * z * C; 	m23 = 0f;
		m30 = 0f; 				m31 = 0f; 				m32 = 0f; 				m33 = 1f;
		prop = AFFINE;
		return this;
	}
	public Matrix4f rotation(float x, float y, float z) {
        float sinX = (float) Math.sin(x), cosX = (float) Math.cos(x);
        float sinY = (float) Math.sin(y), cosY = (float) Math.cos(y);
        float sinZ = (float) Math.sin(z), cosZ = (float) Math.cos(z);

        float n0 = sinX * sinY;
        float n1 = cosX * -sinY;
        
        m00 = cosY * cosZ;		m01 = cosY * -sinZ;		m02 = sinY;			m03 = 0f;
        m10 = n0 * cosZ + cosX * sinZ;		m11 = n0 * -sinZ + cosX * cosZ;		m12 = -sinX * cosY;		m13 = 0f;
        m20 = n1 * cosZ + sinX * sinZ;			m21 = n1 * -sinZ + sinX * cosZ;					m22 = cosX * cosY;					m23 = 0f;
        m30 = 0f;				m31 = 0f;							m32 = 0f;							m33 = 1f;
        prop = AFFINE;
        return this;
    }
	public Matrix4f rotation(Quaternion q) {
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
	        m00 = w2 + x2 - z2 - y2;	m01 = -zw + xy - zw + xy;	m02 = yw + xz + xz + yw;	m03 = 0f;
	        m10 = xy + zw + zw + xy;	m11 = y2 - z2 + w2 - x2;	m12 = yz + yz - xw - xw;	m13 = 0f;
	        m20 = xz - yw + xz - yw;	m21 = yz + yz + xw + xw;	m22 = z2 - y2 - x2 + w2;	m23 = 0f;
	        m30 = 0f;					m31 = 0f;					m32 = 0f;					m33 = 1f;
	        prop = AFFINE;
	        return this;
	}
	public Matrix4f setRotation(float x, float y, float z) {
        float sinX = (float) Math.sin(x), cosX = (float) Math.cos(x);
        float sinY = (float) Math.sin(y), cosY = (float) Math.cos(y);
        float sinZ = (float) Math.sin(z), cosZ = (float) Math.cos(z);

        float n0 = sinX * sinY;
        float n1 = cosX * -sinY;
        
        m00 = cosY * cosZ;		m01 = cosY * -sinZ;		m02 = sinY;	
        m10 = n0 * cosZ + cosX * sinZ;		m11 = n0 * -sinZ + cosX * cosZ;		m12 = -sinX * cosY;
        m20 = n1 * cosZ + sinX * sinZ;			m21 = n1 * -sinZ + sinX * cosZ;					m22 = cosX * cosY;	
        prop &= ~(IDENTITY | TRANSLATION | PERSPECTIVE);
        return this;
    }
	
	//~~~~~~~~~~~SCALE~~~~~~~~~~~\\
	
	public Matrix4f scaling(float scale) {return scaling(scale, scale ,scale);}
	public Matrix4f scaling(Vector3f v) {return scaling(v.x, v.y, v.z);}
	public Matrix4f scaling(float x, float y, float z) {
		m00 = x; m01 = 0f; m02 = 0f; m03 = 0f;
		m10 = 0f; m11 = y; m12 = 0f; m13 = 0f;
		m20 = 0f; m21 = 0f; m22 = z; m23 = 0f;
		m30 = 0f; m31 = 0f; m32 = 0f; m33 = 1f;
		prop = AFFINE;
		return this;
	}
	
	public Matrix4f scale(float scale) {return scale(this, scale, scale, scale, this);}
	public Matrix4f scale(float x, float y, float z) {return scale(this, x, y, z, this);}
	public Matrix4f scale(Vector3f v) {return scale(this, v.x, v.y, v.z, this);}
	public Matrix4f scale(float scale, Matrix4f out) {return scale(this, scale, scale, scale, out);}
	public Matrix4f scale(float x, float y, float z, Matrix4f out) {return scale(this, x, y, z, out);}
	public Matrix4f scale(Vector3f v, Matrix4f out) {return scale(this, v.x, v.y, v.z, out);}
	public static Matrix4f scale(Matrix4f m, float x, float y, float z, Matrix4f out) {
		if ((m.prop & IDENTITY) != 0) return out.scaling(x, y, z);
		out.m00 = m.m00 * x; out.m01 = m.m01 * y; out.m02 = m.m02 * z; out.m03 = m.m03;
        out.m10 = m.m10 * x; out.m11 = m.m11 * y; out.m12 = m.m12 * z; out.m13 = m.m13;
        out.m20 = m.m20 * x; out.m21 = m.m21 * y; out.m22 = m.m22 * z; out.m23 = m.m23;
        out.m30 = m.m30 * x; out.m31 = m.m31 * y; out.m32 = m.m32 * z; out.m33 = m.m33;
        out.prop = (byte) (m.prop & ~(IDENTITY | TRANSLATION | PERSPECTIVE));
        return out;
	}
	
	//~~~~~~~~~~~TRANSFORMATION~~~~~~~~~~~\\
	
	public Matrix4f translationRotateScale(	Vector3f t, Quaternion r, float scale) {return translationRotateScale(t.x, t.y, t.z, r.x, r.y, r.z, r.w, scale, scale, scale);}
	public Matrix4f translationRotateScale(	Vector3f t, Quaternion r, Vector3f s) {return translationRotateScale(t.x, t.y, t.z, r.x, r.y, r.z, r.w, s.x, s.y, s.z);}
	public Matrix4f translationRotateScale(	float tx, float ty, float tz, 
	        								float qx, float qy, float qz, float qw, 
	        								float sx, float sy, float sz) {
		float dqx = qx + qx, dqy = qy + qy, dqz = qz + qz;
		float q00 = dqx * qx, q11 = dqy * qy, q22 = dqz * qz;
		float q01 = dqx * qy, q02 = dqx * qz, q03 = dqx * qw;
		float q12 = dqy * qz, q13 = dqy * qw, q23 = dqz * qw;
		m00 = sx - (q11 + q22) * sx; 	m01 = (q01 - q23) * sy; 		m02 = (q02 + q13) * sz; 		m03 = tx;
		m10 = (q01 + q23) * sx; 		m11 = sy - (q22 + q00) * sy; 	m12 = (q12 - q03) * sz; 	m13 = ty;
		m20 = (q02 - q13) * sx; 		m21 = (q12 + q03) * sy; 		m22 = sz - (q11 + q00) * sz; 		m23 = tz;
		m30 = 0f; 					m31 = 0f; 					m32 = 0f; 					m33 = 1f;
		prop = AFFINE;
		return this;
	}
	
	public Matrix4f inverseTRS(	Vector3f t, Quaternion r, float scale) {return inverseTRS(t.x, t.y, t.z, r.x, r.y, r.z, r.w, scale, scale, scale);}
	public Matrix4f inverseTRS(	Vector3f t, Quaternion r, Vector3f s) {return inverseTRS(t.x, t.y, t.z, r.x, r.y, r.z, r.w, s.x, s.y, s.z);}
	public Matrix4f inverseTRS(	float tx, float ty, float tz, 
								float qx, float qy, float qz, float qw, 
								float sx, float sy, float sz) {
		float nqx = -qx, nqy = -qy, nqz = -qz;
        float dqx = nqx + nqx, dqy = nqy + nqy, dqz = nqz + nqz;
        float q00 = dqx * nqx, q11 = dqy * nqy, q22 = dqz * nqz;
        float q01 = dqx * nqy, q02 = dqx * nqz, q03 = dqx * qw;
        float q12 = dqy * nqz, q13 = dqy * qw, q23 = dqz * qw;
        float isx = 1/sx, isy = 1/sy, isz = 1/sz;
        m00 = isx * (1.0f - q11 - q22); m01 = isx * (q01 - q23); 		m02 = isx * (q02 + q13); 		
		m10 = isy * (q01 + q23); 		m11 = isy * (1.0f - q22 - q00); m12 = isy * (q12 - q03); 		
		m20 = isz * (q02 - q13); 		m21 = isz * (q12 + q03); 		m22 = isz * (1.0f - q11 - q00); 
		m30 = 0f; 						m31 = 0f; 						m32 = 0f; 						m33 = 1f;
		m03 = -m00 * tx - m01 * ty - m02 * tz; 
		m13 = -m10 * tx - m11 * ty - m12 * tz; 
		m23 = -m20 * tx - m21 * ty - m22 * tz;
		prop = AFFINE;
		return this;
	}
	
	
	//~~~~~~~~~~~MULTIPLICATION~~~~~~~~~~~\\
	
	public Matrix4f mul(Matrix4f right) {return mul(this, right, this);}
	public Matrix4f mul(Matrix4f right, Matrix4f out) {return mul(this, right, out);}
	
	
	//EDITS NEEDED
	public static Matrix4f mul(Matrix4f left, Matrix4f right, Matrix4f out) {
		if ((left.prop & IDENTITY) != 0) return out.set(right);
		if ((right.prop & IDENTITY) != 0) return out.set(left);
        if ((right.prop & AFFINE) != 0) {
        	if((left.prop & TRANSLATION) != 0) 	return mulTranslationAffine(left, right, out);
        	if((left.prop & ORTHOGRAPHIC) != 0) return mulOrthographicAffine(left, right, out);
        	if((left.prop & AFFINE) != 0)		return mulAffine(left, right, out);
        	if((left.prop & PERSPECTIVE) != 0) 	return mulPerspectiveAffine(left, right, out);
        	return mulAffineR(left, right, out);
        }
        return mulGeneric(left, right, out);
	}	
	public static Matrix4f mulGeneric(Matrix4f left, Matrix4f right, Matrix4f out) {
		float nm00 = left.m00 * right.m00 + left.m01 * right.m10 + left.m02 * right.m20 + left.m03 * right.m30;
		float nm10 = left.m10 * right.m00 + left.m11 * right.m10 + left.m12 * right.m20 + left.m13 * right.m30;
		float nm20 = left.m20 * right.m00 + left.m21 * right.m10 + left.m22 * right.m20 + left.m23 * right.m30;
		float nm30 = left.m30 * right.m00 + left.m31 * right.m10 + left.m32 * right.m20 + left.m33 * right.m30;
		float nm01 = left.m00 * right.m01 + left.m01 * right.m11 + left.m02 * right.m21 + left.m03 * right.m31;
		float nm11 = left.m10 * right.m01 + left.m11 * right.m11 + left.m12 * right.m21 + left.m13 * right.m31;
		float nm21 = left.m20 * right.m01 + left.m21 * right.m11 + left.m22 * right.m21 + left.m23 * right.m31;
		float nm31 = left.m30 * right.m01 + left.m31 * right.m11 + left.m32 * right.m21 + left.m33 * right.m31;
		float nm02 = left.m00 * right.m02 + left.m01 * right.m12 + left.m02 * right.m22 + left.m03 * right.m32;
		float nm12 = left.m10 * right.m02 + left.m11 * right.m12 + left.m12 * right.m22 + left.m13 * right.m32;
		float nm22 = left.m20 * right.m02 + left.m21 * right.m12 + left.m22 * right.m22 + left.m23 * right.m32;
		float nm32 = left.m30 * right.m02 + left.m31 * right.m12 + left.m32 * right.m22 + left.m33 * right.m32;
		float nm03 = left.m00 * right.m03 + left.m01 * right.m13 + left.m02 * right.m23 + left.m03 * right.m33;
		float nm13 = left.m10 * right.m03 + left.m11 * right.m13 + left.m12 * right.m23 + left.m13 * right.m33;
		float nm23 = left.m20 * right.m03 + left.m21 * right.m13 + left.m22 * right.m23 + left.m23 * right.m33;
		float nm33 = left.m30 * right.m03 + left.m31 * right.m13 + left.m32 * right.m23 + left.m33 * right.m33;
		
		out.m00 = nm00; out.m01 = nm01; out.m02 = nm02; out.m03 = nm03;
        out.m10 = nm10; out.m11 = nm11; out.m12 = nm12; out.m13 = nm13;
        out.m20 = nm20; out.m21 = nm21; out.m22 = nm22; out.m23 = nm23;
        out.m30 = nm30; out.m31 = nm31; out.m32 = nm32; out.m33 = nm33;
        out.prop = 0;
        return out;
	}	
	public static Matrix4f mulAffine(Matrix4f left, Matrix4f right, Matrix4f out) {
		float nm00 = left.m00 * right.m00 + left.m01 * right.m10 + left.m02 * right.m20;
        float nm10 = left.m10 * right.m00 + left.m11 * right.m10 + left.m12 * right.m20;
        float nm20 = left.m20 * right.m00 + left.m21 * right.m10 + left.m22 * right.m20;
        float nm30 = left.m30;
        float nm01 = left.m00 * right.m01 + left.m01 * right.m11 + left.m02 * right.m21;
        float nm11 = left.m10 * right.m01 + left.m11 * right.m11 + left.m12 * right.m21;
        float nm21 = left.m20 * right.m01 + left.m21 * right.m11 + left.m22 * right.m21;
        float nm31 = left.m31;
        float nm02 = left.m00 * right.m02 + left.m01 * right.m12 + left.m02 * right.m22;
        float nm12 = left.m10 * right.m02 + left.m11 * right.m12 + left.m12 * right.m22;
        float nm22 = left.m20 * right.m02 + left.m21 * right.m12 + left.m22 * right.m22;
        float nm32 = left.m32;
        float nm03 = left.m00 * right.m03 + left.m01 * right.m13 + left.m02 * right.m23 + left.m03;
        float nm13 = left.m10 * right.m03 + left.m11 * right.m13 + left.m12 * right.m23 + left.m13;
        float nm23 = left.m20 * right.m03 + left.m21 * right.m13 + left.m22 * right.m23 + left.m23;
        float nm33 = left.m33;
        
        out.m00 = nm00; out.m01 = nm01; out.m02 = nm02; out.m03 = nm03;
        out.m10 = nm10; out.m11 = nm11; out.m12 = nm12; out.m13 = nm13;
        out.m20 = nm20; out.m21 = nm21; out.m22 = nm22; out.m23 = nm23;
        out.m30 = nm30; out.m31 = nm31; out.m32 = nm32; out.m33 = nm33;
        out.prop = AFFINE;
        return out;
	}	
	public static Matrix4f mulTranslationAffine(Matrix4f left, Matrix4f right, Matrix4f out) {
        out.m00 = right.m00; out.m01 = right.m01; out.m02 = right.m02; out.m03 = left.m03 + right.m03;
        out.m10 = right.m10; out.m11 = right.m11; out.m12 = right.m12; out.m13 = left.m13 + right.m13;
        out.m20 = right.m20; out.m21 = right.m21; out.m22 = right.m22; out.m23 = left.m23 + right.m23;
        out.m30 = right.m30; out.m31 = right.m31; out.m32 = right.m32; out.m33 = left.m33;
        out.prop = AFFINE;
        return out;
	}	
	public static Matrix4f mulAffineR(Matrix4f left, Matrix4f right, Matrix4f out) {
		float nm00 = left.m00 * right.m00 + left.m01 * right.m10 + left.m02 * right.m20;
		float nm10 = left.m10 * right.m00 + left.m11 * right.m10 + left.m12 * right.m20;
		float nm20 = left.m20 * right.m00 + left.m21 * right.m10 + left.m22 * right.m20;
		float nm30 = left.m30 * right.m00 + left.m31 * right.m10 + left.m32 * right.m20;
		
		float nm01 = left.m00 * right.m01 + left.m01 * right.m11 + left.m02 * right.m21;
		float nm11 = left.m10 * right.m01 + left.m11 * right.m11 + left.m12 * right.m21;
		float nm21 = left.m20 * right.m01 + left.m21 * right.m11 + left.m22 * right.m21;
		float nm31 = left.m30 * right.m01 + left.m31 * right.m11 + left.m32 * right.m21;
		
		float nm02 = left.m00 * right.m02 + left.m01 * right.m12 + left.m02 * right.m22;
		float nm12 = left.m10 * right.m02 + left.m11 * right.m12 + left.m12 * right.m22;
		float nm22 = left.m20 * right.m02 + left.m21 * right.m12 + left.m22 * right.m22;
		float nm32 = left.m30 * right.m02 + left.m31 * right.m12 + left.m32 * right.m22;
		
		float nm03 = left.m00 * right.m03 + left.m01 * right.m13 + left.m02 * right.m23 + left.m03;
		float nm13 = left.m10 * right.m03 + left.m11 * right.m13 + left.m12 * right.m23 + left.m13;
		float nm23 = left.m20 * right.m03 + left.m21 * right.m13 + left.m22 * right.m23 + left.m23;
		float nm33 = left.m30 * right.m03 + left.m31 * right.m13 + left.m32 * right.m23 + left.m33;
		
        out.m00 = nm00; out.m01 = nm01; out.m02 = nm02; out.m03 = nm03;
        out.m10 = nm10; out.m11 = nm11; out.m12 = nm12; out.m13 = nm13;
        out.m20 = nm20; out.m21 = nm21; out.m22 = nm22; out.m23 = nm23;
        out.m30 = nm30; out.m31 = nm31; out.m32 = nm32; out.m33 = nm33;
        out.prop = (byte) (left.prop & ~(IDENTITY | TRANSLATION | PERSPECTIVE));
        return out;
	}
	public static Matrix4f mulPerspectiveAffine(Matrix4f left, Matrix4f right, Matrix4f out) {
		float nm00 = left.m00 * right.m00;
        float nm10 = left.m11 * right.m10;
        float nm20 = left.m22 * right.m20;
        float nm30 = left.m32 * right.m20;
        float nm01 = left.m00 * right.m01;
        float nm11 = left.m11 * right.m11;
        float nm21 = left.m22 * right.m21;
        float nm31 = left.m32 * right.m21;
        float nm02 = left.m00 * right.m02;
        float nm12 = left.m11 * right.m12;
        float nm22 = left.m22 * right.m22;
        float nm32 = left.m32 * right.m22;
        float nm03 = left.m00 * right.m03;
        float nm13 = left.m11 * right.m13;
        float nm23 = left.m22 * right.m23 + left.m23;
        float nm33 = left.m32 * right.m23;
        out.m00 = nm00; out.m01 = nm01; out.m02 = nm02; out.m03 = nm03;
        out.m10 = nm10; out.m11 = nm11; out.m12 = nm12; out.m13 = nm13;
        out.m20 = nm20; out.m21 = nm21; out.m22 = nm22; out.m23 = nm23;
        out.m30 = nm30; out.m31 = nm31; out.m32 = nm32; out.m33 = nm33;
        out.prop = 0;
        return out;
	}
	public static Matrix4f mulOrthographicAffine(Matrix4f left, Matrix4f right, Matrix4f out) {
		float nm00 = left.m00 * right.m00;
        float nm10 = left.m11 * right.m10;
        float nm20 = left.m22 * right.m20;
        float nm01 = left.m00 * right.m01;
        float nm11 = left.m11 * right.m11;
        float nm21 = left.m22 * right.m21;
        float nm02 = left.m00 * right.m02;
        float nm12 = left.m11 * right.m12;
        float nm22 = left.m22 * right.m22;
        float nm03 = left.m00 * right.m03 + left.m03;
        float nm13 = left.m11 * right.m13 + left.m13;
        float nm23 = left.m22 * right.m23 + left.m23;
        out.m00 = nm00; out.m01 = nm01; out.m02 = nm02; out.m03 = nm03;
        out.m10 = nm10; out.m11 = nm11; out.m12 = nm12; out.m13 = nm13;
        out.m20 = nm20; out.m21 = nm21; out.m22 = nm22; out.m23 = nm23;
        out.m30 = 0f; out.m31 = 0f; out.m32 = 0f; out.m33 = 1f;
        out.prop = AFFINE;
        return out;
	}
	
	//~~~~~~~~~~~TRANSFORM~~~~~~~~~~~\\
	
	public Vector3f transform(Vector3f v) {return transform(this, v.x, v.y, v.z, v);}
	public Vector3f transform(Vector3f v, Vector3f out) {return transform(this, v.x, v.y, v.z, out);}
	public static Vector3f transform(Matrix4f m, Vector3f v, Vector3f out) {return transform(m, v.x, v.y, v.z, out);}
	public static Vector3f transform(Matrix4f m, float x, float y, float z, Vector3f out) {
		out.x = m.m00 * x + m.m01 * y + m.m02 * z + m.m03;
		out.y = m.m10 * x + m.m11 * y + m.m12 * z + m.m13;
		out.z = m.m20 * x + m.m21 * y + m.m22 * z + m.m23;
		return out;
	}
	
	//~~~~~~~~~~~INVERSE~~~~~~~~~~~\\
	//NEVER USE INVERSE EVER IT SUCKSSSSSSSS
	
	public Matrix4f invert() {return invert(this, this);}
	public Matrix4f invert(Matrix4f out) {return invert(this, out);}
	
	//EDITS NEEDED
	public static Matrix4f invert(Matrix4f m, Matrix4f out) {
        if ((m.prop & IDENTITY) != 0) return out.identity();
        if ((m.prop & AFFINE) != 0) return invertAffine(m, out);
        if ((m.prop & PERSPECTIVE) != 0) return invertPerspective(m, out);
        return invertGeneric(m, out);
    }
	public static Matrix4f invertGeneric(Matrix4f m, Matrix4f out) {
        float a = m.m00 * m.m11 - m.m01 * m.m10;
        float b = m.m00 * m.m12 - m.m02 * m.m10;
        float c = m.m00 * m.m13 - m.m03 * m.m10;
        float d = m.m01 * m.m12 - m.m02 * m.m11;
        float e = m.m01 * m.m13 - m.m03 * m.m11;
        float f = m.m02 * m.m13 - m.m03 * m.m12;
        float g = m.m20 * m.m31 - m.m21 * m.m30;
        float h = m.m20 * m.m32 - m.m22 * m.m30;
        float i = m.m20 * m.m33 - m.m23 * m.m30;
        float j = m.m21 * m.m32 - m.m22 * m.m31;
        float k = m.m21 * m.m33 - m.m23 * m.m31;
        float l = m.m22 * m.m33 - m.m23 * m.m32;
        float det = 1.0f / (a * l - b * k + c * j + d * i - e * h + f * g);
        float nm00 = ( m.m11 * l - m.m12 * k + m.m13 * j) * det;
        float nm01 = (-m.m01 * l + m.m02 * k - m.m03 * j) * det;
        float nm02 = ( m.m31 * f - m.m32 * e + m.m33 * d) * det;
        float nm03 = (-m.m21 * f + m.m22 * e - m.m23 * d) * det;
        float nm10 = (-m.m10 * l + m.m12 * i - m.m13 * h) * det;
        float nm11 = ( m.m00 * l - m.m02 * i + m.m03 * h) * det;
        float nm12 = (-m.m30 * f + m.m32 * c - m.m33 * b) * det;
        float nm13 = ( m.m20 * f - m.m22 * c + m.m23 * b) * det;
        float nm20 = ( m.m10 * k - m.m11 * i + m.m13 * g) * det;
        float nm21 = (-m.m00 * k + m.m01 * i - m.m03 * g) * det;
        float nm22 = ( m.m30 * e - m.m31 * c + m.m33 * a) * det;
        float nm23 = (-m.m20 * e + m.m21 * c - m.m23 * a) * det;
        float nm30 = (-m.m10 * j + m.m11 * h - m.m12 * g) * det;
        float nm31 = ( m.m00 * j - m.m01 * h + m.m02 * g) * det;
        float nm32 = (-m.m30 * d + m.m31 * b - m.m32 * a) * det;
        float nm33 = ( m.m20 * d - m.m21 * b + m.m22 * a) * det;
        out.m00 = nm00; out.m01 = nm01; out.m02 = nm02; out.m03 = nm03;
        out.m10 = nm10; out.m11 = nm11; out.m12 = nm12; out.m13 = nm13;
        out.m20 = nm20; out.m21 = nm21; out.m22 = nm22; out.m23 = nm23;
        out.m30 = nm30; out.m31 = nm31; out.m32 = nm32; out.m33 = nm33;
        out.prop = 0;
        return out;
    }
	public static Matrix4f invertPerspective(Matrix4f m, Matrix4f out) {
        float a =  1.0f / (m.m00 * m.m11);
        float l = -1.0f / (m.m23 * m.m32);
        float nm00 = m.m11 * a;
        float nm11 = m.m00 * a;
        float nm23 = -m.m23 * l;
        float nm32 = -m.m32 * l;
        float nm33 = m.m22 * l;
        out.m00 = nm00; 	out.m01 = 0f; 		out.m02 = 0f; 	out.m03 = 0f;
        out.m10 = 0f; 		out.m11 = nm11; 	out.m12 = 0f; 	out.m13 = 0f;
        out.m20 = 0f; 		out.m21 = 0f; 		out.m22 = 0f; 	out.m23 = nm23;
        out.m30 = 0f; 		out.m31 = 0f; 		out.m32 = nm32; out.m33 = nm33;
        out.prop = 0;
        return out;
    }
	public static Matrix4f invertPerspectiveView(Matrix4f persp, Matrix4f view, Matrix4f out) {
        float a =  1.0f / (persp.m00 * persp.m11);
        float l = -1.0f / (persp.m23 * persp.m32);
        float pm00 =  persp.m11 * a;
        float pm11 =  persp.m00 * a;
        float pm23 = -persp.m32 * l;
        float pm32 = -persp.m23 * l;
        float pm33 =  persp.m22 * l;
        float vm30 = -view.m00 * view.m03 - view.m10 * view.m13 - view.m20 * view.m23;
        float vm31 = -view.m01 * view.m03 - view.m11 * view.m13 - view.m21 * view.m23;
        float vm32 = -view.m02 * view.m03 - view.m12 * view.m13 - view.m22 * view.m23;
        out.m00 = view.m00 * pm00; 		out.m01 = view.m10 * pm11; 		out.m02 = vm30 * pm23; 		out.m03 = view.m20 * pm32 + vm30 * pm33;
        out.m10 = view.m01 * pm00; 		out.m11 = view.m11 * pm11; 		out.m12 = vm31 * pm23; 		out.m13 = view.m21 * pm32 + vm31 * pm33;
        out.m20 = view.m02 * pm00; 		out.m21 = view.m12 * pm11; 		out.m22 = vm32 * pm23; 		out.m23 = view.m22 * pm32 + vm32 * pm33;
        out.m30 = 0f; 					out.m31 = 0f; 					out.m32 = pm23; 			out.m33 = pm33;
        out.prop = 0;
        return out;
    }
	public static Matrix4f invertAffine(Matrix4f m, Matrix4f out) {
        float m11m00 = m.m00 * m.m11, m10m01 = m.m01 * m.m10, m10m02 = m.m02 * m.m10;
        float m12m00 = m.m00 * m.m12, m12m01 = m.m01 * m.m12, m11m02 = m.m02 * m.m11;
        float s = 1.0f / ((m11m00 - m10m01) * m.m22 + (m10m02 - m12m00) * m.m21 + (m12m01 - m11m02) * m.m20);
        float m10m22 = m.m10 * m.m22, m10m21 = m.m10 * m.m21, m11m22 = m.m11 * m.m22;
        float m11m20 = m.m11 * m.m20, m12m21 = m.m12 * m.m21, m12m20 = m.m12 * m.m20;
        float m20m02 = m.m20 * m.m02, m20m01 = m.m20 * m.m01, m21m02 = m.m21 * m.m02;
        float m21m00 = m.m21 * m.m00, m22m01 = m.m22 * m.m01, m22m00 = m.m22 * m.m00;
        float nm00 = (m11m22 - m12m21) * s;
        float nm01 = (m21m02 - m22m01) * s;
        float nm02 = (m12m01 - m11m02) * s;
        float nm10 = (m12m20 - m10m22) * s;
        float nm11 = (m22m00 - m20m02) * s;
        float nm12 = (m10m02 - m12m00) * s;
        float nm20 = (m10m21 - m11m20) * s;
        float nm21 = (m20m01 - m21m00) * s;
        float nm22 = (m11m00 - m10m01) * s;
        float nm03 = (m22m01 * m.m13 - m12m01 * m.m23 + m11m02 * m.m23 - m11m22 * m.m03 + m12m21 * m.m03 - m21m02 * m.m13) * s;
        float nm13 = (m20m02 * m.m13 - m10m02 * m.m23 + m12m00 * m.m23 - m12m20 * m.m03 + m10m22 * m.m03 - m22m00 * m.m13) * s;
        float nm23 = (m11m20 * m.m03 - m10m21 * m.m03 + m21m00 * m.m13 - m20m01 * m.m13 + m10m01 * m.m23 - m11m00 * m.m23) * s;
        out.m00 = nm00; out.m01 = nm01; out.m02 = nm02; out.m03 = nm03;
        out.m10 = nm10; out.m11 = nm11; out.m12 = nm12; out.m13 = nm13;
        out.m20 = nm20; out.m21 = nm21; out.m22 = nm22; out.m23 = nm23;
        out.m30 = 0f; out.m31 = 0f; out.m32 = 0f; out.m33 = 1f;
        out.prop = AFFINE;
        return out;
    }
	
	//~~~~~~~~~~~DETERMINANT~~~~~~~~~~~\\
	
	public float determinant() {
        if ((prop & AFFINE) != 0)
            return determinantAffine();
        return (m00 * m11 - m01 * m10) * (m22 * m33 - m23 * m32)
             + (m02 * m10 - m00 * m12) * (m21 * m33 - m23 * m31)
             + (m00 * m13 - m03 * m10) * (m21 * m32 - m22 * m31)
             + (m01 * m12 - m02 * m11) * (m20 * m33 - m23 * m30)
             + (m03 * m11 - m01 * m13) * (m20 * m32 - m22 * m30)
             + (m02 * m13 - m03 * m12) * (m20 * m31 - m21 * m30);
    }	
	public float determinantAffine() {
        return (m00 * m11 - m01 * m10) * m22
             + (m20 * m01 - m00 * m21) * m12
             + (m10 * m21 - m20 * m11) * m02;
    }
	
	//~~~~~~~~~~~PROJECTION~~~~~~~~~~~\\
	
	public Matrix4f perspective(float fov, float aspect, float zNear, float zFar) {return perspective(fov, aspect, zNear, zFar, this);}
	public static Matrix4f perspective(float fov, float aspect, float zNear, float zFar, Matrix4f out){
		float tanHalfFOV = 1.0f / (float)Math.tan(fov * 0.5f), 
				zRange = 1.0f / (zFar - zNear);
		out.m00 = tanHalfFOV / aspect; 
			out.m11 = tanHalfFOV; 
				out.m22 = -(zFar + zNear) * zRange; 	out.m23 = -(zFar + zFar) * zNear * zRange; 
				out.m32 = -1.0f;	out.m33 = 0f;
		out.prop = PERSPECTIVE;
		return out;
	}
	
	public Matrix4f orthographic(float left, float right, float bottom, float top, float near, float far) {return orthographic(left, right, bottom, top, near, far, this);}
	public static Matrix4f orthographic(float left, float right, float bottom, float top, float near, float far, Matrix4f out){
		float width = 1.0f / (right - left), height = 1.0f / (top - bottom), depth = 1.0f / (far - near);
		out.m00 = 2 * width; out.m11 = 2 * height; out.m22 = -2 * depth;
		out.m03 = -(right + left)*width; out.m13 = -(top + bottom)*height; out.m23 = -(far + near)*depth;
		out.m33 = 1.0f;
		out.prop = AFFINE | ORTHOGRAPHIC;
		return out;
	}
	
	//~~~~~~~~~~~LOOK AT~~~~~~~~~~~\\
	
	public Matrix4f setLookAt(Vector3f pos, Vector3f dir, Vector3f up, Vector3f right) {
		m00 = right.x; 	m01 = right.y; 	m02 = right.z; 	m03 = -(right.x * pos.x + right.y * pos.y + right.z * pos.z);
		m10 = up.x;		m11 = up.y;		m12 = up.z;		m13 = -(up.x * pos.x + up.y * pos.y + up.z * pos.z);
		m20 = dir.x;	m21 = dir.y;	m22 = dir.z;	m23 = -(dir.x * pos.x + dir.y * pos.y + dir.z * pos.z);
		m30 = 0f;		m31 = 0f;		m32 = 0f;		m33 = 1f;
		//Compare.assertMat(this, new org.joml.Matrix4f().lookAt(pos.x, pos.y, pos.z, pos.x - dir.x, pos.y - dir.y, pos.z - dir.z, 0,1,0));
		prop = AFFINE;
		return this;
	}
	public Matrix4f setLookAt(Vector3f pos) {return setLookAt(pos.x, pos.y, pos.z, 0f, 0f, 0f, 0f, 1f, 0f);} 
	public Matrix4f setLookAt(Vector3f pos, Vector3f target) {return setLookAt(pos.x, pos.y, pos.z, target.x, target.y, target.z, 0f, 1f, 0f);} 
	public Matrix4f setLookAt(Vector3f pos, Vector3f target, Vector3f up) {return setLookAt(pos.x, pos.y, pos.z, target.x, target.y, target.z, up.x, up.y, up.z);}
	public Matrix4f setLookAt(	float posx, float posy, float posz) {return setLookAt(posx, posy, posz, 0f, 0f, 0f, 0f, 1f, 0f);}
	public Matrix4f setLookAt(	float posx, float posy, float posz, float targetx, float targety, float targetz) {return setLookAt(posx, posy, posz, targetx, targety, targetz, 0f, 1f, 0f);}
	public Matrix4f setLookAt(	float posx, float posy, float posz,
            					float targetx, float targety, float targetz,
            					float upx, float upy, float upz) {
		// Compute direction from position to lookAt
		float dirX = posx - targetx;
		float dirY = posy - targety;
		float dirZ = posz - targetz;
		// Normalize direction
		float invDirLength = 1.0f / (float) Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
		dirX *= invDirLength;
		dirY *= invDirLength;
		dirZ *= invDirLength;
		// left = up x direction
		float leftX = upy * dirZ - upz * dirY;
		float leftY  = upz * dirX - upx * dirZ;
		float leftZ = upx * dirY - upy * dirX;
		// normalize left
		float invLeftLength = 1.0f / (float) Math.sqrt(leftX * leftX + leftY * leftY + leftZ * leftZ);
		leftX *= invLeftLength;
		leftY *= invLeftLength;
		leftZ *= invLeftLength;
		// up = direction x left
		float upnX = dirY * leftZ - dirZ * leftY;
		float upnY = dirZ * leftX - dirX * leftZ;
		float upnZ = dirX * leftY - dirY * leftX;
		
		m00 = leftX; 	m01 = leftY; 	m02 = leftZ; 	m03 = -(leftX * posx + leftY * posy + leftZ * posz);
		m10 = upnX;		m11 = upnY;		m12 = upnZ;		m13 = -(upnX * posx + upnY * posy + upnZ * posz);
		m20 = dirX;		m21 = dirY;		m22 = dirZ;		m23 = -(dirX * posx + dirY * posy + dirZ * posz);
		m30 = 0f;		m31 = 0f;		m32 = 0f;		m33 = 1f;
		
		prop = AFFINE;
		return this;
	}
	
	//~~~~~~~~~~~TRANSPOSE~~~~~~~~~~~\\
	
	public Matrix4f transpose() {return transpose(this, this);}
	public Matrix4f transpose(Matrix4f out) {return transpose(this, out);}
	
	public static Matrix4f transpose(Matrix4f m, Matrix4f out) {
        float nm10 = m.m01;
        float nm20 = m.m02, nm21 = m.m12;
        float nm30 = m.m03, nm31 = m.m13, nm32 = m.m23;
        out.m00 = m.m00; out.m01 = m.m10; out.m02 = m.m20; out.m03 = m.m30;
        out.m10 = nm10; out.m11 = m.m11; out.m12 = m.m21; out.m13 = m.m31;
        out.m20 = nm20; out.m21 = nm21; out.m22 = m.m22; out.m23 = m.m32;
        out.m30 = nm30; out.m31 = nm31; out.m32 = nm32; out.m33 = m.m33;
        out.prop &= ~PERSPECTIVE;
        return out;
	}
	
	//~~~~~~~~~~~MISC~~~~~~~~~~~\\
	
	public String toString() {
		return 	m00 + ", " + m01 + ", " + m02 + ", " + m03 + "\n" +
				m10 + ", " + m11 + ", " + m12 + ", " + m13 + "\n" +
				m20 + ", " + m21 + ", " + m22 + ", " + m23 + "\n" +
				m30 + ", " + m31 + ", " + m32 + ", " + m33;
	}
}
