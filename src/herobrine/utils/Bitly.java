package herobrine.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class Bitly {
  private static final String API_URL = "http://api.bitly.com/v3/shorten";

  String user;
  String key;

  public Bitly(String user, String key) {
    this.key = key;
    this.user = user;
  }

  public String shorten(String url) throws MalformedURLException, UnsupportedEncodingException, IOException {
    QueryBuilder query = new QueryBuilder();
    query.add("format", "txt");
    query.add("longUrl", url);
    query.add("login", user);
    query.add("apiKey", key);
    URLConnection connection = new URL(API_URL + query).openConnection();
    return new BufferedReader(new InputStreamReader(connection.getInputStream())).readLine();
  }

  class QueryBuilder {
    private StringBuilder query;

    public QueryBuilder() {
      query = new StringBuilder();
    }

    public void add(String name, String value) throws UnsupportedEncodingException {
      if (query.length() > 1) query.append("&");
      else query.append("?");

      query.append(URLEncoder.encode(name, "UTF-8"));
      query.append('=');
      query.append(URLEncoder.encode(value, "UTF-8"));
    }

    public String getQuery() {
      return query.toString();
    }

    public String toString() {
      return getQuery();
    }

  }
}
