import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.JLayeredPane;

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
            int move_x = 0, move_y = 0;
            int speed = GateManager.getInstance().getGateMoveSpeed();
            if (moveUp)    { move_y -= speed; moved = true; }
            if (moveDown)  { move_y += speed; moved = true; }
            if (moveLeft)  { move_x -= speed; moved = true; }
            if (moveRight) { move_x += speed; moved = true; }
            if (moved) {
            	if(Math.abs(move_x) + Math.abs(move_y) == speed * 2) {
            		move_x *= 0.7;
            		move_y *= 0.7;
            	}
            	List<Gate> gateList = GateManager.getInstance().getGateList();
            	for(Gate gate : gateList) {
            		gate.setLocation(gate.getX() + move_x, gate.getY() + move_y);
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
