package jvn;

public class SyncronizedShare {
	private boolean isInvalidate;
	private boolean upToDate;
	private boolean blockInvalidation;

	public SyncronizedShare(){
		this.isInvalidate = false;
		this.upToDate = true;
		this.blockInvalidation=false;		
	}

	public boolean isBlockInvalidation() {
		return blockInvalidation;
	}

	public void setBlockInvalidation(boolean blockInvalidation) {
		this.blockInvalidation = blockInvalidation;
	}

	public synchronized boolean isInvalidate() {
		return isInvalidate;
	}

	public synchronized void setInvalidate(boolean isInvalidate) {
		this.isInvalidate = isInvalidate;
	}

	public synchronized boolean isUpToDate() {
		return upToDate;
	}

	public synchronized void setUpToDate(boolean upToDate) {
		this.upToDate = upToDate;
	}

	public synchronized void waitInvalidation(){
		while(isInvalidate && !upToDate){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void waitInvalidationBlocked(){
		while(!isBlockInvalidation()){
			try {
				wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public synchronized void notifyWaiter(){
		notifyAll();
	}

}
