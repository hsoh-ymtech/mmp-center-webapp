package net.mmp.center.webapp.thread;

import java.util.Vector;
import java.util.concurrent.ThreadPoolExecutor;

@SuppressWarnings({ "unchecked", "rawtypes" })
public class JudgeUnderMeasureThreadPool extends ThreadPoolExecutor{

	private Vector taskList = new Vector<JudgeUnderMeasureThread>();
	
	private int lastSelectedTask = 0;
	
	public JudgeUnderMeasureThreadPool() {
		super(1, 1, 10, java.util.concurrent.TimeUnit.SECONDS, new java.util.concurrent.ArrayBlockingQueue(1));
	}
	
	public JudgeUnderMeasureThreadPool(int threadPoolSize) {
		super(threadPoolSize, threadPoolSize, 10, java.util.concurrent.TimeUnit.SECONDS,
				new java.util.concurrent.ArrayBlockingQueue(threadPoolSize));
	}
	
	
	public JudgeUnderMeasureThread get() {
		JudgeUnderMeasureThread thread = null;
		if (taskList.size() - 1 < lastSelectedTask) {
			thread = (JudgeUnderMeasureThread) taskList.get(taskList.size() - 1);
		} else {
			thread = (JudgeUnderMeasureThread) taskList.get(lastSelectedTask);
		}

		lastSelectedTask = ++lastSelectedTask % taskList.size();

		return thread;
	}

	public boolean isThreadFinish() {
		for (int idx = 0; idx < taskList.size(); idx++) {
			JudgeUnderMeasureThread thread = (JudgeUnderMeasureThread) taskList.get(idx);
			if (thread.getThreadStatus() != JudgeUnderMeasureThread.STATUS_FINISH) {
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
			JudgeUnderMeasureThread thread = (JudgeUnderMeasureThread) taskList.get(idx);
			totalcount += thread.getExecuteCount();
		}
		
		return totalcount;
	}
}
