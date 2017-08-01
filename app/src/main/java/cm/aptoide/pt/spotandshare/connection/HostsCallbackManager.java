package cm.aptoide.pt.spotandshare.connection;

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
    Intent i = new Intent();

    if (hostList.size() == 2 && autoShareFilepath != null) {
      i.setAction("AUTO_SHARE_SEND");
      i.putExtra("autoShareFilePath", autoShareFilepath);
      context.sendBroadcast(i);
      autoShareFilepath = null;
    } else if (hostList.size() >= 2) {
      i.setAction("SHOW_SEND_BUTTON");
      context.sendBroadcast(i);
    } else {
      i.setAction("HIDE_SEND_BUTTON");
      context.sendBroadcast(i);
    }
  }
}