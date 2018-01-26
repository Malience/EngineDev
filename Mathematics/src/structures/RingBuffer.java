package structures;

public class RingBuffer<T> {
	T[] buffer;
	int read, write;
	
	@SuppressWarnings("unchecked")
	public RingBuffer(int size){buffer = (T[])new Object[size];}
	
	public void write(T type){
		buffer[write] = type;
		write++;
		if(write >= buffer.length) write = 0;
		if(write == read){read++; if(read >= buffer.length) read = 0;}
	}
	
	public T read(){
		if(read == write) return null;
		T out = buffer[read];
		read++; if(read >= buffer.length) read = 0;
		return out;
	}
	
	public boolean isEmpty(){return read == write;}
}
