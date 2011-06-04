package herobrine.irc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class CommandHandler {
  private static Pattern NICK_PATTERN = Pattern.compile(":(.*)!.*");

  private String[] parts;

  public abstract void handle();

  public void handle(String line) {
    this.parts = line.split(" ");
    handle();
  }

  protected String arg(int i) {
    if (i + 2 > parts.length) return null;
    else return parts[i + 2];
  }

  protected String args(int i) {
    StringBuilder args = new StringBuilder();
    for (int j = i + 2; j < parts.length; j++) {
      args.append(parts[j]);
      if (j + 1 < parts.length) args.append(" ");
    }
    String message = args.toString();
    if (message.startsWith(":")) message = message.substring(1);
    return message;
  }

  protected Person target() {
    Matcher nick = NICK_PATTERN.matcher(parts[0]);
    if (!nick.matches()) return null;
    return Person.person(nick.group(1));
  }
}
