public class Entier implements java.io.Serializable {
	
	
	private static final long serialVersionUID = 1L;
	
	Integer 		data;
	
	public Entier() {
		data = new Integer(0);
	}
	
	public void incrementer() {
		data++;
	}
	
	public void decrementer() {
		data--;
	}
	
	public void raz() {
		data = 0;
	}
	
	public Integer read() {
		return data;	
	}
	
}