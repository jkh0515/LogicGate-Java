import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

public class Menu extends JMenuBar{
	GateFactory gateFactory;
	MyMouseListener myMouseListener;
	
	int menuHeight = 60;
	
	MenuListener menuListener = new MenuListener() {
		public void menuSelected(MenuEvent e) {
			GateManager.getInstance().setGateMoving(false);
		}
		public void menuDeselected(MenuEvent e) {}
		public void menuCanceled(MenuEvent e) {}
	}; 
	
	Menu(GateFactory gateFactory, MyMouseListener myMouseListener) {
		this.gateFactory = gateFactory;
		this.myMouseListener = myMouseListener;
		MenuItemListener menuItemListener = new MenuItemListener(gateFactory, myMouseListener);	
		
		JMenu fileMenu = createMenu("File");
		
		JMenuItem openFile = new JMenuItem("Open File");
		JMenuItem saveFile = new JMenuItem("Save File");
		openFile.addActionListener(menuItemListener);
		saveFile.addActionListener(menuItemListener);
		saveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		fileMenu.add(openFile);
		fileMenu.add(saveFile);
		
		JMenu gateMenu = createMenu("Gate");
		
		String[] gateNames = {"INPUT", "OUTPUT", "AND", "OR", "NOT", "NAND", "NOR", "XOR", "XNOR"};
		
		for(String name : gateNames) {
			JMenuItem item = new JMenuItem(name);
			item.addActionListener(menuItemListener);
			gateMenu.add(item);
		}
		
		JMenu clearMenu = createMenu("Clear");
		
		JMenuItem clearAll = new JMenuItem("Clear All");
		clearAll.addActionListener(menuItemListener);
		clearMenu.add(clearAll);
		clearAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK));
		
		JMenu setMenu = createMenu("Setting");
		
		JMenu setNameActivate = new JMenu("Show Gate Name");
		setMenu.add(setNameActivate);
		
		JMenuItem activateName = new JMenuItem("Activate");
		JMenuItem inactivateName = new JMenuItem("Inactivate");
		activateName.addActionListener(menuItemListener);
		inactivateName.addActionListener(menuItemListener);
		setNameActivate.add(activateName);
		setNameActivate.add(inactivateName);
		
		JMenuItem setGateMoveSpeed = new JMenuItem("Set Move Speed");
		setGateMoveSpeed.addActionListener(menuItemListener);
		setMenu.add(setGateMoveSpeed);
		
		JMenu helpMenu = createMenu("Help");
		
		JMenuItem helpShortcut = new JMenuItem("Shortcut Keys");
		helpShortcut.addActionListener(menuItemListener);
		helpMenu.add(helpShortcut);
		
		JMenuItem helpUse = new JMenuItem("How To Use");
		helpUse.addActionListener(menuItemListener);
		helpMenu.add(helpUse);
		
		JMenuItem exitMenu = new JMenuItem("      Exit");
		exitMenu.setPreferredSize(new Dimension(80, menuHeight));
		exitMenu.setAccelerator(KeyStroke.getKeyStroke((char) KeyEvent.VK_ESCAPE));
		exitMenu.addActionListener(menuItemListener);
		add(exitMenu);
		
		setSize(LogicGateMain.frameWidth, menuHeight);
		setVisible(true);
	}
	
	JMenu createMenu(String str) {
		JMenu menu = new JMenu(" ".repeat(10 - str.length()) + str);
		menu.addMenuListener(menuListener);
		menu.setPreferredSize(new Dimension(80, menuHeight));
		this.add(menu);
		return menu;
	}
}
