public class Point_stub extends SharedObject implements Point_itf, java.io.Serializable {

	private static final long serialVersionUID = 1L;


	/* Constructors */

	public Point_stub(){
		super();
	}

	public Point_stub(java.lang.Object var0, int var1){
		super(var0, var1);
	}

	

	/* Methods */

	public java.lang.String toString(){
		Point vloc = (Point) obj;
		return vloc.toString();
	}

	public double distance(Point_itf var0){
		Point vloc = (Point) obj;
		return vloc.distance(var0);
	}

	public void deplacer(double var0, double var1){
		Point vloc = (Point) obj;
		vloc.deplacer(var0, var1);
	}

	public double getX(){
		Point vloc = (Point) obj;
		return vloc.getX();
	}

	public double getY(){
		Point vloc = (Point) obj;
		return vloc.getY();
	}

	
}