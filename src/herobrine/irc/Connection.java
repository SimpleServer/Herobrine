package herobrine.irc;

import herobrine.Config;
import herobrine.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;

public class Connection {
  private ArrayList<ConnectionHandler> observers = new ArrayList<ConnectionHandler>();

  private Socket socket;
  private BufferedReader input;
  private BufferedWriter output;
  private IRCStream inputStream;
  private boolean run = true;
  private boolean ready = false;
  private boolean debug;

  public Connection(String server, int port, String nick) {
    debug = Config.config().getBoolean("debug");

    try {
      socket = new Socket(server, port);
      input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      output = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    } catch (Exception e) {
      logSevere("Couldn't establish connection");
    }

    inputStream = new IRCStream(input);
    inputStream.start();
  }

  public void observe(ConnectionHandler observer) {
    observers.add(observer);
  }

  public void send(String cmd) {
    if (debug) log("> " + cmd);
    try {
      output.write(cmd + "\n");
      output.flush();
    } catch (IOException e) {
      logSevere("Error sending message");
    }
  }

  public boolean conected() {
    return run;
  }

  public boolean ready() {
    return ready;
  }

  private void log(String msg) {
    if (msg.endsWith("\n")) msg = msg.substring(0, msg.length() - 1);
    Logger.log(msg);
  }

  private void logSevere(String msg) {
    disconnect();
    log("[SEVERE] " + msg);
  }

  private void handleLine(String line) {
    if (debug) log("< " + line);

    for (ConnectionHandler observer : observers) {
      observer.handleLine(line);
    }

    if (!ready && line.startsWith("PING")) {
      ready = true;
      for (ConnectionHandler observer : observers) {
        observer.ready();
      }
    }
  }

  public void disconnect() {
    run = false;
    Logger.info("Disconnected");
    for (ConnectionHandler observer : observers) {
      observer.cleanup();
    }
  }

  private class IRCStream extends Thread {
    private BufferedReader input;

    protected IRCStream(BufferedReader input) {
      this.input = input;
    }

    public void run() {
      try {
        while (run) {
          readLine();
        }
      } catch (Exception e) {
        logSevere("Socket closed");
      }
    }

    private void readLine() throws IOException {
      String line = input.readLine();
      if (line != null) {
        handleLine(line);
      } else {
        logSevere("Socket closed");
      }
    }
  }

}
