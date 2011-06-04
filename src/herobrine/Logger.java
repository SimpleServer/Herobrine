package herobrine;

public class Logger {
  public static void log(String message) {
    System.out.println(message);
  }

  public static void info(String message) {
    log("[INFO] " + message);
  }

  public static void warning(String message) {
    log("[WARNING] " + message);
  }

  public static void error(String message) {
    log("[ERROR] " + message);
  }
}
