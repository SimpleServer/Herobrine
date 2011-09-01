package herobrine.services;

import herobrine.utils.Github;

import java.util.HashMap;
import java.util.Map;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GitObserver extends Service {
  private static final String ORG = "SimpleServer";
  private static final String REPO = "SimpleServer";
  private static final String URL = "https://github.com/" + ORG + "/" + REPO;

  private Map<String, String> heads = new HashMap<String, String>();
  private int lastIssue;
  private boolean firstRun = true;

  public void ready() {
    api.schedule(new TimerTask() {
      public void run() {
        if (checkForCommits() && checkForIssues()) {
          firstRun = false;
        }
        api.flush();
      }
    });
  }

  private boolean checkForIssues() {
    JSONArray issues;
    try {
      issues = Github.getIssues(ORG, REPO);
    } catch (Exception e) {
      warning(e.getMessage());
      return false;
    }

    try {
      if (!firstRun) {
        for (int i = 0; i < issues.length(); i++) {
          JSONObject issue = issues.getJSONObject(i);
          if (lastIssue >= issue.getInt("number")) break;
          else {
            String url = api.shorten(URL + "/issues/" + issue.getInt("number"));
            String title = issue.getString("title");
            api.message("New issue: " + title + " - " + url);
          }
        }
      }
      lastIssue = issues.getJSONObject(0).getInt("number");
    } catch (JSONException e) {
      warning("Github returned invalid JSON");
      return false;
    }

    return true;
  }

  private boolean checkForCommits() {
    JSONArray branches;
    try {
      branches = Github.getBranches(ORG, REPO);
    } catch (Exception e) {
      warning(e.getMessage());
      return false;
    }

    try {
      for (int i = 0; i < branches.length(); i++) {
        JSONObject branch = (JSONObject) branches.get(i);
        String name = branch.getString("name");
        String sha = branch.getJSONObject("commit").getString("sha");

        if (!heads.containsKey(name)) {
          if (!firstRun) {
            String url = api.shorten(URL + "/tree/" + name);
            api.message("New branch \2" + name + "\2 - " + url);
          }
        } else {
          if (!firstRun && !heads.get(name).equals(sha)) {
            compareCommits(heads.get(name), sha, name);
          }
        }
        heads.put(name, sha);
      }
    } catch (JSONException e) {
      warning("Github returned invalid JSON");
      return false;
    }

    return true;
  }

  private void compareCommits(String sha1, String sha2, String branch) throws JSONException {
    JSONArray commits;
    try {
      commits = Github.getCommits(ORG, REPO, branch);
    } catch (Exception e) {
      warning(e.getMessage());
      return;
    }
    int i;
    for (i = 0; i < commits.length(); i++) {
      if (commits.getJSONObject(i).getString("sha").equals(sha2)) {
        JSONObject commit = commits.getJSONObject(i);
        String message = commit.getJSONObject("commit").getString("message").split("\n")[0];
        if (commit.getJSONArray("parents").getJSONObject(0).getString("sha").equals(sha1)) {
          String url = api.shorten(URL + "/commit/" + sha2);
          api.message(String.format("New commit on branch \2%s\2: %s - %s", branch, message, url));
          return;
        } else {
          String url = api.shorten(URL + "/compare/" + sha1 + "..." + sha2);
          api.message(String.format("New commits on branch \2%s\2: %s - %s", branch, message, url));
          return;
        }
      }
    }
    warning("Didn't find commit " + sha1);
  }

}
