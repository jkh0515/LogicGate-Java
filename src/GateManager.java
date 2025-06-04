//import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JLabel;
import javax.swing.JLayeredPane;

public class GateManager { // Gate 관리하는 클래스
	private static GateManager instance = new GateManager();
	private List<Gate> gateList = new ArrayList<>();
	private Map<Input, Output> connectMap = new HashMap<>();
	private boolean nameActivate = false;
	private boolean stateActivate = false;
	private int gateMoveSpeed = 5;
	private boolean gateMoving = true;
	private JLayeredPane layeredPane;
	private Connection connection;
	
	private GateManager() {}
	
	public void init(JLayeredPane layeredPane, Connection connection) {
		this.layeredPane = layeredPane;
		this.connection = connection;
	}
	
	public static GateManager getInstance() {
		return instance;
	}
	
	public List<Gate> getGateList() {
		return gateList;
	}
	
	public Map<Input, Output> getConnectMap() {
		return connectMap;
	}
	
	public boolean getNameActivate() {
		return nameActivate;
	}
	
	public boolean getStateActivate() {
		return stateActivate;
	}
	
	public int getGateMoveSpeed() {
		return gateMoveSpeed;
	}
	
	public void setGateMoveSpeed(int speed) {
		gateMoveSpeed = speed;
	}
	
	public boolean getGateMoving() {
		return gateMoving;
	}
	
	public void setGateMoving(boolean tf) {
		gateMoving = tf;
	}
	
	public List<Component> getGateInRange(Rectangle rec) {
		List<Component> selectedList = new ArrayList<Component>();
		for(Gate gate : gateList) {
			if(rec.contains(gate.getBounds())) {
				selectedList.add(gate);
			}
		}
		return selectedList;
	}
	
	public void addGate(Gate gate) {
		gateList.add(gate);
	}
	
	public void setNameActivate(Boolean tf) {
		nameActivate = tf;
		for(Gate gate : gateList) {
			gate.gateLabel.setVisible(nameActivate);
		}
	}
	
	public void setStateActivate(Boolean tf) {
		stateActivate = tf;
		for(Gate gate : gateList) {
			gate.resultLabel.setVisible(stateActivate);
			for(JLabel inputLabel : gate.inputLabel) {
				inputLabel.setVisible(stateActivate);
			}
		}
	}
	
	public void clearAll() {
		for(Gate gate : gateList) {
			gate.clearLink();
			layeredPane.remove(gate);			
		}
		gateList.clear();
		connection.repaint();
	}
}