package net.mmp.center.webapp.thread;

import java.util.Vector;
import java.util.concurrent.ThreadPoolExecutor;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class FullmeshThreadPool extends ThreadPoolExecutor{

	private Vector taskList = new Vector<FullmeshThread>();
	
	private int lastSelectedTask = 0;
	
	public FullmeshThreadPool() {
		super(1, 1, 10, java.util.concurrent.TimeUnit.SECONDS, new java.util.concurrent.ArrayBlockingQueue(1));
	}
	
	public FullmeshThreadPool(int threadPoolSize) {
		super(threadPoolSize, threadPoolSize, 10, java.util.concurrent.TimeUnit.SECONDS,
				new java.util.concurrent.ArrayBlockingQueue(threadPoolSize));
	}
	
	
	public FullmeshThread get() {
		FullmeshThread thread = null;
		if (taskList.size() - 1 < lastSelectedTask) {
			thread = (FullmeshThread) taskList.get(taskList.size() - 1);
		} else {
			thread = (FullmeshThread) taskList.get(lastSelectedTask);
		}

		lastSelectedTask = ++lastSelectedTask % taskList.size();

		return thread;
	}

	public boolean isThreadFinish() {
		for (int idx = 0; idx < taskList.size(); idx++) {
			FullmeshThread thread = (FullmeshThread) taskList.get(idx);
			if (thread.getThreadStatus() != FullmeshThread.STATUS_FINISH) {
				return false;
			}
		}

		return true;
	}

	public void execute(Runnable runnable) {
		super.execute(runnable);
		taskList.add(runnable);
	}
	
	public int getTotalExecuteCount() {
		int totalcount = 0;
		for (int idx = 0; idx < taskList.size(); idx++) {
			FullmeshThread thread = (FullmeshThread) taskList.get(idx);
			totalcount += thread.getExecuteCount();
		}
		
		return totalcount;
	}
}
