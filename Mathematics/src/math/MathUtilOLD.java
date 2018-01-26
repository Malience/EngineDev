package math;

public class MathUtilOLD {
	//private static final int MATH_PRECISION = 128;
	static final float PI = (float) Math.PI;
	static final float PI2 = (float) Math.PI * 2;
	static final float PIHalf = (float) Math.PI / 2;
	static final float RAD = PI / 180f;
	static final float DEG = 1/RAD;
	
	private static final float[] COS;
	private static final float[] SIN;
	private static final float[] TAN;
	
	static {
		COS = new float[361];
		SIN = new float[361];
		TAN = new float[361];
		for(int i = 0; i <= 360; i++){
			COS[i] = (float) Math.cos(i * RAD);
			SIN[i] = (float) Math.sin(i * RAD);
			TAN[i] = (float) Math.tan(i * RAD);
		}
	}
	
	public static float sin(int i){return SIN[i];}
	public static float sin(float i){
		if(i < 0){
			i = Math.abs(i);
			int n = (int) i;
			return (i - n) * RAD - SIN[n%360];
		}
		int n = (int) i;
		return SIN[n%360] + (i - n) * RAD;
//		i = Math.abs(i - 90);
//		int n = (int) i;
//		return COS[n%360] + (i - n) * (i - n) * COS_COEFF;
	}
	
	//private static float ONESIXTH = 1/6f;
	//private static final float RAD180 = 180 * RAD;
	public static float accsin(float i){
		i = (Math.abs(i))%360;
		if(i > 180) {
			i -= 180;
			return -1 + i * i * COS_COEFF;
		}
		return 1 - i * i * COS_COEFF;
	}
	
	//private static final float RADROOT = (float) (RAD * Math.sqrt(0.5));
	private static final float COS_COEFF = RAD*RAD*0.5f;
	public static float cos(int i){return COS[i];}
	public static float cos(float i){
		i = Math.abs(i);
		int n = (int) i;
		return COS[n%360] + (i - n) * (i - n) * COS_COEFF;
	}
	
	public static float tan(int i){return TAN[i];}
	public static float tan(float i){
		if(i < 0){
			i = Math.abs(i);
			int n = (int) i;
			return -(i - n) * RAD - TAN[n%360];
		}
		int n = (int) i;
		return TAN[n%360] + (i - n) * RAD;
	}
	
	public static float cot(int i){return TAN[i];}
	public static float cot(float i){
		i = Math.abs(i + 90);
		int n = (int) i;
		return TAN[n%360] + (i - n) * RAD;
	}
	
	public static float abs(float i){return (float)Math.abs(i);}
	
	
	public static float barrycentricLerp(Vector3f p1, Vector3f p2, Vector3f p3, Vector2f pos) {
		float det = 1.0f / ((p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z));
		float l1 = ((p2.z - p3.z) * (pos.x - p3.x) + (p3.x - p2.x) * (pos.y - p3.z)) * det;
		float l2 = ((p3.z - p1.z) * (pos.x - p3.x) + (p1.x - p3.x) * (pos.y - p3.z)) * det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}
	
	public static float barrycentricLerp(Vector3f p1, Vector3f p2, Vector3f p3, float posx, float posy) {
		float det = 1.0f / ((p2.z - p3.z) * (p1.x - p3.x) + (p3.x - p2.x) * (p1.z - p3.z));
		float l1 = ((p2.z - p3.z) * (posx - p3.x) + (p3.x - p2.x) * (posy - p3.z)) * det;
		float l2 = ((p3.z - p1.z) * (posx - p3.x) + (p1.x - p3.x) * (posy - p3.z)) * det;
		float l3 = 1.0f - l1 - l2;
		return l1 * p1.y + l2 * p2.y + l3 * p3.y;
	}
	
	public static float barrycentricLerp(float x1, float y1, float z1, float x2, float y2, float z2, float x3, float y3, float z3, float posx, float posy) {
		float det = 1.0f / ((z2 - z3) * (x1 - x3) + (x3 - x2) * (z1 - z3));
		float l1 = ((z2 - z3) * (posx - x3) + (x3 - x2) * (posy - z3)) * det;
		float l2 = ((z3 - z1) * (posx - x3) + (x1 - x3) * (posy - z3)) * det;
		float l3 = 1.0f - l1 - l2;
		return l1 * y1 + l2 * y2 + l3 * y3;
	}
	
	public static float topLeftBarrycentricLerp(float y1, float y2, float y3, float posx, float posy) {
//		float l1 = 1.0f - posx - posy;
//		float l2 = posx;
//		float l3 = 1.0f - l1 - l2;
		return (1.0f - posx - posy) * y1 + posx * y2 + posy * y3;
	}
	
	public static float botRightBarrycentricLerp(float y1, float y2, float y3, float posx, float posy) {
//		float l1 = -posy + 1;
//		float l2 = posx + posy - 1;
//		float l3 = 1 - posy;
		return (1 - posy) * y1 + (posx + posy - 1) * y2 + (1 - posy) * y3;
	}
}
