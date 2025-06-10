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
		//Created a new holder Edit for redo/undo
		JMenu editMenu = createMenu("Edit");
		JMenuItem undoItem   = new JMenuItem("Undo");
		JMenuItem redoItem   = new JMenuItem("Redo");
		
		undoItem.setActionCommand("undo");
		redoItem.setActionCommand("redo");
		
		undoItem.addActionListener(menuItemListener);
		undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
		redoItem.addActionListener(menuItemListener);
		redoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));

		editMenu.add(undoItem);
		editMenu.add(redoItem);

		this.add(editMenu);

		JMenu viewMenu = createMenu("View");

		JMenuItem zoomInView  = new JMenuItem("Zoom In");
		JMenuItem zoomOutView = new JMenuItem("Zoom Out");

		//Created a new holder View for zoom in and out
		zoomInView .setActionCommand("zoomIn");
		zoomOutView.setActionCommand("zoomOut");

		zoomInView .addActionListener(menuItemListener);
		zoomOutView.addActionListener(menuItemListener);

		zoomInView.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, InputEvent.CTRL_DOWN_MASK));
		zoomOutView.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, InputEvent.CTRL_DOWN_MASK));

		viewMenu.add(zoomInView);
		viewMenu.add(zoomOutView);
		this.add(viewMenu);
		
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
		activateName.setActionCommand("nameActivate");
		activateName.addActionListener(menuItemListener);
		inactivateName.setActionCommand("nameInactivate");
		inactivateName.addActionListener(menuItemListener);
		setNameActivate.add(activateName);
		setNameActivate.add(inactivateName);
		
		JMenu setStateActive = new JMenu("Show State");
		setMenu.add(setStateActive);
		
		JMenuItem activateState = new JMenuItem("Activate");
		JMenuItem inactivateState = new JMenuItem("Inactivate");
		activateState.setActionCommand("stateActivate");
		activateState.addActionListener(menuItemListener);
		inactivateState.setActionCommand("stateInactivate");
		inactivateState.addActionListener(menuItemListener);
		setStateActive.add(activateState);
		setStateActive.add(inactivateState); 
		
		JMenuItem setGateMoveSpeed = new JMenuItem("Set Move Speed");
		setGateMoveSpeed.addActionListener(menuItemListener);
		setMenu.add(setGateMoveSpeed);
		
		JMenuItem exitMenu = new JMenuItem("      Exit");
		exitMenu.setPreferredSize(new Dimension(80, menuHeight));
		exitMenu.setAccelerator(KeyStroke.getKeyStroke((char) KeyEvent.VK_ESCAPE));
		exitMenu.addActionListener(menuItemListener);
		add(exitMenu);
	}
	
	JMenu createMenu(String str) {
		JMenu menu = new JMenu(" ".repeat(10 - str.length()) + str);
		menu.addMenuListener(menuListener);
		menu.setPreferredSize(new Dimension(80, menuHeight));
		this.add(menu);
		return menu;
	}
}
