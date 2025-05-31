import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Gate extends JPanel implements Serializable{ //모든 게이트 기본 클래스 
	List<Object> component = new ArrayList<Object>();
	Output output[];
	Input input[];
	JLabel inputLabel[];
	JLabel resultLabel; // 결과 보여주는 label
	JLabel gateLabel; // gate 종류 보여주는 label
	
	JButton btn;
	transient Runnable updateLogic; // 게이트별 논리
	
	transient Image img;
	
	public static final int widthSize = 100;
	public static final int heightSize = 100;
	public static final Color selectColor = Color.GREEN;
	public static final Color originColor = Color.BLUE; 
	
	Color bolderColor = originColor;

	int width = widthSize;
	int height = heightSize;
	int inOutGap = width / 5;
	
	Point gateLabelSize = new Point(30, 30);
	
	private int inputNum;
	private int outputNum;
	private int myState;
	
	Gate(Point InOut) { // 생성자로 input / output 개수를 받음
		component.add(this);
		this.inputNum = InOut.x;
		this.outputNum = InOut.y;
		this.setFocusable(false);
		this.setOpaque(false);
		
		output = new Output[outputNum];
		input = new Input[inputNum];		
		inputLabel = new JLabel[inputNum];
		resultLabel = new JLabel("?");
		
		gateLabel = new JLabel();
		gateLabel.setBounds(widthSize/2 - gateLabelSize.x/2, 0, gateLabelSize.x, gateLabelSize.y);
		gateLabel.setVisible(false);
		this.add(gateLabel);
		
		int inputGap = (heightSize / 5 * 4) / (inputNum + 1);
		int outputGap = (heightSize / 5 * 4) / (outputNum + 1);			

		this.setLayout(null);
		this.setSize(widthSize, heightSize);
		this.setVisible(true);			

		for(int i=0;i<inputNum;i++) { // input 붙이기
			input[i] = new Input(-1, this);
			input[i].setBounds(0 , (i + 1) * inputGap, input[i].width, input[i].height);
			
			inputLabel[i] = new JLabel("?");
			inputLabel[i].setVerticalAlignment(SwingConstants.TOP);
			inputLabel[i].setBounds(input[i].width * 2, (i + 1) * inputGap, input[i].width, input[i].height);
			
			component.add(input[i]);
			component.add(inputLabel[i]);
			this.add(input[i]);
			this.add(inputLabel[i]);
		}
		
		for(int i=0;i<outputNum;i++) { // output 붙이기
			output[i] = new Output(-1, this);
			output[i].setBounds(widthSize - output[i].width * 2, (i + 1) * outputGap, output[i].width, output[i].height);
			
			component.add(output[i]);
			this.add(output[i]);
		}
	}
	
	public int getState() {
		return this.myState;
	}
	
	public int setState(int state) {
		return this.myState = state;
	}
	
	public int getInputNum() {
		return inputNum;
	}
	
	public int getOutputNum() {
		return outputNum;
	}
	
	public void clearLink() {
		for(int i=0;i<inputNum;i++) {
			input[i].link(null);
		}
		for(int i=0;i<outputNum;i++) {
			output[i].clearLink();
		}
	}
	
	public void setScale(double scale) {
		for (Object obj : component) {
			Component comp = (Component) obj;
			comp.setBounds(comp.getX(), comp.getY(), 0, 0);
		}
	}
	
	public void setInputNum(int num) {
		int i;
		Input[] oldInput = input;
		JLabel[] oldInputLabel = inputLabel;
		int oldInputNum = inputNum;
		input = new Input[num];
		inputLabel = new JLabel[num];
		inputNum = num;		
		int inputGap = (height / 5 * 4) / (num + 1);
		for(i=0;i<num;i++) {
			if(i < oldInput.length) {
				input[i] = oldInput[i];				
				inputLabel[i] = oldInputLabel[i];
			}
			else {
				input[i] = new Input(-1, this);
				inputLabel[i] = new JLabel("?");
				inputLabel[i].setVerticalAlignment(SwingConstants.TOP);
				component.add(input[i]);
				component.add(inputLabel[i]);
				this.add(input[i]);
				this.add(inputLabel[i]);
			}
			input[i].height = inputGap;
			input[i].setBounds(0 , (i + 1) * inputGap, input[i].width, input[i].height);
			inputLabel[i].setBounds(input[i].width * 2, (i + 1) * inputGap, input[i].width, input[i].height);
		}
		for(;i<oldInputNum;i++) {
			oldInput[i].link(null);
			component.remove(oldInput[i]);
			component.remove(oldInputLabel[i]);
			this.remove(oldInput[i]);
			this.remove(oldInputLabel[i]);
		}
		updateState();
	}
	
	public void setColor(Color color) {
		for(int i=0;i<outputNum;i++) {
			output[i].lineColor = color;
		}
	}
	
	protected void paintComponent(Graphics g) { // 테두리 그려주는 코드 (이미지로 대체할 예정)
		super.paintComponent(g);
		g.drawImage(img, 0, 0, width, height, this);
	}
	
	void setSelcect(boolean tf) {
		img = new ImageIcon(getClass().getResource("/img/"+gateLabel.getText().toLowerCase() + (tf ? "_selected" : "_normal") + ".png")).getImage();
		repaint();
	}
	
	void updateState() { // 업데이트 로직
		if(updateLogic != null) {
			updateLogic.run();
		}
		resultLabel.setText((myState < 0) ? "?" : Integer.toString(myState));
		for(int i=0;i<inputNum;i++) {
			inputLabel[i].setText((input[i].getState() < 0) ? "?" : Integer.toString(input[i].getState()));
		}
		for(int i=0;i<outputNum;i++) {
			output[i].setState(myState);
		}
	}
	
	public String toString() {
		String str = gateLabel.getText() + "/" +
				this.getLocation().x + "/" +
				this.getLocation().y;
		return str;
	}
}