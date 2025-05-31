import java.io.Serializable;

public class Input extends InOutBox implements Serializable{ // input 클래스
	
	Output output = null;
	Gate myGate = null;
	
	Input(int state, Gate myGate) {
		super();
		this.myGate = myGate;
	}
	
	void updateState() {
		if(output != null) {
			setState(output.getState());
		}
		else {
			setState(-1);
		}
		myGate.updateState();				
	}
	
	void link(Output output) { // output 연결 함수
		if(output != null && output.myGate == this.myGate) {
			output = null;
		}
		if(this.output != null) { // null 연결시 기존 연결 해제
			GateManager.getInstance().getConnectMap().remove(this, this.output);
		}
		this.output = output;
		updateState();			
	}
	
}