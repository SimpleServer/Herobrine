package herobrine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
  private static final String FILEPATH = "config.txt";

  protected Properties options;
  private static Config config = new Config();

  public static Config config() {
    return config;
  }

  private Config() {
    load();
  }

  public boolean contains(String option) {
    String value = options.getProperty(option);
    return value != null && value.trim().length() > 0;
  }

  public String get(String key) {
    return options.getProperty(key);
  }

  public int getInt(String option) {
    String value = options.getProperty(option);
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      Logger.error("Asked for int value of " + option);
      return Integer.MIN_VALUE;
    }
  }

  public boolean getBoolean(String option) {
    return Boolean.parseBoolean(options.getProperty(option));
  }

  public void load() {
    options = new Properties();
    File file = new File(FILEPATH);

    try {
      InputStream stream = new FileInputStream(file);
      try {
        options.load(stream);
      } finally {
        stream.close();
      }
    } catch (FileNotFoundException e) {
      Logger.error("Config not found");
    } catch (IOException e) {
      Logger.error("Can't read config");
    }
  }
}
