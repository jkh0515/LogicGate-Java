import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;

public class MyMouseListener extends MouseAdapter { //main 들어갈 마우스 리스너
	private boolean isMoving = false;
	private boolean selecting = false;
	private boolean isCreating = false;
	private int isDrawing = 0;
	
	List<Integer> click_x, click_y;
	List<Component> selectedList;
	
	Rectangle removeSize;
	Point rightDragStart, rightDragFinish = new Point(0, 0);
	
	Connection connection;
	JLayeredPane layeredPane;
	JLabel removeLabel;
	GateFactory gateFactory;
	
	MyMouseListener(JLayeredPane layeredPane, Connection connection, JLabel removeLabel, GateFactory gateFactory) {
		this.connection = connection;
		this.layeredPane = layeredPane;
		this.removeLabel = removeLabel;
		this.gateFactory = gateFactory;
		selectedList = new ArrayList<Component>();
		click_x = new ArrayList<Integer>();
		click_y = new ArrayList<Integer>();
		removeSize = removeLabel.getBounds();
	}
	
	public void setIsCreating() {
		isCreating = true;
	}
	
    public void mousePressed(MouseEvent e) { // 마우스를 클릭했을때
    	if(!isCreating) {
    		Component comp = e.getComponent();
    		if(e.getButton() == MouseEvent.BUTTON1) {
    			if (comp instanceof Container) {
    				Container container = (Container) comp;
    				Component source = container.findComponentAt(e.getPoint());
    				if (source instanceof Gate) { // Gate 클릭했을때 클릭 좌표 기억해두기, isMoving 변환시켜서 '게이트 움직이기' 활성화
    					if(selectedList.size() == 0) {
    						selectedList.add(source);    
    						((Gate) source).setSelcect(true);
    					}
    					click_x.clear();
    					click_y.clear();
    					for(Component select : selectedList) {
    						click_x.add(e.getX() - select.getLocation().x);
    						click_y.add(e.getY() - select.getLocation().y);    						
    					}
    					isMoving = true;
    				}
    				else { // input / output 클릭했을때 isDrawing 변환시켜서 '선 그리기' 활성화
    					click_x.clear();
    					click_y.clear();
    					source = source.getParent();
    					click_x.add(e.getX());
    					click_y.add(e.getY());
    					connection.setStartPoint(0, click_x.get(0), click_y.get(0));
    					if (source instanceof Input) {
    						isDrawing = 2;
    						selectedList.add(source);
    					}
    					else if (source instanceof Output) {
    						isDrawing = 1;
    						selectedList.add(source);
    					}	            	
    				}
    			}    		
    		}
    		else if(e.getButton() == MouseEvent.BUTTON2) {
    			if(comp instanceof Container) {
    				Container container = (Container) comp;
    				Component source = container.findComponentAt(e.getPoint());
    				String[] options = {"input 개수 변경", "output 연결 색상 변경", "취소"};
    				if(source instanceof Gate) {
    					int result = JOptionPane.showOptionDialog(
    				            null,
    				            "어떤 작업을 진행하시겠습니까?", "선택",
    				            JOptionPane.DEFAULT_OPTION,
    				            JOptionPane.QUESTION_MESSAGE,
    				            null,
    				            options,
    				            options[0]
    				        );
    				        if (result == 0) {
    				        	Integer[] inputLimit = gateFactory.gateInputLimit.get(((Gate) source).gateLabel.getText());
    				        	Integer inputNum = (Integer) JOptionPane.showInputDialog(
    				                    null,
    				                    "input 개수를 선택하세요:", "숫자 선택",
    				                    JOptionPane.QUESTION_MESSAGE,
    				                    null,
    				                    inputLimit,
    				                    inputLimit[0]
    				                );
    				                if (inputNum != null) {
    				                	((Gate) source).setInputNum(inputNum);
    				                }
    				        } else if (result == 1) {
    				            ((Gate) source).setColor(JColorChooser.showDialog(comp, "Output Line 색상 선택", Color.RED));
    				        }
    				        connection.repaint();
    				}
    			}
    		}
    		else if(e.getButton() == MouseEvent.BUTTON3) {
    			selecting = true;
    			rightDragStart = new Point(e.getX(), e.getY());
    			connection.setStartPoint(0, rightDragStart.x, rightDragStart.y);
    		}
    	}
    	else {
    		isCreating = false;
    		for(int i=0;i<selectedList.size();i++) {
    			if(((Gate) selectedList.get(i)).gateLabel.getText().equals("INPUT")) {
    				((Gate) selectedList.get(i)).btn.setVisible(true);
    			}    			
    		}
    		for(Component select : selectedList) {
    			if(select instanceof Gate) {
    				((Gate) select).setSelcect(false);;    				
    			}
    		}
    		selectedList.clear();
    		connection.repaint();
    	}
    }
    
