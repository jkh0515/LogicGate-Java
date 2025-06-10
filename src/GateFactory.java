import java.awt.Color;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;

public class GateFactory { // 게이트 만드는 클래스
	
	JLayeredPane layeredPane;
	Connection connection;
	Rectangle layerSize;
	
	Map<String, Point> gateInOut = Map.of(
			"INPUT", new Point(0, 1), 
			"OUTPUT", new Point(1, 0),
			"AND", new Point(2, 1),
			"OR", new Point(2, 1),
			"NOT", new Point(1, 1),
			"NAND", new Point(2, 1),
			"NOR", new Point(2, 1),
			"XOR", new Point(2, 1),
			"XNOR", new Point(2, 1)
			);
	
	Map<String, Integer[]> gateInputLimit = Map.of(
		    "INPUT",  new Integer[]{0},
		    "OUTPUT", new Integer[]{1},
		    "AND",    new Integer[]{2, 3, 4, 5},
		    "OR",     new Integer[]{2, 3, 4, 5},
		    "NOT",    new Integer[]{1},
		    "NAND",   new Integer[]{2, 3, 4, 5},
		    "NOR",    new Integer[]{2, 3, 4, 5},
		    "XOR",    new Integer[]{2},
		    "XNOR",   new Integer[]{2}
		);
	
	GateFactory(JLayeredPane layeredPane, Connection connection) {
		this.layeredPane = layeredPane;
		this.connection = connection;
		layerSize = connection.getBounds();
	}
	
	JLabel removeLabel() { // 삭제 label ('게이트 움직이기' 활성화 상태일때만 보임)
		JLabel removeLabel = new JLabel("REMOVE");
		removeLabel.setOpaque(true);
		removeLabel.setForeground(Color.WHITE);
		removeLabel.setBackground(Color.BLUE);
		removeLabel.setBounds(layerSize.width / 2 - 30, layerSize.height - 50, 60, 30);
		removeLabel.setVisible(false);
		layeredPane.add(removeLabel, JLayeredPane.DRAG_LAYER);
		return removeLabel;
	}

	
	Gate setGate(Gate myGate, int x, int y) { // Gate 기본 설정 함수
		myGate.setBounds(x, y, myGate.widthSize + 20, myGate.heightSize + 20);
		myGate.gateLabel.setVisible(GateManager.getInstance().getNameActivate());
		myGate.resultLabel.setVisible(GateManager.getInstance().getStateActivate());
		for(JLabel label : myGate.inputLabel) {
			label.setVisible(GateManager.getInstance().getStateActivate());
		}
		
		// changed for using redo/undo
		GateManager gm = GateManager.getInstance();
		GateManager.getInstance()
        .getHistory()
        .doCommand(new AddGateCommand(gm, myGate, x, y)); 
		
		
		
		myGate.setSelcect(false);
		myGate.updateState();
		myGate.inOutLine = new ImageIcon(getClass().getResource("/img/in_out_line.png")).getImage();
		return myGate;
	}
	
	Gate inputGate(Gate newGate, int x, int y) { // inputGate 만드는 함수
		
		if(newGate.btn != null) {
			newGate.remove(newGate.btn);
		}
		
		newGate.btn = new JButton("0"); // 버튼 추가
		newGate.btn.setFocusable(false);
		newGate.btn.setMargin(new Insets(0, 0, 0, 0));
		newGate.btn.setBounds(35, 30 + Gate.fontSize, newGate.widthSize - 62, newGate.heightSize - 60);
		newGate.btn.setFont(newGate.outputLabel.getFont().deriveFont(15f));
		
		newGate.gateLabel.setText("INPUT");
		
		newGate.btn.addActionListener(new ActionListener() { // 버튼 리스너 추가 (클릭했을때 변환 / output 상태 변환 함수 호출)
            public void actionPerformed(ActionEvent e) {
                newGate.setState((newGate.getState() == 0) ? 1 : 0);
                newGate.btn.setText(Integer.toString(newGate.getState()));
                for(int i=0;i<newGate.getOutputNum();i++) {
                	newGate.output[i].setState(newGate.getState());
                }
                newGate.updateState();
            }
        });
		newGate.btn.setVisible(false);
		newGate.component.add(newGate.btn);
		newGate.add(newGate.btn);
		
		return setGate(newGate, x, y);
	}
	
	Gate outputGate(Gate newGate, int x, int y) { // outputGate 만드는 함수
		
		newGate.outputLabel.setFont(newGate.outputLabel.getFont().deriveFont(20f));
		newGate.outputLabel.setBounds(newGate.width / 2 + 2, newGate.height / 2 - 10 + 7, 25, 25);
		newGate.add(newGate.outputLabel);
		
		newGate.gateLabel.setText("OUTPUT");
		
		newGate.updateLogic = () -> {
			int nowState = newGate.input[0].getState();
			newGate.setState(nowState);
			newGate.outputLabel.setText(nowState < 0 ? "?" : Integer.toString(nowState));
		};
		
		return setGate(newGate, x, y);
	}
	
