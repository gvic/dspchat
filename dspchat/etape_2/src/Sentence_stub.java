public class Sentence_stub extends SharedObject implements Sentence_itf,
		java.io.Serializable {

	private static final long serialVersionUID = 1L;

	public Sentence_stub(Object o, int i) {
		super(o, i);
	}

	public void write(String text) {
		Sentence s = (Sentence) obj;
		s.write(text);
	}

	public String read() {
		Sentence s = (Sentence) obj;
		return s.read();
	}

}