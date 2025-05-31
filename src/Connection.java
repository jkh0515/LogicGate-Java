import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

public class Connection extends JPanel implements Serializable { // 연결 선 그려주는 클래스
	
	private int isDraw = 0;
	private Point start, finish;
	
	Connection() {
		this.setOpaque(false);
		this.setFocusable(false);
	}
	
	public void setStartPoint(int mode, int sx, int sy) {
		isDraw = mode;
		start = new Point(sx, sy);
	}
	
	public void setFinishPoint(int mode, int fx, int fy) {
		isDraw = mode;
		finish = new Point(fx, fy);
	}
	
    protected void paintComponent(Graphics g) { // 선 그리는 함수
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setStroke(new BasicStroke(3));

        for (Map.Entry<Input, Output> entry : GateManager.getInstance().getConnectMap().entrySet()) {
            Point startPoint = entry.getValue().getPoint(this);
            Point finalPoint = entry.getKey().getPoint(this);
            g2d.setColor(entry.getValue().lineColor);
            Point middlePoint = new Point((startPoint.x + finalPoint.x) / 2, (startPoint.y + finalPoint.y) / 2);
            if(startPoint.x < finalPoint.x) {
            	g2d.drawLine(startPoint.x, startPoint.y, middlePoint.x, startPoint.y);
            	g2d.drawLine(middlePoint.x, startPoint.y, middlePoint.x, finalPoint.y);
            	g2d.drawLine(middlePoint.x, finalPoint.y, finalPoint.x, finalPoint.y);            	
            }
            else {
            	g2d.drawLine(startPoint.x, startPoint.y, startPoint.x, middlePoint.y);
            	g2d.drawLine(startPoint.x, middlePoint.y, finalPoint.x, middlePoint.y);
            	g2d.drawLine(finalPoint.x, middlePoint.y, finalPoint.x, finalPoint.y);            		
            }
        }

        if(isDraw == 1) {
            g2d.setColor(Color.PINK);
            g2d.drawLine(start.x, start.y, finish.x, finish.y);
        }
        else if(isDraw == 2) {
        	g2d.setColor(Color.LIGHT_GRAY);
        	g2d.drawRect(Math.min(start.x, finish.x), Math.min(start.y, finish.y), Math.abs(finish.x - start.x), Math.abs(finish.y - start.y));
        }
    }
}