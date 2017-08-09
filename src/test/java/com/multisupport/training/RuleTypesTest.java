package com.multisupport.training;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import junit.framework.Assert;

import org.drools.decisiontable.InputType;
import org.drools.decisiontable.SpreadsheetCompiler;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.event.rule.DebugRuleRuntimeEventListener;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.multisupport.training.model.Item;
import com.multisupport.training.model.Order;

public class RuleTypesTest {

	@Test
	public void testMultipleRuleTypes() {
		KieServices ks = KieServices.Factory.get();
		KieContainer kcontainer = ks.getKieClasspathContainer();
		final KieSession ksession = kcontainer.newKieSession();
		ksession.insert(new Item(randomId(), 22.1));
		ksession.addEventListener(new DebugAgendaEventListener(System.out));
		ksession.addEventListener(new DebugRuleRuntimeEventListener(System.out));
		final List<String> rulesFired = new ArrayList<>();
		ksession.addEventListener(new DefaultAgendaEventListener() {
			@Override
			public void afterMatchFired(AfterMatchFiredEvent event) {
				rulesFired.add(event.getMatch().getRule().getName());
			}
		});
		
		int amount = ksession.fireAllRules();
		Assert.assertEquals(2, amount);
		Assert.assertTrue(rulesFired.contains("Print item with price over 22 (natural language)"));
		Assert.assertTrue(rulesFired.contains("Print item with price over 22"));
	}
	
	@Test
	public void testDecisionTable() {
		KieServices ks = KieServices.Factory.get();
		KieContainer kcontainer = ks.getKieClasspathContainer();
		final KieSession ksession = kcontainer.newKieSession("decisionTableKS");//defined in kmodule.xml
		
		Item item1 = new Item(randomId(), 5.0);
		Item item2 = new Item(randomId(), 11.0);
		Item item3 = new Item(randomId(), 100.1);
		
		ksession.insert(item1);
		ksession.insert(item2);
		ksession.insert(item3);
		ksession.insert(new Order(item1, 10, item2, 5));
		ksession.insert(new Order(item3, 100));
		
		ksession.addEventListener(new DebugAgendaEventListener(System.out));
		ksession.addEventListener(new DebugRuleRuntimeEventListener(System.out));
		final List<String> rulesFired = new ArrayList<>();
		ksession.addEventListener(new DefaultAgendaEventListener() {
			@Override
			public void afterMatchFired(AfterMatchFiredEvent event) {
				rulesFired.add(event.getMatch().getRule().getName());
			}
		});
		
		int amount = ksession.fireAllRules();
		Assert.assertEquals(3, amount);
		System.out.println(rulesFired);
		Assert.assertTrue(rulesFired.contains("Discount bracket_12"));
		Assert.assertTrue(rulesFired.contains("Discount bracket_15"));
		Assert.assertTrue(rulesFired.contains("Discount bracket_29"));
	}
	
	@Test
	public void testTurnDecisionTableToDRL() {
		SpreadsheetCompiler compiler = new SpreadsheetCompiler();
		String drl = compiler.compile("/com/multisupport/dtables/OrderRules.xls", InputType.XLS);
		System.out.println(drl);
	}
	

	private URI randomId() {
		return URI.create("urn:" + UUID.randomUUID().toString());
	}

}
