package herobrine.services;

import herobrine.irc.Person;

public class Admin extends Service {
  public void setup() {
    if (!config.contains("owner")) warning("No owner set");
  }

  public void privateMessage(Person source, String message) {
    if (source.name().equals(config.get("owner"))) {
      if (message.equals("quit")) {
        api.disconnect();
        api.quit();
      }
    }
  }
}
