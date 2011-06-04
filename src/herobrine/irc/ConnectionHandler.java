package herobrine.irc;

public interface ConnectionHandler {
  public void handleLine(String line);

  public void ready();

  public void cleanup();
}
