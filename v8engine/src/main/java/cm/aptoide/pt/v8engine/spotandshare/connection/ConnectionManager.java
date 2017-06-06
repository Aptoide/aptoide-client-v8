package cm.aptoide.pt.v8engine.spotandshare.connection;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.v8engine.spotandshare.DataHolder;
import cm.aptoide.pt.v8engine.spotandshare.NetworkHolder;
import cm.aptoide.pt.v8engine.spotandshare.group.Group;
import cm.aptoide.pt.v8engine.spotandshare.group.GroupParser;
import cm.aptoide.pt.v8engine.spotandshare.group.GroupValidator;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by filipegoncalves on 31-01-2017.
 */

public class ConnectionManager implements NetworkHolder {

  public final static int ERROR_ON_RECONNECT = 0;
  public final static int ERROR_INVALID_GROUP = 1;
  public final static int SUCCESSFUL_JOIN = 2;
  public final static int ERROR_UNKNOWN = 3;

  public static final int SUCCESS_HOTSPOT_CREATION = 6;
  public static final int FAILED_TO_CREATE_HOTSPOT = 7;
  public static final String UNIQUE_ID = "uniqueID";
  public static final int RULE_VERSION = 2;
  public static final String TAG = ConnectionManager.class.getSimpleName();
  private static ConnectionManager instance;
  private final Context context;
  private final SharedPreferences prefs;
  private final GroupValidator groupValidator;
  private final HotspotControlCounter hotspotControlCounter;
  private final GroupParser groupParser;
  private HotspotManager hotspotManager;
  private HotspotSSIDCodeMapper hotspotSSIDCodeMapper;
  private WifiManager wifimanager;
  private ArrayList<Group> clients;
  private WifiStateListener listenerActivateButtons;
  private WifiStateListener listenerJoinWifi;
  private InactivityListener inactivityListener;
  private ClientsConnectedListener clientsConnectedListener;
  private Timer scanner;
  private String chosenHotspot;
  private Network network;
  private BroadcastReceiver activateButtonsReceiver = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      if (wifimanager == null) {
        wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
      }
      if (wifimanager.getWifiState() == 3 && listenerActivateButtons != null) {
        listenerActivateButtons.onStateChanged(wifimanager.isWifiEnabled());
        context.unregisterReceiver(this);
      }
    }
  };
  private BroadcastReceiver scanAPTXVNetworks = new BroadcastReceiver() {
    public boolean showedNoHotspotMessage;
    public int noHotspotsFoundCounter;

    @Override public void onReceive(Context context, Intent intent) {
      List<Group> scanResultsSSID = new ArrayList<>();
      if (clients == null) {
        clients = new ArrayList<Group>();
      }

      List<ScanResult> connResults = wifimanager.getScanResults();

      boolean changes = false;
      if (connResults != null && connResults.size() != 0) {
        for (int i = 0; i < connResults.size(); i++) {
          String ssid = connResults.get(i).SSID;
          if (groupValidator.filterSSID(ssid)) {

            try {
              Group group = groupParser.parse(connResults.get(i).SSID);
              scanResultsSSID.add(group);

              if (!clients.contains(group)) {
                clients.add(group);
                changes = true;
              }
            } catch (ParseException e) {
              Logger.d(TAG, "Tried parsing an invalid group name SSID.");
            }
          }
        }
        if (noHotspotsFoundCounter >= 2 && clients.size() < 1 && !showedNoHotspotMessage) {
          showedNoHotspotMessage = true;
          if (inactivityListener != null) {
            inactivityListener.onInactivity(true);
          }
        }

        groupValidator.flagGhosts(clients);

        for (int j = 0; j < clients.size(); j++) {
          Group tmp = clients.get(j);
          if (!tmp.isGhost() && !scanResultsSSID.contains(tmp)) {
            clients.remove(tmp);
            changes = true;
            if (!TextUtils.isEmpty(chosenHotspot) && tmp.getSsid()
                .equals(chosenHotspot)) {
              listenerJoinWifi.onStateChanged(false);
            }
            Logger.d(TAG, "removed this : " + tmp);
          }
        }
      } else {
        Logger.d(TAG, "THERE ARE NO APTXV NETWORKS");
        if (noHotspotsFoundCounter >= 2 && !showedNoHotspotMessage) {
          showedNoHotspotMessage = true;
          if (inactivityListener != null) {
            inactivityListener.onInactivity(true);
          }
        }
      }
      if (changes) {
        ArrayList<Group> clearedList = groupValidator.removeGhosts(clients);
        clientsConnectedListener.onNewClientsConnected(clearedList);
      }
      if (noHotspotsFoundCounter <= 2
          && !showedNoHotspotMessage) {//to warn that there are no networks and they should create one.
        noHotspotsFoundCounter++;
      }
    }
  };
  private boolean reconnected;
  private BroadcastReceiver connectingWifi = new BroadcastReceiver() {

    @Override public void onReceive(Context context, Intent intent) {

      boolean isWifiConnected = false;
      ConnectivityManager conMgr =
          (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      int currentApiVersion = Build.VERSION.SDK_INT;
      if (currentApiVersion >= Build.VERSION_CODES.LOLLIPOP) {
        Network[] networks = conMgr.getAllNetworks();
        if (networks == null) {
          isWifiConnected = false;
        } else {
          for (Network network : networks) {
            NetworkInfo info = conMgr.getNetworkInfo(network);
            if (info != null && info.getType() == ConnectivityManager.TYPE_WIFI) {
              if (info.isAvailable() && info.isConnected()) {

                WifiInfo wifiInfo = wifimanager.getConnectionInfo();
                if (wifiInfo.getSSID()
                    .contains("APTXV")) {
                  isWifiConnected = true;
                  DataHolder.getInstance().network = network;
                  break;
                } else {//connected to the wrong network
                  if (!reconnected && !TextUtils.isEmpty(chosenHotspot)) {
                    reconnected = true;
                    new Thread(new Runnable() {
                      @Override public void run() {
                        joinHotspot(chosenHotspot, false);
                      }
                    }).start();
                  } else {
                    listenerJoinWifi.onStateChanged(false);
                    try {
                      context.unregisterReceiver(this);
                    } catch (IllegalArgumentException e) {
                      Logger.e(TAG,
                          "There was an error while trying to unregister the ConnectingToWifi receiver");
                    }
                    reconnected = false;
                    break;
                  }
                }
              }
            }
          }
        }
      } else {
        NetworkInfo[] netInf = conMgr.getAllNetworkInfo();
        for (NetworkInfo inf : netInf) {
          if (inf.getState() == NetworkInfo.State.CONNECTED
              && inf.getType() == ConnectivityManager.TYPE_WIFI) {

            WifiInfo wifiInfo = wifimanager.getConnectionInfo();
            if (wifiInfo.getSSID()
                .contains("APTXV")) {
              isWifiConnected = true;
              break;
            } else {
              if (!reconnected && !TextUtils.isEmpty(chosenHotspot)) {
                reconnected = true;
                new Thread(new Runnable() {
                  @Override public void run() {
                    joinHotspot(chosenHotspot, false);
                  }
                }).start();
              } else {
                listenerJoinWifi.onStateChanged(false);
                try {
                  context.unregisterReceiver(this);
                  context.unregisterReceiver(scanAPTXVNetworks);
                } catch (IllegalArgumentException e) {
                  Logger.e(TAG,
                      "There was an error while trying to unregister the ConnectingToWifi or the ScanAPTXNetworks receiver");
                }
                reconnected = false;
                break;
              }
            }
          }
        }
      }
      if (isWifiConnected) {
        listenerJoinWifi.onStateChanged(true);
        try {
          context.unregisterReceiver(this);
          context.unregisterReceiver(scanAPTXVNetworks);
          Logger.d("BROADCASTRECEIVER", "Unregistered scan receiver INSIDE CONNECTING WIFI");
        } catch (IllegalArgumentException e) {
          Logger.e(TAG,
              "There was an error while trying to unregister the wifireceiver and the wifireceiverforconnectingwifi");
        }
      }
    }
  };

  private ConnectionManager(Context context, SharedPreferences sharedPreferences,
      WifiManager wifimanager, HotspotSSIDCodeMapper hotspotSSIDCodeMapper,
      HotspotControlCounter hotspotControlCounter, GroupParser groupParser,
      GroupValidator groupValidator, HotspotManager hotspotManager) {
    this.context = context;
    this.wifimanager = wifimanager;
    prefs = sharedPreferences;
    this.hotspotSSIDCodeMapper = hotspotSSIDCodeMapper;
    this.hotspotControlCounter = hotspotControlCounter;
    this.groupParser = groupParser;
    this.groupValidator = groupValidator;
    this.hotspotManager = hotspotManager;
  }

  public static ConnectionManager getInstance(Context context) {
    if (instance == null) {
      SharedPreferences defaultSharedPreferences =
          PreferenceManager.getDefaultSharedPreferences(context);
      HotspotSSIDCodeMapper hotspotSSIDCodeMapper = new HotspotSSIDCodeMapper();

      instance = new ConnectionManager(context, defaultSharedPreferences,
          (WifiManager) context.getSystemService(Context.WIFI_SERVICE), hotspotSSIDCodeMapper,
          new HotspotControlCounter(defaultSharedPreferences, hotspotSSIDCodeMapper),
          new GroupParser(), new GroupValidator(), new HotspotManager(context));
    }
    return instance;
  }

  public void start(WifiStateListener listener) {
    prefs.edit()
        .putBoolean("wifiOnStart", this.wifimanager.isWifiEnabled())
        .apply();

    this.listenerActivateButtons = listener;
    context.registerReceiver(activateButtonsReceiver,
        new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
  }

  public void evaluateWifi(WifiStateListener listener) {
    this.listenerJoinWifi = listener;
    context.registerReceiver(connectingWifi,
        new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
  }

  public void searchForAPTXVNetworks(InactivityListener inactivityListener,
      ClientsConnectedListener clientsConnectedListener) {
    this.inactivityListener = inactivityListener;
    this.clientsConnectedListener = clientsConnectedListener;

    context.registerReceiver(scanAPTXVNetworks,
        new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    scheduleScan();
  }

  private void scheduleScan() {
    if (wifimanager == null) {
      wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }
    if (!wifimanager.isWifiEnabled()) {
      wifimanager.setWifiEnabled(true);
    }
    scanner = new Timer();
    scanner.scheduleAtFixedRate(new TimerTask() {
      @Override public void run() {
        wifimanager.startScan();
      }
    }, 1000, 7000);//delay, interval
  }

  public void resetHotspot(boolean enable) {
    hotspotManager.resetHotspot(enable);
  }

  public int enableHotspot(String deviceName) {
    String ssid = String.valueOf(hotspotSSIDCodeMapper.encode(RULE_VERSION))
        + "APTXV"
        + hotspotControlCounter.incrementAndGetStringCounter()
        + "_"
        + getRandomAlphanumericString(5)
        + "_"
        + deviceName
        + getSpotShareID()
        + "";
    return hotspotManager.enablePrivateHotspot(ssid);
  }

  private String getRandomAlphanumericString(int length) {
    StringBuilder sb = new StringBuilder();
    int tmp;
    for (int i = 0; i < length; i++) {
      tmp = generateRandomID();
      sb.append(hotspotSSIDCodeMapper.encode(tmp));
    }
    return sb.toString();
  }

  private String getSpotShareID() {
    String id = prefs.getString(UNIQUE_ID, "default");
    if (id.equals("default")) {
      int tmp = generateRandomID();
      id = String.valueOf(hotspotSSIDCodeMapper.encode(tmp));
      prefs.edit()
          .putString(UNIQUE_ID, id)
          .apply();
    }
    return id;
  }

  private int generateRandomID() {
    Random r = new Random();
    return r.nextInt(62);
  }

  public int joinHotspot(String chosenHotspot, boolean shouldReconnect) {
    WifiConfiguration conf = new WifiConfiguration();
    Logger.d(TAG, "chosen hotspot is : " + chosenHotspot);
    conf.SSID = "\"" + chosenHotspot + "\"";
    conf.preSharedKey = "\"passwordAptoide\"";
    conf.hiddenSSID = true;
    conf.status = WifiConfiguration.Status.ENABLED;
    conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

    int netid = wifimanager.addNetwork(conf);
    Logger.d(TAG, "netid is: " + netid);

    List<WifiConfiguration> list = wifimanager.getConfiguredNetworks();
    if (list != null) {
      for (WifiConfiguration i : list) {
        if (i.SSID != null && i.SSID.equals("\"" + chosenHotspot + "\"")) {
          try {
            boolean b = wifimanager.disconnect();
            try {
              Thread.sleep(800);
            } catch (InterruptedException e) {
            }
            boolean enab = wifimanager.enableNetwork(i.networkId, true);
            Logger.d(TAG,
                "i.networkId " + i.networkId + "\n" + "o net id do add esta a : " + netid);

            try {
              Thread.sleep(2000);
            } catch (InterruptedException e) {
            }

            if (shouldReconnect) {
              this.chosenHotspot = chosenHotspot;//to save in case of needing to reconnect
              boolean recon = wifimanager.reconnect();
              try {
                Thread.sleep(2000);
              } catch (InterruptedException e) {
              }

              if (recon) {
                return SUCCESSFUL_JOIN;
              } else {
                return ERROR_ON_RECONNECT;
              }
            } else {
              this.chosenHotspot = chosenHotspot;//to save in case of needing to reconnect
              return SUCCESSFUL_JOIN;
            }
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      }
    }
    return ERROR_UNKNOWN;
  }

  public void cleanNetworks() {
    if (wifimanager == null) {
      wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }
    List<WifiConfiguration> list = wifimanager.getConfiguredNetworks();
    if (list != null) {
      for (WifiConfiguration i : list) {
        String[] separated = i.SSID.split("_");
        String tmp = separated[0].trim();
        if (tmp.contains("APTXV")) {
          boolean remove = wifimanager.removeNetwork(i.networkId);
          Logger.d(TAG, "boolean from remove network is : " + remove);
        } else {
          Logger.d(TAG, "Not a APTXV network. Can not remove this network;");
        }
      }
    }
  }

  public String getIPAddress() {
    return intToIp(wifimanager.getDhcpInfo().serverAddress);
  }

  private String intToIp(int i) {
    return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF) + "." + ((i >> 24)
        & 0xFF);
  }

  public void resume() {
    context.registerReceiver(activateButtonsReceiver,
        new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
    context.registerReceiver(connectingWifi,
        new IntentFilter(WifiManager.NETWORK_STATE_CHANGED_ACTION));
    context.registerReceiver(scanAPTXVNetworks,
        new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
  }

  public void stop() {
    Logger.d(TAG, "Going to cancel the tasks");
    if (clients != null) {
      clients.clear();
    }
    try {
      context.unregisterReceiver(activateButtonsReceiver);
    } catch (IllegalArgumentException e) {
      Logger.e(TAG, "error unregistering activateButtonsReceiver");
    }

    try {
      context.unregisterReceiver(scanAPTXVNetworks);
    } catch (IllegalArgumentException e) {
      Logger.e(TAG, "error unregistering scanAPTXVNetworks");
    }

    try {
      context.unregisterReceiver(connectingWifi);
    } catch (IllegalArgumentException e) {
      Logger.e(TAG, "error unregistering connectingWifi");
    }

    this.listenerJoinWifi = null;
    this.listenerActivateButtons = null;
    this.clientsConnectedListener = null;
    this.inactivityListener = null;
    if (scanner != null) {
      scanner.cancel();
      scanner.purge();
    }
  }

  public void recoverNetworkState() {
    if (wifimanager == null) {
      wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }
    wifimanager.disconnect();
    Boolean wifiOnStart = prefs.getBoolean("wifiOnStart", false);
    if (wifiOnStart) {
      wifimanager.setWifiEnabled(true);
      Logger.d(TAG, "Recovering wifi state, it was on before. ");
    } else {
      wifimanager.setWifiEnabled(false);
    }
  }

  public void enableWifi(boolean enable) {
    if (wifimanager == null) {
      wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }
    wifimanager.setWifiEnabled(enable);
  }

  @Override public Network getNetwork() {
    return network;
  }

  public interface WifiStateListener {
    void onStateChanged(boolean enabled);
  }

  /**
   * To show innactivity message on the view
   */
  public interface InactivityListener {
    void onInactivity(boolean inactive);
  }

  /**
   * Return the list of new connected clients
   */
  public interface ClientsConnectedListener {
    void onNewClientsConnected(ArrayList<Group> clients);
  }
}
