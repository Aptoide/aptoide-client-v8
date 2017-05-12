package cm.aptoide.pt.v8engine.install.remote;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

/**
 * Created by franciscoaleixo on 15/08/2016.
 */
public class RemoteInstallationSenderManager {
  static final String TAG = RemoteInstallationSenderManager.class.getSimpleName();
  static final String SERVICE_TYPE = "_aptoide-rmtinst._tcp.local.";
  static final String PAYLOAD_TAG = "apkinstall_appid=";
  static final String SUCCESS_TAG = "receivedpayload=";
  static final String INVALID_PAYLOAD_RESPONSE = "INVALIDPAYLOAD";
  static final String SERVER_TAG_TO_REMOVE = ".local.";
  static final int TIMEOUT = 10000;
  static final int DISCOVERY_TO = 20000;

  /**
   * Service variables
   */

  JmDNS jmDNS;
  WifiManager.MulticastLock multicastLock;
  RemoteInstallationSenderListener listener;
  Context context;
  Handler handler;

  SocketClientThread clientServerThread;

  public RemoteInstallationSenderManager(Context context) {
    this.context = context;
    this.handler = new Handler();
  }

  /**
   * mDNS Service Methods
   */

  public void discoverAptoideTVServices(RemoteInstallationSenderListener listener) {
    this.listener = listener;

    new Thread(new Runnable() {
      @Override public void run() {
        try {
          WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
          multicastLock = wifi.createMulticastLock(context.getClass()
              .getName());
          multicastLock.setReferenceCounted(true);
          multicastLock.acquire();

          jmDNS = JmDNS.create(getAddress(), Build.MODEL);
          jmDNS.addServiceListener(SERVICE_TYPE, new ServiceListener() {
            @Override public void serviceAdded(ServiceEvent event) {
              Log.i(TAG, "Remote Installation - A service has been added: " + event.getInfo());
            }

            @Override public void serviceRemoved(ServiceEvent event) {
              ServiceInfo info = event.getInfo();
              final InetAddress address = info.getInet4Addresses()[0];
              final int port = info.getPort();
              String name = info.getServer();
              if (name.contains(SERVER_TAG_TO_REMOVE)) {
                name = name.substring(0, name.length() - SERVER_TAG_TO_REMOVE.length());
              }
              final String fName = name;

              Log.i(TAG, "Remote Installation - A service has been removed: " + event.getInfo());
              runOnUiThread(new Runnable() {
                @Override public void run() {
                  RemoteInstallationSenderManager.
                      this.listener.onAptoideTVServiceLost(
                      new ReceiverDevice(fName, address, port));
                }
              });
            }

            @Override public void serviceResolved(ServiceEvent event) {
              ServiceInfo info = event.getInfo();
              final InetAddress address = info.getInet4Addresses()[0];
              final int port = info.getPort();
              String name = info.getServer();
              if (name.contains(SERVER_TAG_TO_REMOVE)) {
                name = name.substring(0, name.length() - SERVER_TAG_TO_REMOVE.length());
              }
              final String fName = name;

              Log.i(TAG, "Remote Installation - A service has been resolved: " + event.getInfo());
              runOnUiThread(new Runnable() {
                @Override public void run() {
                  RemoteInstallationSenderManager.
                      this.listener.onAptoideTVServiceFound(
                      new ReceiverDevice(fName, address, port));
                }
              });
            }
          });
          runOnUiThread(new Runnable() {
            @Override public void run() {
              RemoteInstallationSenderManager.this.listener.onDiscoveryStarted();
            }
          });
        } catch (IOException e) {
          runOnUiThread(new Runnable() {
            @Override public void run() {
              RemoteInstallationSenderManager.this.listener.onNoNetworkAccess();
            }
          });
          Log.i(TAG, "Remote Installation - Error on discover: " + e);
        }
      }
    }).start();

    runOnUiThread(new Runnable() {
      @Override public void run() {
        stopDiscoveringAptoideTVServices();
      }
    }, DISCOVERY_TO);
  }

  InetAddress getAddress() throws UnknownHostException {
    WifiManager wifi = (WifiManager) context.getSystemService(android.content.Context.WIFI_SERVICE);
    WifiInfo wifiInfo = wifi.getConnectionInfo();
    int intaddr = wifiInfo.getIpAddress();

    byte[] byteaddr = new byte[] {
        (byte) (intaddr & 0xff), (byte) (intaddr >> 8 & 0xff), (byte) (intaddr >> 16 & 0xff),
        (byte) (intaddr >> 24 & 0xff)
    };
    return InetAddress.getByAddress(byteaddr);
  }

  void runOnUiThread(Runnable runnable) {
    handler.post(runnable);
  }

  void runOnUiThread(Runnable runnable, long time) {
    handler.postDelayed(runnable, time);
  }

  public void stopDiscoveringAptoideTVServices() {
    new AsyncTask<Void, Void, Void>() {
      private boolean closed = false;

      @Override protected Void doInBackground(Void... params) {
        try {
          if (jmDNS != null) {
            jmDNS.unregisterAllServices();
            jmDNS.close();
            closed = true;
            jmDNS = null;
          }
          if (multicastLock != null) {
            multicastLock.release();
            multicastLock = null;
          }
        } catch (Exception ex) {
          ex.printStackTrace();
        }
        return null;
      }

      @Override protected void onPostExecute(Void aVoid) {
        if (closed) {
          listener.onDiscoveryStopped();
        }
        super.onPostExecute(aVoid);
      }
    }.execute((Void) null);
  }

  public void sendAppId(ReceiverDevice device, String app) {
    clientServerThread = new SocketClientThread(device, app);
    clientServerThread.execute();
  }

  /**
   * Socket thread and related methods
   */

  private class SocketClientThread extends AsyncTask<Void, Void, Boolean> {
    private ReceiverDevice device;
    private String app;

    public SocketClientThread(ReceiverDevice device, String app) {
      this.device = device;
      this.app = app;
    }

    @Override protected Boolean doInBackground(Void... voids) {
      Socket clientSocket = null;
      PrintWriter out = null;
      BufferedReader in = null;
      try {
        clientSocket = new Socket(device.getAddress(), device.getPort());
        clientSocket.setSoTimeout(TIMEOUT);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        out.println(PAYLOAD_TAG + app);
        Log.i(TAG, "Remote Install - SocketClientThread Sent: " + PAYLOAD_TAG + app);

        String response = in.readLine();
        Log.i(TAG, "Remote Install - SocketClientThread Received: " + response);

        if (response.equals(SUCCESS_TAG + app)) {
          return true;
        } else if (response.equals(INVALID_PAYLOAD_RESPONSE)) {
          return false;
        }
      } catch (IOException e) {
        return false;
      } finally {
        if (clientSocket != null && !clientSocket.isClosed()) {
          try {
            clientSocket.close();
          } catch (IOException e) {
            Log.e(TAG, "Remote Install - SocketClientThread Closing Error: " + e);
          }
        }
        if (out != null) {
          out.close();
        }
        if (in != null) {
          try {
            in.close();
          } catch (IOException e) {
            Log.e(TAG, "Remote Install - SocketClientThread BR Closing Error: " + e);
          }
        }
      }
      return false;
    }

    @Override protected void onPostExecute(Boolean success) {
      if (success) {
        listener.onAppSendSuccess();
      } else {
        listener.onAppSendUnsuccess();
      }
      super.onPostExecute(success);
    }
  }
}
