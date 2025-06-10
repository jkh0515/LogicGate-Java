public class ConnectCommand implements Command {
    private final GateManager gateManager;
    private final Input input;
    private final Output output;

    public ConnectCommand(GateManager gateManager, Input input, Output output) {
        this.gateManager = gateManager;
        this.input = input;
        this.output = output;
    }

    @Override
    public void execute() {
        input.link(output);
        output.link(input);
        //redraw wires
        gateManager.repaintConnections();
    }

    @Override
    public void undo() {
        input.link(null);
        //remove from the internal list:
        output.inputList.remove(input);
        //clean up the map just in case
        gateManager.getConnectMap().remove(input);
        //redraw wires
        gateManager.repaintConnections();
    }
}