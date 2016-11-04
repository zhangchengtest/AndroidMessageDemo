package com.cicada.asms;

/**
 * 循环守护线程
 * 
 * @author zhangcheng
 *
 */
public abstract class LoopingThread extends Thread {

	private boolean running = true;

	public LoopingThread(boolean daemon) {
		setDaemon(daemon);
		setPriority(MAX_PRIORITY);
	}

	public LoopingThread(boolean daemon, String name) {
		super(name);
		setDaemon(daemon);
		setPriority(MAX_PRIORITY);
	}

	/**
	 * stop looping
	 */
	public synchronized void shutdown() {
		Logger.info("{} will shutdown"+ this.getClass().getSimpleName());
		running = false;
	}

	/**
	 * run the tasks periodically, ignore any execution exception
	 */
	public void run() {
		try {
			while (isRunning()) {
				try {
					iterate();
				} catch (InterruptedException e) {
					Logger.error("LoopingThread runnig interrupted"+ e.getMessage());
				} catch (Exception x) {
					// ignore
					Logger.error("error running LoopingThread"+ x);
				}
				if (getSleepDuration() > 0L) {
					try {
						sleep(getSleepDuration());
					} catch (InterruptedException e) {
						Logger.error("LoopingThread sleeping interrupted"+ e.getMessage());
					}
				}
			}
		} finally {
			shutdown();
		}
	}

	public synchronized boolean isRunning() {
		return running;
	}

	/**
	 * get the default sleep duration in milliseconds.
	 * 
	 * @return
	 */
	public abstract long getSleepDuration();

	/**
	 * the actually work during each iteration+ exceptions may be thrown
	 */
	protected abstract void iterate() throws Exception;

}