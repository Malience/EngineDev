

import math.Compare;
import math.Matrix3f;
import math.Matrix4f;
import math.Vector3f;

public class MatrixTest {

	public static void main(String [] args) {
		Matrix4f m0 = new Matrix4f();
		Matrix4f m1 = new Matrix4f();
		Matrix3f m2 = new Matrix3f();
		
		org.joml.Matrix4f j0 = new org.joml.Matrix4f();
		org.joml.Matrix4f j1 = new org.joml.Matrix4f();
		org.joml.Matrix3f j2 = new org.joml.Matrix3f();
		
		Compare.assertMat(m0, j0);
		
		Compare.assertMat(m2, j2);
		
//		m0.translationRotateScale(2, 8, 5, 1, 3, 1, 1, 22, 2, 20);
//		m1.translationRotateScale(1, 3, 8, 2, 1, 3, 2, 7, 8, 3);
//		j0.translationRotateScale(2, 8, 5, 1, 3, 1, 1, 22, 2, 20);
//		j1.translationRotateScale(1, 3, 8, 2, 1, 3, 2, 7, 8, 3);
//		
//		
//		Matrix4f.mulOrthographicAffine(m0, m1, m0);
//		j0.mulOrthoAffine(j1);
//		
//		
//		m0.scale(30f, 40f, 50f);
//		j0.scale(30f, 40f, 50f);
		m0.setLookAt(0, 1, 20, 90, 0, 1, 0, 1, 0);
		
		Vector3f pos = new Vector3f(0, 1, 20);
		Vector3f target = new Vector3f(90, 0, 1);
		Vector3f up = new Vector3f(0, 1, 0);
		Vector3f right = new Vector3f();
		
		Vector3f forward = pos.sub(target, new Vector3f());
		
		float dirX = pos.x - target.x;
		float dirY = pos.y - target.y;
		float dirZ = pos.z - target.z;
		float invDirLength = 1.0f / (float) Math.sqrt(dirX * dirX + dirY * dirY + dirZ * dirZ);
		dirX *= invDirLength;
		dirY *= invDirLength;
		dirZ *= invDirLength;
		forward.normalize();
		
		
//		System.out.println("x: " + dirX + " y: " + dirY + " z: " + dirZ);
//		System.out.println(forward);
		
		
		up.cross(forward, right).normalize(); 
		forward.cross(right, up).normalize();
		
//		System.out.println(forward);
		m1.setLookAt(pos, forward, up, right);
		//m0.perspective(60f, 800f/600f, .1f, 1000f);
		j0.lookAt(0, 1, 20, 90, 0, 1, 0, 1, 0);
		//j0.perspective(60f, 800f/600f, .1f, 1000f);
		
		m2.m00 = 2f; m2.m01 = 5f; m2.m02 = 7f;
		m2.m10 = 23f; m2.m11 = 25f; m2.m12 = 7f;
		m2.m20 = 22f; m2.m21 = 65f; m2.m22 = 17f;
		
		j2.m00 = 2f; j2.m01 = 5f; j2.m02 = 7f;
		j2.m10 = 23f; j2.m11 = 25f; j2.m12 = 7f;
		j2.m20 = 22f; j2.m21 = 65f; j2.m22 = 17f;
		j2.transpose();
		
		Compare.assertMat(m0, j0);
		Compare.assertMat(m2, j2);
		
		Matrix3f m3 = new Matrix3f();
		m3.m00 = 22f; m3.m01 = 5f; m3.m02 = 27f;
		m3.m10 = 3f; m3.m11 = 15f; m3.m12 = 7f;
		m3.m20 = 2f; m3.m21 = 6f; m3.m22 = 17;
		
		org.joml.Matrix3f j3 = new org.joml.Matrix3f();
		j3.m00 = 22f; j3.m01 = 5f; j3.m02 = 27f;
		j3.m10 = 3f; j3.m11 = 15f; j3.m12 = 7f;
		j3.m20 = 2f; j3.m21 = 6f; j3.m22 = 17;
		
		
		j3.transpose();
		
		Vector3f v0 = new Vector3f(6,2,10);
		org.joml.Vector3f jv0 = new org.joml.Vector3f(6,2,10);
		
		m2.invert();
		j2.invert();
		
		m2.transpose();
		j2.transpose();
		
		Compare.assertMat(m2, j2);
		
		m2.mulTranspose(m3, m2);
		j2.transpose().mul(j3);
		
		System.out.println(m2);
		System.out.println(j2);
		
		m2.transform(v0);
		j2.transform(jv0);
		
		System.out.println(v0);
		System.out.println(jv0);
		
		Compare.assertMat(m2, j2);
//		System.out.println(m0);
//		System.out.println(m1);
//		System.out.println(j0);
	}

}
