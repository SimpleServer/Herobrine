package herobrine.services;

import java.io.IOException;
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
  private static final Pattern POST_ID = Pattern.compile(".*pid__(\\d*)__st.*");
  private static final int THREAD = 309373;
  private static final int DELAY = 30 * 1000;

  private Timer timer;
  private int lastPost = 0;

  public void ready() {
    timer = new Timer();
    timer.schedule(new PostFinder(), 0, DELAY);
  }

  public void cleanup() {
    timer.cancel();
  }

  protected void foundPost(int id) {
    if (id != lastPost) {
      if (lastPost != 0) {
        api.message("New post: http://tinyurl.com/64t2owd");
      }
      lastPost = id;
    }
  }

  private class PostFinder extends TimerTask {

    @Override
    public void run() {
      try {
        foundPost(getLatestPost(THREAD));
      } catch (MalformedURLException e) {
        error("Forum URL is malformed");
      } catch (IOException e) {
        warning("Can't fetch forum");
      } catch (Exception e) {
        error("Unknown error occurred while fetching forum");
      }
    }

    public int getLatestPost(int thread) throws Exception {
      Matcher matcher = POST_ID.matcher(getLocation(String.format(URL, thread)));
      if (!matcher.matches()) throw new Exception();
      return Integer.parseInt(matcher.group(1));
    }

    private String getLocation(String url) throws MalformedURLException, IOException {
      URLConnection connection = new URL(url).openConnection();
      ((HttpURLConnection) connection).setInstanceFollowRedirects(true);
      return connection.getHeaderField("X-URL");
    }
  }
}
