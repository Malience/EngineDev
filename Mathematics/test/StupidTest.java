
public class StupidTest {
	public static void main(String [] args) {
		long times = 20;
		long loops = 100000000L;
		
		
		
		
		Stupid1 cast = new Stupid2();
		Stupid2 nocast = new Stupid2();
		
		for(long i = 0; i < loops; i++) {
			((Stupid2)cast).add(4, 8);
		}
		
		for(long i = 0; i < loops; i++) {
			nocast.add(4, 8);
		}
		
		long casttime = 0, nocasttime = 0;
		long nanotime = System.nanoTime();
		
		for(long i = 0; i < loops; i++) {
			((Stupid2)cast).add(4, 8);
		}
		casttime += (System.nanoTime() - nanotime);
		System.out.println("Cast Time = " + (System.nanoTime() - nanotime));
		
		nanotime = System.nanoTime();
		for(long i = 0; i < loops; i++) {
			nocast.add(4, 8);
		}
		nocasttime += (System.nanoTime() - nanotime);
		System.out.println("Nocast Time = " + (System.nanoTime() - nanotime));
		nanotime = System.nanoTime();
		
		for(long i = 0; i < loops; i++) {
			((Stupid2)cast).add(4, 8);
		}
		casttime += (System.nanoTime() - nanotime);
		System.out.println("Cast Time = " + (System.nanoTime() - nanotime));
		nanotime = System.nanoTime();
		
		for(long i = 0; i < loops; i++) {
			((Stupid2)cast).add(4, 8);
		}
		casttime += (System.nanoTime() - nanotime);
		System.out.println("Cast Time = " + (System.nanoTime() - nanotime));
		nanotime = System.nanoTime();
		for(long i = 0; i < loops; i++) {
			nocast.add(4, 8);
		}
		nocasttime += (System.nanoTime() - nanotime);
		System.out.println("Nocast Time = " + (System.nanoTime() - nanotime));
		
		nanotime = System.nanoTime();
		for(long i = 0; i < loops; i++) {
			nocast.add(4, 8);
		}
		nocasttime += (System.nanoTime() - nanotime);
		System.out.println("Nocast Time = " + (System.nanoTime() - nanotime));
		
		
		System.out.println("\n Avg. cast time = " + casttime/3f);
		System.out.println("\n Avg. nocast time = " + nocasttime/3f);
		System.out.println(casttime/(float)nocasttime);
		
		nocasttime = casttime = 0;
		
		for(long j = 0; j < times; j++) {
			nanotime = System.nanoTime();
			for(long i = 0; i < loops; i++) {
				if(cast instanceof Stupid2)
					((Stupid2)cast).add(4, 8 + j);
			}
			casttime += (System.nanoTime() - nanotime);
		}
		
		for(long j = 0; j < times; j++) {
			nanotime = System.nanoTime();
			for(long i = 0; i < loops; i++) {
				nocast.add(4, 8 + j);
			}
			nocasttime += (System.nanoTime() - nanotime);
		}
		
		
		
		System.out.println("\n Avg. cast time = " + casttime/(float)times);
		System.out.println("\n Avg. nocast time = " + nocasttime/(float)times);
		System.out.println(casttime/(float)nocasttime);
	}
	
	static class Stupid1{
		float x, y;
		
	}
	
	static class Stupid2 extends Stupid1{
		public void add(float a, float b) {
			x = y + a + b;
			y = a + b * a;
		}
	}
}
