import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.io.Serializable;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class InOutBox extends JPanel implements Serializable{ // input / output 기본 클래스
	private int state = -1;
	JLabel label;
	
	public static final int widthSize = 10;
	public static final int heightSize = 10;
	
	int width = widthSize;
	int height = heightSize;
	
	InOutBox() { // 기본 '-' 글 적기 (이미지로 대체 예정)
		this.setFocusable(false);
		this.setLayout(null);
		label = new JLabel("-");
		label.setVerticalAlignment(SwingConstants.TOP);
		label.setBounds(0, 0, width, height);
		this.add(label);
	}
	
	public int getState() {
		return state;
	}
	
	public int setState(int state) {
		return this.state = state;
	}
	
	protected void paintComponent(Graphics g) { // 테두리 그리기 (이미지로 대체 예정)
		super.paintComponent(g);
		if(state < 0) {
			g.setColor(Color.RED);			
		}
		else {
			g.setColor(Color.BLUE);
		}
		g.drawRect(0, 0, width-1, height-1);
	}
	
	Point getPoint(JComponent relativeTo) { // 현재 위치하고 있는 중앙 좌표 구하기 함수 (연결할때 사용)
		
	    Point myPoint = getLocation();
	    myPoint.x += getWidth() / 2;
	    myPoint.y += getHeight() / 2;
	    
	    SwingUtilities.convertPointToScreen(myPoint, getParent());
	    SwingUtilities.convertPointFromScreen(myPoint, relativeTo);
	    return myPoint;
	}
}