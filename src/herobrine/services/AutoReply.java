package herobrine.services;

import static herobrine.utils.Messages.response;
import herobrine.irc.Person;
import herobrine.irc.Room;
import herobrine.utils.Messages.Response;

import java.util.HashMap;
import java.util.Map;

public class AutoReply extends Service {
  Map<String, Person> secretList1 = new HashMap<String, Person>();
  Map<String, Person> secretList2 = new HashMap<String, Person>();

  String lastMessageBy = "";
  String currentMessageBy = "";

  public void privateMessage(Person source, String message) {
    if (!source.name().equals(config.get("owner"))) {
      if (secretList1.containsKey(source.name()) || secretList1.containsValue(source)) {
        if (!secretList2.containsKey(source.name()) && !secretList2.containsValue(source)) {
          api.message(source, "You know that I am a bot... so stop talking to me!");
          secretList2.put(source.name(), source);
        }
      } else {
        if (message.equals("quit")) {
          api.message(source, response(Response.NOT_OWNER));
        } else {
          String msg = response(Response.PRIVATE_CHAT);
          api.message(source, msg);
          if (msg.contains("Luke, I am your father! No... I am just")) {
            secretList1.put(source.name(), source);
          }
        }
      }
    }
  }

  public void roomMessage(Person target, Room room, String message) {
    lastMessageBy = currentMessageBy;
    currentMessageBy = target.name();

    String msg = message.toLowerCase();
    String nick = config.get("nick").toLowerCase();

    if (msg.toLowerCase().contains(nick) || (lastMessageBy.equals(nick) &&
        (msg.contains("?") || msg.toLowerCase().contains("is ") ||
            msg.toLowerCase().contains("are ") || msg.toLowerCase().contains("why ")))) {
      if (!target.name().equals(config.get("owner "))) {
        if (secretList1.containsKey(target.name()) || secretList1.containsValue(target)) {
          if (secretList2.containsKey(target.name()) || secretList2.containsValue(target)) {
            api.message(room, response(Response.AFTER_TELLING));
            currentMessageBy = nick;
            secretList2.put(target.name(), target);
          } else {
            String response = "";
            if (msg.contains("@" + nick) || msg.contains(nick + ":")) {
              response = response(Response.ROOM_CHAT_TO, msg);
            } else response = response(Response.ROOM_CHAT_ABOUT, msg);
            if (!response.equals("")) {
              api.message(room, response);
              currentMessageBy = nick;
            }
          }
        } else {
          String response;
          if (msg.contains("@" + nick) || msg.contains(nick + ":")) {
            response = response(Response.ROOM_CHAT_TO, msg);
            if (response.equals("")) {
              response = response(Response.ROOM_CHAT_TO);
              currentMessageBy = nick;
            }
          } else {
            response = response(Response.ROOM_CHAT_ABOUT, msg);
            if (response.equals("")) {
              response = response(Response.ROOM_CHAT_ABOUT);
              currentMessageBy = nick;
            }
          }
          if (response.contains("Do you know Notch? Well...")) {
            secretList1.put(target.name(), target);
            api.message(target, response);
            api.message(room, response(Response.AT_TELLING));
            currentMessageBy = nick;
          } else {
            api.message(room, response);
            currentMessageBy = nick;
          }
        }
      }
    }
  }
}
