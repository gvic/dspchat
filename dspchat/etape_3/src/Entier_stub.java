public class Entier_stub extends SharedObject implements Entier_itf,
		java.io.Serializable {

	private static final long serialVersionUID = 1L;

	/* Constructors */

	public Entier_stub() {
		super();
	}

	public Entier_stub(java.lang.Object var0, int var1) {
		super(var0, var1);
	}

	/* Methods */

	public java.lang.Integer read() {
		Entier vloc = (Entier) obj;
		return vloc.read();
	}

	public void incrementer() {
		Entier vloc = (Entier) obj;
		vloc.incrementer();
	}

	public void decrementer() {
		Entier vloc = (Entier) obj;
		vloc.decrementer();
	}

	public void raz() {
		Entier vloc = (Entier) obj;
		vloc.raz();
	}

}