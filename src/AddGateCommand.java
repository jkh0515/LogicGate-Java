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
        // Add back to the UI
        JLayeredPane lp = gm.getLayeredPane();
        lp.add(gate, JLayeredPane.PALETTE_LAYER);
        // Add to the model
        gm.addGate(gate);
        // Refresh
        lp.revalidate();
        lp.repaint();
    }

    @Override
    public void undo() {
        // Remove from UI
        JLayeredPane lp = gm.getLayeredPane();
        lp.remove(gate);
        // Remove from model
        gm.removeGate(gate);
        // Refresh
        lp.revalidate();
        lp.repaint();
    }
}
