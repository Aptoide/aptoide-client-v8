package cm.aptoide.pt.spotandshareandroid;

import android.content.Context;
import android.content.Intent;
import cm.aptoide.pt.spotandshare.socket.entities.Host;
import cm.aptoide.pt.spotandshare.socket.interfaces.HostsChangedCallback;
import java.util.List;

/**
 * Created by filipe on 21-03-2017.
 */

public class HostsCallbackManager implements HostsChangedCallback {

  private final Context context;

  public HostsCallbackManager(Context context) {
    this.context = context;
  }

  @Override public void hostsChanged(List<Host> hostList) {
    System.out.println("hostsChanged() called with: " + "hostList = [" + hostList + "]");
    DataHolder.getInstance().setConnectedClients(hostList);
    Intent i = new Intent();
    if (hostList.size() >= 2) {
      System.out.println("sending broadcast of show_send_button");
      i.setAction("SHOW_SEND_BUTTON");
      context.sendBroadcast(i);
    } else {
      i.setAction("HIDE_SEND_BUTTON");
      context.sendBroadcast(i);
    }
  }
}