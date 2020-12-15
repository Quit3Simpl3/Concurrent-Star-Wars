package bgu.spl.mics;

import java.util.concurrent.TimeUnit;

/**
 * A Future object represents a promised result - an object that will
 *  * eventually be resolved to hold a result of some operation. The class allows
 *  * Retrieving the result once it is available.
 *  *
 *  * Only private methods may be added to this class.
 *  * No public constructor is allowed except for the empty constructor.
 */

public class Future<T> {
	// Private fields:
	private boolean isDone;
    private T result;

	/**
	 * This should be the the only public constructor in this class.
	 */
	public Future() {
		this.isDone = false;
		this.result = null;
	}

	/**
     * retrieves the result the Future object holds if it has been resolved.
     * This is a blocking method! It waits for the computation in case it has
     * not been completed.
     * <p>
     * @return return the result of type T if it is available, if not wait until it is available.
     * 	       
     */
	public synchronized T get() {
		while (!this.isDone()) {
			try {
				this.wait();
			}
			catch (InterruptedException e) {}
		}
		return this._get_result();
	}

	/*
	* returns the result saved in the Future object.
	 */
	private T _get_result() {
		return this.result;
	}
	
	/**
     * Resolves the result of this Future object.
     */
	public synchronized void resolve (T result) {
		if(result == null)
			throw new IllegalArgumentException("null is forbidden");

		this.result = result; // IMPORTANT: do this before setting 'isDone', to prevent get() before setting the result.
		this.isDone = true;
		this.notifyAll(); // Update the waiting thread so that it checks isDone()
	}
	
	/**
     * @return true if this object has been resolved, false otherwise
     */
	public boolean isDone() {
		return this.isDone;
	}
	
	/**
     * retrieves the result the Future object holds if it has been resolved,
     * This method is non-blocking, it has a limited amount of time determined
     * by {@code timeout}
     * <p>
     * @param timeout 	the maximal amount of time units to wait for the result.
     * @param unit		the {@link TimeUnit} time units to wait.
     * @return return the result of type T if it is available, if not, 
     * 	       wait for {@code timeout} TimeUnits {@code unit}. If time has
     *         elapsed, return null.
     */
	public synchronized T get(long timeout, TimeUnit unit) {
		long timeout_millis = TimeUnit.MILLISECONDS.convert(timeout, unit);
		long start = System.currentTimeMillis();

		do {
			if (this.isDone()) return this._get_result();
			try {
				this.wait(1); // Thread waits for 1 millis and then checks again
			}
			catch (InterruptedException e) {}
		} while(!((System.currentTimeMillis() - start) > timeout_millis));

		return null;
	}
}
