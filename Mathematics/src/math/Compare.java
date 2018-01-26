package math;
public class Compare {

	public static boolean mat(Matrix4f m, org.joml.Matrix4f joml) {
		//m = m.transpose(new Matrix4f());
		return 	m.m00 == joml.m00() && m.m01 == joml.m01() && m.m02 == joml.m02() && m.m03 == joml.m03() &&
				m.m10 == joml.m10() && m.m11 == joml.m11() && m.m12 == joml.m12() && m.m13 == joml.m13() &&
				m.m20 == joml.m20() && m.m21 == joml.m21() && m.m22 == joml.m22() && m.m23 == joml.m23() &&
				m.m30 == joml.m30() && m.m31 == joml.m31() && m.m32 == joml.m32() && m.m33 == joml.m33();
	}
	
	public static boolean mat(Matrix3f m, org.joml.Matrix3f joml) {
		//m = m.transpose(new Matrix4f());
		return 	m.m00 == joml.m00() && m.m01 == joml.m01() && m.m02 == joml.m02() &&
				m.m10 == joml.m10() && m.m11 == joml.m11() && m.m12 == joml.m12() &&
				m.m20 == joml.m20() && m.m21 == joml.m21() && m.m22 == joml.m22();
	}
	
	public static void assertMat(Matrix4f m, org.joml.Matrix4f joml) {
		m = m.transpose(new Matrix4f());
		if(!mat(m, joml)) {
			System.err.println("Matrices are not equal!");
			System.err.println(m.m00 + ", " + m.m01 + ", " + m.m02 + ", " + m.m03 + "\t\t != \t\t" + joml.m00() + ", " + joml.m01() + ", " + joml.m02() + ", " + joml.m03());
			System.err.println(m.m10 + ", " + m.m11 + ", " + m.m12 + ", " + m.m13 + "\t\t != \t\t" + joml.m10() + ", " + joml.m11() + ", " + joml.m12() + ", " + joml.m13());
			System.err.println(m.m20 + ", " + m.m21 + ", " + m.m22 + ", " + m.m23 + "\t\t != \t\t" + joml.m20() + ", " + joml.m21() + ", " + joml.m22() + ", " + joml.m23());
			System.err.println(m.m30 + ", " + m.m31 + ", " + m.m32 + ", " + m.m33 + "\t\t != \t\t" + joml.m30() + ", " + joml.m31() + ", " + joml.m32() + ", " + joml.m33());
		}
	}
	
	public static void assertMat(Matrix3f m, org.joml.Matrix3f joml) {
		m = m.transpose(new Matrix3f());
		if(!mat(m, joml)) {
			System.err.println("Matrices are not equal!");
			System.err.println(m.m00 + ", " + m.m01 + ", " + m.m02 + "\t\t != \t\t" + joml.m00() + ", " + joml.m01() + ", " + joml.m02());
			System.err.println(m.m10 + ", " + m.m11 + ", " + m.m12 + "\t\t != \t\t" + joml.m10() + ", " + joml.m11() + ", " + joml.m12());
			System.err.println(m.m20 + ", " + m.m21 + ", " + m.m22 + "\t\t != \t\t" + joml.m20() + ", " + joml.m21() + ", " + joml.m22());
		}
	}
}
