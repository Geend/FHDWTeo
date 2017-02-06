package de.fhdw.ml.transactionFramework.administration;

class Mutex {
	
	private Thread lockedFor;

	synchronized public void enter() {
		while (this.lockedFor != null && this.lockedFor != Thread.currentThread())
			try {
				this.wait();
			} catch (InterruptedException e) {
				throw new Error(e);
			}
		this.lockedFor = Thread.currentThread();
	}
	synchronized public void leave() {
		if (this.lockedFor != Thread.currentThread()) throw new Error("Wrong mutex protokoll!");
		this.lockedFor = null;
		this.notify();
	}		
}