	Gate andGate(Gate newGate, int x, int y) { // andGate 만드는 함수
		
		newGate.gateLabel.setText("AND");
		
		newGate.updateLogic = () -> { // and 계산
			int tempState = newGate.input[0].getState();
			for(int i=1;tempState!=-1&&i<newGate.getInputNum();i++) {
				if(newGate.input[i].getState() == -1) {
					tempState = -1;
					break;
				}
				tempState &= newGate.input[i].getState();
			}
			newGate.setState(tempState);
			for(int i=0;i<newGate.getOutputNum();i++) {
				newGate.output[i].setState(newGate.getState());					
			}
		};
		
		return setGate(newGate, x, y);
	}
	
	Gate orGate(Gate newGate, int x, int y) { // orGate 만드는 함수
		
		newGate.gateLabel.setText("OR");
		
		newGate.updateLogic = () -> { // or 계산
			int tempState = newGate.input[0].getState();
			for(int i=1;tempState!=-1&&i<newGate.getInputNum();i++) {
				if(newGate.input[i].getState() == -1) {
					tempState = -1;
					break;
				}
				tempState |= newGate.input[i].getState();
			}
			newGate.setState(tempState);
			for(int i=0;i<newGate.getOutputNum();i++) {
				newGate.output[i].setState(newGate.getState());					
			}
		};
		
		return setGate(newGate, x, y);
	}
	
	Gate notGate(Gate newGate, int x, int y) { // notGate 만드는 함수
		
		newGate.gateLabel.setText("NOT");
		
		newGate.updateLogic = () -> { // not 계산
			if(newGate.input[0].getState() == -1) {
				newGate.setState(-1);
			}
			else {
				newGate.setState(newGate.input[0].getState() ^ 1);					
			}
			newGate.output[0].setState(newGate.getState());
		};
		return setGate(newGate, x, y);
	}
	

	Gate nandGate(Gate newGate, int x, int y) {
		
		newGate.gateLabel.setText("NAND");
		
		newGate.updateLogic = () -> {
			int tempState = newGate.input[0].getState();
			for(int i=1;tempState!=-1&&i<newGate.getInputNum();i++) {
				if(newGate.input[i].getState() == -1) {
					tempState = -1;
					break;
				}
				tempState &= newGate.input[i].getState();
			}
			newGate.setState(tempState ^ 1);
			for(int i=0;i<newGate.getOutputNum();i++) {
				newGate.output[i].setState(newGate.getState());					
			}
		};
		
		return setGate(newGate, x, y);
	}
	

	Gate norGate(Gate newGate, int x, int y) {
		
		newGate.gateLabel.setText("NOR");
		
		newGate.updateLogic = () -> {
			int tempState = newGate.input[0].getState();
			for(int i=1;tempState!=-1&&i<newGate.getInputNum();i++) {
				if(newGate.input[i].getState() == -1) {
					tempState = -1;
					break;
				}
				tempState |= newGate.input[i].getState();
			}
			newGate.setState(tempState ^ 1);
			for(int i=0;i<newGate.getOutputNum();i++) {
				newGate.output[i].setState(newGate.getState());					
			}
		};
		
		return setGate(newGate, x, y);
	}
	

	Gate xorGate(Gate newGate, int x, int y) {
		
		newGate.gateLabel.setText("XOR");
		
		newGate.updateLogic = () -> {
			int tempState = newGate.input[0].getState();
			for(int i=1;tempState!=-1&&i<newGate.getInputNum();i++) {
				if(newGate.input[i].getState() == -1) {
					tempState = -1;
					break;
				}
				tempState ^= newGate.input[i].getState();
			}
			newGate.setState(tempState);
			for(int i=0;i<newGate.getOutputNum();i++) {
				newGate.output[i].setState(newGate.getState());					
			}
		};
		
		return setGate(newGate, x, y);
	}
	
	Gate xnorGate(Gate newGate, int x, int y) {
		
		newGate.gateLabel.setText("XNOR");
		
		newGate.updateLogic = () -> {
			int tempState = newGate.input[0].getState();
			for(int i=1;tempState!=-1&&i<newGate.getInputNum();i++) {
				if(newGate.input[i].getState() == -1) {
					tempState = -1;
					break;
				}
				tempState ^= newGate.input[i].getState();
			}
			newGate.setState(tempState ^ 1);
			for(int i=0;i<newGate.getOutputNum();i++) {
				newGate.output[i].setState(newGate.getState());					
			}
		};
		
		return setGate(newGate, x, y);
	}
}