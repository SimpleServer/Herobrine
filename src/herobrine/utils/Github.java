package herobrine.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;

public class Github {
  private static final String URL = "https://api.github.com";

  public static JSONArray getIssues(String user, String repo) throws Exception {
    return getJSONArray(user, repo, "issues");
  }

  public static JSONArray getCommits(String user, String repo, String branch) throws Exception {
    return getJSONArray(user, repo, "commits?sha=" + branch);
  }

  public static JSONArray getBranches(String user, String repo) throws Exception {
    return getJSONArray(user, repo, "branches");
  }

  private static JSONArray getJSONArray(String user, String repo, String request) throws Exception {
    try {
      return getRemoteJSONArray(String.format("%s/%s/%s/%s/%s", URL, "repos", user, repo, request));
    } catch (MalformedURLException e) {
      throw new Exception("Github URL is malformed");
    } catch (IOException e) {
      throw new Exception("Error while using github API");
    } catch (JSONException e) {
      throw new Exception("Github returned invalid JSON");
    }
  }

  private static JSONArray getRemoteJSONArray(String url) throws JSONException, IOException {
    return new JSONArray(getRemoteContent(url));
  }

  private static String getRemoteContent(String url) throws IOException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(new URL(url).openStream()));

    StringBuilder content = new StringBuilder();

    while (reader.ready()) {
      content.append(reader.readLine());
    }

    return content.toString();
  }
}
