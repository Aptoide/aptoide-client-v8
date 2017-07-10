package cm.aptoide.pt.spotandshare.socket.message.interfaces;

/**
 * Created by neuro on 10-07-2017.
 */

public interface Accepter<T> {

  T getMeta();

  void accept();
}
