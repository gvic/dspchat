import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Hashtable;

public class Server extends UnicastRemoteObject implements Server_itf {

	private static final long serialVersionUID = 1L;
	/*
	 * HashMapS which contain serverObjects (either registered or not) public
	 * accessor needed by the Client method lookup
	 */
	private Hashtable<String, Pair<Integer, ServerObject_itf>> serverObjectsList;
	private HashMap<Integer, String> registeredNames;
	private Hashtable<Integer, ServerObject_itf> notRegisteredServerObject;

	// count number (Integer in order to use locks on this object)
	private Integer idCpt = 0;

	protected Server() throws RemoteException {
		serverObjectsList = new Hashtable<String, Pair<Integer, ServerObject_itf>>();
		notRegisteredServerObject = new Hashtable<Integer, ServerObject_itf>();
		registeredNames = new HashMap<Integer, String>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Server_itf#lookup(java.lang.String)
	 */
	@Override
	public int lookup(String name) throws RemoteException {
		int i = -1;
		Pair<Integer, ServerObject_itf> p = serverObjectsList.get(name);
		if (p != null) {
			i = serverObjectsList.get(name).getFirst();
		}
		return i;
	}
	
	// This method is used in the third step of the project
	// we use SharedObjects to communicate with the Client
//	public SharedObject lookup_step3(String name) throws RemoteException {
//		SharedObject so = null;
//
//		Pair<Integer, ServerObject_itf> p = serverObjectsList.get(name);
//		if (p != null) {
//			Object o = p.getSecond().getObj();
//			so = Client.instanciateStub(o, p.getFirst());
//		}
//		return so;
//	}

	/*
	 * Method used to retrieve the real object (!= lookup)
	 */
	public ServerObject_itf getServerObject(String name) throws RemoteException {
		return serverObjectsList.get(name).getSecond();
	}

	/*
	 * Registers an object with a name.
	 * 
	 * @see Server_itf#register(java.lang.String, int)
	 */
	@Override
	public void register(String name, int id) throws RemoteException {
		ServerObject_itf so = notRegisteredServerObject.get(id);
		/* Petite vérification ... */
		if (so != null) {
			Pair<Integer, ServerObject_itf> pair = new Pair<Integer, ServerObject_itf>(
					id, so);
			serverObjectsList.put(name, pair);
			registeredNames.put(pair.getFirst(), name);
			// Alert server admin
			System.out.println("Object " + id + " registered.");
		} else {
			System.out.println("Object " + id + " doesn't exist on the Server");
		}
	}

	/*
	 * Create ServerObject Be careful on the idCpt++ (needs synchro)
	 * 
	 * @see Server_itf#create(java.lang.Object)
	 */
	@Override
	public int create(Object o) throws RemoteException {
		int i = -1;

		synchronized (idCpt) {
			idCpt++;

			ServerObject_itf so = new ServerObject(o, idCpt);
			notRegisteredServerObject.put(idCpt, so);

			// Alert server admin
			System.out.println("Object " + idCpt + " created.");

			i = this.idCpt;
		}

		return i;
	}

	/*
	 * "Les appels au serveur sont transférés au ServerObject concerné"
	 * 
	 * @see Server_itf#lock_read(int, Client_itf)
	 */
	@Override
	public Object lock_read(int id, Client_itf client) throws RemoteException {
		ServerObject_itf so = serverObjectsList.get(registeredNames.get(id))
				.getSecond();
		Object o = so.lock_read(client);
		return o;
	}

	/*
	 * "Les appels au serveur sont transférés au ServerObject concerné"
	 * 
	 * @see Server_itf#lock_write(int, Client_itf)
	 */
	@Override
	public Object lock_write(int id, Client_itf client) throws RemoteException {
		// Il faut pas mettre un synchronized ici sur le lock write ?
		ServerObject_itf so = serverObjectsList.get(registeredNames.get(id))
				.getSecond();
		Object o = so.lock_write(client);
		return o;
	}

	/**
	 * Main method : Instantiate the server and bind it to the RMI Naming space
	 * 
	 * @param args
	 *            contains the server port
	 */
	public static void main(String args[]) {
		int port;
		String URL;
		try {
			// Parse String into an Integer
			Integer I = new Integer(args[0]);
			port = I.intValue();
		} catch (Exception ex) {
//			System.out.println(" Please enter: Server <port>");
//			return;
			port = 8008;
		}

		try {
			// Création du serveur de nom - rmiregistry
			@SuppressWarnings("unused")
			Registry registry = LocateRegistry.createRegistry(port);

			// Création d'un serveur
			Server_itf server = new Server();

			// Calcul de l’URL du serveur
			URL = "//" + InetAddress.getLocalHost().getHostName() + ":" + port
					+ "/ox";
			Naming.bind(URL, server);

			System.out.println("Server launched : ");
			System.out.println(URL);

		} catch (Exception exc) {
			exc.printStackTrace();
		}
	}
}