    public void mouseMoved(MouseEvent e) {
    	if(isCreating) {
    		for(int i=0;i<selectedList.size();i++) {
				int newX = e.getX() + click_x.get(i);
				int newY = e.getY() + click_y.get(i);
				selectedList.get(i).setLocation(newX, newY);				
			}
    	}
    }
    
    public void mouseDragged(MouseEvent e) { // 마우스 드래그할때
    	if (isMoving) { // '게이트 움직이기' 활성화 상태일때 저장해놓은 좌표 비교해서 움직이기
			for(int i=0;i<selectedList.size();i++) {
				int newX = e.getX() - click_x.get(i);
				int newY = e.getY() - click_y.get(i);
				selectedList.get(i).setLocation(newX, newY);				
			}
			removeLabel.setVisible(true);
			connection.repaint();
		}
    	else if (isDrawing != 0) { // '선 그리기' 활성화 상태일때 선그리는 connection 좌표 보내주기
			connection.setFinishPoint(1, e.getX(), e.getY());
			connection.repaint();
		}    		
    	else if (selecting) {
			rightDragFinish = new Point(e.getX(), e.getY());
			connection.setFinishPoint(2, e.getX(), e.getY());
			connection.repaint();
		}
    }
    
    public void mouseReleased(MouseEvent e) { // 마우스를 땠을때
    	if(e.getButton() == MouseEvent.BUTTON1) {
    		if(isDrawing != 0) { // '선 그리기' 활성화 상태일때 마우스 위치가 연결 할 수 있는 곳이라면 연결 / 아니라면 연결되있던거 연결 해제
    			Component comp = e.getComponent();
    			if (comp instanceof Container) {
    				Container container = (Container) comp;
    				Component source = container.findComponentAt(e.getPoint()).getParent();
    				if(source instanceof Input && isDrawing == 1) {
    					((Input) source).link((Output) selectedList.get(0));
    					((Output) selectedList.get(0)).link((Input) source);
    				}
    				else if(source instanceof Output && isDrawing == 2) {
    					((Output) source).link((Input) selectedList.get(0));
    					((Input) selectedList.get(0)).link((Output) source);
    				}
    				else {
    					if(selectedList.get(0) instanceof Input) {
    						((Input) selectedList.get(0)).link(null);
    					}
    				}
    			}
    			isDrawing = 0;
    			connection.setFinishPoint(0, 0, 0);
    		}
    		else if(isMoving) { // '게이트 움직이기' 활성화 상태일때 삭제하는 label 위에 마우스가 있으면 삭제
    			Point mousePoint = e.getPoint();
    			if(removeSize.contains(mousePoint)) {
    				for(Component select : selectedList) {
    					((Gate) select).clearLink();
    					layeredPane.remove(select);    					    					
    				}
    			}
    			isMoving = false;
    			removeLabel.setVisible(false);
    		}
    		for(Component select : selectedList) {
    			if(select instanceof Gate) {
    				((Gate) select).setSelcect(false);    				
    			}
    		}
    		selectedList.clear();
    		connection.repaint();
    	}
    	else if(e.getButton() == MouseEvent.BUTTON3) {
    		for(Component select : selectedList) {
    			if(select instanceof Gate) {
    				((Gate) select).setSelcect(false);    			
    			}
    		}
    		Rectangle rightDrag = new Rectangle(Math.min(rightDragStart.x, rightDragFinish.x), 
    				Math.min(rightDragStart.y, rightDragFinish.y),
    				Math.abs(rightDragFinish.x - rightDragStart.x),
    				Math.abs(rightDragFinish.y - rightDragStart.y));
    		selectedList = GateManager.getInstance().getGateInRange(rightDrag);
    		for(Component select : selectedList) {
    			((Gate) select).setSelcect(true);
    		}
    		connection.setFinishPoint(0, 0, 0);
    		connection.repaint();
    		selecting = false;
    	}
	}
}