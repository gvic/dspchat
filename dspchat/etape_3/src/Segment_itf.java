
public interface Segment_itf extends SharedObject_itf {

	public Point_itf getE1();
	
	public Point_itf getE2();
	
	public void setE1(Point_itf p);
	
	public void setE2(Point_itf p);
	
	public double longueur();
	
	public Point milieu();
	
}
