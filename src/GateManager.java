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
	private List<Gate> gateList = new ArrayList<>(); // 현재 모든 Gate 저장
	private Map<Input, Output> connectMap = new HashMap<>(); // 연결 선들 저장
	private boolean nameActivate = true; // Gate 이름 보여주기 여부
	private boolean stateActivate = true; // Gate 상태 보여주기 여부
	private int gateMoveSpeed = 5; // 현재 wasd 이동 속도
	private boolean gateMoving = true; 
	private JLayeredPane layeredPane;
	private Connection connection;
	private final HistoryManager history = new HistoryManager();
	private double scale = 1.0;
	
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
	
	public List<Component> getGateInRange(Rectangle rec) { // 우클릭 범위 지정 시 사용
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
	
	//Redo/Undo called
	public HistoryManager getHistory() { return history; }

	public void undo() {
	  history.undo();
	  connection.repaint();
	}
	
	public void redo() {
	  history.redo();
	  connection.repaint();
	}
	
	public JLayeredPane getLayeredPane() {
	    return layeredPane;
	}
	
	public void repaintConnections() {
	    connection.repaint();
	}
	
	//ZoomIn/ZoomOut called
	public void zoomIn() {
	    scale *= 1.1;
	    ((ZoomableLayeredPane) layeredPane).setScale(scale);
	}
	public void zoomOut() {
	    scale /= 1.1;
	    ((ZoomableLayeredPane) layeredPane).setScale(scale);
	}
	public double getScale() {
	    return scale;
	}
	
	public void clearAll() {
		for(Gate gate : gateList) {
			gate.clearLink();
			layeredPane.remove(gate);			
		}
		gateList.clear();
		connection.repaint();
	}
    
	// Remove gate from UI and internal List
	public void removeGate(Gate gate) {
	    
	    for (Output out : gate.output) {
	        out.clearLink(); 
	    }
	    
	    for (Input in : gate.input) {
	        in.link(null);    
	    }
	    
	    layeredPane.remove(gate);
	    gateList.remove(gate);
	    layeredPane.revalidate();
	    layeredPane.repaint();
	}
}