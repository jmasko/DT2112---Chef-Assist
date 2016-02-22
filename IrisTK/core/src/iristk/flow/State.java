/*******************************************************************************
 * Copyright (c) 2014 Gabriel Skantze.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Gabriel Skantze - initial API and implementation
 ******************************************************************************/
package iristk.flow;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import iristk.flow.FlowRunner.FlowThread;
import iristk.system.Event;
import iristk.util.DelayedEvent;
import iristk.util.Record;

public abstract class State  {
	
	public static final int EVENT_IGNORED = 0;
	public static final int EVENT_CONSUMED = 1;
	public static final int EVENT_ABORTED = 2;
	
	public State caller = null;
	public FlowRunner flowRunner;
	public State returnTo = null;
	public Event returnEvent = null;
	private Set<DelayedEvent> delayedEvents = new HashSet<>();
	public FlowThread flowThread;
	public Record params = new Record();
	private HashMap<Integer,Integer> countHash = new HashMap<>();
		
	// Returns true if event is consumed
	public int onFlowEvent(Event event) {
		return EVENT_IGNORED;
	}

	@Override
	public String toString() {
		return getClass().getName();
	}
	
	protected int callerHandlers(Event event) {
		if (caller != null) {
			int eventResult = caller.onFlowEvent(event);
			if (eventResult != EVENT_IGNORED) return eventResult;
		}
		return EVENT_IGNORED;
	}
	
	protected void forgetOnExit(DelayedEvent dev) {
		delayedEvents.add(dev);
	}
	
	public void onexit() {
		for (DelayedEvent dev : delayedEvents) {
			dev.forget();
		}
	}
	
	public void onentry() {
	}
	
	protected void log(Object object) {
		flowRunner.logStream.println(object + "");
	}

	public void setFlowThread(FlowThread flowThread) {
		this.flowThread = flowThread;
		this.flowRunner = flowThread.getFlowRunner();
	}
	
	protected int getCount(int key) {
		if (!countHash.containsKey(key)) {
			return 0; 
		} else {
			return countHash.get(key);
		}
	}
	
	protected void incrCount(int key) {
		countHash.put(key, getCount(key) + 1);
	}
	
}
