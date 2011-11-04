
public interface Point_itf extends SharedObject_itf {

	public void deplacer(double x, double y);
	
	public double distance(Point_itf p);
	
	public double getX();
	
	public double getY();
	
	public String toString();
	
}
