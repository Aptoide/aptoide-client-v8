package cm.aptoide.pt.spotandshare.socket.message.handlers.v1;

import cm.aptoide.pt.spotandshare.socket.message.Message;
import cm.aptoide.pt.spotandshare.socket.message.MessageHandler;
import cm.aptoide.pt.spotandshare.socket.message.interfaces.Sender;
import cm.aptoide.pt.spotandshare.socket.message.messages.v1.AndroidAppInfoMessage;
import cm.aptoide.pt.spotandshare.socket.message.server.AptoideMessageServerSocket;
import java.util.LinkedList;
import java.util.List;

import static cm.aptoide.pt.spotandshare.socket.message.handlers.v1.HandlersFactoryV1.ExitMessageHandler;
import static cm.aptoide.pt.spotandshare.socket.message.handlers.v1.HandlersFactoryV1.RequestPermissionToSendHandler;

/**
 * Created by neuro on 02-02-2017.
 */

public class DefaultServerHandlersListV1 {

  public static List<MessageHandler<? extends Message>> create(
      AptoideMessageServerSocket aptoideMessageServerSocket) {
    List<MessageHandler<? extends Message>> messageHandlers = new LinkedList<>();

    MessageHandler<AndroidAppInfoMessage> messageHandler =
        new MessageHandler<AndroidAppInfoMessage>(AndroidAppInfoMessage.class) {
          @Override
          public void handleMessage(AndroidAppInfoMessage message, Sender<Message> messageSender) {
          }
        };

    messageHandlers.add(messageHandler);
    messageHandlers.add(new RequestPermissionToSendHandler(aptoideMessageServerSocket));
    messageHandlers.add(new ExitMessageHandler(aptoideMessageServerSocket));

    return messageHandlers;
  }
}
