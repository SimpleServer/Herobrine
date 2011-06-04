package herobrine.services;

import herobrine.irc.API;
import herobrine.irc.Person;
import herobrine.irc.Room;
import herobrine.Config;
import herobrine.Logger;

public class Service {

  protected API api;
  protected Config config;

  public void setAPI(API api) {
    this.api = api;
    config = Config.config();
    setup();
  }

  public void setup() {
  }

  public void ready() {
  }

  public void cleanup() {
  }

  public void privateMessage(Person target, String message) {
  }

  public void roomMessage(Person target, Room room, String message) {
  }

  public void ctcpRequest(Person target, String message) {
  }

  public void ping(String source) {
  }

  protected void info(String message) {
    Logger.info(message);
  }

  protected void error(String message) {
    Logger.error(message);
  }

  protected void warning(String message) {
    Logger.warning(message);
  }

  protected void log(String message) {
    Logger.log(message);
  }
}
