public class ConnectCommand implements Command {
    private final GateManager gm;
    private final Input input;
    private final Output output;

    public ConnectCommand(GateManager gm, Input input, Output output) {
        this.gm = gm;
        this.input = input;
        this.output = output;
    }

    @Override
    public void execute() {
        input.link(output);
        output.link(input);
        //redraw wires
        gm.repaintConnections();
    }

    @Override
    public void undo() {
        input.link(null);
        //remove from the internal list:
        output.inputList.remove(input);
        //clean up the map just in case
        gm.getConnectMap().remove(input);
        //redraw wires
        gm.repaintConnections();
    }
}