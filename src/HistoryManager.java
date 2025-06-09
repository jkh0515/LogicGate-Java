import java.util.ArrayDeque;
import java.util.Deque;

public class HistoryManager {
  private final Deque<Command> undoStack = new ArrayDeque<>();
  private final Deque<Command> redoStack = new ArrayDeque<>();

  public void doCommand(Command cmd) {
    cmd.execute();
    undoStack.push(cmd);
    redoStack.clear();
  }
  public void undo() {
    if (!undoStack.isEmpty()) {
      Command cmd = undoStack.pop();
      cmd.undo();
      redoStack.push(cmd);
    }
  }
  public void redo() {
    if (!redoStack.isEmpty()) {
      Command cmd = redoStack.pop();
      cmd.execute();
      undoStack.push(cmd);
    }
  }
  public boolean canUndo() { return !undoStack.isEmpty(); }
  public boolean canRedo() { return !redoStack.isEmpty(); }
}