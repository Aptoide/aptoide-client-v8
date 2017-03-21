package cm.aptoide.pt.spotandshare.socket;

import java.io.IOException;
import java.net.Socket;
import lombok.Setter;

/**
 * Created by neuro on 27-01-2017.
 */

public abstract class AptoideClientSocket extends AptoideSocket {

  private final String hostName;
  private final int port;
  private String fallbackHostName;
  @Setter private int retries;
  private Socket socket;

  public AptoideClientSocket(String hostName, String fallbackHostName, int port) {
    this(hostName, port);
    this.fallbackHostName = fallbackHostName;
  }

  public AptoideClientSocket(String hostName, int port) {
    this.hostName = hostName;
    this.port = port;
  }

  public AptoideClientSocket(int bufferSize, String hostName, String fallbackHostName, int port) {
    this(bufferSize, hostName, port);
    this.fallbackHostName = fallbackHostName;
  }

  public AptoideClientSocket(int bufferSize, String hostName, int port) {
    super(bufferSize);
    this.hostName = hostName;
    this.port = port;
  }

  @Override public AptoideSocket start() {

    socket = null;

    String[] hosts = new String[] { hostName, fallbackHostName };

    for (String host : hosts) {
      if (host != null) {
        retries = 3;

        while (socket == null && retries-- > 0) {
          try {
            socket = new Socket(hostName, port);
          } catch (IOException e) {
            e.printStackTrace(System.out);
            System.out.println("Failed to connect to " + hostName + ":" + port);
            if (onError != null) {
              onError.onError(e);
            }
            try {
              Thread.sleep(1000);
            } catch (InterruptedException e1) {
              e1.printStackTrace();
            }
          }
        }
      }
    }

    if (socket == null) {
      if (onError != null) {
        onError.onError(new IOException(
            getClass().getSimpleName() + " Couldn't connect to " + hosts + ":" + port));
      }
      return null;
    }

    try {
      onConnected(socket);
    } catch (IOException e) {
      e.printStackTrace(System.out);
      if (onError != null) {
        onError.onError(e);
      }
    } finally {
      try {
        socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    System.out.println("ShareApps: Thread "
        + Thread.currentThread().getId()
        + " finished "
        + getClass().getSimpleName());
    return this;
  }

  protected abstract void onConnected(Socket socket) throws IOException;

  @Override public void shutdown() {
    super.shutdown();
    try {
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
