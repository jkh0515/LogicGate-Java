import javax.swing.JLayeredPane;

public class AddGateCommand implements Command {
    private final GateManager gm;
    private final Gate gate;
    private final int x, y;

    public AddGateCommand(GateManager gm, Gate gate, int x, int y) {
        this.gm = gm;
        this.gate = gate;
        this.x = x; this.y = y;
    }

    @Override
    public void execute() {
        // 1) Add back to the UI
        JLayeredPane lp = gm.getLayeredPane();
        lp.add(gate, JLayeredPane.PALETTE_LAYER);
        // 2) Add to the model
        gm.addGate(gate);
        // 3) Refresh
        lp.revalidate();
        lp.repaint();
    }

    @Override
    public void undo() {
        // 1) Remove from UI
        JLayeredPane lp = gm.getLayeredPane();
        lp.remove(gate);
        // 2) Remove from model
        gm.removeGate(gate);
        // 3) Refresh
        lp.revalidate();
        lp.repaint();
    }
}
