public class Segment_stub extends SharedObject implements Segment_itf, java.io.Serializable {

	private static final long serialVersionUID = 1L;


	/* Constructors */

	public Segment_stub(){
		super();
	}

	public Segment_stub(java.lang.Object var0, int var1){
		super(var0, var1);
	}

	

	/* Methods */

	public  Point_itf getE1(){
		Segment vloc = (Segment) obj;
		return vloc.getE1();
	}

	public Point_itf getE2(){
		Segment vloc = (Segment) obj;
		return vloc.getE2();
	}

	public void setE1(Point_itf var0){
		Segment vloc = (Segment) obj;
		vloc.setE1(var0);
	}

	public void setE2(Point_itf var0){
		Segment vloc = (Segment) obj;
		vloc.setE2(var0);
	}

	public double longueur(){
		Segment vloc = (Segment) obj;
		return vloc.longueur();
	}

	public Point milieu(){
		Segment vloc = (Segment) obj;
		return vloc.milieu();
	}

	
}