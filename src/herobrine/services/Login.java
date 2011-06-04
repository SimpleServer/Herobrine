package herobrine.services;

public class Login extends Service {
  public void setup() {
    api.send(20, "NICK", api.nick());
    api.send(10, "USER", api.nick(), "0", "*", api.nick());
  }

  public void ready() {
    if (config.contains("password")) api.authenticate(config.get("password"));
    else info("No password set");
  }
}
