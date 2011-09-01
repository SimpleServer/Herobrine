package herobrine.irc;

import static herobrine.Logger.error;
import static herobrine.Logger.info;
import static herobrine.Logger.warning;
import static herobrine.irc.Format.CTCP;
import static herobrine.irc.Room.room;
import herobrine.Config;
import herobrine.services.Service;
import herobrine.utils.Bitly;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;

public class API implements ConnectionHandler {

  private Connection connection;
  private Timer timer;
  private Bitly bitly;

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

    timer = new Timer();
    if (config.contains("bitlyUser") && config.contains("bitlyKey")) {
      bitly = new Bitly(config.get("bitlyUser"), config.get("bitlyKey"));
    } else {
      warning("No bit.ly credentials set");
    }

    info("Loading services");
    loadServices();

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

    info("Establishing connection");
    connection = new Connection(server, port, nick);
    connection.observe(this);

    flush();
  }

  public void listen(Service service) {
    services.add(service);
  }

  public String nick() {
    return nick;
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
      try {
        handlers.get(cmd).handle(line);
      } catch (Exception e) {
        error("Error parsing command '" + cmd + "'");
        if (Config.config().getBoolean("debug")) e.printStackTrace();
      }
    }

    flush();
  }

  public void ready() {
    for (Service service : services) {
      service.ready();
    }
  }

  public synchronized void cleanup() {
    timer.cancel();
    for (Service service : services) {
      service.cleanup();
    }
    notify();
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

  public void flush() {
    while (commandBuffer.size() > 0) {
      Command command = commandBuffer.poll();
      connection.send(command.command());
    }
  }

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

  public void schedule(TimerTask task) {
    timer.schedule(task, 100, Config.config().getInt("delay") * 1000);
  }

  public String shorten(String url) {
    try {
      return bitly.shorten(url);
    } catch (Exception e) {
      return url;
    }
  }

  public void quit() {
    if (Config.config().contains("quit")) send(-10, "QUIT", Config.config().get("quit"));
    else send(-10, "QUIT");
  }
}
