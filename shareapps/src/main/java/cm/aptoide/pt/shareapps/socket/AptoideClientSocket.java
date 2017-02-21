package cm.aptoide.pt.shareapps.socket;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import lombok.Setter;

/**
 * Created by neuro on 27-01-2017.
 */

public abstract class AptoideClientSocket extends AptoideSocket {

  private final String hostName;
  private final int port;
  @Setter private int retries = 3;

  public AptoideClientSocket(String hostName, int port) {
    this.hostName = hostName;
    this.port = port;
  }

  public AptoideClientSocket(int bufferSize, String hostName, int port) {
    super(bufferSize);
    this.hostName = hostName;
    this.port = port;
  }

  @Override public AptoideSocket start() {

    Socket socket = null;

    while (socket == null && retries-- > 0) {
      try {
        socket = new Socket(hostName, port);
      } catch (IOException e) {
        e.printStackTrace();
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e1) {
          e1.printStackTrace();
        }
      }
    }

    try {
      onConnected(socket);
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        if (socket != null) {
          socket.close();
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    System.out.println(
        "ShareApps: Thread " + Thread.currentThread().getId() + " finished receiving files.");
    return this;
  }

  protected abstract void onConnected(Socket socket) throws IOException;
}
