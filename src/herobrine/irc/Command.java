package herobrine.irc;

public class Command implements Comparable<Command> {

  private int priority;
  private String command;

  public Command(String command) {
    this(0, command);
  }

  public Command(int priority, String command) {
    this.priority = priority;
    this.command = command;
  }

  @Override
  public int compareTo(Command other) {
    if (other.priority() > priority) return 1;
    if (other.priority() < priority) return -1;
    else return 0;
  }

  public int priority() {
    return priority;
  }

  public String command() {
    return command;
  }

}
