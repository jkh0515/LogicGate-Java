import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.List;

import javax.swing.JLayeredPane;
import javax.swing.SwingUtilities;

public class MainKeyListener extends KeyAdapter{
	JLayeredPane layeredPane;
	Thread moveThread;
	
	private volatile boolean moveLeft = false;
	private volatile boolean moveRight = false;
	private volatile boolean moveUp = false;
	private volatile boolean moveDown = false;
	
	MainKeyListener(JLayeredPane layeredPane) {
		this.layeredPane = layeredPane;
	}
	
	public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: moveUp = true; break;
            case KeyEvent.VK_S: moveDown = true; break;
            case KeyEvent.VK_A: moveLeft = true; break;
            case KeyEvent.VK_D: moveRight = true; break;
        }
		if(moveThread == null || !moveThread.isAlive()) {
			GateManager.getInstance().setGateMoving(true);
			moveThread = new Thread(() -> move());
			moveThread.start();
		}
    }
	
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_W: moveUp = false; break;
            case KeyEvent.VK_S: moveDown = false; break;
            case KeyEvent.VK_A: moveLeft = false; break;
            case KeyEvent.VK_D: moveRight = false; break;
        }
    }
    
    public void move() {
        while (GateManager.getInstance().getGateMoving()) {
            boolean moved = false;
            int[] move = {0, 0};
            int speed = GateManager.getInstance().getGateMoveSpeed();
            if (moveUp)    { move[1] -= speed; moved = true; }
            if (moveDown)  { move[1] += speed; moved = true; }
            if (moveLeft)  { move[0] -= speed; moved = true; }
            if (moveRight) { move[0] += speed; moved = true; }
            if (moved) {
            	if(Math.abs(move[0]) + Math.abs(move[1]) == speed * 2) {
            		move[0] *= 0.7;
            		move[1] *= 0.7;
            	}
	        	List<Gate> gateList = GateManager.getInstance().getGateList();
	        	for(Gate gate : gateList) {
	        		if(gate.isSelcted == true) continue;
	        		gate.setLocation(gate.getX() + move[0], gate.getY() + move[1]);
	        	}
	        	layeredPane.repaint();
            }
            try {
                Thread.sleep(15);
            } catch (InterruptedException e) { }
        }
        moveUp = false;
        moveDown = false;
        moveLeft = false;
        moveRight = false;
    }
}
