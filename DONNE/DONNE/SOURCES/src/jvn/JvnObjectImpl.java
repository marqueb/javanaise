package jvn;

import java.io.Serializable;

import com.sun.corba.se.impl.presentation.rmi.DynamicMethodMarshallerImpl.ReaderWriter;

public class JvnObjectImpl implements JvnObject{

	int id;
	Serializable object;
	transient Lock lock;
	transient SyncronizedShare synchonized;
	public JvnObjectImpl(Serializable object, int id) {
		super();
		this.id = id;	
		this.object = object;
		this.lock= Lock.NL;
		synchonized = new SyncronizedShare();
	}


	public Lock getLock() {
		return lock;
	}

	public void setLock(Lock verrou) {
		this.lock = verrou;
	}

	@Override
	public void jvnLockRead() throws JvnException {
		if(synchonized== null){
			lock = Lock.NL;
			synchonized = new SyncronizedShare();
		}

		synchonized.setBlockInvalidation(false);
		//wait if invalidation is running
		synchonized.waitInvalidation();
		synchonized.notifyWaiter();
		synchonized.setBlockInvalidation(true);

		//update current lock
		switch (lock){
		case NL:
			synchonized.notifyWaiter();
			lock = Lock.R;
			synchonized.setBlockInvalidation(false);
			this.object = JvnServerImpl.jvnGetServer().jvnLockRead(id);
			break;
		case R:
			synchonized.notifyWaiter();
			lock = Lock.RC;
			synchonized.setBlockInvalidation(false);
			this.object = JvnServerImpl.jvnGetServer().jvnLockRead(id);
			break;
		case W:
			synchonized.notifyWaiter();
			lock = Lock.R;
			synchonized.setBlockInvalidation(false);
			this.object = JvnServerImpl.jvnGetServer().jvnLockRead(id);
		case WC:
			synchonized.notifyWaiter();
			lock = Lock.RWC;
			synchonized.setBlockInvalidation(false);
			this.object = JvnServerImpl.jvnGetServer().jvnLockRead(id);
			break;
		case RC :
			synchonized.notifyWaiter();
			lock = Lock.R;
			synchonized.setBlockInvalidation(false);
			this.object = JvnServerImpl.jvnGetServer().jvnLockRead(id);
			break;
		case RWC :
			synchonized.notifyWaiter();
			lock = Lock.RWC;
			synchonized.setBlockInvalidation(false);
			this.object = JvnServerImpl.jvnGetServer().jvnLockRead(id);
			break;
		default :
			throw new JvnException("Erreur lors de la lecture");
		}
		synchonized.notifyWaiter();
		synchonized.setBlockInvalidation(false);
	}

	@Override
	public void jvnLockWrite() throws JvnException {
		if(lock == null){
			lock = Lock.NL;
		}
		if(synchonized== null)
			synchonized = new SyncronizedShare();
		synchonized.waitInvalidation();
		synchonized.setBlockInvalidation(true);
		switch (lock){
		case NL:
			synchonized.notifyWaiter();
			synchonized.setBlockInvalidation(false);
			lock = Lock.W;
			this.object = JvnServerImpl.jvnGetServer().jvnLockWrite(id);
			synchonized.setBlockInvalidation(true);
			break;
		case R:
			synchonized.notifyWaiter();
			synchonized.setBlockInvalidation(false);
			lock = Lock.W;
			this.object = JvnServerImpl.jvnGetServer().jvnLockWrite(id);
			synchonized.setBlockInvalidation(true);
			break;
		case W:
			synchonized.notifyWaiter();
			synchonized.setBlockInvalidation(false);
			lock = Lock.W;
			this.object = JvnServerImpl.jvnGetServer().jvnLockWrite(id);
			synchonized.setBlockInvalidation(true);
			break;
		case WC:
			synchonized.notifyWaiter();
			synchonized.setBlockInvalidation(false);
			lock = Lock.W;
			this.object = JvnServerImpl.jvnGetServer().jvnLockWrite(id);
			synchonized.setBlockInvalidation(true);
			break;
		case RC :
			synchonized.notifyWaiter();
			synchonized.setBlockInvalidation(false);
			lock = Lock.W;
			this.object = JvnServerImpl.jvnGetServer().jvnLockWrite(id);
			synchonized.setBlockInvalidation(true);
			lock = Lock.RWC;
			break;
		case RWC :
			synchonized.notifyWaiter();
			synchonized.setBlockInvalidation(false);
			lock = Lock.W;
			this.object = JvnServerImpl.jvnGetServer().jvnLockWrite(id);
			synchonized.setBlockInvalidation(true);
			break;
		default :
			throw new JvnException("Erreur lors de l'écriture");
		}
	}

	@Override
	public void jvnUnLock() throws JvnException {
		switch (lock){
		case NL:
			break;
		case R:
			lock = Lock.RC;
			break;
		case W:
			lock = Lock.WC;
			break;
		case WC:
			break;
		case RC :
			break;
		case RWC :
			break;
		default :
			throw new JvnException("Erreur lors de la liberation des verroux");
		}
		synchonized.notifyWaiter();
		synchonized.setBlockInvalidation(false);
	}

	@Override
	public int jvnGetObjectId() throws JvnException {
		return this.id;
	}

	@Override
	public Serializable jvnGetObjectState() throws JvnException {
		return this.object;
	}

	@Override
	public void jvnInvalidateReader() throws JvnException {
		synchonized.setInvalidate(true);
		synchonized.setUpToDate(false);
		
		synchonized.waitInvalidationBlocked();
		lock = Lock.NL;
		
		synchonized.setInvalidate(false);
		synchonized.setUpToDate(true);
		synchonized.notifyWaiter();
	}

	@Override
	public Serializable jvnInvalidateWriter() throws JvnException {
		synchonized.setInvalidate(true);
		synchonized.setUpToDate(false);
		
		synchonized.waitInvalidationBlocked();
		lock = Lock.NL;
		
		synchonized.setInvalidate(false);
		synchonized.setUpToDate(true);
		synchonized.notifyWaiter();
		return jvnGetObjectState();
	}

	public void setObject(Serializable o) {
		this.object = o;
	}


	@Override
	public Serializable jvnInvalidateWriterForReader() throws JvnException {
		synchonized.setInvalidate(true);
		synchonized.setUpToDate(false);
		
		synchonized.waitInvalidationBlocked();
		lock = Lock.RC;
		
		synchonized.setInvalidate(false);
		synchonized.setUpToDate(true);
		synchonized.notifyWaiter();
		return jvnGetObjectState();
	}

}
