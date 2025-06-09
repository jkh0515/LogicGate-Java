import java.awt.Point;
import java.awt.Component;
import java.util.List;

public class MoveGateCommand implements Command {
    private final List<Component> gates;
    private final List<Point>   from, to;

    public MoveGateCommand(List<Component> gates,
                           List<Point>   from,
                           List<Point>   to) {
        this.gates = gates;
        this.from  = from;
        this.to    = to;
    }

    @Override
    public void execute() {
        for (int i = 0; i < gates.size(); i++) {
            gates.get(i).setLocation(to.get(i));
        }
    }

    @Override
    public void undo() {
        for (int i = 0; i < gates.size(); i++) {
            gates.get(i).setLocation(from.get(i));
        }
    }
}
