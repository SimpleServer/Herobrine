package herobrine.test;

import static org.junit.Assert.*;
import herobrine.irc.CommandHandler;

import org.junit.Before;
import org.junit.Test;

public class CommandHandlerTest {

  private CommandHandler handler;

  @Before
  public void setUp() {
    handler = new CommandHandler() {
      public void handle() {
        assertEquals(target(), "Simon");
      }
    };
  }

  @Test
  public void testTarget() {
    handler.handle(":Simon!bla@blubb PRIVMSG Globi :Hi there");
  }

}
