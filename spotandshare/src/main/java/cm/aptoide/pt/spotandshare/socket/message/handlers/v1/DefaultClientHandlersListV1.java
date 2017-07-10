package cm.aptoide.pt.spotandshare.socket.message.handlers.v1;

import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshare.socket.interfaces.FileLifecycleProvider;
import cm.aptoide.pt.spotandshare.socket.interfaces.SocketBinder;
import cm.aptoide.pt.spotandshare.socket.message.Message;
import cm.aptoide.pt.spotandshare.socket.message.MessageHandler;
import cm.aptoide.pt.spotandshare.socket.message.client.AptoideMessageClientSocket;
import cm.aptoide.pt.spotandshare.socket.message.interfaces.AndroidAppInfoAccepter;
import cm.aptoide.pt.spotandshare.socket.message.interfaces.StorageCapacity;
import java.util.LinkedList;
import java.util.List;

import static cm.aptoide.pt.spotandshare.socket.message.handlers.v1.HandlersFactoryV1.HostLeftMessageHandler;
import static cm.aptoide.pt.spotandshare.socket.message.handlers.v1.HandlersFactoryV1.ReceiveApkHandler;
import static cm.aptoide.pt.spotandshare.socket.message.handlers.v1.HandlersFactoryV1.SendApkHandler;
import static cm.aptoide.pt.spotandshare.socket.message.handlers.v1.HandlersFactoryV1.ServerLeftHandler;

/**
 * Created by neuro on 02-02-2017.
 */

public class DefaultClientHandlersListV1 {

  public static List<MessageHandler<? extends Message>> create(String rootDir,
      StorageCapacity storageCapacity, FileLifecycleProvider<AndroidAppInfo> fileLifecycleProvider,
      AptoideMessageClientSocket aptoideMessageClientController, SocketBinder socketBinder,
      AndroidAppInfoAccepter androidAppInfoAccepter) {
    List<MessageHandler<? extends Message>> messageHandlers = new LinkedList<>();

    messageHandlers.add(new SendApkHandler(fileLifecycleProvider));
    messageHandlers.add(
        new ReceiveApkHandler(rootDir, storageCapacity, fileLifecycleProvider, socketBinder,
            androidAppInfoAccepter));
    messageHandlers.add(new HostLeftMessageHandler(aptoideMessageClientController));
    messageHandlers.add(new ServerLeftHandler(aptoideMessageClientController));

    return messageHandlers;
  }
}
