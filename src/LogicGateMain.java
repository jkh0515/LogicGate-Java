import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;


public class LogicGateMain {
	public static final int frameWidth = 1200;
	public static final int frameHeight = 800;
	//
	static double zoomSize = 1.0;
	static List<Gate> gateList;
	//
	public static void main(String[] args) {

	    JFrame frame = new JFrame("Logic Gate"); // 메인 프레임 생성
	    frame.setSize(frameWidth, frameHeight);
	    frame.setResizable(false);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	    //JLayeredPane layeredPane = new JLayeredPane(); // 레이어 프레임 생성 OG
	    
	    ZoomableLayeredPane layeredPane = new ZoomableLayeredPane(); // //
	    
	    layeredPane.setLayout(null);
	    layeredPane.setPreferredSize(new Dimension(frameWidth, frameHeight));
	    layeredPane.setFocusable(false);
	    
	    Connection connection = new Connection(); // 선 그리기 클래스 생성
	    connection.setBounds(0, 0, frameWidth, frameHeight);
	    layeredPane.add(connection, JLayeredPane.DEFAULT_LAYER);
	    
	    GateFactory gateFactory = new GateFactory(layeredPane, connection);
	    GateManager.getInstance().init(layeredPane, connection);
	    
	    JLabel removeLabel = gateFactory.removeLabel();

	    MyMouseListener myMouseListener = new MyMouseListener(layeredPane, connection, removeLabel, gateFactory);
	    layeredPane.addMouseListener(myMouseListener);
	    layeredPane.addMouseMotionListener(myMouseListener);
	    
	    layeredPane.setFocusable(true);
	    layeredPane.requestFocusInWindow();
	    layeredPane.addKeyListener(new MainKeyListener(layeredPane));
	    
	    Menu menu = new Menu(gateFactory, myMouseListener);
	    frame.setJMenuBar(menu); // //

	    //layeredPane.add(menu, JLayeredPane.DRAG_LAYER); OG
	    
	    frame.setContentPane(layeredPane);
	    frame.pack();
	    frame.setVisible(true);
	    
	    JComponent glass = (JComponent) frame.getGlassPane();
        glass.setLayout(null);
        glass.add(removeLabel);
        glass.setVisible(true);

        // position it at bottom‐center, 50px up from the pane
        Runnable position = () -> {
            int w = removeLabel.getPreferredSize().width;
            int h = removeLabel.getPreferredSize().height;
            int pw = layeredPane.getWidth();
            int ph = layeredPane.getHeight();
            removeLabel.setBounds(
                (pw - w)/2,
                ph - h - 50,
                w, h
            );
        };
        // for initial placement
        position.run();
        // to keep it in place if the window ever resizes
        frame.addComponentListener(new ComponentAdapter() {
            @Override public void componentResized(ComponentEvent e) {
                position.run();
            }
        });
	}

}
