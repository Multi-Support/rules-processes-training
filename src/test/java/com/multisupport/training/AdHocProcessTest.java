package com.multisupport.training;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.jbpm.process.instance.event.listeners.RuleAwareProcessEventLister;
import org.jbpm.process.instance.event.listeners.TriggerRulesEventListener;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.event.rule.DebugRuleRuntimeEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;

public class AdHocProcessTest {

	@Test
	public void testAdHocProcess() {
		KieServices ks = KieServices.Factory.get();
		KieContainer kcontainer = ks.getKieClasspathContainer();
		final KieSession ksession = kcontainer.newKieSession();
		TestAsyncWorkItemHandler htHandler = new TestAsyncWorkItemHandler();
		ksession.getWorkItemManager().registerWorkItemHandler("Human Task", htHandler);
		ksession.addEventListener(new RuleAwareProcessEventLister());
		ksession.addEventListener(new TriggerRulesEventListener(ksession));
		ksession.addEventListener(new DebugAgendaEventListener(System.out));
		ksession.addEventListener(new DebugRuleRuntimeEventListener(System.out));
		ProcessInstance processInstance = ksession.startProcess("com.multisupport.training.adhocProcess");
		
		Assert.assertEquals(ProcessInstance.STATE_ACTIVE, processInstance.getState());
		
		WorkItem item = htHandler.getLastItem();

		Assert.assertEquals("decide what's next", item.getParameter("TaskName"));
		
		Map<String, Object> results = new HashMap<String, Object>();
		results.put("processVar1", "SOME ACTUAL VALUE");
		KieAdHoc.completeHumanTask(item, results, ksession);
		
		WorkItem item2 = htHandler.getLastItem();
		Assert.assertNotNull(item2);
		Assert.assertEquals("execute action", item2.getParameter("TaskName"));

		results.clear();
		results.put("processVar2", Integer.valueOf(22));
		KieAdHoc.completeHumanTask(item2, results, ksession);
		
		Assert.assertEquals(ProcessInstance.STATE_COMPLETED, processInstance.getState());
	}
}
