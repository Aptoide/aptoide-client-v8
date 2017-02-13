package cm.aptoide.pt.shareapps.socket.message.interfaces;

import cm.aptoide.pt.shareapps.socket.entities.Host;

/**
 * Created by neuro on 29-01-2017.
 */

public interface Sender<T> {
  void send(T t);

  Host getHost();
}
