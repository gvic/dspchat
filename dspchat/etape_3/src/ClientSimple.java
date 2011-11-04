import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class ClientSimple extends JFrame {

	private static final long serialVersionUID = 1L;
	static final int STOP = 0;
	static final int INC = 1;
	static final int READ = 3;	
	static final int RAZ = 4;	
	static final int RAND = 5;
	
	Entier_itf entier;
	ClientSimple me;
	
	JTextArea lock;
	JTextArea value;
	JTextArea comment;
	JTextField nbRead;
	
	int nRead;
	
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
				if (one.nRead > 0) {
					one.nRead--;
				}
				if (one.nRead != 0) {
					one.comment.setText("Ask the lock_read");
					// lock the object in read mode
					one.entier.lock_read();
					one.comment.setText("Got the lock_read");
					
					// invoke the method
					Integer val = one.entier.read();
					// Show the value 
					one.value.setText(val+"");
					
					one.comment.setText("Ask the unlock");
					// unlock the object
					one.entier.unlock();
					one.comment.setText("Got the unlock");
				}
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
			case RAND:				
				Double a = Math.random();
				if (a >= 0.5) {
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
				} else {
					one.comment.setText("Ask the lock_read");
					// lock the object in read mode
					one.entier.lock_read();
					one.comment.setText("Got the lock_read");
					
					// invoke the method
					Integer val1 = one.entier.read();
					// Show the value 
					one.value.setText(val1+"");
					
					one.comment.setText("Ask the unlock");
					// unlock the object
					one.entier.unlock();
					one.comment.setText("Got the unlock");
				}
				break;
			}
		}
	}

	@SuppressWarnings("deprecation")
	public ClientSimple(Entier_itf s) {
		entier = s;
		me = this;
		
		setLayout(new GridLayout(2,5));

		lock = new JTextArea("",3,8);
		lock.setEditable(false);
		lock.setForeground(Color.red);
		lock.setMargin(new Insets(10, 40, 0, 0));
		lock.setBackground(Color.DARK_GRAY);
		add(lock);
		
		value =new JTextArea("",3,8);
		value.setEditable(false);
		value.setForeground(Color.green);
		value.setMargin(new Insets(10, 10, 0, 0));
		value.setBackground(Color.DARK_GRAY);
		add(value);
		
		comment =new JTextArea("",3,8);
		comment.setEditable(false);
		comment.setForeground(Color.white);
		comment.setMargin(new Insets(10, 5, 0, 0));
		comment.setBackground(Color.DARK_GRAY);
		add(comment);
		
		nbRead = new JTextField("-1");
		nbRead.setBounds(20, 20, 50, 15);
		nbRead.setInputVerifier(new InputVerifier() {
			@Override
			public boolean verify(JComponent input) {
				boolean res = false;
				JTextField tf = (JTextField) input;
				try {
					Integer.parseInt(tf.getText());
					res = true;
				} catch (NumberFormatException ex) {
					ex.printStackTrace();
					res = false;
				}
				return res;
			}
			
			@Override
			public boolean shouldYieldFocus(JComponent input) {
				boolean ok = verify(input);
				
				if (!ok) {
					JOptionPane.showMessageDialog(me, "The input entered should be a number!");
				}
				return ok;
			}
		});
		add(nbRead);
		
		JTextArea jt =new JTextArea("",3,8);
		jt.setEditable(false);
		jt.setForeground(Color.black);
		jt.setBackground(Color.white);
		jt.setFont(new Font("Arial", Font.BOLD, 12));
		jt.setMargin(new Insets(10, 20, 0, 0));
		jt.setText("ClientSimple");
		add(jt);
			
		JButton inc_button = new JButton("Inc\n(loop write)");
		inc_button.addActionListener(new myActionListener(this, ClientSimple.INC));
		add(inc_button);
		JButton raz_button = new JButton("Raz\n(1 write)");
		raz_button.addActionListener(new myActionListener(this, ClientSimple.RAZ));
		add(raz_button);
		JButton random_button = new JButton("Random\n(loop randomly read or inc)");
		random_button.addActionListener(new myActionListener(this, ClientSimple.RAND));
		add(random_button);	
		JButton lire_button = new JButton("Read\n(nb specified above)");
		lire_button.addActionListener(new lectListener(this, ClientSimple.READ));
		add(lire_button);		
		JButton pause_button = new JButton("Pause");
		pause_button.addActionListener(new myActionListener(this, ClientSimple.STOP));
		add(pause_button);		
		
		setSize(600,100);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		pack();
		show();
		
	}
}

class myActionListener implements ActionListener {
	ClientSimple simple;
	int state;
	
	public myActionListener(ClientSimple s, int _state) {
		simple = s;
		state = _state;
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("Clicked at "+e.getWhen()+", with this action :"+e.getActionCommand()+".");
		simple.state = state;
	}
}

class lectListener extends myActionListener {
	public lectListener(ClientSimple s, int state) {
		super(s,state);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		super.actionPerformed(e);
		String snb = simple.nbRead.getText();
		int nb = -1;
		try {
			nb = Integer.parseInt(snb);
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
			simple.state = ClientSimple.STOP;
		}
		
		simple.nRead = nb;
	}
}