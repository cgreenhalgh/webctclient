/**
 * 
 */
package webctclient;

import javax.swing.SwingUtilities;

/**
 * @author cmg
 *
 */
public class BackgroundTask {
	/** progress callback */
	public static interface Progress {
		/** 
		 * 
		 * @param progress 0-1 degree of completion (<0 if degree unknown)
		 */
		public void update(float progress);
	}
	/** task interface */
	public static interface Task {
		/** 
		 * Task to be in background thread - override.
		 * 
		 * @return success
		 */
		public boolean task();
	}
	/** status values */
	public static enum Status { NEW, RUNNING, CANCELLED, SUCCESS, FAILURE };
	/** current status */
	private Status status;
	/** task callback - run in background thread */
	private Task task;
	/** on success callback - run in swing event thread */
	private Runnable onSuccess;
	/** on failure callback - run in swing event thread  */
	private Runnable onFailure;
	/** progress callback  - run in swing event thread */
	private Progress onProgress;
	/** thread */
	private Thread thread;
	/** cons */
	public BackgroundTask(Task task, Runnable onSuccess, Runnable onFailure, Progress onProgress) {
		this.task = task;
		this.onSuccess = onSuccess;
		this.onFailure = onFailure;
		this.onProgress = onProgress;
		status = Status.NEW;
	}
	/** cons */
	public BackgroundTask() {
		status = Status.NEW;
	}
	
	/** default task = calls task */
	protected boolean task() {
		if (task!=null)
			return task.task();
		return true;		
	}
	/** default on success = calls onSuccess */
	protected void onSuccess() {
		if (onSuccess!=null)
			onSuccess.run();		
	}
	/** default on failure = calls onFailure */
	protected void onFailure() {
		
		if (onFailure!=null)
			onFailure.run();		
	}
	/** default on progress = calls onProgress */
	protected void onProgress(float progress) {
		if (onProgress!=null)
			onProgress.update(progress);		
	}
	/** start task */
	public final synchronized void start() {
		if (status!=Status.NEW)
			throw new IllegalStateException("Cannot start() BackgroundThread with status "+status);
		status = Status.RUNNING;
		thread = new Thread() {
			public void run() {
				boolean ok = false;
				try {
					ok = task();
				} catch (Exception e) {
					// ...
				}
				if (ok) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							synchronized (BackgroundTask.this) {
								if (status==Status.RUNNING) {
									status = Status.SUCCESS;
									thread = null;
									onSuccess();
								}
							}
						}
					});
				} else {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							synchronized (BackgroundTask.this) {
								if (status==Status.RUNNING) {
									status = Status.FAILURE;
									thread = null;
									onFailure();
								}								
							}							
						}
					});
				}
			}
		};
		thread.start();
	}
	/** cancel
	 * @return true if soon enough to cancel, i.e. before onSuccess/onFailure called
	 */
	public final synchronized boolean cancel() {
		if (status==Status.NEW) {
			status = Status.CANCELLED;
			return true;
		}
		if (status==Status.RUNNING) {
			status = Status.CANCELLED;
			thread.interrupt();
			return true;
		}
		// too late
		return false;
	}
}
