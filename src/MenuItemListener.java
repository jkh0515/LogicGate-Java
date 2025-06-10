import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class MenuItemListener implements ActionListener {
	
	GateFactory gateFactory;
	MyMouseListener myMouseListener;
	String ext = "gate";
	
	MenuItemListener(GateFactory gateFactory, MyMouseListener myMouseListener) {
		this.gateFactory = gateFactory;
		this.myMouseListener = myMouseListener;
	}
	
	public void actionPerformed(ActionEvent e) {
		String str = e.getActionCommand();
		GateManager.getInstance().setGateMoving(false);
		
		// new undo/redo handlers —— 
	    if (str.equals("undo")) {
	        GateManager.getInstance().undo();
	        return;
	    }
	    if (str.equals("redo")) {
	        GateManager.getInstance().redo();
	        return;
	    }

	    //new ZoomIn/Out handlers 
	    if (str.equals("zoomIn")) {
	        GateManager.getInstance().zoomIn();
	        return;
	    }
	    if (str.equals("zoomOut")) {
	        GateManager.getInstance().zoomOut();
	        return;
	    }
	    
		if(str.equals("Clear All") ) {
			int result = JOptionPane.showConfirmDialog(
					null, 
					"모든 게이트들을 삭제하시겠습니까?\n(저장되지 않습니다).", 
					"경고", 
					JOptionPane.YES_NO_OPTION, 
					JOptionPane.WARNING_MESSAGE
					);
			if(result == JOptionPane.YES_OPTION) {
				GateManager.getInstance().clearAll();
			}
		}
		else if (str.equals("nameActivate") || str.equals("nameInactivate")) {
		    boolean on = str.equals("nameActivate");
		    GateManager gm = GateManager.getInstance();
		    gm.setNameActivate(on);
		    for (Gate g : gm.getGateList()) {g.gateLabel.setVisible(on);}
		    gm.getLayeredPane().repaint();
		}
		else if (str.equals("stateActivate") || str.equals("stateInactivate")) {
		    boolean on = str.equals("stateActivate");
		    GateManager gm = GateManager.getInstance();
		    gm.setStateActivate(on);

		   
		    for (Gate g : gm.getGateList()) {
		        g.resultLabel.setVisible(on);
		        for (JLabel inLbl : g.inputLabel) {
		            inLbl.setVisible(on);
		        }
		    }
		    gm.getLayeredPane().repaint();
		}
		else if(str.contains("Speed")) {
			Integer[] speedArray = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        	Integer speed = (Integer) JOptionPane.showInputDialog(
                    null,
                    "움직임의 빠르기를 정하세요. :", "숫자 선택",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    speedArray,
                    speedArray[3]
                );
                if (speed != null) {
                	GateManager.getInstance().setGateMoveSpeed(speed);
                }
		}
		else if(str.contains("Exit") ) {
			int result = JOptionPane.showConfirmDialog(
					null, 
					"종료하시겠습니까?\n(저장되지 않습니다).", 
					"종료", 
					JOptionPane.YES_NO_OPTION, 
					JOptionPane.WARNING_MESSAGE
					);
			if(result == JOptionPane.YES_OPTION) {
				System.exit(0);
			}
		}
		else if(str.equals("Open File")) {
			JFileChooser fileChooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("*.gate 파일", ext);
			fileChooser.setFileFilter(filter);
			fileChooser.setDialogTitle("파일 열기");
			int result = fileChooser.showOpenDialog(null);
			if (result == JFileChooser.APPROVE_OPTION) {
			    File selectedFile = fileChooser.getSelectedFile();
			    String fileName = selectedFile.getName();
			    if(fileName.substring(fileName.lastIndexOf(".") +1 ).equals(ext)) {
			    	String line;
			    	try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(selectedFile))){
			    		List<Gate> loadedGateList = (List<Gate>) ois.readObject();
			    		Map<Input, Output> loadedMap = ((Map<Input, Output>) ois.readObject());
			    		myMouseListener.setIsCreating();
			    		myMouseListener.click.clear();
			    		for(Gate gate : loadedGateList) {
			    			String createGateName = gate.gateLabel.getText().toLowerCase() + "Gate";
							Method method = GateFactory.class.getDeclaredMethod(createGateName, Gate.class, int.class, int.class);
			    			gate = (Gate) method.invoke(gateFactory, gate, gate.getX(), gate.getY());
			    			if(gate.gateLabel.getText().equals("Input")) {			    				
			    				gate.btn.setVisible(false);
			    			}
			    			myMouseListener.click.getFirst().add(gate.getX() - loadedGateList.get(0).getX() - Gate.widthSize / 2);
			    			myMouseListener.click.getSecond().add(gate.getY() - loadedGateList.get(0).getY() - Gate.heightSize / 2);
			    			gate.setSelcect(true);
			    			myMouseListener.selectedList.add((Component) gate);
			    		}
			    		GateManager.getInstance().getConnectMap().putAll(loadedMap);
			    	} catch (Exception ex) {
			    		JOptionPane.showMessageDialog(null, "파일 형식이 올바르지 않습니다!", "오류", JOptionPane.ERROR_MESSAGE);
					}  	
			    }
			    else {
			    	JOptionPane.showMessageDialog(null, ".gate 파일을 선택해주세요!", "경고", JOptionPane.WARNING_MESSAGE);
			    }
			}
		}
		else if(str.equals("Save File")) {
			JFileChooser fileChooser = new JFileChooser();
			FileNameExtensionFilter filter = new FileNameExtensionFilter("*.gate 파일", ext);
			fileChooser.setFileFilter(filter);
			fileChooser.setDialogTitle("파일 저장");
			int result = fileChooser.showSaveDialog(null);
			if (result == JFileChooser.APPROVE_OPTION) {
			    File file = fileChooser.getSelectedFile();
			    String fileName = file.getName();
			    if(!fileName.toLowerCase().endsWith("." + ext)) {
			    	file = new File(file.getParentFile(), fileName + "." + ext);
			    }
			    List<Gate> gateList = GateManager.getInstance().getGateList();
			    Map<Input, Output> connectMap = GateManager.getInstance().getConnectMap();
		        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
		            oos.writeObject(gateList); // 리스트 직렬화
		            oos.writeObject(connectMap);
		            JOptionPane.showMessageDialog(null, "저장 완료!");
		        } catch (IOException ex) {
		            ex.printStackTrace();
		            JOptionPane.showMessageDialog(null, "저장 실패!", "오류", JOptionPane.ERROR_MESSAGE);
		        }
			}
		}
		else {			
			myMouseListener.setIsCreating();
			try {
				String createGateName = str.toLowerCase() + "Gate";
				Method method = GateFactory.class.getDeclaredMethod(createGateName, Gate.class, int.class, int.class);
				Object createGate = method.invoke(gateFactory, new Gate(gateFactory.gateInOut.get(str)), 0, 0);
				((Gate) createGate).setSelcect(true);
				myMouseListener.click.clear();
				myMouseListener.selectedList.add((Component) createGate);
				myMouseListener.click.getFirst().add(-Gate.widthSize / 2);
				myMouseListener.click.getSecond().add(-Gate.heightSize /2);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

}
