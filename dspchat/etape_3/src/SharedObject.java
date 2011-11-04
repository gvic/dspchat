import java.io.ObjectStreamException;
import java.io.Serializable;

public class SharedObject implements Serializable, SharedObject_itf {

	private static final long serialVersionUID = 1L;
	static final int NL = 0; // NL : 0 : no local lock
	static final int RLC = 1; // RLC : 1 : read lock cached (not taken)
	static final int WLC = 2; // WLC : 2 : write lock cached
	static final int RLT = 3; // RLT : 3 : read lock taken
	static final int WLT = 4; // WLT : 4 : write lock taken
	static final int RLT_WLC = 5; // RLT_WLC : 5 : read lock taken and write
	// lock cache
	
	boolean client_callback = false; // Deserialization done by server the first time

	// The object
	public Object obj;
	// Object id
	public int id;
	// The lock
	public int lock;
	
	private boolean callback_processing;
	
	// Default constructor : start with No Lock
	public SharedObject() {
		lock = NL;
	}

	// Constructor used to create a SharedObject
	public SharedObject(Object o, int i) {
		this();
		obj = o;
		id = i;
		callback_processing = false;
	}

	// invoked by the user program on the client node
	public void lock_read() {
		boolean make_request = false;

		synchronized (this) {
			while (callback_processing) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			switch (lock) {
			case NL:
				make_request = true;
				// this.obj = Client.lock_read(id);
				lock = RLT;
				break;
			case RLC: /*
					 * The lock had been cached, it's useless to make a request
					 * to Client layer
					 */
				lock = RLT;
				break;
			case WLC: /*
					 * The WL is stronger than RL, we don't need to call Client
					 * layer
					 */
				lock = RLT_WLC;
				break;
			case RLT: /* Nothing to do */
				break;
			case WLT: /* Warn the user, he has to unlock the WL himself */
				System.out.println("You had not release the write lock");
				break;
			case RLT_WLC:
				break;

			default:
				break;

			}
		}
		if (make_request)
			obj = Client.lock_read(id);
	}

	// invoked by the user program on the client node
	public void lock_write() {
		boolean make_request = false;

		synchronized (this) { // On doit synchronizer car le thread qui passe
			// par la doit etre
			// le proprietaire du lock sur cette objet pour le wait
			while (callback_processing) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			switch (lock) {
			case NL:
				make_request = true;
				lock = WLT; // A VERIFIER SI C'EST A FAIRE ICI
				break;
			case RLC:
				make_request = true;
				lock = WLT; // A
				break;
			case WLC: /*
					 * The WL is stronger than RL, we don't need to call Client
					 * layer
					 */
				lock = WLT;
				break;
			case RLT: /*
					 * The client cannot switch directly into WLT state from RLT
					 * state
					 */
				System.out
						.println("You have to remove the reader lock before using a write lock.");
				break;
			case WLT: /* Nothing to do */
				break;
			case RLT_WLC:
				lock = WLT;
				break;

			default:
				break;

			}
		}

		if (make_request)
			obj = Client.lock_write(id);

	}

	// invoked by the user program on the client node
	public synchronized void unlock() {

		switch (lock) {
		case NL:
			break;
		case RLC:
			break;
		case WLC:
			break;
		case RLT: /* We cached the read lock */
			lock = RLC;
			break;
		case WLT: /* We cached the write lock */
			lock = WLC;
			break;
		case RLT_WLC: /* We get back the WLC state */
			lock = WLC;
			break;

		default:
			break;

		}

		notify(); // Le sharedObject notifie qu'il a finit son travail
	}

	// callback invoked remotely by the server
	public synchronized Object reduce_lock() {

		this.callback_processing = true;

		switch (lock) {
		/* Se reporter au diagramme d'états diapo 27 des slides */
		case WLC:
			lock = RLC;
			break;
		case WLT:
			/**
			 * Il faut faire attention ici : ce n'est pas a nous de passer le
			 * lock en RLC, c'est le client qui le fera lorsqu'il appelera
			 * unlock() sur l'objet. Seulement a partir de ce moment on pourra
			 * appliquer le reduce_lock. Ce qui justifie l'utilisation du wait()
			 * puisque unlock() emet un notify()
			 */
			while (lock == WLT) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (lock == WLC) {
				lock = RLC;
			}
			break;

		case RLT_WLC:
			lock = RLT;
			break;
		default:
			break;
		}

		// Des qu'on a émis le notify on peut mettre le booleen
		// unlock_processing a false
		this.callback_processing = false;

		notify();

		return obj;
	}

	// callback invoked remotely by the server
	public synchronized void invalidate_reader() {

		this.callback_processing = true;

		switch (lock) {
		/* Se reporter au diagramme d'états diapo 27 des slides */
		case RLC:
			lock = NL;
			break;
		case RLT:
			/** Meme principe que pour reduce_lock */
			while (lock == RLT) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (lock == RLC) {
				lock = NL;
			}
			break;
		default:
			break;
		}

		this.callback_processing = false;

		notify();

	}

	public synchronized Object invalidate_writer() {

		this.callback_processing = true;

		switch (lock) {
		/* Se reporter au diagramme d'états diapo 27 des slides */
		case WLC:
			lock = NL;
			break;
		case WLT:
			/** Meme principe que reduce_lock */
			while (lock == WLT) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			/**
			 * Si un unlock() a reveillé le wait, on est passé en WLC Mais vu
			 * qu'on invalide les incrvains il faut positionner le lock a NL
			 */
			if (lock == WLC) {
				lock = NL;
			}
			break;
		case RLT_WLC:
			while (lock == WLT) {
				try {
					wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			lock = NL;
			break;
		default:
			break;

		}
		this.callback_processing = false;

		notify();
		return obj;
	}

	@Override
	public int getId() {
		return this.id;
	}

	@Override
	public Object getObj() {
		return this.obj;
	}

	static public String lockToString(int l) {
		String lock = "";

		switch (l) {
		case SharedObject.NL:
			lock = "NL";
			break;
		case SharedObject.RLC:
			lock = "RLC";
			break;
		case SharedObject.WLC:
			lock = "WLC";
			break;
		case SharedObject.RLT:
			lock = "RLT";
			break;
		case SharedObject.WLT:
			lock = "WLT";
			break;
		case SharedObject.RLT_WLC:
			lock = "RLT_WLC";
			break;

		default:
			break;

		}

		return lock;
	}
	
    public Object readResolve() throws ObjectStreamException {
		Object res = this;
		
		// Is the method called ?
		System.out.println("readResolve called!");
		
		// Unmarshaling process for the Client but not for the server..
		if (client_callback){
			try {
				SharedObject so = Client.lookup(id);
				if (so != null) {
					// SO already exists in the client!
					// Update ref and return the SO retrieved
					so.obj = obj;
					res = so;
				}
			} catch(NullPointerException ex) {
				ex.printStackTrace();
				// Exception raised because called by server
			}
		} else {
			client_callback = true;
		}
		
		return res;
	}
}
