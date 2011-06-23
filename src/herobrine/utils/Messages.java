package herobrine.utils;

public class Messages {
  public enum Response {
    AFTER_TELLING, AT_TELLING, NOT_OWNER,
    PRIVATE_CHAT, ROOM_CHAT_ABOUT, ROOM_CHAT_TO
  }

  private AnalysedResponse[] indepthReplies =
    {
        new AnalysedResponse(AnalysedResponseTypes.AWESOME),
        new AnalysedResponse(AnalysedResponseTypes.BYE),
        new AnalysedResponse(AnalysedResponseTypes.HELLO),
        new AnalysedResponse(AnalysedResponseTypes.IGNORE),
        new AnalysedResponse(AnalysedResponseTypes.WHY)
    };

  private enum AnalysedResponseTypes {
    AWESOME(new String[] { "awesome", "great", "nice" },
            new String[] { "Thanks! :)", "You are nice.", "", "", "" }),
    BYE(new String[] { "cu", "see you", "bye", "goodbye", "good bye" },
        new String[] { "bye!", "bye", "cu", "cu", "see you!" }),
    HELLO(new String[] { "hi", "hello", "back" },
          new String[] { "Hello.", "Hello.", "Hi!", "Hi!", "Hi!" }),
    IGNORE(new String[] { "ignore", "ignoring", "ignored" },
           new String[] { "I'm not ignoring anyone. I am listening. Silently.", "Well. Maybe I am... Maybe not...", "Do you really care?", "", "" }),
    STUPID(new String[] { "stupid", "bad", "dumb", "suck", "worst" },
           new String[] { "NO!", "You are just stupid...", "", "", "" }),
    WHY(new String[] { "why" },
        new String[] { "No reason...", "Don't know.", "", "", "" });

    private final String[] triggers;
    private final String[] replies;

    AnalysedResponseTypes(String[] triggers, String[] replies) {
      this.triggers = triggers;
      this.replies = replies;
    }

    public String[] triggers() {
      return triggers;
    }

    public String[] replies() {
      return replies;
    }
  }

  private class AnalysedResponse {
    private final String[] replies;
    private final String[] triggers;

    AnalysedResponse(AnalysedResponseTypes type) {
      this.replies = type.replies();
      this.triggers = type.triggers();
    }

    public boolean isTriggered(String msg) {
      for (String trigger : triggers) {
        if (msg.toLowerCase().contains(" " + trigger.toLowerCase()) ||
            msg.toLowerCase().contains(trigger.toLowerCase() + " ")) {
          return true;
        }
      }
      return false;
    }

    public String getMessage() {
      switch ((int) Math.round(Math.random() * replies.length)) {
        case 1:
          return replies[0];
        case 2:
          return replies[1];
        case 3:
          return replies[2];
        case 4:
          return replies[3];
        default:
          return replies[4];
      }
    }
  }

  private String getResponse(Response type) {
    switch (type) {
      case AFTER_TELLING:
        return responseAfterTelling();

      case AT_TELLING:
        return responseRoomAtTelling();

      case NOT_OWNER:
        return responseNotOwner();

      case PRIVATE_CHAT:
        return responsePrivateChat();

      case ROOM_CHAT_ABOUT:
        return responseRoomChatAbout();

      case ROOM_CHAT_TO:
        return responseRoomChatTo();

      default:
        return "";
    }
  }

  private String getResponse(Response type, String message) {
    switch (type) {
      case AFTER_TELLING:
      case AT_TELLING:
      case NOT_OWNER:
      case PRIVATE_CHAT:
      default:
        return getResponse(type);

      case ROOM_CHAT_ABOUT:
      case ROOM_CHAT_TO:
        for (AnalysedResponse response : indepthReplies) {
          if (response.isTriggered(message)) {
            return response.getMessage();
          }
        }
        return "";
    }
  }

  private String responseAfterTelling() {
    switch ((int) Math.round(Math.random() * 5)) {
      case 1:
        return "Please stop talking...";
      case 2:
      case 3:
        return "I will now ignore you!";
      default:
        return "You already know my identity!";
    }
  }

  private String responseNotOwner() {
    switch ((int) Math.round(Math.random() * 5)) {
      case 1:
        return "I will not obey you!";
      case 2:
        return "Please stop.";
      case 3:
        return "I have no interest in listening to you!";
      default:
        return "You cannot command me to quit!";
    }
  }

  private String responsePrivateChat() {
    switch ((int) Math.round(Math.random() * 5)) {
      case 1:
        return "No... He's not my brother! Oh... nevermind!";
      case 2:
      case 3:
        return "Pleas stop talking to me...";
      case 4:
        return "Why did you really contact me?";
      default:
        return "Luke, I am your father! No... I am just Notch's brother... So stop talking to me!";
    }
  }

  private String responseRoomAtTelling() {
    switch ((int) Math.round(Math.random() * 5)) {
      case 1:
      case 2:
      case 3:
        return "Please stop talking to me now...";
      default:
        return "I told you my secret! Please leave me alone now...";
    }
  }

  private String responseRoomChatAbout() {
    switch ((int) Math.round(Math.random() * 10)) {
      case 0:
        return "Do you know Notch? Well... He is really my brother... So please leave me alone!";
      case 1:
      case 2:
      case 3:
        return "I don't trust you... that much.";
      case 4:
        return "No! I'm not his brother... just... nevermind!";
      case 5:
      case 6:
      case 7:
        return "I don't like when you talk like that.";
      case 8:
        return "Well... I have changed... but noone believes me!";
      default:
        return "Please do not talk about my past... I'm not the same anymore!";
    }
  }

  private String responseRoomChatTo() {
    switch ((int) Math.round(Math.random() * 10)) {
      case 1:
        return "Notch is not my brother! well, kinda... no!";
      case 2:
        return "I like turtles! Do you like turtles?";
      case 3:
      case 4:
        return "I have heard that you cannot be trusted...";
      case 5:
        return "Did you need something from me?";
      case 6:
      default:
        return "Do you know Notch? Well... I am his brother! Please leave me alone now.";
    }
  }

  private static class MessagesHolder {
    public static final Messages INSTANCE = new Messages();
  }

  public static String response(Response type) {
    return MessagesHolder.INSTANCE.getResponse(type);
  }

  public static String response(Response type, String message) {
    return MessagesHolder.INSTANCE.getResponse(type, message);
  }
}
