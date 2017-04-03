package cm.aptoide.pt.spotandshareandroid;

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
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static android.content.Context.CONNECTIVITY_SERVICE;

/**
 * Created by filipegoncalves on 31-01-2017.
 */

public class ConnectionManager {

  public final static int ERROR_ON_RECONNECT = 0;
  public final static int ERROR_INVALID_GROUP = 1;
  public final static int SUCCESSFUL_JOIN = 2;
  public final static int ERROR_UNKNOWN = 3;
  public final static int ERROR_MOBILE_DATA_ON_TOAST = 4;
  public final static int ERROR_MOBILE_DATA_ON_DIALOG = 5;

  public static final int SUCCESS_HOTSPOT_CREATION = 6;
  public static final int FAILED_TO_CREATE_HOTSPOT = 7;
  public static final String UNIQUE_ID = "uniqueID";
  private static ConnectionManager instance;
  private final Context context;
  private final SharedPreferences prefs;
  private final GroupValidator groupValidator;
  private final HotspotControlCounter hotspotControlCounter;
  private final GroupParser groupParser;
  private WifiManager wifimanager;
  private ArrayList<Group> clients;
  private WifiStateListener listenerActivateButtons;
  private WifiStateListener listenerJoinWifi;
  private InactivityListener inactivityListener;
  private ClientsConnectedListener clientsConnectedListener;
  private Timer scanner;
  private String chosenHotspot;
  private BroadcastReceiver activateButtonsReceiver = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      if (wifimanager == null) {
        wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
      }
      if (listenerActivateButtons != null) {
        listenerActivateButtons.onStateChanged(wifimanager.isWifiEnabled());
      }
    }
  };
  private BroadcastReceiver scanAPTXVNetworks = new BroadcastReceiver() {
    public boolean showedNoHotspotMessage;
    public int noHotspotsFoundCounter;

    @Override public void onReceive(Context context, Intent intent) {
      System.out.println("TOU AQUI NO WIFI RECEIVER !! ");
      System.out.println("o noHotspotsFOundCounter esta a : " + noHotspotsFoundCounter);
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
                System.out.println("Estou no : " + connResults.get(i).toString());
              }
            } catch (ParseException e) {
              Log.d("ConnectionManager: ", "Tried parsing an invalid group name SSID.");
            }
          }

        }
        if (noHotspotsFoundCounter >= 2 && clients.size() < 1 && !showedNoHotspotMessage) {
          showedNoHotspotMessage = true;
          inactivityListener.onInactivity(true);
        }

        clients = groupValidator.removeGhosts(clients);

        for (int j = 0; j < clients.size(); j++) {
          Group tmp = clients.get(j);
          System.out.println("this is one of the keyword : " + tmp);
          if (!scanResultsSSID.contains(tmp)) {
            clients.remove(tmp);
            changes = true;
            System.out.println("removed this : " + tmp);
          }
        }
      } else {
        System.out.println("tHERE ARE NO APTXV NETWORKS");
        if (noHotspotsFoundCounter >= 2 && !showedNoHotspotMessage) {
          showedNoHotspotMessage = true;
          inactivityListener.onInactivity(true);
        }
      }
      if (changes) {
        clientsConnectedListener.onNewClientsConnected(clients);
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
                if (wifiInfo.getSSID().contains("APTXV")) {
                  isWifiConnected = true;
                  DataHolder.getInstance().network = network;
                  break;
                } else {//connected to the wrong network
                  if (!reconnected && !chosenHotspot.isEmpty()) {
                    reconnected = true;
                    new Thread(new Runnable() {
                      @Override public void run() {
                        joinHotspot(chosenHotspot, true);
                      }
                    }).start();
                  } else {
                    listenerJoinWifi.onStateChanged(false);
                    try {
                      context.unregisterReceiver(this);
                      //// TODO: 28-03-2017 filipe  add unregister scanAptxNETWORKS
                    } catch (IllegalArgumentException e) {
                      System.out.println(
                          "There was an error while trying to unregister the wifireceiver and the wifireceiverforconnectingwifi");
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
        //se build < 21
        NetworkInfo[] netInf = conMgr.getAllNetworkInfo();
        for (NetworkInfo inf : netInf) {
          System.out.println("Netowrk info : " + inf.toString());//not finding the right one.
          System.out.println("The state is :  : " + inf.getState());
          if (inf.getState() == NetworkInfo.State.CONNECTED
              && inf.getType() == ConnectivityManager.TYPE_WIFI) {

            WifiInfo wifiInfo = wifimanager.getConnectionInfo();
            if (wifiInfo.getSSID().contains("APTXV")) {
              isWifiConnected = true;
              break;
              //// TODO: 28-03-2017 filipe add reconnect to these lower versions
            } else {
              if (!reconnected && !chosenHotspot.isEmpty()) {
                reconnected = true;
                new Thread(new Runnable() {
                  @Override public void run() {
                    joinHotspot(chosenHotspot, true);
                  }
                }).start();
              } else {
                listenerJoinWifi.onStateChanged(false);
                try {
                  context.unregisterReceiver(this);
                  context.unregisterReceiver(scanAPTXVNetworks);
                } catch (IllegalArgumentException e) {
                  System.out.println(
                      "There was an error while trying to unregister the wifireceiver and the wifireceiverforconnectingwifi");
                }
              }
              reconnected = false;
              break;
            }
          }
        }
      }
      if (isWifiConnected) {
        listenerJoinWifi.onStateChanged(true);
        try {
          context.unregisterReceiver(this);
          context.unregisterReceiver(scanAPTXVNetworks);
        } catch (IllegalArgumentException e) {
          System.out.println(
              "There was an error while trying to unregister the wifireceiver and the wifireceiverforconnectingwifi");
        }
      }
    }
  };

  private ConnectionManager(Context context, SharedPreferences sharedPreferences,
      WifiManager wifimanager, HotspotControlCounter hotspotControlCounter, GroupParser groupParser,
      GroupValidator groupValidator) {
    this.context = context;
    this.wifimanager = wifimanager;
    prefs = sharedPreferences;
    this.hotspotControlCounter = hotspotControlCounter;
    this.groupParser = groupParser;
    this.groupValidator = groupValidator;
  }

  public static ConnectionManager getInstance(Context context) {
    if (instance == null) {
      SharedPreferences defaultSharedPreferences =
          PreferenceManager.getDefaultSharedPreferences(context);
      instance = new ConnectionManager(context, defaultSharedPreferences,
          (WifiManager) context.getSystemService(Context.WIFI_SERVICE),
          new HotspotControlCounter(defaultSharedPreferences), new GroupParser(),
          new GroupValidator());
    }
    return instance;
  }

  public void start(WifiStateListener listener) {
    prefs.edit().putBoolean("wifiOnStart", this.wifimanager.isWifiEnabled()).apply();

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
    WifiManager wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    WifiConfiguration wc = DataHolder.getInstance().getWcOnJoin();

    Method[] wmMethods = wifimanager.getClass()
        .getDeclaredMethods();   //Get all declared methods in WifiManager class
    boolean methodFound = false;
    for (Method method : wmMethods) {
      if (method.getName().equals("setWifiApEnabled")) {

        try {
          method.invoke(wifimanager, wc, enable);
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          e.printStackTrace();
        }
      }
    }
  }

  public int enableHotspot(String randomAlphaNum, String deviceName) {
    if (wifimanager == null) {
      wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }
    if (wifimanager.isWifiEnabled()) {
      wifimanager.setWifiEnabled(false);
    }
    Method[] wmMethods = wifimanager.getClass()
        .getDeclaredMethods();   //Get all declared methods in WifiManager class
    boolean methodFound = false;
    for (Method method : wmMethods) {
      if (method.getName().equals("getWifiApConfiguration")) {
        System.out.println("saving old ssid .");
        try {
          WifiConfiguration config = (WifiConfiguration) method.invoke(wifimanager);
          System.out.println("THE ACTUAL SSID IS : : : " + config.SSID);
          DataHolder.getInstance().setWcOnJoin(config);
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          e.getCause().printStackTrace();
          e.printStackTrace();
        }
      }
      if (method.getName().equals("setWifiApEnabled")) {
        methodFound = true;
        WifiConfiguration netConfig = new WifiConfiguration();
        netConfig.SSID = "" + "APTXV" + hotspotControlCounter.incrementAndGetStringCounter()
            + "_"
            + randomAlphaNum
            + "_"
            + deviceName + getRandomID() + "";
        netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
        netConfig.preSharedKey = "passwordAptoide";
        netConfig.status = WifiConfiguration.Status.ENABLED;
        netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        netConfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        try {
          boolean apstatus = (Boolean) method.invoke(wifimanager, netConfig, true);
          for (Method isWifiApEnabledmethod : wmMethods) {
            if (isWifiApEnabledmethod.getName().equals("isWifiApEnabled")) {
              while (!(Boolean) isWifiApEnabledmethod.invoke(wifimanager)) {
              }
              for (Method method1 : wmMethods) {
                if (method1.getName().equals("getWifiApState")) {
                  int apstate;
                  apstate = (Integer) method1.invoke(wifimanager);
                }
              }
            }
          }
          if (apstatus) {
            return ConnectionManager.SUCCESS_HOTSPOT_CREATION;
          } else {
            return ConnectionManager.FAILED_TO_CREATE_HOTSPOT;
          }
        } catch (IllegalArgumentException e) {
          e.printStackTrace();
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        } catch (InvocationTargetException e) {
          e.getCause().printStackTrace();
          e.printStackTrace();
        }
      }
    }
    return ConnectionManager.ERROR_UNKNOWN;
  }

  private String getRandomID() {
    String id = prefs.getString(UNIQUE_ID, "default");
    if (id.equals("default")) {
      id = Utils.generateRandomAlphanumericString(2);
      prefs.edit().putString(UNIQUE_ID, id).apply();
    }
    return id;
  }

  public int joinHotspot(String chosenHotspot, boolean shouldReconnect) {

    WifiConfiguration conf = new WifiConfiguration();
    System.out.println("chosen hotspot is : " + chosenHotspot);
    conf.SSID = "\"" + chosenHotspot + "\"";
    conf.preSharedKey = "\"passwordAptoide\"";
    conf.hiddenSSID = true;
    conf.status = WifiConfiguration.Status.ENABLED;
    conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

    int netid = wifimanager.addNetwork(conf);
    Log.e("O net id meu esta a : ", "netid : " + netid);

    List<WifiConfiguration> list = wifimanager.getConfiguredNetworks();
    if (list != null) {
      for (WifiConfiguration i : list) {
        Log.i("config network", "list of config networks is : " + i.toString());
        if (i.SSID != null && i.SSID.equals("\"" + chosenHotspot + "\"")) {
          Log.d("cONFIG nETOWKRS", "Found List of COnfigured Networks APTXV");
          try {
            boolean b = wifimanager.disconnect();
            System.out.println("o boolean do disconnect " + b);
            try {
              Thread.sleep(800);
            } catch (InterruptedException e) {
            }
            boolean enab = wifimanager.enableNetwork(i.networkId, true);
            System.out.print(
                "i.networkId " + i.networkId + "\n" + "o net id do add esta a : " + netid);
            System.out.println("o boolean do resetHotspot : " + enab);

            try {
              Thread.sleep(2000);
            } catch (InterruptedException e) {
            }

            if (shouldReconnect) {
              boolean recon = wifimanager.reconnect();
              System.out.println("O boolean do reconnect ta a : " + recon);
              try {
                Thread.sleep(2000);
              } catch (InterruptedException e) {
              }

              if (recon) {
                System.out.println("Correctly joined the network");
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
        System.out.println("Trying to remove a APTXV network.");
        System.out.println("This one is i : " + i.SSID);
        System.out.println("SEPARATED 0 is : " + tmp);
        if (tmp.contains("APTXV")) {
          System.out.println("TRying to remove a network");
          boolean remove = wifimanager.removeNetwork(i.networkId);
          System.out.println("boolean from remove network is : " + remove);
        } else {
          System.out.println("tmp is not aptxV can not remove this network;");
        }
      }
    }
  }

  public boolean isMobileDataOn() {
    boolean isOn = false;
    System.out.println("Inside the getslots");
    TelephonyManager mTelephonyManager =
        (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      List<SubscriptionInfo> aux =
          SubscriptionManager.from(context).getActiveSubscriptionInfoList();
      if (aux != null) {
        for (int i = 0; i < aux.size(); i++) {
          try {
            Method getDataEnabled =
                mTelephonyManager.getClass().getMethod("getDataEnabled", int.class);
            if ((boolean) getDataEnabled.invoke(mTelephonyManager, aux.get(i).getSimSlotIndex())) {
              isOn = true;
            }
          } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            isOn = false;
          }
        }
      }
    } else {
      isMobileDataEnabled();
    }
    return isOn;
  }

  public Boolean isMobileDataEnabled() {
    Object connectivityService = context.getSystemService(CONNECTIVITY_SERVICE);
    ConnectivityManager cm = (ConnectivityManager) connectivityService;

    try {
      Class<?> c = Class.forName(cm.getClass().getName());
      Method m = c.getDeclaredMethod("getMobileDataEnabled");
      m.setAccessible(true);
      return (Boolean) m.invoke(cm);
    } catch (Exception e) {
      if (e instanceof NoSuchMethodException) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          boolean isDataEnabled =
              Settings.Global.getInt(context.getContentResolver(), "mobile_data", 0) == 1;
          return isDataEnabled;
        }
      }
      e.printStackTrace();
      return false;
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
    System.out.println("Going to cancel the tasks");
    if (clients != null) {
      clients.clear();
    }
    this.listenerJoinWifi = null;
    this.listenerActivateButtons = null;
    this.clientsConnectedListener = null;
    this.inactivityListener = null;
    try {
      context.unregisterReceiver(activateButtonsReceiver);
      context.unregisterReceiver(scanAPTXVNetworks);
      context.unregisterReceiver(connectingWifi);
    } catch (IllegalArgumentException e) {
    }
    if (scanner != null) {
      scanner.cancel();
      scanner.purge();
      scanner = null;
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
      System.out.println("Recovering wifi state, it was on before. ");
    } else {
      wifimanager.setWifiEnabled(false);
    }
  }

  //public void reconnectToGroup(final String group) {
  //  executor.execute(new Runnable() {
  //    @Override public void run() {
  //      joinHotspot(group, true);
  //    }
  //  });
  //}

  //private boolean isGhost(Group group, ArrayList<Group> clients) {
  //  String ssidDeviceID = ssid.split("_")[2];
  //  for (int i = 0; i < clients.size(); i++) {
  //    String tmp = clients.get(i);
  //    String deviceID = tmp.split("_")[2];
  //    if (deviceID.equals(ssidDeviceID)) {
  //
  //    }
  //  }
  //  return true;
  //}
  //
  //private void removeGhost(String tmp) {
  //
  //}

  public void enableWifi(boolean enable) {
    if (wifimanager == null) {
      wifimanager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
    }
    wifimanager.setWifiEnabled(enable);
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
