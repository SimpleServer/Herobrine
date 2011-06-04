package herobrine.services;

import herobrine.irc.Person;

import java.util.Date;

public class CTCP extends Service {
  private static final String VERSION = "Qualoo IRC Bot";

  public void ctcpRequest(Person target, String message) {
    if (message.equals("VERSION")) api.CTCPReply(target, "VERSION", VERSION);
    else if (message.equals("TIME")) api.CTCPReply(target, "DATE", new Date().toString());
    else if (message.startsWith("PING")) api.CTCPReply(target, message);
    else if (message.equals("LAG")) return;
    else info("Unknown CTCP request: " + message);
  }
}
