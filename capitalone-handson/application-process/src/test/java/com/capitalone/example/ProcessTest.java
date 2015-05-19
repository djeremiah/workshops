package com.capitalone.example;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import junit.framework.Assert;

import org.drools.core.util.KieFunctions;
import org.jbpm.process.workitem.rest.RESTWorkItemHandler;
import org.jbpm.test.JBPMHelper;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.manager.RuntimeEnvironmentBuilder;
import org.kie.api.runtime.manager.RuntimeManager;
import org.kie.api.runtime.manager.RuntimeManagerFactory;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkflowProcessInstance;
import org.kie.api.task.TaskService;

public class ProcessTest {

	private static RuntimeManager manager;

	@BeforeClass
	public static void setupManager() {
		KieServices ks = KieServices.Factory.get();
		KieContainer kContainer = ks.getKieClasspathContainer();
		KieBase kbase = kContainer.getKieBase("kbase");
		JBPMHelper.startH2Server();
		JBPMHelper.setupDataSource();
		EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("org.jbpm.persistence.jpa");
		RuntimeEnvironmentBuilder builder = RuntimeEnvironmentBuilder.Factory
				.get().newDefaultBuilder().entityManagerFactory(emf)
				.knowledgeBase(kbase);
		manager = RuntimeManagerFactory.Factory.get()
				.newSingletonRuntimeManager(builder.get(),
						"com.sample:example:1.0");
	}

	@Test
	public void testSteveApproved() {
		RuntimeEngine engine = manager.getRuntimeEngine(null);
		KieSession ksession = engine.getKieSession();
		ksession.getWorkItemManager().registerWorkItemHandler("Rest", new RESTWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("CreditApproval", new CreditApiWorkItemHandler());
		TaskService taskService = engine.getTaskService();
		
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("applicantName", "Steve");
		parameters.put("income", 10000);

		ProcessInstance process = ksession.startProcess("com.capitalone.example.ApplicationProcess", parameters);
		Assert.assertEquals(ProcessInstance.STATE_COMPLETED, process.getState());
		Assert.assertTrue((Boolean)((WorkflowProcessInstance)process).getVariable("creditpassed"));
	
		manager.disposeRuntimeEngine(engine);
	}
	
	@Test
	public void testBobNotApproved() {
		RuntimeEngine engine = manager.getRuntimeEngine(null);
		KieSession ksession = engine.getKieSession();
		ksession.getWorkItemManager().registerWorkItemHandler("Rest", new RESTWorkItemHandler());
		ksession.getWorkItemManager().registerWorkItemHandler("CreditApproval", new CreditApiWorkItemHandler());
		TaskService taskService = engine.getTaskService();
		
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("applicantName", "Bob");
		parameters.put("income", 10000);

		ksession.startProcess("com.capitalone.example.ApplicationProcess", parameters);

//		// let john execute Task 1
//		List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner(
//				"john", "en-UK");
//		TaskSummary task = list.get(0);
//		System.out.println("John is executing task " + task.getName());
//		taskService.start(task.getId(), "john");
//		taskService.complete(task.getId(), "john", null);
//
//		// let mary execute Task 2
//		list = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
//		task = list.get(0);
//		System.out.println("Mary is executing task " + task.getName());
//		taskService.start(task.getId(), "mary");
//		taskService.complete(task.getId(), "mary", null);

		manager.disposeRuntimeEngine(engine);
	}
}
