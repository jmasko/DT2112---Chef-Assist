package iristk.situated;

import iristk.system.Event;
import iristk.system.InitializationException;
import iristk.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SystemAgentModule extends SituationModule implements SituationListener {

	private SystemAgent systemAgent;
	
	public SystemAgentModule(String id) {
		this.systemAgent = new SystemAgent(id, situation);
		situation.getSystemAgents().put(id, systemAgent);
		situation.addSituationListener(this);
	}

	@Override
	public void init() throws InitializationException {
		systemAgent.systemAgentModule = this;
		super.init();
	}
	
	@Override
	public void onEvent(Event event) {
		super.onEvent(event);
		if (event.triggers("action.situation.detect")) {
			sendSenseSituation();
		}
	}

	void sendSenseSituation() {
		Event event = new Event("sense.situation");
		event.put(systemAgent.id, systemAgent);
		send(event);
	}
	
	private List<String> getClosestBodies() {
		ArrayList<String> bodies = new ArrayList<>();
		for (String bodyId : new ArrayList<>(situation.getBodies().keySet())) {
			Body body = situation.getBodies().get(bodyId);
			if (!bodyId.equals(systemAgent.id) && systemAgent.isInInteractionSpace(body)) {
				bodies.add(bodyId);
			}
		}
		Collections.sort(bodies, new Comparator<String>() {
			@Override
			public int compare(String o1, String o2) {
				double diff = systemAgent.getHeadLocation().distance(situation.getBodies().get(o1).getHeadLocation()) - 
						systemAgent.getHeadLocation().distance(situation.getBodies().get(o2).getHeadLocation());
				if (diff < 0) return -1;
				else if (diff > 0) return 1;
				else return 0;
			}
		});
		while (bodies.size() > systemAgent.getMaxUsers())
			bodies.remove(bodies.size() - 1);
		return bodies;
	}

	@Override
	public void situationUpdated() {
		ArrayList<String> moved = new ArrayList<>();
		List<String> bodyIds = getClosestBodies();
		for (Agent user : systemAgent.getUsers()) {
			if (!bodyIds.contains(user.id)) {
				systemAgent.removeUser(user);
				Event event = new Event("sense.user.leave");
				event.put("user", user.id);
				event.put("sensor", user.sensor);
				event.put("agent", systemAgent.id);
				send(event);
			}
		}
		for (String bodyId : bodyIds) {
			Body body = situation.getBodies().get(bodyId);
			if (!systemAgent.hasUser(bodyId)) {
				Agent user = new Agent();
				user.putAllExceptNull(body);
				systemAgent.addUser(user);
				Event event = new Event("sense.user.enter");
				event.put("user", bodyId);
				event.put("sensor", body.sensor);
				event.put("agent", systemAgent.id);
				event.put("head:location", body.head.location);
				event.put("head:rotation", body.head.rotation);
				send(event);
			} else {
				Agent user = systemAgent.getUser(bodyId);
				if (!Utils.equals(user.head.location, body.head.location) || !Utils.equals(user.head.rotation, body.head.rotation)) {
					moved.add(bodyId);
				}
				String oldAttending = user.attending;
				user.attending = Agent.UNKNOWN;
				if (body.head.rotation != null) {
					double minangle = 20;	
					List<Agent> targets = systemAgent.getUsers();
					targets.remove(user);
					targets.add(systemAgent);
					for (Agent target : targets) {
						double angle = body.gazeAngle(target.getHeadLocation());
						if (angle < minangle) {
							user.attending = target.id;
							minangle = angle;
						}
					}
				}
				if (!user.attending.equals(oldAttending)) {
					Event event = new Event("sense.user.attend");
					event.put("user", user.id);
					event.put("sensor", body.sensor);
					event.put("target", user.attending);
					event.put("agent", systemAgent.id);
					send(event);
				}
				for (String field : body.getFields()) {
					user.put(field, body.get(field));
				}
			}
		}
		if (moved.size() > 0) {
			Event event = new Event("sense.user.move");
			for (String userId : moved) {
				Body body = new Body(userId);
				body.sensor = systemAgent.getUser(userId).sensor;
				body.head.location = systemAgent.getUser(userId).head.location;
				body.head.rotation = systemAgent.getUser(userId).head.rotation;
				event.put(userId, body);
			}
			event.put("agent", systemAgent.id);
			send(event);
		}
		Event moveEvent = new Event("sense.item.move");
		String prominent = null;
		double maxdist = 0;
		for (Item item : situation.getItems().values()) {
			Item oldItem = systemAgent.getItems().get(item.id);
			if (oldItem == null || !oldItem.getPosition().equals(item.getPosition())) {
				double dist = (oldItem == null ? 1 : oldItem.location.distance(item.location));
				moveEvent.put("items:" + item.id, dist);
				if (dist > maxdist) {
					maxdist = dist;
					prominent = item.id;
				}
				if (oldItem == null)
					systemAgent.getItems().put(item.id, (Item) item.deepClone());
				else
					systemAgent.getItems().get(item.id).putAllExceptNull(item);
			} 
		}
		for (String itemId : new ArrayList<String>(systemAgent.getItems().keySet())) {
			if (situation.getItem(itemId) == null)
				systemAgent.getItems().remove(itemId);
		}
		if (moveEvent.has("items")) {
			moveEvent.put("prominent", prominent);
			send(moveEvent);
		}
	}

	public SystemAgent getSystemAgent() {
		return systemAgent;
	}

}
