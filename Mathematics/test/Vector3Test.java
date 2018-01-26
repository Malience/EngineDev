import math.Vector3f;

public class Vector3Test {
	public static void main(String [] args) {
		Vector3f v0 = new Vector3f(2, 3, 4);
		Vector3f v1 = new Vector3f(6, 5, 8);
		
		org.joml.Vector3f j0 = new org.joml.Vector3f(2, 3, 4);
		org.joml.Vector3f j1 = new org.joml.Vector3f(6, 5, 8);
		
		System.out.println(v0.cross(v1));
		System.out.println(j0.cross(j1));
	}
}
