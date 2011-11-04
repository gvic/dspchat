
public class Point implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private double x;
	private double y;

	public Point() {
		x = 0;
		y = 0;
	}
	
	public Point(double _x, double _y) {
		x = _x;
		y = _y;
	}
	
	public void deplacer(double _x, double _y) {
		x = _x;
		y = _y;
	}
	
	public double distance(Point_itf p) {
		return Math.sqrt((p.getX() - x)*(p.getX() - x) + (p.getY() - y)*(p.getY() - y));
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public String toString() {
		return "Point("+x+","+y+")";
	}
	
}
