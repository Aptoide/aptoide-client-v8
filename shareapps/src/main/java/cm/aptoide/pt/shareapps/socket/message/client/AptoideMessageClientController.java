package cm.aptoide.pt.shareapps.socket.message.client;

import cm.aptoide.pt.shareapps.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.shareapps.socket.interfaces.FileClientLifecycle;
import cm.aptoide.pt.shareapps.socket.interfaces.FileServerLifecycle;
import cm.aptoide.pt.shareapps.socket.message.AptoideMessageController;
import cm.aptoide.pt.shareapps.socket.message.HandlersFactory;
import cm.aptoide.pt.shareapps.socket.message.Message;
import cm.aptoide.pt.shareapps.socket.message.interfaces.Sender;
import cm.aptoide.pt.shareapps.socket.message.interfaces.StorageCapacity;

/**
 * Created by neuro on 29-01-2017.
 */
public class AptoideMessageClientController extends AptoideMessageController
    implements Sender<Message> {

  public AptoideMessageClientController(String rootDir, StorageCapacity storageCapacity,
      FileServerLifecycle<AndroidAppInfo> serverLifecycle,
      FileClientLifecycle<AndroidAppInfo> fileClientLifecycle) {
    super(HandlersFactory.newDefaultClientHandlersList(rootDir, storageCapacity, serverLifecycle,
        fileClientLifecycle), fileClientLifecycle);
  }
}
