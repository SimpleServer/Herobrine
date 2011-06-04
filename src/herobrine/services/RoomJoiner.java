package herobrine.services;

public class RoomJoiner extends Service {
  public void ready() {
    if (!config.contains("room")) warning("No room set");
    else api.join(config.get("room"));
  }
}
