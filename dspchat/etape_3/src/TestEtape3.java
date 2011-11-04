import java.awt.*;
import java.awt.event.*;
import java.rmi.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.rmi.registry.*;

public class TestEtape3 extends Frame {

	private static final long serialVersionUID = 1L;
	public TextArea text;
	public TextField dataX;
	public TextField dataY;
	Segment_itf segment;
	static String myName;
	
	static int pointId = 10;

	public static void main(String argv[]) {

		if (argv.length != 1) {
			System.out.println("java TestEtape3 <name>");
			return;
		}
		myName = argv[0];

		// initialize the system
		Client.init();

		// look up the IRC object in the name server
		// if not found, create it, and register it in the name server
//		Point_itf p1 = (Point_itf) Client.lookup("p1");
//		if (p1 == null) {
//			p1 = (Point_itf) Client.create(new Point(1,2));
//			Client.register("p1", p1);
//		}
//		
//		Point_itf p2 = (Point_itf) Client.lookup("p2");
//		if (p2 == null) {
//			p2 = (Point_itf) Client.create(new Point());
//			Client.register("p2", p2);
//		}
//		
//		
//		p1.lock_read();
//		p2.lock_read();
		
		Segment_itf s = (Segment_itf) Client.lookup("segment");
//		if (s == null) {
//			s = (Segment_itf) Client.create(new Segment(p1, p2));
//			Client.register("segment", s);
//		}
//		
//		p1.unlock();
//		p2.unlock();
		
		// create the graphical part
		new TestEtape3(s);
	}

	public TestEtape3(Segment_itf s) {

		setLayout(new FlowLayout());

		text = new TextArea(10, 60);
		text.setEditable(false);
		text.setForeground(Color.red);
		add(text);

		dataX = new TextField(60);
		add(dataX);
		dataY = new TextField(60);
		add(dataY);

		Button write_button = new Button("write p1");
		write_button.addActionListener(new writeP1Listener(this));
		add(write_button);
		Button read_button = new Button("read s.p1");
		read_button.addActionListener(new readSP1Listener(this));
		add(read_button);

		setSize(470, 300);
		text.setBackground(Color.black);
		show();

		segment = s;
	}
}

class readSP1Listener implements ActionListener {
	TestEtape3 etape3;

	public readSP1Listener(TestEtape3 i) {
		etape3 = i;
	}

	public void actionPerformed(ActionEvent e) {

		// lock the object in read mode
		etape3.segment.lock_read();

		// invoke the method
		Point_itf p = etape3.segment.getE1();

		// unlock the object
		etape3.segment.unlock();

		// display the read value
		etape3.text.append("Extremite1 de s = "+ p.toString() + "\n");
	}
}

class readSOsListener implements ActionListener {
	TestEtape3 etape3;

	public readSOsListener(TestEtape3 i) {
		etape3 = i;
	}

	public void actionPerformed(ActionEvent e) {

		// lock the object in read mode
		etape3.segment.lock_read();

		// invoke the method
		Point_itf p = etape3.segment.getE1();

		// unlock the object
		etape3.segment.unlock();

		// display the read value
		etape3.text.append("Extremite1 de s = "+ p.toString() + "\n");
	}
}

class writeP1Listener implements ActionListener {
	TestEtape3 etape3;

	public writeP1Listener(TestEtape3 i) {
		etape3 = i;
	}

	public void actionPerformed(ActionEvent e) {

		// get the value to be written from the buffer
		String sX = etape3.dataX.getText();
		String sY = etape3.dataY.getText();
		double X, Y;
		
		try {
			X = Double.parseDouble(sX);
			Y = Double.parseDouble(sY);
			
			Point_itf p3 = (Point_itf) Client.lookup("p"+TestEtape3.pointId);
			if (p3 == null) {
				p3 = (Point_itf) Client.create(new Point(X,Y));
				Client.register("p"+TestEtape3.pointId, p3);
			}
			TestEtape3.pointId++;
			
			
			// lock the object in write mode
			etape3.segment.lock_write();

			p3.lock_read();
			// invoke the method
			etape3.segment.setE1(p3);
			p3.unlock();
			etape3.dataX.setText("");
			etape3.dataY.setText("");

			// unlock the object
			etape3.segment.unlock();
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
		}

	}
}
