import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class ClientSimple extends JFrame {

	private static final long serialVersionUID = 1L;
	static final int STOP = 0;
	static final int INC = 1;
	static final int READ = 3;
	static final int RAZ = 4;

	Entier_itf entier;

	JTextArea lock;
	JTextArea value;
	JTextArea comment;

	int state = STOP;

	public static void main(String argv[]) {

		// initialize the system
		Client.init();

		// look up the IRC object in the name server
		// if not found, create it, and register it in the name server
		Entier_itf s = (Entier_itf) Client.lookup("Entier");
		if (s == null) {
			s = (Entier_itf) Client.create(new Entier());
			Client.register("Entier", s);
		}

		// create the graphical part
		ClientSimple one = new ClientSimple(s);

		boolean end = false;
		// The main loop
		while (!end) {
			// Show the lock state
			one.lock.setText(SharedObject
					.lockToString(((SharedObject) one.entier).lock));

			// What to do?
			switch (one.state) {
			case STOP:
				one.comment.setText("I am stopped!");
				break;

			case INC:
				one.comment.setText("Ask the lock_write");
				// lock the object in write mode
				one.entier.lock_write();
				one.comment.setText("Got the lock_write");

				// invoke the method
				one.entier.incrementer();

				one.comment.setText("Ask the unlock");
				// unlock the object
				one.entier.unlock();
				one.comment.setText("Got the unlock");
				break;

			case READ:
				one.comment.setText("Ask the lock_read");
				// lock the object in read mode
				one.entier.lock_read();
				one.comment.setText("Got the lock_read");

				// invoke the method
				Integer val = one.entier.read();
				// Show the value
				one.value.setText(val + "");

				one.comment.setText("Ask the unlock");
				// unlock the object
				one.entier.unlock();
				one.comment.setText("Got the unlock");
				break;

			case RAZ:
				one.comment.setText("Ask the lock_write");
				// lock the object in write mode
				one.entier.lock_write();
				one.comment.setText("Got the lock_write");

				// invoke the method
				one.entier.raz();

				one.comment.setText("Ask the unlock");
				// unlock the object
				one.entier.unlock();
				one.comment.setText("Got the unlock");

				one.state = ClientSimple.STOP;
				break;
			}
		}
	}

	@SuppressWarnings("deprecation")
	public ClientSimple(Entier_itf s) {
		entier = s;

		setLayout(new GridLayout(2, 4));

		lock = new JTextArea("", 3, 8);
		lock.setEditable(false);
		lock.setForeground(Color.red);
		lock.setMargin(new Insets(10, 40, 0, 0));
		lock.setBackground(Color.DARK_GRAY);
		add(lock);

		value = new JTextArea("", 3, 8);
		value.setEditable(false);
		value.setForeground(Color.green);
		value.setMargin(new Insets(10, 10, 0, 0));
		value.setBackground(Color.DARK_GRAY);
		add(value);

		comment = new JTextArea("", 3, 8);
		comment.setEditable(false);
		comment.setForeground(Color.white);
		comment.setMargin(new Insets(10, 5, 0, 0));
		comment.setBackground(Color.DARK_GRAY);
		add(comment);

		JTextArea jt = new JTextArea("", 3, 8);
		jt.setEditable(false);
		jt.setForeground(Color.black);
		jt.setBackground(Color.white);
		jt.setFont(new Font("Arial", Font.BOLD, 12));
		jt.setMargin(new Insets(10, 20, 0, 0));
		jt.setText("ClientSimple");
		add(jt);

		JButton inc_button = new JButton("Inc");
		inc_button.addActionListener(new incListener(this));
		add(inc_button);
		JButton raz_button = new JButton("Raz");
		raz_button.addActionListener(new razListener(this));
		add(raz_button);
		JButton lire_button = new JButton("Read");
		lire_button.addActionListener(new lireListener(this));
		add(lire_button);
		JButton pause_button = new JButton("Pause");
		pause_button.addActionListener(new pauseListener(this));
		add(pause_button);

		setSize(600, 100);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		show();

	}
}

abstract class myActionListener implements ActionListener {
	ClientSimple simple;

	public myActionListener(ClientSimple s) {
		simple = s;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("Clicked at " + e.getWhen() + ", with this action :"
				+ e.getActionCommand() + ".");
	}
}

class pauseListener extends myActionListener {

	public pauseListener(ClientSimple c) {
		super(c);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		simple.state = ClientSimple.STOP;
	}

}

class razListener extends myActionListener {
	public razListener(ClientSimple i) {
		super(i);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		simple.state = ClientSimple.RAZ;
	}

}

class lireListener extends myActionListener {
	public lireListener(ClientSimple i) {
		super(i);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		simple.state = ClientSimple.READ;

	}
}

class incListener extends myActionListener {
	public incListener(ClientSimple i) {
		super(i);
	}

	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		simple.state = ClientSimple.INC;
	}
}
