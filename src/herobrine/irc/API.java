package herobrine.irc;

import herobrine.Config;
import herobrine.services.Service;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.util.*;

import static herobrine.irc.Format.*;
import static herobrine.irc.Person.person;
import static herobrine.irc.Room.room;
import static herobrine.Logger.*;

public class API implements ConnectionHandler {

  private Connection connection;

  private HashMap<String, CommandHandler> handlers = new HashMap<String, CommandHandler>();
  private List<Service> services = new ArrayList<Service>();

  private ArrayList<Room> rooms = new ArrayList<Room>();
  private PriorityQueue<Command> commandBuffer = new PriorityQueue<Command>();

  private String nick;

  public API() {
    Config config = Config.config();
    String server;
    int port;

    if (!config.contains("server")) {
      error("No server set");
      return;
    } else {
      server = config.get("server");
    }

    if (config.contains("nick")) {
      nick = config.get("nick");
    } else {
      warning("No nick set");
      nick = "HerobrineBot";
    }

    if (config.contains("port")) {
      port = config.getInt("port");
    } else {
      warning("No port set");
      port = 6667;
    }

    info("Loading services");
    loadServices();

    info("Establishing connection");
    connection = new Connection(server, port, nick);
    connection.observe(this);

    flushBuffer();

    handlers.put("PRIVMSG", new CommandHandler() {
      public void handle() {
        String message = args(1);
        if (arg(0).equals(nick())) {
          if (message.charAt(0) == 0x01) {
            message = message.substring(1, message.length() - 1);
            for (Service service : services) {
              service.ctcpRequest(target(), message);
            }
          } else {
            for (Service service : services) {
              service.privateMessage(target(), message);
            }
          }
        } else {
          for (Service service : services) {
            service.roomMessage(target(), room(arg(0)), message);
          }
        }
      }
    });

    handlers.put("PING", new CommandHandler() {
      public void handle() {
        for (Service service : services) {
          service.ping(arg(0));
        }
      }
    });
  }

  public void listen(Service service) {
    services.add(service);
  }

  public String nick() {
    return nick;
  }

  private void flushBuffer() {
    while (commandBuffer.size() > 0) {
      Command command = commandBuffer.poll();
      connection.send(command.command());
    }
  }

  private void loadServices() {
    Reflections r = new Reflections("herobrine", new SubTypesScanner());
    Set<Class<? extends Service>> services = r.getSubTypesOf(Service.class);

    for (Class<? extends Service> service : services) {
      if (Modifier.isAbstract(service.getModifiers())) {
        continue;
      }

      Service instance;

      try {
        instance = service.getConstructor().newInstance(new Object[] {});
        instance.setAPI(this);
        listen(instance);
        info("Loaded " + service.getSimpleName());
      } catch (Exception e) {
        e.printStackTrace();
        warning("Skipping service " + service.getName());
        continue;
      }
    }
  }

  @Override
  public synchronized void handleLine(String line) {
    if (!line.startsWith(":")) line = ": " + line;
    String[] parts = line.split(" ");
    String cmd = parts[1];
    if (handlers.containsKey(cmd)) {
      handlers.get(cmd).handle(line);
    }

    flushBuffer();
  }

  public void ready() {
    for (Service service : services) {
      service.ready();
    }
  }

  public void cleanup() {
    for (Service service : services) {
      service.cleanup();
    }
  }

  public static String join(String[] parts) {
    if (parts.length == 0) return "";
    StringBuilder line = new StringBuilder();
    for (String word : parts) {
      line.append(word);
      line.append(" ");
    }
    line.deleteCharAt(line.length() - 1);
    return line.toString();
  }

  /* API Actions */

  public void authenticate(String password) {
    send("NICKSERV IDENTIFY", password);
  }

  public void join(String room) {
    send("JOIN", room);
    rooms.add(room(room));
  }

  public void message(String message) {
    for (Room room : rooms) {
      message(room, message);
    }
  }

  public void message(Person target, String message) {
    send("PRIVMSG", target.name(), ":" + message);
  }

  public void message(Room target, String message) {
    send("PRIVMSG", target.name(), ":" + message);
  }

  public void CTCPReply(Person target, String... arguments) {
    send(String.format("NOTICE %s :%s%s%s", target, CTCP, join(arguments), CTCP));
  }

  public void CTCPRequest(Person target, String message) {
    send(String.format("PRIVMSG %s :%s%s%s", target, CTCP, message, CTCP));
  }

  public void send(String... command) {
    send(new Command(join(command)));
  }

  public void send(int priority, String... command) {
    send(new Command(priority, join(command)));
  }

  public void send(Command command) {
    commandBuffer.add(command);
  }

  public void disconnect() {
    connection.disconnect();
  }

}