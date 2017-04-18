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
  private String autoShareFilepath;

  public HostsCallbackManager(Context context) {
    this.context = context;
  }

  public HostsCallbackManager(Context context, String autoShareFilepath) {
    this(context);
    this.autoShareFilepath = autoShareFilepath;
  }

  @Override public void hostsChanged(List<Host> hostList) {
    System.out.println("hostsChanged() called with: " + "hostList = [" + hostList + "]");
    Intent i = new Intent();

    if (hostList.size() == 2 && autoShareFilepath != null) {
      i.setAction("AUTO_SHARE_SEND");
      i.putExtra("autoShareFilePath", autoShareFilepath);
      context.sendBroadcast(i);
    } else if (hostList.size() >= 2) {//// FIXME: 17-04-2017 fix the = 2 situation after a autoshare
      System.out.println("sending broadcast of show_send_button");
      i.setAction("SHOW_SEND_BUTTON");
      context.sendBroadcast(i);
    } else {
      i.setAction("HIDE_SEND_BUTTON");
      context.sendBroadcast(i);
    }
  }
}