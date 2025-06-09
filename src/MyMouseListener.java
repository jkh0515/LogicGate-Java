import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;

public class MyMouseListener extends MouseAdapter { //main 들어갈 마우스 리스너
	private boolean isMoving = false;
	private boolean selecting = false;
	private boolean isCreating = false;
	private int isDrawing = 0;
	private java.util.List<java.awt.Point> moveStartPositions; //for redo/undo
	
	Pair<List<Integer>, List<Integer>> click;
	List<Component> selectedList;
	
	Rectangle removeSize;
	Pair<Point, Point> rightDrag = new Pair<>(new Point(0, 0), new Point(0, 0));
	
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
		click = new Pair<>(new ArrayList<Integer>(), new ArrayList<Integer>());
		removeSize = removeLabel.getBounds();
	}
	
	public void setIsCreating() {
		isCreating = true;
	}
	
	public void mousePressed(MouseEvent e) { // 마우스를 클릭했을때
    	double scale = GateManager.getInstance().getScale();
    	int lx    = (int)(e.getX() / scale);
    	int ly    = (int)(e.getY() / scale);

    	if(!isCreating) {
    		// dropped the comp/container
    		if(e.getButton() == MouseEvent.BUTTON1) {
    			Component source = layeredPane.findComponentAt(lx, ly);
    			if (source instanceof Gate) { // Gate 클릭했을때 클릭 좌표 기억해두기, isMoving 변환시켜서 '게이트 움직이기' 활성화
					if(selectedList.size() == 0) {
						selectedList.add(source);    
						((Gate) source).setSelcect(true);
					}
					
					//* for storing the previous location
					moveStartPositions = selectedList.stream()
					    .map(c -> new Point(c.getLocation()))  // copy each location
					    .collect(Collectors.toList());
					
					click.clear();
					for(Component select : selectedList) {
						click.getFirst().add(lx - select.getLocation().x);
						click.getSecond().add(ly - select.getLocation().y);    						
					}

			
					isMoving = true;

				}
				else { // input / output 클릭했을때 isDrawing 변환시켜서 '선 그리기' 활성화
					click.clear();
					source = source.getParent();
					click.getFirst().add(lx);
					click.getSecond().add(ly);
					connection.setStartPoint(0, click.getPoint());
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
    		
    		else if(e.getButton() == MouseEvent.BUTTON2) {
				Component source = layeredPane.findComponentAt(lx, ly);
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
				        	((Gate) source).setColor(
				        			  JColorChooser.showDialog(
				        			    source,
				        			    "Output Line 색상 선택",
				        			    Color.RED
				        			  )
				        			);
				        }
				        connection.repaint();
				}
    		}
    		else if(e.getButton() == MouseEvent.BUTTON3) {
    			selecting = true;
    			rightDrag.setFirst(new Point(lx, ly));
    			connection.setStartPoint(0, rightDrag.getFirst());
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
    		//
    		
    		moveStartPositions = new ArrayList<>();
    		for (Component c : selectedList) {
    		  moveStartPositions.add(c.getLocation());
    		}
    		//
    		selectedList.clear();
    		connection.repaint();
    	}
    }
   
    public void mouseMoved(MouseEvent e) {
      	double scale = GateManager.getInstance().getScale();
    	int lx    = (int)(e.getX() / scale);
    	int ly    = (int)(e.getY() / scale);
    	
    	if(isCreating) {
    		for(int i=0;i<selectedList.size();i++) {
				int newX = lx + click.getFirst().get(i);
				int newY = ly + click.getSecond().get(i);
				selectedList.get(i).setLocation(newX, newY);				
			}
    	}
    }
    
    public void mouseDragged(MouseEvent e) { // 마우스 드래그할때
    	double scale = GateManager.getInstance().getScale();
        int lx = (int)(e.getX() / scale);
        int ly = (int)(e.getY() / scale);

        if (isMoving) {  // '게이트 움직이기' 활성화 상태일때 저장해놓은 좌표 비교해서 움직이기
            for (int i = 0; i < selectedList.size(); i++) {
                Component g = selectedList.get(i);
                int offX = click.getFirst().get(i);
                int offY = click.getSecond().get(i);
                g.setLocation(lx - offX, ly - offY);
            }
            removeLabel.setVisible(true);
           
            connection.repaint();
            layeredPane.repaint();
        }
        
        else if (isDrawing != 0) { // '선 그리기' 활성화 상태일때 선그리는 connection 좌표 보내주기
            connection.setFinishPoint(1, new Point(lx, ly));
            connection.repaint();
        }
        
        else if (selecting) {
            rightDrag.setSecond(new Point(lx, ly));
            connection.setFinishPoint(2, new Point(lx, ly));
            connection.repaint();
        }
    }
	public void mouseReleased(MouseEvent e) { // 마우스를 땠을때

        double scale = GateManager.getInstance().getScale();
        int lx = (int)(e.getX() / scale);
        int ly = (int)(e.getY() / scale);


        if (e.getButton() == MouseEvent.BUTTON1) {

            if (isDrawing != 0) {// '선 그리기' 활성화 상태일때 마우스 위치가 연결 할 수 있는 곳이라면 연결 / 아니라면 연결되있던거 연결 해제
                // Find the component under the logical point
                Component raw = layeredPane.findComponentAt(lx, ly);
                Component source = raw;
                while (source != null 
                       && !(source instanceof Input) 
                       && !(source instanceof Output)) {
                    source = source.getParent();
                }

                GateManager gm = GateManager.getInstance();
                if (source instanceof Input && isDrawing == 1) {
                    Input in  = (Input) source;
                    Output out = (Output) selectedList.get(0);
                    gm.getHistory().doCommand(new ConnectCommand(gm, in, out));
                }
                else if (source instanceof Output && isDrawing == 2) {
                    Output out = (Output) source;
                    Input in  = (Input) selectedList.get(0);
                    gm.getHistory().doCommand(new ConnectCommand(gm, in, out));
                }
                else if (selectedList.get(0) instanceof Input) {
                    ((Input) selectedList.get(0)).link(null);
                }

                isDrawing = 0;
                connection.setFinishPoint(0, new Point(0, 0));
            }
            else if (isMoving) { // '게이트 움직이기' 활성화 상태일때 삭제하는 label 위에 마우스가 있으면 삭제
                Point logicalPoint = new Point(lx, ly);
                boolean deleted = false;
                if (removeSize.contains(logicalPoint)) {
                    for (Component select : selectedList) {
                        ((Gate) select).clearLink();
                        layeredPane.remove(select);
                        GateManager.getInstance().getGateList().remove(select);
                    }
                    deleted = true;
                }
                //*Copy new positions & call command
                if (!deleted && !selectedList.isEmpty()) {
     
                    List<Component> moved = new ArrayList<>(selectedList);

                    List<Point> newPositions = moved.stream()
                        .map(c -> new Point(c.getLocation()))
                        .collect(Collectors.toList());

                    MoveGateCommand cmd = new MoveGateCommand(
                        moved,
                        moveStartPositions,
                        newPositions
                    );
                    GateManager.getInstance().getHistory().doCommand(cmd);
                }  
                
                isMoving = false;
                removeLabel.setVisible(false);
            }


            for (Component select : selectedList) {
                if (select instanceof Gate) {
                    ((Gate) select).setSelcect(false);
                }
            }
            selectedList.clear();
            connection.repaint();
        }
        else if (e.getButton() == MouseEvent.BUTTON3) {
            for (Component select : selectedList) {
                if (select instanceof Gate) {
                    ((Gate) select).setSelcect(false);
                }
            }
            // get all gates in the dragged rectangle
            Rectangle rect = rightDrag.getRectangle();
            selectedList = GateManager.getInstance().getGateInRange(rect);
            for (Component select : selectedList) {
                ((Gate) select).setSelcect(true);
            }

            connection.setFinishPoint(0, new Point(0, 0));
            connection.repaint();
            selecting = false;
        }
    }
}