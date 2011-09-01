package herobrine.services;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForumObserver extends Service {
  private static final String URL = "http://www.minecraftforum.net/topic/%s-/page__view__getlastpost";
  private static final Pattern POST_ID = Pattern.compile(".*pid__(\\d*)__st__(\\d*)(#.*)?");
  private static final int THREAD = 309373;

  private int lastPost = 0;

  public void ready() {
    api.schedule(new PostFinder());
  }

  protected void foundPost(int id, String url) {
    if (id != lastPost) {
      if (lastPost != 0) {
        url = api.shorten(url);
        api.message("New post: " + url);
        api.flush();
      }
      lastPost = id;
    }
  }

  private class PostFinder extends TimerTask {

    private String lastURL;
    private int lastID;

    @Override
    public void run() {
      try {
        getLatestPost(THREAD);
        foundPost(lastID, lastURL);
      } catch (MalformedURLException e) {
        warning("Forum URL is malformed");
      } catch (IOException e) {
        warning("Can't fetch forum");
      } catch (Exception e) {
        warning("Unknown error occurred while fetching forum");
      }
    }

    public void getLatestPost(int thread) throws Exception {
      lastURL = getLocation(String.format(URL, thread));
      lastURL = getLocation(lastURL);
      Matcher matcher = POST_ID.matcher(lastURL);
      if (!matcher.matches()) throw new Exception("Can't read URL");
      lastID = Integer.parseInt(matcher.group(1));
    }

    private String getLocation(String url) throws MalformedURLException, IOException {
      URLConnection connection = new URL(url).openConnection();
      ((HttpURLConnection) connection).setInstanceFollowRedirects(false);
      return connection.getHeaderField("Location");
    }
  }
}
