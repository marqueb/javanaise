package jvn;

public class Mapping {

	private JvnRemoteServer js;
	Lock lock;

	public Mapping(JvnRemoteServer js) {
		this.js = js;
		lock = Lock.NL;
	}

	public JvnRemoteServer getJs() {
		return js;
	}

	public void setJs(JvnRemoteServer js) {
		this.js = js;
	}

	public Lock getLock() {
		return lock;
	}

	public void setLock(Lock lock) {
		this.lock = lock;
	}

}
