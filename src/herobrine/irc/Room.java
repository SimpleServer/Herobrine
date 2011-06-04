package herobrine.irc;

import java.util.HashMap;

public class Room {
  private static HashMap<String, Room> rooms = new HashMap<String, Room>();

  public static Room room(String name) {
    if (!rooms.containsKey(name)) rooms.put(name, new Room(name));
    return rooms.get(name);
  }

  private String name;

  private Room(String name) {
    this.name = name;
  }

  public String name() {
    return name;
  }

  public void changeName(String name) {
    rooms.remove(this.name);
    this.name = name;
    rooms.put(name, this);
  }
}
