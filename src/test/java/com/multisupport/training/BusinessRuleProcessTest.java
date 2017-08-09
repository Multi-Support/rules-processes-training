package com.multisupport.training;

import java.util.HashMap;
import java.util.Map;

import org.jbpm.process.instance.event.listeners.TriggerRulesEventListener;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;

public class BusinessRuleProcessTest {

    private KieSession ksession;

    @Test
    public void taskTypeVarietyProcessTest(){
        KieServices ks = KieServices.Factory.get();
        KieContainer kcontainer = ks.getKieClasspathContainer();
        this.ksession = kcontainer.newKieSession();
        ksession.addEventListener(new TriggerRulesEventListener(ksession));
        
    	//Register WorkItemManagers for all the generic tasks in the process
        TestAsyncWorkItemHandler taskHandler = new TestAsyncWorkItemHandler();
        ksession.getWorkItemManager().registerWorkItemHandler("Human Task", taskHandler);
        ksession.getWorkItemManager().registerWorkItemHandler("mySpecialTaskType", taskHandler);
        
    	//Start the process using its id
    	Map<String, Object> variables = new HashMap<String, Object>();
    	variables.put("input", "222");
        ProcessInstance process = ksession.startProcess("com.multisupport.training.businessRuleProcess", variables);

        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, process.getState());
        ksession.getWorkItemManager().completeWorkItem(taskHandler.getLastItem().getId(), null);
        
        Assert.assertEquals(ProcessInstance.STATE_ACTIVE, process.getState());
        //we see the parameter in the last task
        WorkItem workItem = taskHandler.getLastItem();
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);
        Object param = workItem.getParameter("ruleExecution");
        Assert.assertNotNull(param);
        Assert.assertEquals("message", param);
        ksession.getWorkItemManager().completeWorkItem(workItem.getId(), null);

        //after completing the last task, we finish the process
        Assert.assertEquals(ProcessInstance.STATE_COMPLETED, process.getState());
    }
}
