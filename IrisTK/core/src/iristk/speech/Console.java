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
package iristk.speech;

import iristk.cfg.ParseResult;
import iristk.cfg.Parser;
import iristk.system.Event;
import iristk.system.InitializationException;
import iristk.system.IrisGUI;
import iristk.system.IrisModule;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

public class Console extends IrisModule {

	private JTextPane textPane;
	private StyledDocument doc;
	private String actionId;
	private Integer timeout;
	private String lastAction = "";
	private int lastPos = 0;
	private boolean startOfSpeech;
	private JTextField textInput;
	private Parser parser;
	private boolean synthesizer = true;
	private boolean recognizer = true;
	private JPanel window;
	
	public Console(IrisGUI gui) {
		window = new JPanel(new BorderLayout());
		textPane = new JTextPane();
		textPane.setEditable(false);
		doc = textPane.getStyledDocument();

		window.add(new JScrollPane(textPane));
		
		textInput = new JTextField();
		textInput.setEditable(false);
		textInput.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent key) {	
			}
			@Override
			public void keyReleased(KeyEvent key) {
			}
			@Override
			public void keyPressed(KeyEvent key) {
				if (key.getKeyCode() == 10) {
					sendSpeech(textInput.getText());
					textInput.setText("");
					textInput.setEditable(false);
				} else if (!startOfSpeech) {
					startOfSpeech();
				}
			}
		});
		
		window.add(textInput, BorderLayout.PAGE_END);
		
		gui.addDockPanel("console", "Console", window, true);
	}
	
	public void useSynthesizer(boolean cond) {
		this.synthesizer = cond;
	}

	public void useRecognizer(boolean cond) {
		this.recognizer = cond;
	}
	
	@Override
	public void init() throws InitializationException {
	}

	private void startOfSpeech() {
		startOfSpeech = true;
		Event speech = new Event("sense.speech.start");
		speech.put("action", actionId);
		send(speech);
	}

	private void sendSpeech(String text) {
		if (text.length() > 0) {
			Event end = new Event("sense.speech.end");
			end.put("action", actionId);
			send(end);
			Event rec = new Event("sense.speech.rec");
			rec.put("text", text);
			if (parser != null) {
				ParseResult presult = parser.parse(text);
				rec.put("sem", presult.getSem());
			}
			rec.put("action", actionId);
			send(rec);
		} else {
			Event speech = new Event("sense.speech.rec.silence");
			speech.put("action", actionId);
			send(speech);
		}
	}

	@Override
	public void onEvent(final Event event) {
		if (event.getName().equals("action.speech")) {
			
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						String text = getText(event);
						textPane.setParagraphAttributes(getStyle(event.getString("agent", "system")), true);
						doc.insertString(doc.getLength(), text + "\n", null);
						textPane.setCaretPosition(doc.getLength());
					} catch (BadLocationException e) {
						e.printStackTrace();
					}
				}
			});

			if (!synthesizer) {
				Event speech = new Event("monitor.speech.end");
				speech.put("action", event.getId());
				send(speech);
			}
			
		} else if (event.getName().equals("sense.speech.rec")) {
			EventQueue.invokeLater(new Runnable() {
				@Override
				public void run() {
					addSpeechRecResult(event);
				}
			});
		} else if (event.getName().equals("sense.speech.partial")) {
			EventQueue.invokeLater(new Runnable() {

				@Override
				public void run() {
					addPartialSpeechRecResult(event);
				}
			});
		} else if (event.getName().equals("action.listen")) {
			if (!recognizer) {
				textInput.setEditable(true);
				actionId = event.getId();
				timeout = event.getInteger("timeout");
				startOfSpeech = false;
			}
		}
		// TODO: grammar -> context
		/*else if (event.triggers("action.grammar.sem.load")) {
			if (!recognizer) {
				try {
					if (parser == null)
						parser = new Parser();
					parser.addGrammar(new SRGSGrammar(event.getString("srgs")));
				} catch (JAXBException e) {
					e.printStackTrace();
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		*/
	}
	
	private String getText(Event event) {
		if (event.getString("display") != null) {
			return event.getString("display");
		} else {
			String text = event.getString("text");
			text = text.replaceAll("<.*?>", "").trim();
			if (text.length() == 0)
				text = "?";
			return text;
		}
	}

	protected synchronized void addPartialSpeechRecResult(Event event) {
		try {
			String action = event.getString("action") + event.getString("sensor");
			if (action.equals(lastAction)) 
				doc.remove(lastPos, doc.getLength() - lastPos);
			lastPos = doc.getLength();
			lastAction = action;
			String text = getText(event);
			textPane.setParagraphAttributes(getStyle(event.getString("sensor", "user")), true);
			doc.insertString(doc.getLength(), text + "\n", null);
			textPane.setCaretPosition(doc.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	protected synchronized void addSpeechRecResult(Event event) {
		try {
			String action = event.getString("action") + event.getString("sensor");
			if (action.equals(lastAction)) 
				doc.remove(lastPos, doc.getLength() - lastPos);
			lastPos = doc.getLength();
			lastAction = action;
			String text = getText(event);
			textPane.setParagraphAttributes(getStyle(event.getString("sensor", "user")), true);
			doc.insertString(doc.getLength(), text + "\n", null);
			textPane.setCaretPosition(doc.getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	protected AttributeSet getStyle(String key) {
		SimpleAttributeSet style = new SimpleAttributeSet();
		Color color;
		if (key.equals("system"))
			color = Color.RED;
		else
			color = getColor(key);
		StyleConstants.setForeground(style, color);
		return style;
	}
	
	private Map<String,Color> colorMap = new HashMap<>();
	private ArrayList<Color> colorList = new ArrayList<>();
	{
		colorList.add(Color.CYAN);
		colorList.add(Color.MAGENTA);
		colorList.add(Color.PINK);
		colorList.add(Color.ORANGE);
		colorList.add(Color.BLUE);
		colorList.add(Color.GREEN);
	}
	
	private Color getColor(String key) {
		if (!colorMap.containsKey(key)) {
			Color color = colorList.get(colorList.size()-1);
			colorMap.put(key, color);
		}
		Color color = colorMap.get(key);
		colorList.remove(color);
		colorList.add(0, color);
		return color;
	}

	public void setParser(Parser parser) {
		this.parser = parser;
	}
}
