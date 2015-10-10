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


public class JvnCoordImpl 	
extends UnicastRemoteObject 
implements JvnRemoteCoord{

	int unique;
	List<CoordStruct> coordStruct;
	JvnObject j;
	JvnRemoteServer jsTest;


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
		coordStruct = new ArrayList<CoordStruct>();
		unique=0;
	}

	/**
	 *  Allocate a NEW JVN object id (usually allocated to a 
	 *  newly created JVN object)
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public synchronized int jvnGetObjectId()
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
	public synchronized void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js)
			throws java.rmi.RemoteException,jvn.JvnException{
		List<Mapping> tmp = new ArrayList<Mapping>();
		tmp.add(new Mapping(js));
		coordStruct.add(new CoordStruct(jon, jo, jo.jvnGetObjectId(), tmp));
	}

	/**
	 * Get the reference of a JVN object managed by a given JVN server 
	 * @param jon : the JVN object name
	 * @param js : the remote reference of the JVNServer
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public synchronized JvnObject jvnLookupObject(String jon, JvnRemoteServer js)
			throws java.rmi.RemoteException,jvn.JvnException{
		int i =0;
		boolean trouve = false;
		while(i < coordStruct.size() && !trouve){
			if(coordStruct.get(i).getJon().equals(jon)){
				trouve = true;
			}else{
				i++;
			}
		}
		if(trouve){
			CoordStruct tmp = coordStruct.get(i);
			tmp.getServer().add(new Mapping(js));
			return tmp.getJo();
		}else{
			return null;
		}
	}

	
	/**
	 * Get a Read lock on a JVN object managed by a given JVN server 
	 * @param joi : the JVN object identification
	 * @param js  : the remote reference of the server
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException, JvnException
	 **/
	public synchronized Serializable jvnLockRead(int joi, JvnRemoteServer js)
			throws java.rmi.RemoteException, JvnException{
		CoordStruct object=null;
		int i=0;
		boolean trouve = false;
		boolean trouve2 = false;
		while(i < coordStruct.size() && !trouve){
			if(coordStruct.get(i).getJoi()==joi){
				trouve = true;
			}else{
				i++;
			}
		}
		if(trouve){
			object = coordStruct.get(i);
			int j = 0;
			while(j < object.getServer().size() && !trouve2){
				if(coordStruct.get(i).getServer().get(j).getJs().equals(js)){
					trouve2 = true;
					if(object.getServerWriter()!=null){
						Serializable tmp = object.getServerWriter().jvnInvalidateWriterForReader(joi);
						object.getJo().setObject(tmp);
						object.setServerWriter(null);
					}
					
				}else{
					j++;
				}
			}
		}
		//update object lock on the coordinator structure
		object.getServer().get(joi).setLock(Lock.R);
		if(!trouve2){
			throw new JvnException("Erreur lock read dans le store");
		}else{
			return object.getJo().jvnGetObjectState();
		}
	}

	/**
	 * Get a Write lock on a JVN object managed by a given JVN server 
	 * @param joi : the JVN object identification
	 * @param js  : the remote reference of the server
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException, JvnException
	 **/
	public synchronized Serializable jvnLockWrite(int joi, JvnRemoteServer js)
			throws java.rmi.RemoteException, JvnException{
		CoordStruct object=null;
		int i = 0;
		boolean trouve = false;
		boolean trouve2 = false;
		while(i < coordStruct.size() && !trouve){
			if(coordStruct.get(i).getJoi()==joi){
				trouve = true;
			}else{
				i++;
			}
		}
		if(trouve){
			int j = 0;
			while(j < coordStruct.get(i).getServer().size() && !trouve2){
				if(coordStruct.get(i).getServer().get(j).getJs().equals(js)){
					trouve2 = true;
					object = coordStruct.get(i);
				}
				if(coordStruct.get(i).getServer().get(j).getLock() == Lock.W){
					coordStruct.get(i).getServer().get(j).getJs().jvnInvalidateWriter(coordStruct.get(i).getJoi());
				}
				if(coordStruct.get(i).getServer().get(j).getLock() == Lock.R){
					coordStruct.get(i).getServer().get(j).getJs().jvnInvalidateReader(coordStruct.get(i).getJoi());
				}
				j++;
			}
		}
		if(!trouve2){
			throw new JvnException ("Erreur d'�criture dans le store");
		}else{
			object.setServerWriter(js);
			//update object lock on the coordinator structure
			object.getServer().get(joi).setLock(Lock.R);
			return object.getJo().jvnGetObjectState();
		}
	}

	/**
	 * A JVN server terminates
	 * @param js  : the remote reference of the server
	 * @throws java.rmi.RemoteException, JvnException
	 **/
	public synchronized void jvnTerminate(JvnRemoteServer js)
			throws java.rmi.RemoteException, JvnException {
		for (CoordStruct cs : coordStruct) {
			for (Mapping m : cs.getServer()){
				if(m.getJs().equals(js))
					cs.getServer().remove(m);
			}
		}
	}
}


