package cm.aptoide.pt.shareapps.socket;

import cm.aptoide.pt.shareapps.socket.entities.Host;
import cm.aptoide.pt.shareapps.socket.interfaces.serveraction.ServerAction;
import cm.aptoide.pt.shareapps.socket.interfaces.serveraction.ServerActionDispatcher;
import cm.aptoide.pt.shareapps.socket.util.ServerSocketTimeoutManager;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import lombok.Getter;

/**
 * Created by neuro on 27-01-2017.
 */
// TODO: 01-02-2017 neuro messagereceiver nao é! lol
public abstract class AptoideServerSocket extends AptoideSocket implements ServerActionDispatcher {

  private static final int TIMEOUT = 5000;

  private final int port;
  ServerSocketTimeoutManager serverSocketTimeoutManager;
  private List<Socket> connectedSockets = new LinkedList<>();
  private ServerSocket ss;
  private boolean serving = false;
  private boolean shutdown = false;
  private LinkedBlockingQueue<ServerAction> queuedServerActions = new LinkedBlockingQueue<>();
  @Getter private Host host;

  public AptoideServerSocket(int port) {
    this.port = port;
  }

  public AptoideServerSocket(int bufferSize, int port) {
    super(bufferSize);
    this.port = port;
  }

  @Override public AptoideSocket start() {
    executorService.execute(newOrderDispatcherLooper());

    if (serving) {
      System.out.println("AptoideFileServerSocket already serving!");
      return this;
    } else {
      serving = true;
    }

    try {
      ss = new ServerSocket(port);
      serverSocketTimeoutManager = new ServerSocketTimeoutManager(ss, TIMEOUT);
      serverSocketTimeoutManager.reserTimeout();
      host = Host.from(ss);
      System.out.println(
          Thread.currentThread().getId() + ": Starting server in port " + port + ": " + this);
      while (true) {
        Socket socket = ss.accept();
        connectedSockets.add(socket);

        executorService.execute(() -> {
          try {
            System.out.println(Thread.currentThread().getId()
                + ": "
                + this.getClass().getSimpleName()
                + ": Adding new client "
                + socket.getInetAddress().getHostAddress()
                + ":"
                + socket.getPort());
            onNewClient(socket);
            serverSocketTimeoutManager.reserTimeout();
          } finally {
            try {
              serverSocketTimeoutManager.reserTimeout();
              connectedSockets.remove(socket);
              System.out.println("Closing server client socket.");
              socket.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        });
      }
    } catch (IOException e) {
      // Ignore, when socket is closed during accept() it lands here.
      System.out.println("Server explicitly closed.");
    }
    return this;
  }

  protected Runnable newOrderDispatcherLooper() {
    return () -> {
      try {
        while (true) {
          ServerAction take = queuedServerActions.take();
          take.execute();
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    };
  }

  protected abstract void onNewClient(Socket socket);

  public void shutdown() {

    if (shutdown) {
      System.out.println("Server already shut down!");
      return;
    }

    shutdown = true;

    if (!ss.isClosed()) {
      try {
        ss.close();
      } catch (IOException e) {
        e.printStackTrace();
      }

      for (Socket socket : connectedSockets) {
        try {
          socket.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    } else {
      System.out.println("AptoideFileServerSocket already shutdown!");
    }

    shutdownExecutorService();
  }

  public List<Host> getConnectedHosts() {
    List<Host> hosts = new LinkedList<>();

    for (Socket connectedSocket : connectedSockets) {
      hosts.add(
          new Host(connectedSocket.getInetAddress().getHostAddress(), connectedSocket.getPort()));
    }

    return hosts;
  }

  @Override public void dispatchServerAction(ServerAction serverAction) {
    System.out.println(Thread.currentThread().getId() + ": Adding action to serverActions.");
    try {
      queuedServerActions.put(serverAction);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
