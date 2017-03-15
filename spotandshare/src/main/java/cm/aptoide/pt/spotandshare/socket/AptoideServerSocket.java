package cm.aptoide.pt.spotandshare.socket;

import cm.aptoide.pt.spotandshare.socket.entities.Host;
import cm.aptoide.pt.spotandshare.socket.interfaces.HostsChangedCallback;
import cm.aptoide.pt.spotandshare.socket.interfaces.Stoppable;
import cm.aptoide.pt.spotandshare.socket.interfaces.serveraction.ServerAction;
import cm.aptoide.pt.spotandshare.socket.interfaces.serveraction.ServerActionDispatcher;
import cm.aptoide.pt.spotandshare.socket.util.ServerSocketTimeoutManager;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by neuro on 27-01-2017.
 */
// TODO: 01-02-2017 neuro messagereceiver nao é! lol
public abstract class AptoideServerSocket extends AptoideSocket implements ServerActionDispatcher {

  private final int port;
  private ServerSocketTimeoutManager serverSocketTimeoutManager;
  private List<Socket> connectedSockets = new LinkedList<>();
  private ServerSocket ss;
  private boolean serving = false;
  private boolean shutdown = false;
  private LinkedBlockingQueue<ServerAction> queuedServerActions = new LinkedBlockingQueue<>();
  @Getter private Host host;
  private int timeout;
  @Setter private HostsChangedCallback hostsChangedCallbackCallback;

  public AptoideServerSocket(int port, int timeout) {
    this.port = port;
    this.timeout = timeout;
  }

  public AptoideServerSocket(int bufferSize, int port, int timeout) {
    super(bufferSize);
    this.port = port;
    this.timeout = timeout;
  }

  @Override public AptoideSocket start() {
    Stoppable dispatcherLooper = newOrderDispatcherLooper();
    executorService.execute(dispatcherLooper);

    if (serving) {
      System.out.println("ShareApps: AptoideFileServerSocket already serving!");
      return this;
    } else {
      serving = true;
    }

    try {
      ss = new ServerSocket(port);
      serverSocketTimeoutManager = new ServerSocketTimeoutManager(ss, timeout);
      serverSocketTimeoutManager.reserTimeout();
      host = Host.from(ss);
      System.out.println(Thread.currentThread().getId()
          + ": Starting server in port "
          + port
          + " and ip "
          + host.getIp()
          + ": "
          + this);
      while (true) {
        Socket socket = ss.accept();
        connectedSockets.add(socket);
        if (hostsChangedCallbackCallback != null) {
          hostsChangedCallbackCallback.hostsChanged(getConnectedHosts());
        }

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
            //serverSocketTimeoutManager.reserTimeout();
          } catch (IOException e) {
            e.printStackTrace(System.out);
            if (onError != null) {
              onError.onError(e);
            }
          } finally {
            try {
              serverSocketTimeoutManager.reserTimeout();
              connectedSockets.remove(socket);
              System.out.println("ShareApps: Closing " + getClass().getSimpleName() + " socket.");
              socket.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        });
      }
    } catch (IOException e) {
      // Ignore, when socket is closed during accept() it lands here.
      System.out.println("ShareApps: Server explicitly closed " + this.getClass().getSimpleName());
      dispatcherLooper.stop();
      try {
        // Hammered. To unlock queuedServerActions.
        queuedServerActions.put(() -> {
        });
      } catch (InterruptedException e1) {
        e1.printStackTrace();
      }
      shutdown();
      try {
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
      } catch (InterruptedException e1) {
        e1.printStackTrace();
      }
    }
    return this;
  }

  protected Stoppable newOrderDispatcherLooper() {
    return new Stoppable() {

      private boolean running;

      @Override public void stop() {
        running = false;
      }

      @Override public void run() {
        running = true;

        try {
          while (running) {
            ServerAction take = queuedServerActions.take();
            take.execute();
          }
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    };
  }

  public List<Host> getConnectedHosts() {
    List<Host> hosts = new LinkedList<>();

    for (Socket connectedSocket : connectedSockets) {
      hosts.add(
          new Host(connectedSocket.getInetAddress().getHostAddress(), connectedSocket.getPort()));
    }

    return hosts;
  }

  protected abstract void onNewClient(Socket socket) throws IOException;

  public void shutdown() {

    if (shutdown) {
      System.out.println("ShareApps: Server already shut down!");
      return;
    }

    shutdown = true;

    if (ss != null && !ss.isClosed()) {//todo need to solve in the future this nullpointerexception
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

      try {
        queuedServerActions.put(() -> {
        });
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    } else {
      System.out.println("ShareApps: AptoideFileServerSocket already shutdown!");
    }

    shutdownExecutorService();
  }

  @Override public void dispatchServerAction(ServerAction serverAction) {
    System.out.println(Thread.currentThread().getId() + ": Adding action to serverActions.");
    try {
      queuedServerActions.put(serverAction);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void removeHost(Host host) {
    Iterator<Socket> iterator = connectedSockets.iterator();
    while (iterator.hasNext()) {
      Socket socket = iterator.next();
      if (socket.getInetAddress().getHostAddress().equals(host.getIp())) {
        iterator.remove();
        hostsChangedCallbackCallback.hostsChanged(getConnectedHosts());
        System.out.println("AptoideServerSocket: Host " + host + " removed from the server.");
      }
    }

  }
}
