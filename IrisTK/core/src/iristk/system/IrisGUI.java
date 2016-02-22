package iristk.system;

import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.*;

import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.theme.ThemeMap;


public class IrisGUI extends JFrame implements EventListener {

	private CControl control;
	private File configFile;
	private IrisSystem system;
	private HashMap<String,Dockable> dockables = new HashMap<>();
	private JMenu windowMenu;
	
	static {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e2) {
			e2.printStackTrace();
		} catch (InstantiationException e2) {
			e2.printStackTrace();
		} catch (IllegalAccessException e2) {
			e2.printStackTrace();
		} catch (UnsupportedLookAndFeelException e2) {
			e2.printStackTrace();
		}
	}

	public IrisGUI(IrisSystem system, File configFile) {
		this.system  = system;
		this.configFile = configFile;
		
		system.addEventListener(this);
		
		setTitle("IrisTK - " + system.getName());
		setIconImage(new ImageIcon(getClass().getResource("iristk.png")).getImage());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		control = new CControl(this);
		
		ThemeMap themes = control.getThemes();
		themes.select(ThemeMap.KEY_ECLIPSE_THEME);
		
		setLayout(new GridLayout(1, 1));
		add(control.getContentArea());
		
		setBounds(0, 0, 1000, 900);
		
		createMenu();
	}
	
	public IrisGUI(IrisSystem system) {
		this(system, new File(system.getPackageDir(), system.getName() + ".gui"));
	}

	private void createMenu() {
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		windowMenu = new JMenu("Window");
		menuBar.add(windowMenu);		
		
		JMenuItem item = new JMenuItem("Save perspective");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				savePerspective();
			}
		});
		windowMenu.add(item);
		item = new JMenuItem("Load perspective");
		item.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadPerspective();
			}
		});
		windowMenu.add(item);
		windowMenu.addSeparator();
	}
	
	protected void systemStarted() {
		loadPerspective();
		setVisible(true);
	}
	
	public void savePerspective() {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					control.save("default");
					configFile.getParentFile().mkdirs();
					control.write(configFile);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
	}
	
	public void loadPerspective() {
		if (configFile.exists()) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					try {
						control.read(configFile);
						control.load("default");
						for (Dockable dockable : dockables.values()) {
							dockable.checkVisible();
						}
					} catch (IOException e) {
						System.out.println("Could not read " + configFile + ", deleting it");
						configFile.delete();
					}
				}
			});
		}
	}
	
	public void addDockPanel(String id, String title, Component component, final boolean visible) {
		final Dockable dockable = new Dockable(id, title, component);
		dockables.put(id, dockable);
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				control.addDockable(dockable);
				dockable.setVisible(visible);
			}});
	}

	@Override
	public void onEvent(Event event) {
		if (event.triggers("monitor.system.start")) {
			systemStarted();
		}
	}

	public void showDockPanel(String dockId, boolean visible) {
		dockables.get(dockId).setVisible(visible);
	}
	
	private class Dockable extends DefaultSingleCDockable implements ActionListener {
		
		private JCheckBoxMenuItem menuItem;

		public Dockable(String id, String title, Component component) {
			super(id, title, component);
			menuItem = new JCheckBoxMenuItem(title);
			menuItem.addActionListener(this);
			windowMenu.add(menuItem);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					Dockable.super.setVisible(menuItem.isSelected());
				}
			});
		}
		
		@Override
		public void setVisible(final boolean visible) {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					Dockable.super.setVisible(visible);
					checkVisible();
				}
			});
		}
		
		public void checkVisible() {
			menuItem.setSelected(isVisible());
		}
		
	}

	public File getDataDir() {
		return configFile.getParentFile();
	}

	
}
