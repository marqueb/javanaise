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
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;



public class JvnServerImpl extends UnicastRemoteObject implements JvnLocalServer, JvnRemoteServer{

	// A JVN server is managed as a singleton 
	private static JvnServerImpl js = null;
	JvnRemoteCoord coord;
	List<JvnObject> object;

	/**
	 * Default constructor
	 * @throws JvnException
	 **/
	private JvnServerImpl() throws Exception {
		super();
		//Recuperation d'une interface coordinateur distant
		try{
			object = new ArrayList<JvnObject>();
			coord = (JvnRemoteCoord)Naming.lookup("//localhost:20011/coordinator");
		}
		catch(Exception e){
			System.out.println("exception lookup coord "+e);
		}

	}

	/**
	 * Static method allowing an application to get a reference to 
	 * a JVN server instance
	 * @throws JvnException
	 **/
	public static JvnServerImpl jvnGetServer() {
		if (js == null){
			try {
				js = new JvnServerImpl();	
			} catch (Exception e) {
				return null;
			}
		}
		return js;
	}

	/**
	 * The JVN service is not used anymore
	 * @throws JvnException
	 **/
	public  void jvnTerminate()
			throws jvn.JvnException {
		// to be completed 
	} 

	/**
	 * creation of a JVN object
	 * @param o : the JVN obj
	 * ect state
	 * @throws JvnException
	 **/
	public  JvnObject jvnCreateObject(Serializable o)
			throws jvn.JvnException { 		
		JvnObject jvn=null;
		try {
			jvn = new JvnObjectImpl(o, coord.jvnGetObjectId());
			object.add(jvn);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jvn; 
	}

	/**
	 *  Associate a symbolic name with a JVN object
	 * @param jon : the JVN object name
	 * @param jo : the JVN object 
	 * @throws JvnException
	 **/
	public  void jvnRegisterObject(String jon, JvnObject jo)
			throws jvn.JvnException {
		try {
			coord.jvnRegisterObject(jon, jo, this);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Provide the reference of a JVN object beeing given its symbolic name
	 * @param jon : the JVN object name
	 * @return the JVN object 
	 * @throws JvnException
	 **/
	public  JvnObject jvnLookupObject(String jon)
			throws jvn.JvnException {
		try {
			JvnObject tmp = coord.jvnLookupObject(jon, this);
			if(tmp != null){
				object.add(tmp);
			}
			return tmp;
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}	

	/**
	 * Get a Read lock on a JVN object 
	 * @param joi : the JVN object identification
	 * @return the current JVN object state
	 * @throws  JvnException
	 **/
	public Serializable jvnLockRead(int joi)
			throws JvnException {
		try {
			return coord.jvnLockRead(joi, this);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}	
	/**
	 * Get a Write lock on a JVN object 
	 * @param joi : the JVN object identification
	 * @return the current JVN object state
	 * @throws  JvnException
	 **/
	public Serializable jvnLockWrite(int joi)
			throws JvnException {
		try {
			return coord.jvnLockWrite(joi, this);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}	


	/**
	 * Invalidate the Read lock of the JVN object identified by id 
	 * called by the JvnCoord
	 * @param joi : the JVN object id
	 * @return void
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public void jvnInvalidateReader(int joi)
			throws java.rmi.RemoteException,jvn.JvnException {
		boolean trouve = false;
		int i = 0;
		while (i<object.size() && !trouve){
			if(object.get(i).jvnGetObjectId()==joi){
				trouve = true;
				object.get(i).jvnInvalidateReader();
			}else{
				i++;
			}
		}
		if(!trouve){
			throw new JvnException("Erreur lors de l'invalidation des lecteurs");
		}
		
	}

	/**
	 * Invalidate the Write lock of the JVN object identified by id 
	 * @param joi : the JVN object id
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public Serializable jvnInvalidateWriter(int joi)
			throws java.rmi.RemoteException,jvn.JvnException { 
		boolean trouve = false;
		int i = 0;
		while (i<object.size() && !trouve){
			if(object.get(i).jvnGetObjectId()==joi){
				trouve = true;
			}else{
				i++;
			}
		}
		if(!trouve){
			throw new JvnException("Erreur lors de l'invalidation des écritures ");
		}
		return object.get(i).jvnInvalidateWriter();
	}

	/**
	 * Reduce the Write lock of the JVN object identified by id 
	 * @param joi : the JVN object id
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public Serializable jvnInvalidateWriterForReader(int joi)
			throws java.rmi.RemoteException,jvn.JvnException { 
		boolean trouve = false;
		int i = 0;
		while (i<object.size() && !trouve){
			if(object.get(i).jvnGetObjectId()==joi){
				trouve = true;
			}else{
				i++;
			}
		}
		if(!trouve){
			throw new JvnException("Erreur pendant l'invalidation des écritures pour le lecteur");
		}
		return object.get(i).jvnInvalidateWriterForReader();
	}

}


