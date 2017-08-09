package com.multisupport.training;

import java.util.Stack;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class TestAsyncWorkItemHandler implements WorkItemHandler {

	private Stack<WorkItem> items = new Stack<WorkItem>();

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		this.items.add(workItem);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		this.items.remove(workItem);
	}
	
	public synchronized WorkItem getLastItem() {
		return items.size() > 0 ? items.pop() : null;
	}

	
}
