package cm.aptoide.pt.spotandshare.socket.message;

import cm.aptoide.pt.spotandshare.socket.message.interfaces.Sender;

/**
 * Created by neuro on 19-01-2017.
 */
public abstract class MessageHandler<T extends Message> {

  public final Class<? extends Message> aClass;

  public MessageHandler(Class<? extends Message> aClass) {
    this.aClass = aClass;
  }

  abstract public void handleMessage(T message, Sender<Message> messageSender);
}
