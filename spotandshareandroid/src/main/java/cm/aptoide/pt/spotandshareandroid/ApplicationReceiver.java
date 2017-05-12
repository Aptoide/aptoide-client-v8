package cm.aptoide.pt.spotandshareandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;

/**
 * Created by filipegoncalves on 10-02-2017.
 * <p>
 * This class is to intermeditate the connection between the view and the service (receive) part.
 */

public class ApplicationReceiver {

  private Context context;
  private boolean isHotspot;
  private String nickname;
  private int port;
  private String targetIPAddress;
  private boolean outsideShare;
  private BroadcastReceiver receive;
  private IntentFilter receiveFilter;
  private ReceiveAppListener listener;
  private String autoShareFilePath;

  public ApplicationReceiver(Context context, boolean isHotspot, int port, String targetIPAddress,
      String nickname) {
    this.context = context;
    this.isHotspot = isHotspot;
    this.port = port;
    this.targetIPAddress = targetIPAddress;
    this.nickname = nickname;
    receiveFilter = new IntentFilter();
    receiveFilter.addAction("RECEIVEAPP");
    receiveFilter.addAction("ERRORRECEIVING");
    receiveFilter.addAction("SERVER_LEFT");
  }

  public ApplicationReceiver(Context context, boolean isHotspot, int port, String targetIPAddress,
      String nickname, String autoShareFilePath) {
    this(context, isHotspot, port, targetIPAddress, nickname);
    this.autoShareFilePath = autoShareFilePath;
  }

  public void startListening(ReceiveAppListener receiveAppListener) {
    this.listener = receiveAppListener;
    Intent intent = generateIntentToStartReceiving();
    context.startService(intent);
    receive = new BroadcastReceiver() {
      @Override public void onReceive(Context context, Intent intent) {
        if (intent.getAction() != null && intent.getAction()
            .equals("RECEIVEAPP")) {
          boolean finishedReceiving = intent.getBooleanExtra("FinishedReceiving", false);
          String appName = intent.getStringExtra("appName");
          if (finishedReceiving) {
            String filePath = intent.getStringExtra("filePath");
            String packageName = intent.getStringExtra("packageName");
            boolean needResend = intent.getBooleanExtra("needReSend", false);
            listener.onReceivedApp(appName, filePath, needResend);
          } else {
            listener.onStartedReceiving(appName);
          }
        } else if (intent.getAction() != null && intent.getAction()
            .equals("ERRORRECEIVING")) {
          listener.onErrorReceiving();
        } else if (intent.getAction() != null && intent.getAction()
            .equals("SERVER_LEFT")) {
          listener.onServerLeft();
        }
      }
    };
    context.registerReceiver(receive, receiveFilter);
  }

  public Intent generateIntentToStartReceiving() {
    Intent receiveIntent = null;
    if (isHotspot) {
      receiveIntent = new Intent(context, HighwayServerService.class);
      System.out.println("Will start a server service");
    } else {

      System.out.println("Will start a client service");

      receiveIntent = new Intent(context, HighwayClientService.class);
      receiveIntent.putExtra("targetIP", targetIPAddress);
    }
    receiveIntent.putExtra("nickname", nickname);
    receiveIntent.putExtra("port", port);
    receiveIntent.putExtra("ExternalStoragePath",
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            .toString());
    receiveIntent.putExtra("isHotspot", isHotspot);
    receiveIntent.putExtra("isOutsideShare", outsideShare);
    receiveIntent.setAction("RECEIVE");

    if (autoShareFilePath != null) {
      receiveIntent.putExtra("autoShareFilePath", autoShareFilePath);
    }

    return receiveIntent;
  }

  public void setTargetIPAddress(String targetIPAddress) {
    this.targetIPAddress = targetIPAddress;
  }

  public void stop() {
    removeListener();
  }

  public void removeListener() {
    if (listener != null) {
      this.listener = null;
      try {
        context.unregisterReceiver(receive);
      } catch (IllegalArgumentException e) {
      }
    }
  }

  interface ReceiveAppListener {

    void onStartedReceiving(String appName);

    void onReceivedApp(String appName, String filePath, boolean needResend);

    void onErrorReceiving();

    void onServerLeft();
  }
}
