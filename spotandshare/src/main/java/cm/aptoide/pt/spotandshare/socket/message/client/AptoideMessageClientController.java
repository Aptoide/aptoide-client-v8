package cm.aptoide.pt.spotandshare.socket.message.client;

import cm.aptoide.pt.spotandshare.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.spotandshare.socket.interfaces.FileClientLifecycle;
import cm.aptoide.pt.spotandshare.socket.interfaces.FileServerLifecycle;
import cm.aptoide.pt.spotandshare.socket.interfaces.SocketBinder;
import cm.aptoide.pt.spotandshare.socket.message.AptoideMessageController;
import cm.aptoide.pt.spotandshare.socket.message.Message;
import cm.aptoide.pt.spotandshare.socket.message.handlers.v1.DefaultClientHandlersListV1;
import cm.aptoide.pt.spotandshare.socket.message.interfaces.Sender;
import cm.aptoide.pt.spotandshare.socket.message.interfaces.StorageCapacity;

/**
 * Created by neuro on 29-01-2017.
 */
public class AptoideMessageClientController extends AptoideMessageController
    implements Sender<Message> {

  public AptoideMessageClientController(AptoideMessageClientSocket aptoideMessageClientSocket,
      String rootDir, StorageCapacity storageCapacity,
      FileServerLifecycle<AndroidAppInfo> serverLifecycle,
      FileClientLifecycle<AndroidAppInfo> fileClientLifecycle, SocketBinder socketBinder) {
    super(DefaultClientHandlersListV1.create(rootDir, storageCapacity, serverLifecycle,
        fileClientLifecycle, aptoideMessageClientSocket, socketBinder), fileClientLifecycle);
  }
}
