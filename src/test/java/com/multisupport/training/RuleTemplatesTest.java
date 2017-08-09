package com.multisupport.training;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import junit.framework.Assert;

import org.drools.template.DataProviderCompiler;
import org.drools.template.objects.ArrayDataProvider;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.Message;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DebugAgendaEventListener;
import org.kie.api.event.rule.DebugRuleRuntimeEventListener;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import com.multisupport.training.model.Item;
import com.multisupport.training.model.Order;

public class RuleTemplatesTest {

	@Test
	public void testRuleTemplate() {
		String[][] rows = {
			{ "0", "10", "100", "1000000", "5% discount", "caw5pcdct" },
			{ "10", "20", "10", "100", "5% discount", "caw5pcdct" },
			{ "10", "20", "100", "1000000", "8% discount", "cpj8pcdct" },
			{ "20", "50", "10", "100", "5% discount", "caw5pcdct" },
			{ "20", "50", "100", "1000000", "8% discount", "cpj8pcdct" },
			{ "50", "100", "2", "10", "5% discount", "caw5pcdct" },
			{ "50", "100", "10", "100", "8% discount", "cpj8pcdct" },
			{ "50", "100", "100", "1000000", "12% discount", "cxw12pcdct" },
			{ "100", "1000000", "2", "10", "5% discount", "caw5pcdct" },
			{ "100", "1000000", "10", "100", "8% discount", "cpj8pcdct" },
			{ "100", "1000000", "100", "1000000", "12% discount", "cxw12pcdct" }
		};
		//DataProvider is an interface that can be implemented, with just a hasNext and next method like an iterator of String arrays.
		//ArrayDataProvider is a provided implementation of it.
		ArrayDataProvider provider = new ArrayDataProvider(rows);
		DataProviderCompiler compiler = new DataProviderCompiler();
		String drl = compiler.compile(provider, getClass().getResourceAsStream("/com/multisupport/templates/rule-template.drt"));
		System.out.println(drl);
		final KieSession ksession = createSession(drl);

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
		Assert.assertTrue(rulesFired.contains("Discount bracket_10"));
		Assert.assertTrue(rulesFired.contains("No discount applied"));
	}
	
	private KieSession createSession(String drl) {
		KieServices ks = KieServices.Factory.get();
		KieFileSystem kfs = ks.newKieFileSystem();
		kfs.write("src/main/resources/rule.drl", drl);
		KieBuilder kbuilder = ks.newKieBuilder(kfs);
		kbuilder.buildAll();
		if (kbuilder.getResults().hasMessages(Message.Level.ERROR)) {
			throw new IllegalArgumentException("Problem compiling kbase: " + kbuilder.getResults());
		}
		KieContainer kcontainer = ks.newKieContainer(kbuilder.getKieModule().getReleaseId());
		final KieSession ksession = kcontainer.newKieSession();
		return ksession;
	}

	private URI randomId() {
		return URI.create("urn:" + UUID.randomUUID().toString());
	}

}
