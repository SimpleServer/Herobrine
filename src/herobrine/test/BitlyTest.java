package herobrine.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import herobrine.Config;
import herobrine.utils.Bitly;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.junit.Test;

public class BitlyTest {
  private final static String TEST_URL = "http://www.google.com/";

  @Test
  public void testShorten() throws MalformedURLException, UnsupportedEncodingException, IOException {
    Bitly bitly = new Bitly(Config.config().get("bitlyUser"), Config.config().get("bitlyKey"));
    String url = bitly.shorten(TEST_URL);
    assertTrue(url != null);
    assertEquals(TEST_URL, getLocation(url));
  }

  private String getLocation(String url) throws MalformedURLException, IOException {
    URLConnection connection = new URL(url).openConnection();
    ((HttpURLConnection) connection).setInstanceFollowRedirects(false);
    return connection.getHeaderField("Location");
  }

}
