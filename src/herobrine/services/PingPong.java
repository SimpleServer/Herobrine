package herobrine.services;

public class PingPong extends Service {
  public void ping(String source) {
    api.send("PONG", source);
  }
}
