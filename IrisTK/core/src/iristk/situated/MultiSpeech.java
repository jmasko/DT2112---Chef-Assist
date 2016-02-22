package iristk.situated;

import java.util.*;

import iristk.system.Event;
import iristk.util.Record;

public class MultiSpeech {

	private HashMap<String,Boolean> attsys = new HashMap<>();
	private HashMap<String,Event> events = new HashMap<>();
	private HashSet<String> started = new HashSet<>();
	
	public int speakers = 0;
	public int runningRecognizers = 0;
	private SystemAgent systemAgent;
	
	public MultiSpeech(SystemAgent systemAgent) {
		this.systemAgent = systemAgent;
	}

	public void addStart(String agentId, boolean isAttSys) {
		speakers++;
		runningRecognizers++;
		attsys.put(agentId, isAttSys);
		started.add(agentId);
	}

	public void attendingSystem(String agentId) {
		attsys.put(agentId, true);
	}

	public boolean someAttendingSystem() {
		for (boolean is : attsys.values())
			if (is) return true;
		return false;
	}
	
	public boolean hasStarted(String userId) {
		return started.contains(userId);
	}

	private List<String> getAttendingSpeakers() {
		List<String> result = new ArrayList<>();
		for (String userId : events.keySet()) {
			if (attsys.get(userId)) 
				result.add(userId);
 		}
		return result;
	}
	
	public void addRec(String agentId, Event event) {
		if (!event.triggers("sense.speech.rec.silence")) 
			runningRecognizers--;
		if (event.triggers("sense.speech.rec"))
			events.put(agentId, event);
	}

	public Event getEvent() {
		if (events.size() == 0) {
			return new Event("sense.user.silence");
		}
		Agent attendedUser = systemAgent.getAttendedUser();
		if (attendedUser != null) {
			// System attending one user
			if (events.size() == 1) {
				// Only one answered
				if (events.containsKey(attendedUser.id)) {
					// The one attended answered
					return event("sense.user.speak", attendedUser.id);
				} else {
					// The one not attended answered
					return event("sense.user.speak.side", events.keySet().iterator().next());
				}
			} else {
				// Multiple answered
				if (!events.containsKey(attendedUser.id)) {
					// The attended user did not answer
					List<String> attending = getAttendingSpeakers();
					String speaker = attending.size() > 0 ? attending.get(0) : events.keySet().iterator().next();
					return event("sense.user.speak.side", speaker);
				} else {
					return event("sense.user.speak", attendedUser.id);
				}
			}
		} else {
			// System attending all
			if (events.size() == 1) {
				// Only one user replied
				return event("sense.user.speak", events.keySet().iterator().next());
			} else {
				// Multiple user replied
				List<String> attending = getAttendingSpeakers();
				if (attending.size() == 1) {
					// Only one of them attended the system
					return event("sense.user.speak", attending.get(0));
				} else if (attending.size() > 1) {
					// Many of them attended the system
					return multiEvent(attending);
				} else {
					// None of them attended the system
					return multiEvent(new ArrayList<String>(events.keySet()));
				}
			}
		}
	}

	private Event event(String name, String userId) {
		Event e = new Event(name);
		e.copyParams(events.get(userId));
		e.put("agent", systemAgent.id);
		e.put("user", userId);
		e.put("attsys", attsys.get(userId));
		addAll(e);
		return e;
	}
	
	private Event multiEvent(List<String> userIds) {
		Event e = new Event("sense.user.speak.multi");
		e.put("agent", systemAgent.id);
		// Merge the different users into one representation
		for (String userId : userIds) {
			e.put("user", userId);
			e.put("attsys", attsys.get(userId));
			Object sem = events.get(userId).get("sem");
			if (sem instanceof Record) {
				if (e.get("sem") instanceof Record) {
					e.getRecord("sem").adjoin((Record)sem);
				} else {
					e.put("sem", sem);
				}
			}
			//e.putAllExceptNull(events.get(userId).getEventParams());
		}
		// But also store all users under the field "all"
		addAll(e);
		return e;
	}

	private void addAll(Event e) {
		if (events.size() > 1) {
			List list = new ArrayList();
			for (String userId : events.keySet()) {
				Record r = new Record();
				r.put("attsys", attsys.get(userId));
				r.put("user", userId);
				r.putAllExceptNull(events.get(userId).getEventParams());
				list.add(r);
			}
			e.put("all", list);
		}
	}

	
}
