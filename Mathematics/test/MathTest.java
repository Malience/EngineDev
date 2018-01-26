

//import java.nio.FloatBuffer;

import math.MathUtil;
import math.MathUtilOLD;

public class MathTest {
	public static int num_tests = 1000000;
	public static float[] preset, dataset;
	public static float max = 500f;
	public static float min = -500f;
	static {
		preset = new float[num_tests];
		dataset = new float[num_tests];
		for(int i = 0; i < num_tests; i++){
			preset[i] = (float) (Math.random() * (max - min)) + min;
			dataset[i] = (float) (Math.random() * (max - min)) + min;
		}
	}
	
	public static FloatTest[] tests = {
			MathUtilOLD::sin,
			MathUtilOLD::accsin,
			(float f) -> {return (float)Math.sin(f*MathUtil.RAD);},
//			MathUtil::cos,
//			(float f) -> {return (float)Math.cos(f);},
//			MathUtil::tan,
//			(float f) -> {return (float)Math.tan(f);},
//			MathUtil::cot,
//			(float f) -> {return 1/(float)Math.tan(f);}
	};
	
	public static void main(String [] args){
		//Warmup
		for(int i = 0; i < tests.length; i++) tests[i].run(preset[i]);
		
		for(int i = 0; i < tests.length; i++){
			long time = System.currentTimeMillis();
			//float out = runTest(tests[i]);
			System.out.println("Test #" + i + ": " + (System.currentTimeMillis() - time));
		}
		
//		for(float i = -360; i <= 360; i += 1/16f){
//			System.out.println("Error at " + i + ": " + ((float)Math.sin(i * MathUtil.PI) - MathUtil.sin(i)));
//		
//		}
		
		test(-359.9f);
	}
	
	public static void test(float f){
		System.out.println(MathUtilOLD.cos(f));
		System.out.println(MathUtilOLD.accsin(f));
		System.out.println((float)Math.cos(f * MathUtil.RAD));
	}
	
	static float runTest(FloatTest test){
		float out = 0;
		for(int i = 0; i < num_tests; i++){
			out += test.run(dataset[i]);
		}
		return out;
	}
	
	@FunctionalInterface
	private interface FloatTest{float run(float f);}
}
