package com.base.engine.physics;

import math.MathUtil;
import math.Vector3f;
import primitives.Capsule;

public class InertiaTensors {
	public static Vector3f rigidBoxTensor(float halfx, float halfy, float halfz) {
		float xsqrd = halfx * halfx, ysqrd = halfy * halfy, zsqrd = halfz * halfz;
		return new Vector3f(3.0f/(ysqrd + zsqrd), 3.0f/(xsqrd + zsqrd), 3.0f/(xsqrd + ysqrd));
	}
	
	public static Vector3f rigidSphereTensor(float radius) {
		float value = 2.5f / (radius * radius);
		return new Vector3f(value, value, value);
	}
	
	public static Vector3f rigidCapsuleTensor(Capsule c) {
		float h = c.a.distance(c.b);
		float r = c.r;
		float r2 = r * r;
		float h2 = h * h;
		float mcy = h * r2 * MathUtil.PI;
		float mhs = 2f * MathUtil.THIRD * r * r2 * MathUtil.PI;
		float nxz = mcy * (h2 * MathUtil.TWELFTH + r2 * 0.25f) + (mhs + mhs) * (r2 * 0.4f + h2 * 0.5f + 3f * h * r * 0.125f);
		return new Vector3f(nxz, mcy * r2 * 0.5f + mhs * r2 * 0.8f, nxz);
	}
}
