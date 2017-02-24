package cm.aptoide.pt.shareapps.socket.interfaces;

import cm.aptoide.pt.shareapps.socket.entities.Host;
import java.util.List;

/**
 * Created by neuro on 21-02-2017.
 */

public interface HostsChangedCallback {

  void hostsChanged(List<Host> hostList);
}
