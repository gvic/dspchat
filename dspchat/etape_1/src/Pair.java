
public class Pair<T1, T2> {

	private T1 first;
	private T2 second;
	
	public Pair() {
	}
	
	public Pair(T1 f, T2 s) {
		this();
		setFirst(f);
		setSecond(s);
	}

	public void setFirst(T1 first) {
		this.first = first;
	}

	public T1 getFirst() {
		return first;
	}

	public void setSecond(T2 second) {
		this.second = second;
	}

	public T2 getSecond() {
		return second;
	}
	
}
