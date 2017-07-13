package cm.aptoide.pt.spotandshare.socket.message;

import cm.aptoide.pt.spotandshare.socket.message.interfaces.Sender;
import cm.aptoide.pt.spotandshare.socket.message.messages.v1.AckMessage;

/**
 * Created by neuro on 19-01-2017.
 */
public abstract class MessageHandler<T extends Message> {

  final Class<? extends Message> aClass;

  public MessageHandler(Class<? extends Message> aClass) {
    this.aClass = aClass;
  }

  abstract public void handleMessage(T message, Sender<Message> messageSender);

  protected void sendAck(Sender<Message> messageSender) {
    messageSender.send(new AckMessage(messageSender.getHost(), true));
  }

  protected void sendAck(Sender<Message> messageSender, boolean success) {
    messageSender.send(new AckMessage(messageSender.getHost(), success));
  }
}
