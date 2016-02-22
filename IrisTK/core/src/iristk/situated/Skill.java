package iristk.situated;

import org.eclipse.jetty.server.Handler;

import iristk.flow.Flow;
import iristk.speech.Context;
import iristk.system.InitializationException;
import iristk.system.IrisModule;
import iristk.util.Language;

public abstract class Skill {
	
	private SkillRunner skillRunner;

	public abstract Flow init() throws Exception;
	
	public abstract String getName();
	
	public SkillRequirements getRequirements() {
		return new SkillRequirements();
	}
	
	public String getWebPage() {
		return null;
	}
	
	public void stop() {
	}
	
	public String[] getStates() {
		return new String[0];
	}
	
	protected Language getPreferredLanguage() {
		return skillRunner.getPreferredLanguage();
	}
	
	protected SystemAgentFlow getSystemAgentFlow() {
		return skillRunner.getSystemAgentFlow();
	}
	
	protected void addModule(IrisModule module) throws InitializationException {
		skillRunner.addModule(module);
	}
	
	protected void loadContext(String name, Context context) throws Exception {
		skillRunner.loadContext(name, context);
	}
	
	protected void setDefaultContext(String name) throws Exception {
		skillRunner.setDefaultContext(name);
	}
	
	protected void registerWebHandler(Handler handler) {
		skillRunner.registerWebHandler(handler);
	}

	public void setSkillRunner(SkillRunner skillRunner) {
		this.skillRunner = skillRunner;
	}
	
	
}
