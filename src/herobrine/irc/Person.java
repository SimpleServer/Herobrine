package herobrine.irc;

import java.util.HashMap;

public class Person {
  private static HashMap<String, Person> persons = new HashMap<String, Person>();

  public static Person person(String name) {
    if (!persons.containsKey(name)) persons.put(name, new Person(name));
    return persons.get(name);
  }

  private String name;

  private Person(String name) {
    this.name = name;
  }

  public String name() {
    return name;
  }

  public void changeName(String name) {
    persons.remove(this.name);
    this.name = name;
    persons.put(name, this);
  }

  public String toString() {
    return name;
  }
}
