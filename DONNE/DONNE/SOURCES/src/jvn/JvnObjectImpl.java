package jvn;

import java.io.Serializable;

public class JvnObjectImpl implements JvnObject{

	int id;
	Serializable o;
	enum Verrou { NL, R, RC, W, WC, RWC };
	Verrou verrou;

	public JvnObjectImpl(Serializable o, int id) {
		super();
		this.id = id;	
		this.o = o;
		this.verrou= Verrou.NL;
	}

	
	public Verrou getVerrou() {
		return verrou;
	}

	public void setVerrou(Verrou verrou) {
		this.verrou = verrou;
	}
	
	@Override
	public void jvnLockRead() throws JvnException {
		this.o = JvnServerImpl.jvnGetServer().jvnLockRead(id);
		verrou = Verrou.R;
	}

	@Override
	public void jvnLockWrite() throws JvnException {
		this.o = JvnServerImpl.jvnGetServer().jvnLockWrite(id);
		verrou = Verrou.W;
	}

	@Override
	public void jvnUnLock() throws JvnException {
		verrou = Verrou.NL;

	}

	@Override
	public int jvnGetObjectId() throws JvnException {
		return this.id;
	}

	@Override
	public Serializable jvnGetObjectState() throws JvnException {
		return this.o;
	}

	@Override
	public void jvnInvalidateReader() throws JvnException {
		// TODO Auto-generated method stub

	}

	@Override
	public Serializable jvnInvalidateWriter() throws JvnException {
		// TODO Auto-generated method stub
		return null;
	}

	public Serializable getO() {
		return o;
	}


	public void setO(Serializable o) {
		this.o = o;
	}


	@Override
	public Serializable jvnInvalidateWriterForReader() throws JvnException {
		// TODO Auto-generated method stub
		return null;
	}

}
