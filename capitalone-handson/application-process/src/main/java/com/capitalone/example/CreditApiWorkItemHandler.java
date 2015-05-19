package com.capitalone.example;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.process.workitem.AbstractLogOrThrowWorkItemHandler;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemManager;

public class CreditApiWorkItemHandler extends AbstractLogOrThrowWorkItemHandler {

	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {

		String applicantName = (String)workItem.getParameter("in_applicantName");
		Integer income = (Integer)workItem.getParameter("in_income");
		
		Map<String, Object> results = new HashMap<String, Object>();
		Boolean result = false;
		if("Steve".equals(applicantName) || income > 100000)
			result = true;

		results.put("out_creditpassed", result);
		manager.completeWorkItem(workItem.getId(), results);
	}

	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
		// nothing to do

	}

}
