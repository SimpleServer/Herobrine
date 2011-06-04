package herobrine.services;

import herobrine.utils.Bitly;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForumObserver extends Service {
  private static final String URL = "http://www.minecraftforum.net/topic/%s-/page__view__getlastpost";
  private static final String DIRECT_URL = "http://www.minecraftforum.net%s#entry%s";
  private static final Pattern POST_ID = Pattern.compile(".*pid__(\\d*)__st__(\\d*)");
  private static final int THREAD = 309373;
  private static final int DELAY = 30 * 1000;

  private Timer timer;
  private int lastPost = 0;
  private Bitly bitly;

  public void setup() {
    if (config.contains("bitlyUser") && config.contains("bitlyKey")) {
      bitly = new Bitly(config.get("bitlyUser"), config.get("bitlyKey"));
    } else {
      warning("No bit.ly credentials set");
    }
  }

  public void ready() {
    timer = new Timer();
    timer.schedule(new PostFinder(), 10, DELAY);
  }

  public void cleanup() {
    timer.cancel();
  }

  protected void foundPost(int id, String url) {
    if (id != lastPost) {
      if (lastPost != 0) {
        url = String.format(DIRECT_URL, url, id);
        if (bitly != null) {
          try {
            String shortURL = bitly.shorten(url);
            url = shortURL;
          } catch (MalformedURLException e) {
            error("bit.ly API URL is malformed");
          } catch (UnsupportedEncodingException e) {
            error("Can't encode bit.ly query");
          } catch (IOException e) {
            warning("Can't shorten URL");
          }
        }
        api.message("New post: " + url);
        api.flush();
      }
      lastPost = id;
    }
  }

  private class PostFinder extends TimerTask {

    private String lastURL;
    private int lastID;
    private int lastLocalID;

    @Override
    public void run() {
      try {
        getLatestPost(THREAD);
        foundPost(lastID, lastURL);
      } catch (MalformedURLException e) {
        error("Forum URL is malformed");
      } catch (IOException e) {
        warning("Can't fetch forum");
      } catch (Exception e) {
        error("Unknown error occurred while fetching forum");
      }
    }

    public void getLatestPost(int thread) throws Exception {
      lastURL = getLocation(String.format(URL, thread));
      Matcher matcher = POST_ID.matcher(lastURL);
      if (!matcher.matches()) throw new Exception();
      lastID = Integer.parseInt(matcher.group(1));
      lastLocalID = Integer.parseInt(matcher.group(2));
    }

    private String getLocation(String url) throws MalformedURLException, IOException {
      URLConnection connection = new URL(url).openConnection();
      ((HttpURLConnection) connection).setInstanceFollowRedirects(true);
      return connection.getHeaderField("X-URL");
    }
  }
}
