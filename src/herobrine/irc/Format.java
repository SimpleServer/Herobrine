package herobrine.irc;

public class Format {
  public static final String CTCP = ascii(1);
  public static final String BOLD = ascii(2);
  public static final String COLOR = ascii(3);
  public static final String WHITE = color('0');
  public static final String BLACK = color('1');
  public static final String RED = color('2');
  public static final String ORANGE = color('3');
  public static final String YELLOW = color('4');
  public static final String LIGHT_GREEN = color('5');
  public static final String GREEN = color('6');
  public static final String BLUE_GREEN = color('7');
  public static final String CYAN = color('8');
  public static final String LIGHT_BLUE = color('9');
  public static final String BLUE = color(':');
  public static final String PURPLE = color(';');
  public static final String MAGENTA = color('<');
  public static final String PURPLE_RED = color('=');
  public static final String GRAY = color('>');
  public static final String DARK_GRAY = color('?');
  public static final String DARK_RED = color('@');
  public static final String DARK_ORANGE = color('A');
  public static final String DARK_YELLOW = color('B');
  public static final String DARK_LIGHT_GREEN = color('C');
  public static final String DARK_GREEN = color('D');
  public static final String DARK_BLUE_GREEN = color('E');
  public static final String DARK_CYAN = color('F');
  public static final String DARK_LIGHT_BLUE = color('G');
  public static final String DARK_BLUE = color('H');
  public static final String DARK_PURPLE = color('I');
  public static final String DARK_MAGENTA = color('J');
  public static final String DARK_PURPLE_RED = color('K');

  private static String ascii(int i) {
    return new Character((char) i).toString();
  }

  private static String color(char color) {
    return COLOR + color;
  }
}
