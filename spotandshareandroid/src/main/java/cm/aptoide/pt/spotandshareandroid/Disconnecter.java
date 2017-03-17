package cm.aptoide.pt.spotandshareandroid;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

/**
 * Created by filipe on 14-03-2017.
 */

public class Disconnecter {

  private Context context;
  private DisconnectListener listener;
  private IntentFilter intentFilter;
  private BroadcastReceiver receiver = new BroadcastReceiver() {
    @Override public void onReceive(Context context, Intent intent) {
      if (intent.getAction() != null && intent.getAction().equals("SERVER_DISCONNECT")) {
        if (listener != null) {
          listener.onServerDisconnected();
        }
      } else if (intent.getAction() != null && intent.getAction().equals("CLIENT_DISCONNECT")) {
        if (listener != null) {
          listener.onClientDisconnected();
        }
      }
    }
  };

  public Disconnecter(Context context) {
    this.context = context;
    this.intentFilter = new IntentFilter();
    intentFilter.addAction("SERVER_DISCONNECT");
    intentFilter.addAction("CLIENT_DISCONNECT");
  }

  public void listenToDisconnect(DisconnectListener listener) {
    this.listener = listener;
    context.registerReceiver(receiver,intentFilter);
  }

  public void stop(){
    if (listener != null) {
      this.listener = null;
      try {
        context.unregisterReceiver(receiver);
      } catch (IllegalArgumentException e) {
      }
    }
  }
  interface DisconnectListener {
    void onServerDisconnected();

    void onClientDisconnected();
  }
}
