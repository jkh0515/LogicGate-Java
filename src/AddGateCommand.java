import javax.swing.JLayeredPane;

public class AddGateCommand implements Command {
    private final GateManager gateManager;
    private final Gate gate;
    private final int x, y;

    public AddGateCommand(GateManager gateManager, Gate gate, int x, int y) {
        this.gateManager = gateManager;
        this.gate = gate;
        this.x = x; this.y = y;
    }

    @Override
    public void execute() {
        // Add back to the UI
        JLayeredPane layeredPane = gateManager.getLayeredPane();
        layeredPane.add(gate, JLayeredPane.PALETTE_LAYER);
        // Add to the model
        gateManager.addGate(gate);
        // Refresh
        layeredPane.revalidate();
        layeredPane.repaint();
    }

    @Override
    public void undo() {
        // Remove from UI
        JLayeredPane layeredPane = gateManager.getLayeredPane();
        layeredPane.remove(gate);
        // Remove from model
        gateManager.removeGate(gate);
        // Refresh
        layeredPane.revalidate();
        layeredPane.repaint();
    }
}
