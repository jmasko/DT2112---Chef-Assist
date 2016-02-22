package iristk.situated;

import org.eclipse.jetty.server.Handler;

import iristk.speech.Context;
import iristk.speech.RecognizerException;
import iristk.system.InitializationException;
import iristk.system.IrisModule;
import iristk.util.Language;

public interface SkillRunner {

	void loadContext(String name, Context context) throws RecognizerException;

	void setDefaultContext(String name);

	void registerWebHandler(Handler handler);

	void addModule(IrisModule module) throws InitializationException;

	Language getPreferredLanguage();

	SystemAgentFlow getSystemAgentFlow();

}
