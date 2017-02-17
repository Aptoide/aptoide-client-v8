package cm.aptoide.pt.shareapppsandroid;

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

  public ApplicationReceiver(Context context, boolean isHotspot, int port, String targetIPAddress,
      String nickname) {
    this.context = context;
    this.isHotspot = isHotspot;
    this.port = port;
    this.targetIPAddress = targetIPAddress;
    this.nickname = nickname;
    receiveFilter = new IntentFilter();
    receiveFilter.addAction("RECEIVEAPP");
  }

  public void startListening(ReceiveAppListener receiveAppListener) {
    this.listener = receiveAppListener;
    Intent intent = generateIntentToStartReceiving();
    context.startService(intent);
    receive = new BroadcastReceiver() {
      @Override public void onReceive(Context context, Intent intent) {
        boolean finishedReceiving = intent.getBooleanExtra("FinishedReceiving", false);
        String appName = intent.getStringExtra("appName");
        if (finishedReceiving) {
          String tmpFilePath = intent.getStringExtra("tempFilePath");
          boolean needResend = intent.getBooleanExtra("needReSend", false);
          listener.onReceivedApp(appName, tmpFilePath, needResend);
        } else {
          listener.onStartedReceiving(appName);
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
      DataHolder.getInstance().createConnectedClientsList();
    } else {
      //            String aux = calculateActualIP();
      //            if (!targetIPAddress.equals(aux)) {
      //                targetIPAddress = aux;
      //            }
      System.out.println("Will start a client service");

      receiveIntent = new Intent(context, HighwayClientService.class);
      receiveIntent.putExtra("targetIP", targetIPAddress);
    }
    receiveIntent.putExtra("nickname", nickname);
    receiveIntent.putExtra("port", port);
    receiveIntent.putExtra("ExternalStoragePath",
        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());
    receiveIntent.putExtra("isHotspot", isHotspot);
    receiveIntent.putExtra("isOutsideShare", outsideShare);
    receiveIntent.setAction("RECEIVE");
    return receiveIntent;
  }

  public void setTargetIPAddress(String targetIPAddress) {
    this.targetIPAddress = targetIPAddress;
  }

  public void removeListener() {
    if (listener != null) {
      this.listener = null;
      //unregister receiver
      context.unregisterReceiver(receive);
    }
  }

  interface ReceiveAppListener {

    void onStartedReceiving(String appName);

    void onReceivedApp(String appName, String tmpFilePath, boolean needResend);
  }
}
