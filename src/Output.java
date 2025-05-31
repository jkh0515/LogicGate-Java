import java.awt.Color;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Output extends InOutBox implements Serializable{ // output 클래스
	
	List<Input> inputList;
	Color lineColor = Color.RED;
	Gate myGate = null;

	Output(int state, Gate myGate) {
		super();
		inputList = new ArrayList<Input>();
		this.myGate = myGate;
		setState(state);
	}
	
	public int setState(int state) {
		super.setState(state);
		for(int i=0;i<inputList.size();i++) {
			inputList.get(i).updateState();
		}
		return getState();
	}
	
	void clearLink() {
		for(Input input : inputList) {
			input.link(null);
		}
		GateManager.getInstance().getConnectMap().values().removeAll(Collections.singleton(this));
	}
	
	void link(Input input) { // input 연결 함수
		if(input != null) {
			if(this.myGate != input.myGate) {
				inputList.add(input);
				GateManager.getInstance().getConnectMap().put(input, this);								
			}
		}
	}
	
}