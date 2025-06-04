import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;

public class Pair<F, S> {
    private F first;
    private S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() { return first; }
    
    public S getSecond() { return second; }
    
    public void clear() {
    	((ArrayList<Integer>) first).clear();
    	((ArrayList<Integer>) second).clear();
    }
    
    public Point getPoint() {
    	return new Point(((ArrayList<Integer>) first).get(0), ((ArrayList<Integer>) second).get(0));
    }
    
    public Rectangle getRectangle() {
    	Point f = (Point) first;
    	Point s = (Point) second;
    	return new Rectangle(Math.min(f.x, s.x), Math.min(f.y, s.y), Math.abs(s.x - f.x), Math.abs(s.y - f.y));
    }
    
    public void setFirst(F first) { this.first = first; }
    
    public void setSecond(S second) { this.second = second; }
}