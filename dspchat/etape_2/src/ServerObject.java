import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Iterator;

public class ServerObject implements Serializable, ServerObject_itf {

	private static final long serialVersionUID = 1L;
	private static final int NL = 0; // NL : 0 : no local lock
	private static final int RL = 1; // RL : 1 : read locked
	private static final int WL = 2; // WL : 2 : write locked

	// The object
	public Object obj;
	// Object id
	private int id;

	// The lock
	public int lock;

	// reader Clients
	private HashSet<Client_itf> readerClients;
	private Client_itf writerClient;

	// Default constructor : start with No Lock
	public ServerObject() {
		lock = NL;
		readerClients = new HashSet<Client_itf>();
		writerClient = null;
	}

	// Constructor used to create a ServerObject
	public ServerObject(Object o, int i) {
		this();
		this.obj = o;
		this.id = i;
	}

	/**
	 * Peut être appelé par plusieurs clients, d'ou le synchronized
	 */
	@Override
	public synchronized Object lock_read(Client_itf c) {
		if (lock == WL) {
			try {
				// On récupère la derniere version de l'objet
				obj = writerClient.reduce_lock(id);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			// L'ancien écrivain devient lecteur
			readerClients.add(writerClient);
		}

		// L'ancien écrivain n'existe plus!
		writerClient = null;
		// Le client demandeur est aussi un lecteur
		readerClients.add(c);
		// Le verrou passe en mode lecture
		lock = RL;

		return obj; // On retourne l'objet touché par le dernier écrivain
	}

	@Override
	public synchronized Object lock_write(Client_itf c) {

		try {
			switch (lock) {
			case WL:
				obj = writerClient.invalidate_writer(id); // On invalide le
															// dernier client
															// qui était ecrvain
				break;
			case RL:
				Iterator<Client_itf> clts = readerClients.iterator();
				while (clts.hasNext()) {
					clts.next().invalidate_reader(id);
				}
				break;

			default:
				break;

			}

			// Vide la liste des lecteurs
			readerClients.clear();
			// On met a jour le client écrivain
			writerClient = c;
			// Le server object passe a le verrou d'ecriture
			lock = WL;
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		return obj; // On retourne l'objet touché par le dernier ecrivain
	}
}
