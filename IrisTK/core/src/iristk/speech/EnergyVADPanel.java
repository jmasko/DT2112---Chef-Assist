package iristk.speech;

import iristk.system.IrisGUI;
import iristk.util.Utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class EnergyVADPanel extends JPanel {

	private LevelBar leftLevelBar;
	private LevelBar rightLevelBar;
	private EnergyVAD leftVAD;
	private EnergyVAD rightVAD;
	private JCheckBox adaptive;
	private JTextField silenceLevel;
	private JTextField speechDelta;
	private JTextField silenceDelta;
	
	public EnergyVADPanel(boolean editable) {
		super(new BorderLayout());
		leftLevelBar = new LevelBar(0);
		rightLevelBar = new LevelBar(1);
		
		GridLayout layout = new GridLayout(1, 2);
		layout.setHgap(20);
		JPanel bars = new JPanel(layout);
		bars.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
		bars.add(leftLevelBar);
		bars.add(rightLevelBar);
		add(bars);

		JPanel controls = new JPanel(new GridLayout(3, 3));
		silenceLevel = new JTextField();
		silenceLevel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setSilenceLevel(Integer.parseInt(silenceLevel.getText()));
				repaint();
			}
		});
		if (!editable)
			silenceLevel.setEnabled(false);

		adaptive = new JCheckBox("Adaptive");
		adaptive.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				setAdaptive(adaptive.isSelected());
				repaint();
			}
		});
		if (!editable)
			adaptive.setEnabled(false);
		
		speechDelta = new JTextField();
		speechDelta.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setDeltaSpeech(Integer.parseInt(speechDelta.getText()));
				repaint();
			}
		});
		if (!editable)
			speechDelta.setEnabled(false);
		silenceDelta = new JTextField();
		silenceDelta.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setDeltaSil(Integer.parseInt(silenceDelta.getText()));
				repaint();
			}
		});
		if (!editable)
			silenceDelta.setEnabled(false);
		controls.add(new JLabel("Silence level: "));
		controls.add(silenceLevel);
		controls.add(adaptive);
		controls.add(new JLabel("Speech delta: "));
		controls.add(speechDelta);
		controls.add(new JPanel());
		controls.add(new JLabel("Silence delta: "));
		controls.add(silenceDelta);
		
		JButton saveButton = new JButton("Store");
		saveButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				leftVAD.storeParameters();
				if (rightVAD != null && !Utils.eq(leftVAD.getDeviceName(), rightVAD.getDeviceName())) {
					rightVAD.storeParameters();
				}
			}
		});
		if (!editable)
			saveButton.setEnabled(false);
		controls.add(saveButton);
		add(controls, BorderLayout.PAGE_END);
		
	}
	
	public void addToGUI(IrisGUI gui) {
		gui.addDockPanel("vad", "VAD", this, false);
	}

	protected void setAdaptive(boolean b) {
		silenceLevel.setEditable(!b);
		if (leftVAD != null)
			leftVAD.setAdaptive(b);
		if (rightVAD != null)
			rightVAD.setAdaptive(b);
	}

	protected void setSilenceLevel(int level) {
		if (leftVAD != null)
			leftVAD.setSilenceLevel(level);
		if (rightVAD != null)
			rightVAD.setSilenceLevel(level);
		silenceLevel.setText(level + "");
	}
	
	protected int getSilenceLevel() {
		return Integer.parseInt(silenceLevel.getText());
	}
	
	protected void setDeltaSpeech(int level) {
		if (leftVAD != null)
			leftVAD.setDeltaSpeech(level);
		if (rightVAD != null)
			rightVAD.setDeltaSpeech(level);
		speechDelta.setText(level + "");
	}
	
	protected void setDeltaSil(int level) {
		if (leftVAD != null)
			leftVAD.setDeltaSil(level);
		if (rightVAD != null)
			rightVAD.setDeltaSil(level);
		silenceDelta.setText(level + "");
	}
	
	private class LevelBar extends JPanel implements EnergyVAD.VADListener {

		private boolean inSpeech;
		private int energy;
		private int channel;

		public LevelBar(int channel) {
			this.channel = channel;
		}
		
		@Override
		public void vadEvent(long streamPos, boolean inSpeech, int energy) {
			this.inSpeech = inSpeech;
			this.energy = energy;
			repaint();
		}
		
		private int getY(int energy) {
			return (int) (getHeight() * (1.0 - (energy / 70.0)));
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			g.setColor(Color.BLACK);
			g.fillRect(0, 0, getWidth(), getHeight());
			int y = getY(energy);
			if (inSpeech)
				g.setColor(Color.ORANGE);
			else
				g.setColor(Color.GREEN);
			if (channel == 0 && leftVAD != null || channel == 1 && rightVAD != null) {
				g.fillRect(0, y, getWidth(), getHeight() - y);
				EnergyVAD vad = channel == 0 ? leftVAD : rightVAD;
				g.setColor(Color.white);
				y = getY(vad.getSilenceLevel());
				g.drawLine(0, y, getWidth(), y);
				y = getY(vad.getSilenceLevel() + vad.getDeltaSpeech());
				g.drawLine(0, y, getWidth(), y);
				y = getY(vad.getSilenceLevel() + vad.getDeltaSil());
				g.drawLine(0, y, getWidth(), y);
				if (vad.isAdaptive())
					silenceLevel.setText("" + vad.getSilenceLevel());
			}
		}

		public void clear() {
			energy = 0;
			repaint();
		}
		
	}
	
	private void setFormValues() {
		speechDelta.setText("" + leftVAD.getDeltaSpeech());
		silenceDelta.setText("" + leftVAD.getDeltaSil());
		silenceLevel.setText("" + leftVAD.getSilenceLevel());
		adaptive.setSelected(leftVAD.isAdaptive());
		silenceLevel.setEditable(!leftVAD.isAdaptive());
	}

	public void setLeftVAD(EnergyVAD vad) {
		this.leftVAD = vad;
		leftVAD.addVADListener(leftLevelBar);
		setFormValues();
	}
	
	public void setRightVAD(EnergyVAD vad) {
		this.rightVAD = vad;
		if (rightVAD != null)
			rightVAD.addVADListener(rightLevelBar);
	}
}
