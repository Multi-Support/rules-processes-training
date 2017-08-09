package com.multisupport.training;

import java.io.IOException;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.Map;

import org.drools.core.base.WrappedStatefulKnowledgeSessionForRHS;
import org.drools.core.common.InternalAgenda;
import org.jbpm.workflow.core.impl.NodeImpl;
import org.jbpm.workflow.instance.node.DynamicNodeInstance;
import org.jbpm.workflow.instance.node.DynamicUtils;
import org.kie.api.runtime.KieContext;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.process.WorkItem;

public class KieAdHoc {

	public static void completeAdHocProcess(DynamicNodeInstance dn, Object... params) { 
		Map<String, Object> parameters = extractParameters(params);
		for (String key : parameters.keySet()) {
			dn.getProcessInstance().setVariable(key, parameters.get(key));
		}
		dn.triggerCompleted(NodeImpl.CONNECTION_DEFAULT_TYPE);
	}
	
	public static void completeHumanTask(WorkItem workItem, Map<String, Object> results, KieRuntime kruntime) {
		System.out.println("COMPLETING ITEM!!!");
		kruntime.getWorkItemManager().completeWorkItem(workItem.getId(), results);
		Object adHocName = workItem.getParameter("ad-hoc-name");
		Object adHocNodeId = workItem.getParameter("ad-hoc-nodeid");
		if (adHocName != null) {
			InternalAgenda agenda = (InternalAgenda) kruntime.getAgenda();
			agenda.activateRuleFlowGroup(
					(String) adHocName, 
					workItem.getProcessInstanceId(), 
					(String) adHocNodeId);
			((KieSession) kruntime).fireAllRules();
			agenda.deactivateRuleFlowGroup((String) adHocName);
		}
	}
	
	
	public static void addHumanTask(DynamicNodeInstance dn, KieContext kcontext, String taskName, Object... params) {
		KieRuntime kruntime = getKieRuntime(kcontext.getKieRuntime());
		Map<String, Object> parameters = extractParameters(params);
		parameters.put("TaskName", taskName);
		parameters.put("ad-hoc-name", dn.getNodeName());
		parameters.put("ad-hoc-nodeid", dn.getUniqueId());
		DynamicUtils.addDynamicWorkItem(dn, kruntime, "Human Task", parameters);
		((KieSession) kruntime).fireAllRules();
	}

	private static Map<String, Object> extractParameters(Object... params) {
		Map<String, Object> parameters = new HashMap<>();
		if (params.length % 2 != 0) {
			throw new IllegalArgumentException("Must have a pair number of dynamic parameters (for key-values)");
		}
		if (params.length > 0) {
			for (int index = 0; index < params.length; index+=2) {
				String key = String.valueOf(params[index]);
				Object value = params[index+1];
				parameters.put(key, value);
			}
		}
		return parameters;
	}
	
	private static KieRuntime getKieRuntime(KieRuntime kruntime) {
		if (kruntime instanceof WrappedStatefulKnowledgeSessionForRHS) {
			WrappedStatefulKnowledgeSessionForRHS rhsRuntime = 
					(WrappedStatefulKnowledgeSessionForRHS) kruntime;
			try {
				MockObjOutput out = new MockObjOutput();
				rhsRuntime.writeExternal(out);
				return (KieRuntime) out.getWrittenObject();
			} catch (Exception e) { }
		}
		return kruntime;
	}

	private static final String ERROR = "Method not supported. Only writeObject is overriden";
	
	public static class MockObjOutput implements ObjectOutput {

		private Object writtenObject;
		
		public Object getWrittenObject() {
			return writtenObject;
		}

		@Override
		public void writeBoolean(boolean v) throws IOException {
			throw new UnsupportedOperationException(ERROR);
		}

		@Override
		public void writeByte(int v) throws IOException {
			throw new UnsupportedOperationException(ERROR);
		}

		@Override
		public void writeShort(int v) throws IOException {
			throw new UnsupportedOperationException(ERROR);
		}

		@Override
		public void writeChar(int v) throws IOException {
			throw new UnsupportedOperationException(ERROR);
		}

		@Override
		public void writeInt(int v) throws IOException {
			throw new UnsupportedOperationException(ERROR);
		}

		@Override
		public void writeLong(long v) throws IOException {
			throw new UnsupportedOperationException(ERROR);
		}

		@Override
		public void writeFloat(float v) throws IOException {
			throw new UnsupportedOperationException(ERROR);
		}

		@Override
		public void writeDouble(double v) throws IOException {
			throw new UnsupportedOperationException(ERROR);
		}

		@Override
		public void writeBytes(String s) throws IOException {
			throw new UnsupportedOperationException(ERROR);
		}

		@Override
		public void writeChars(String s) throws IOException {
			throw new UnsupportedOperationException(ERROR);
		}

		@Override
		public void writeUTF(String s) throws IOException {
			throw new UnsupportedOperationException(ERROR);
		}

		@Override
		public void writeObject(Object obj) throws IOException {
			this.writtenObject = obj;
		}

		@Override
		public void write(int b) throws IOException {
			throw new UnsupportedOperationException(ERROR);
		}

		@Override
		public void write(byte[] b) throws IOException {
			throw new UnsupportedOperationException(ERROR);
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			throw new UnsupportedOperationException(ERROR);
		}

		@Override
		public void flush() throws IOException {
			throw new UnsupportedOperationException(ERROR);
		}

		@Override
		public void close() throws IOException {
			throw new UnsupportedOperationException(ERROR);
		}

	}
}
