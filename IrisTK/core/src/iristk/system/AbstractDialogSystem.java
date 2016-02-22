package iristk.system;

import iristk.flow.FlowModule;
import iristk.speech.Context;
import iristk.util.Language;
import iristk.util.NameFilter;

import java.io.File;
import java.util.HashSet;

public class AbstractDialogSystem extends IrisSystem {
	
	private Language language = Language.ENGLISH_US;
	private HashSet<String> components = new HashSet<>();
	protected LoggingModule loggingModule;
	
	public AbstractDialogSystem(Class<?> packageClass) throws Exception {
		super(packageClass);
	}
	
	public AbstractDialogSystem(String name, File packageDir) throws Exception {
		super(name, packageDir);
	}

	public void setLanguage(Language lang) {
		this.language = lang;
	}
	
	public Language getLanguage() {
		return language;
	}
	
	public void setupLogging(File dir, boolean logOnSystemStart) throws InitializationException {
		loggingModule = new LoggingModule(dir, NameFilter.ALL, logOnSystemStart);
		addModule(loggingModule);
	}
	
	@Override
	public void addModule(IrisModule module) throws InitializationException {
		if (module instanceof FlowModule && loggingModule != null) {
			loggingModule.addFlowModule((FlowModule) module);
		}
		super.addModule(module);
	}
	
	@Override
	public synchronized void addModule(String name, IrisModule module) throws InitializationException {
		if (module instanceof FlowModule && loggingModule != null) {
			loggingModule.addFlowModule((FlowModule) module);
		}
		super.addModule(name, module);
	}
	
	public LoggingModule getLoggingModule() {
		return loggingModule;
	}
	
	public void loadContext(String name, Context context) {
		context.name = name;
		Event event = new Event("action.context.load");
		event.put("context", context);
		send(event);
	}
	
	public void unloadContext(String name) {
		Event event = new Event("action.context.unload");
		event.put("context", name);
		send(event);
	}
	
	public void setDefaultContext(String names) {
		Event event = new Event("action.context.default");
		event.put("context", names);
		send(event);
	}
	
	/*
	public void loadSpeechGrammar(String name, Grammar grammar) throws SAXException, IOException {
		String srgs = new SRGSGrammar(grammar).toString();
		SRGSGrammar.validate(srgs);
		Event event = new Event("action.grammar.speech.load");
		event.put("name", name);
		event.put("srgs", srgs);
		send(event, getName());
	}
	
	public void loadSpeechGrammar(String name, URI uri) throws SAXException, IOException {
		Event event = new Event("action.grammar.speech.load");
		event.put("name", name);
		event.put("uri", uri.toString());
		send(event, getName());
	}
	
	public void loadSpeechGrammar(String name, String srgsString) throws JAXBException, SAXException, IOException {
		loadSpeechGrammar(name, new SRGSGrammar(srgsString));
	}
	
	public void loadSemGrammar(String name, URI uri) throws SAXException, IOException {
		Event event = new Event("action.grammar.sem.load");
		event.put("name", name);
		event.put("uri", uri.toString());
		send(event, getName());
	}
	
	public void loadSemGrammar(String name, String srgs) throws SAXException, IOException {
		SRGSGrammar.validate(srgs);
		Event event = new Event("action.grammar.sem.load");
		event.put("name", name);
		event.put("srgs", srgs);
		send(event, getName());
	}

	public void loadSemGrammar(String name, Grammar grammar) throws SAXException, IOException {
		loadSemGrammar(name, new SRGSGrammar(grammar).toString());
	}

	public void setDefaultGrammars(String names) {
		Event event = new Event("action.grammar.default");
		event.put("names", names);
		send(event, "system");
	}
	*/
	
	protected void onlyOnce(String name) throws InitializationException {
		if (components.contains(name)) {
			throw new InitializationException("Only one " + name + " can be initialized");
		}
		components.add(name);
	}

}
