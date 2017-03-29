package cm.aptoide.pt.spotandshareandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by filipegoncalves on 09-02-2017.
 *
 * Middle men between presenter and service. Service will instead of sending intents to the view,
 * will send broadcast receivers to here.
 * and then from here to the listener on the presenter and then to the view.
 */

public class ApplicationSender {

  private static ApplicationSender instance;
  private Context context;
  private SendListener sendListener;
  private HostsListener hostsListener;
  private boolean isHotspot;
  private String port;
  private String targetIPAddress;
  private BroadcastReceiver send;
  private BroadcastReceiver hostsReceiver =
      new BroadcastReceiver() {//todo extract to a ClientsManager class
        @Override public void onReceive(Context context, Intent intent) {
          if (intent.getAction() != null && intent.getAction().equals("SHOW_SEND_BUTTON")) {
            System.out.println("Ordering to show send button");
            hostsListener.onAvailableClients();
          } else if (intent.getAction() != null && intent.getAction().equals("HIDE_SEND_BUTTON")) {
            hostsListener.onNoClients();
          }
        }
      };
  private IntentFilter intentFilter;
  private IntentFilter hostsFilter;

  public ApplicationSender(Context context, boolean isHotspot) {
    this.context = context;
    this.isHotspot = isHotspot;
    this.intentFilter = new IntentFilter();
    intentFilter.addAction("SENDAPP");
    intentFilter.addAction("ERRORSENDING");
    //if (isHotspot) {
      hostsFilter = new IntentFilter();
      hostsFilter.addAction("SHOW_SEND_BUTTON");
      hostsFilter.addAction("HIDE_SEND_BUTTON");
      context.registerReceiver(hostsReceiver, hostsFilter);
    //}
  }

  public static ApplicationSender getInstance(Context context, boolean isHotspot) {
    if (instance == null) {
      instance = new ApplicationSender(context, isHotspot);
    }
    return instance;
  }

  public static void reset() {
    if (instance != null) {
      instance.removeSendListener();
    }
    instance = null;

  }

  public void removeSendListener() {
    if (sendListener != null) {
      this.sendListener = null;
      try {
        context.unregisterReceiver(send);
      } catch (IllegalArgumentException e) {
      }
    }
  }

  public void sendApp(List<App> selectedApps) {
    Intent intent = generateIntentToSend(selectedApps);
    context.startService(intent);
    if (send == null) {
      send = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) {
          //dps aqui intent.getAction...
          if (intent.getAction() != null && intent.getAction().equals("SENDAPP")) {
            boolean isSent = intent.getBooleanExtra("isSent", false);
            boolean needReSend = intent.getBooleanExtra("needReSend", false);
            String appName = intent.getStringExtra("appName");
            String packageName = intent.getStringExtra("packageName");
            int positionToReSend = intent.getIntExtra("positionToReSend", 100000);

            if (!isSent || needReSend) {
              sendListener.onAppStartingToSend(appName, packageName, needReSend, isSent,
                  positionToReSend);
            } else {
              System.out.println("Application Sender : : : : Sent an App");
              sendListener.onAppSent(appName, needReSend, isSent, false, positionToReSend);
            }
          } else if (intent.getAction() != null && intent.getAction().equals("ERRORSENDING")) {
            sendListener.onErrorSendingApp();
          }
        }
      };
      context.registerReceiver(send, intentFilter);
    }
  }

  public Intent generateIntentToSend(List<App> selectedApps) {
    Intent sendIntent = null;
    if (isHotspot) {
      sendIntent = new Intent(context, HighwayServerService.class);
    } else {
      sendIntent = new Intent(context, HighwayClientService.class);
      sendIntent.putExtra("targetIP", targetIPAddress);
    }
    sendIntent.putExtra("port", port);
    sendIntent.putExtra("isHotspot", isHotspot);

    //        sendIntent.putExtra("fromOutside",false);

    Bundle tmp = new Bundle();
    tmp.putParcelableArrayList("listOfAppsToInstall",
        new ArrayList<Parcelable>(selectedApps));//change listOfAppsToInstall to listOfAppsTOSend
    sendIntent.putExtra("bundle", tmp);
    sendIntent.setAction("SEND");
    return sendIntent;
  }

  public void setSendListener(SendListener sendListener) {
    this.sendListener = sendListener;
  }

  public void setHostsListener(HostsListener hostsListener) {
    this.hostsListener = hostsListener;
  }

  public void reSendApp(List<App> appToResend, int position) {
    Intent intent = generateIntentToSend(appToResend);
    intent.putExtra("positionToReSend", position);
    context.startService(intent);
    send = new BroadcastReceiver() {
      @Override public void onReceive(Context context, Intent intent) {
        boolean isSent = intent.getBooleanExtra("isSent", false);
        boolean needReSend = intent.getBooleanExtra("needReSend", false);
        String appName = intent.getStringExtra("appName");
        String packageName = intent.getStringExtra("packageName");
        int positionToReSend = intent.getIntExtra("positionToReSend", 100000);
        sendListener.onAppSent(appName, needReSend, isSent, false, positionToReSend);
      }
    };
  }

  public void stop() {
    //removeSendListeners();
    //removeHostsListener();
  }

  private void removeHostsListener() {
    if (hostsListener != null) {
      this.hostsListener = null;
      try {
        context.unregisterReceiver(hostsReceiver);
      } catch (IllegalArgumentException e) {
      }
    }
  }

  public interface SendListener {

    void onAppStartingToSend(String appName, String packageName, boolean needReSend, boolean isSent,
        int positionToReSend);

    void onAppSent(String appName, boolean needReSend, boolean isSent, boolean received,
        int positionToReSend);

    void onErrorSendingApp();
  }

  public interface HostsListener {

    void onNoClients();

    void onAvailableClients();
  }
}
