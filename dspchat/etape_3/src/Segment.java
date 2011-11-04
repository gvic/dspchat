import java.io.Serializable;


public class Segment implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Point_itf extremite1;
	private Point_itf extremite2;
	
	public Segment(Point_itf p1, Point_itf p2) {
		extremite1 = p1;
		extremite2 = p2;
	}
	
	public Point_itf getE1() {
		return extremite1;
	}
	
	public Point_itf getE2() {
		return extremite2;
	}
	
	public void setE1(Point_itf p) {
		extremite1 = p;
	}
	
	public void setE2(Point_itf p) {
		extremite2 = p;
	}
	
	public double longueur() {
		return extremite1.distance(extremite2);
	}
	
	public Point milieu() {
		double x = (extremite2.getX()-extremite1.getX())/2;
		double y = (extremite2.getY()-extremite1.getY())/2;
		return new Point(x,y);
	}
	
}
