import javax.swing.JLayeredPane;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Dimension;
import java.awt.Color;

public class ZoomableLayeredPane extends JLayeredPane {
    private double scale = 1.0;

    public ZoomableLayeredPane() {
        // ensure the entire background is painted
        setOpaque(true);
        setBackground(Color.WHITE);
    }

    public void setScale(double scale) {
        this.scale = scale;
        revalidate();
        repaint();
    }

    public double getScale() {
        return scale;
    }

    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setColor(getBackground());
        g2.fillRect(0, 0, getWidth(), getHeight());

        g2.scale(scale, scale);super.paint(g2);
        g2.dispose();
    }

    public Dimension getPreferredSize() {
        Dimension d = super.getPreferredSize();
        return new Dimension(
            (int)(d.width * scale),
            (int)(d.height * scale)
        );
    }
} 
