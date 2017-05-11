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
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by neuro on 27-01-2017.
 */
// TODO: 01-02-2017 neuro messagereceiver nao é! lol
public abstract class AptoideServerSocket extends AptoideSocket implements ServerActionDispatcher {

  private static final String TAG = AptoideServerSocket.class.getSimpleName();
  private final int port;
  private final int timeout;
  @Getter private boolean shutdown = false;
  private ServerSocketTimeoutManager serverSocketTimeoutManager;
  private List<Socket> connectedSockets = new CopyOnWriteArrayList<>();
  private ServerSocket ss;
  private boolean serving = false;
  private LinkedBlockingQueue<ServerAction> queuedServerActions = new LinkedBlockingQueue<>();
  @Getter private Host host;
  private int serverSocketTimeout;
  @Setter private HostsChangedCallback hostsChangedCallbackCallback;

  public AptoideServerSocket(int port, int serverSocketTimeout, int timeout) {
    this.port = port;
    this.serverSocketTimeout = serverSocketTimeout;
    this.timeout = timeout;
  }

  public AptoideServerSocket(int bufferSize, int port, int serverSocketTimeout, int timeout) {
    super(bufferSize);
    this.port = port;
    this.serverSocketTimeout = serverSocketTimeout;
    this.timeout = timeout;
  }

  @Override public AptoideSocket start() {
    Stoppable dispatcherLooper = newOrderDispatcherLooper();
    executorService.execute(dispatcherLooper);

    if (serving) {
      Print.d(TAG, "start: ShareApps: AptoideFileServerSocket already serving!");
      return this;
    } else {
      serving = true;
    }

    try {
      ss = new ServerSocket(port);
      ss.setSoTimeout(serverSocketTimeout);
      serverSocketTimeoutManager = new ServerSocketTimeoutManager(ss, serverSocketTimeout);
      serverSocketTimeoutManager.reserTimeout();
      host = new Host("192.168.43.1", ss.getLocalPort());
      Print.d(TAG, "start: "
          + Thread.currentThread().getId()
          + ": Starting server in port "
          + port
          + " and ip "
          + host.getIp()
          + ": "
          + this);
      while (true) {
        Socket socket = ss.accept();
        socket.setSoTimeout(timeout);
        connectedSockets.add(socket);
        if (hostsChangedCallbackCallback != null) {
          hostsChangedCallbackCallback.hostsChanged(getConnectedHosts());
        }

        executorService.execute(() -> {
          try {
            Print.d(TAG, "start: "
                + Thread.currentThread().getId()
                + ": "
                + this.getClass().getSimpleName()
                + ": Adding new client "
                + socket.getInetAddress().getHostAddress()
                + ":"
                + socket.getPort());
            onNewClient(socket);
            //serverSocketTimeoutManager.reserTimeout();
          } catch (IOException e) {
            if (onError != null) {
              onError.onError(e);
            }
          } finally {
            try {
              serverSocketTimeoutManager.reserTimeout();
              connectedSockets.remove(socket);
              Print.d(TAG, "Closing socket " + socket);
              socket.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
          }
        });
      }
    } catch (IOException e) {
      // Ignore, when socket is closed during accept() it lands here.
      if (e instanceof SocketTimeoutException) {
        e.printStackTrace();
      }
      Print.d(TAG, "start: ShareApps: Server explicitly closed " + this.getClass().getSimpleName());
      dispatcherLooper.stop();
      try {
        // Hammered. To unlock queuedServerActions.
        queuedServerActions.put(() -> {
        });
      } catch (InterruptedException e1) {
        e1.printStackTrace();
      }

      try {
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
      } catch (InterruptedException e1) {
        e1.printStackTrace();
      }
    }
    return this;
  }

  public void shutdown() {

    if (shutdown) {
      Print.w(TAG, "shutdown: ShareApps: Server already shut down!");
      return;
    }

    shutdown = true;
    onError = null;

    if (ss != null && !ss.isClosed()) {//todo need to solve in the future this nullpointerexception
      try {
        Print.d(TAG, "Closing socket " + ss.getClass().getSimpleName());
        ss.close();
      } catch (IOException e) {
        e.printStackTrace();
      }

      Iterator<Socket> iterator = connectedSockets.iterator();
      while (iterator.hasNext()) {
        Socket next = iterator.next();
        try {
          Print.d(TAG, "Closing socket " + next);
          next.close();
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
    }

    shutdownExecutorService();
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

  @Override public void dispatchServerAction(ServerAction serverAction) {
    Print.d(TAG, "dispatchServerAction() called with: serverAction = [" + serverAction + "]");
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
        connectedSockets.remove(socket);
        hostsChangedCallbackCallback.hostsChanged(getConnectedHosts());
        Print.d(TAG, "removeHost: AptoideServerSocket: Host " + host + " removed from the server.");
      }
    }
  }
}
