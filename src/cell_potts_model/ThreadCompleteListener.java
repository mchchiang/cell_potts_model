package cell_potts_model;

/**
 * ThreadCompleteListener.java
 * 
 * An interface implemented by classes which need to know when a thread
 * finishes all its tasks  
 * 
 * @author MichaelChiang
 *
 */
public interface ThreadCompleteListener {
	
	/**
	 * Notify the thread has completed its task
	 * @param r the runnable passed to the thread
	 */
	public void notifyThreadComplete(Runnable r);
}
