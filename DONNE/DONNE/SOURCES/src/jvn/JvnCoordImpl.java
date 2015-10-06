/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

import irc.Sentence;
import jvn.JvnObjectImpl.Verrou;


public class JvnCoordImpl 	
extends UnicastRemoteObject 
implements JvnRemoteCoord{

	List<Integer> r, rc;
	int w, wc, rwc;
	int unique;
	List<Mapping> table;
	JvnObject j;
	//Serializble o;

	public static void main(String argv[]) {
		try {
			LocateRegistry.createRegistry(20011);


			//Creation de l'objet distant
			JvnCoordImpl jc = new JvnCoordImpl();

			//Enregistrement de l'object distant dans le RMI
			//Appel à registry avec un identifiant donné

			Naming.rebind("//localhost:20011/coordinator", jc);

		} catch (Exception e) {
			System.out.println("IRC problem : " + e.getMessage());
		}
	}


	/**
	 * Default constructor
	 * @throws JvnException
	 **/
	private JvnCoordImpl() throws Exception {
		r = new ArrayList <Integer>();
		rc = new ArrayList <Integer>();
		table = new ArrayList<Mapping>();
		w = wc = rwc = -1;
		unique=0;
	}

	/**
	 *  Allocate a NEW JVN object id (usually allocated to a 
	 *  newly created JVN object)
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public int jvnGetObjectId()
			throws java.rmi.RemoteException,jvn.JvnException {
		return unique++;
	}

	/**
	 * Associate a symbolic name with a JVN object
	 * @param jon : the JVN object name
	 * @param jo  : the JVN object 
	 * @param joi : the JVN object identification
	 * @param js  : the remote reference of the JVNServer
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js)
			throws java.rmi.RemoteException,jvn.JvnException{
		j = jo;
		table.add(new Mapping(jo.jvnGetObjectId(), jon, jo, js));
	}

	/**
	 * Get the reference of a JVN object managed by a given JVN server 
	 * @param jon : the JVN object name
	 * @param js : the remote reference of the JVNServer
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public JvnObject jvnLookupObject(String jon, JvnRemoteServer js)
			throws java.rmi.RemoteException,jvn.JvnException{
		if(table.isEmpty()){
			return null;
		}else{
			Mapping object=null;
			for (Mapping m : table ){
				if(m.getJon().equals(jon)){
					object = m;
				}
			}
			table.add(new Mapping(0, jon, object.getJo(), js));
			return object.getJo();
		}
	}

	/**
	 * Get a Read lock on a JVN object managed by a given JVN server 
	 * @param joi : the JVN object identification
	 * @param js  : the remote reference of the server
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException, JvnException
	 **/
	public Serializable jvnLockRead(int joi, JvnRemoteServer js)
			throws java.rmi.RemoteException, JvnException{
		Mapping object=null;
		for (Mapping m : table ){
			if(m.getJoi() == joi && js.equals((JvnRemoteServer)m.getJs())){
				object = m;
			}
		}
		object.getJo().setVerrou(Verrou.R);
		return object.getJo().jvnGetObjectState();
	}

	/**
	 * Get a Write lock on a JVN object managed by a given JVN server 
	 * @param joi : the JVN object identification
	 * @param js  : the remote reference of the server
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException, JvnException
	 **/
	public Serializable jvnLockWrite(int joi, JvnRemoteServer js)
			throws java.rmi.RemoteException, JvnException{
		//System.out.println("jambon 1 "+joi+" "+js.toString());
		Mapping object = null;
		for (Mapping m : table ){
			if(m.getJoi() == joi && js.equals((JvnRemoteServer)m.getJs())){
				object = m;
			}
			if(m.getJo().getVerrou()== Verrou.W){
				m.getJs().jvnInvalidateWriter(m.getJoi());
			}
			if(m.getJo().getVerrou()== Verrou.R){
				m.getJs().jvnInvalidateReader(m.getJoi());
			}
		}
		return object.getJo().jvnGetObjectState();
	}

	/**
	 * A JVN server terminates
	 * @param js  : the remote reference of the server
	 * @throws java.rmi.RemoteException, JvnException
	 **/
	public void jvnTerminate(JvnRemoteServer js)
			throws java.rmi.RemoteException, JvnException {
		for (Mapping m : table) {
			if(js.equals((JvnRemoteServer)m.getJs()))
				table.remove(m);
		}
	}
}


