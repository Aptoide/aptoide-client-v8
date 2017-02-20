package cm.aptoide.pt.shareapps.socket.message;

import cm.aptoide.pt.shareapps.socket.entities.AndroidAppInfo;
import cm.aptoide.pt.shareapps.socket.entities.FileInfo;
import cm.aptoide.pt.shareapps.socket.entities.Host;
import cm.aptoide.pt.shareapps.socket.file.ShareAppsClientSocket;
import cm.aptoide.pt.shareapps.socket.file.ShareAppsServerSocket;
import cm.aptoide.pt.shareapps.socket.interfaces.FileClientLifecycle;
import cm.aptoide.pt.shareapps.socket.interfaces.FileServerLifecycle;
import cm.aptoide.pt.shareapps.socket.message.interfaces.Sender;
import cm.aptoide.pt.shareapps.socket.message.interfaces.StorageCapacity;
import cm.aptoide.pt.shareapps.socket.message.messages.AckMessage;
import cm.aptoide.pt.shareapps.socket.message.messages.AndroidAppInfoMessage;
import cm.aptoide.pt.shareapps.socket.message.messages.ExitMessage;
import cm.aptoide.pt.shareapps.socket.message.messages.ReceiveApk;
import cm.aptoide.pt.shareapps.socket.message.messages.RequestPermissionToSend;
import cm.aptoide.pt.shareapps.socket.message.messages.SendApk;
import cm.aptoide.pt.shareapps.socket.message.server.AptoideMessageServerSocket;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by neuro on 02-02-2017.
 */

public class HandlersFactory {

  @Deprecated public static List<MessageHandler<? extends Message>> newDefaultServerHandlersList(
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

  public static List<MessageHandler<? extends Message>> newDefaultClientHandlersList(String rootDir,
      StorageCapacity storageCapacity, FileServerLifecycle<AndroidAppInfo> serverLifecycle,
      FileClientLifecycle<AndroidAppInfo> fileClientLifecycle) {
    List<MessageHandler<? extends Message>> messageHandlers = new LinkedList<>();

    messageHandlers.add(new SendApkHandler(serverLifecycle));
    messageHandlers.add(new ReceiveApkHandler(rootDir, storageCapacity, fileClientLifecycle));

    return messageHandlers;
  }

  private static class RequestPermissionToSendHandler
      extends MessageHandler<RequestPermissionToSend> {

    private final AptoideMessageServerSocket messageServerSocket;

    public RequestPermissionToSendHandler(AptoideMessageServerSocket messageServerSocket) {
      super(RequestPermissionToSend.class);
      this.messageServerSocket = messageServerSocket;
    }

    @Override public void handleMessage(RequestPermissionToSend requestPermissionToSend,
        Sender<Message> messageSender) {
      messageServerSocket.requestPermissionToSendApk(requestPermissionToSend);
    }
  }

  private static class SendApkHandler extends MessageHandler<SendApk> {

    private final FileServerLifecycle<AndroidAppInfo> serverLifecycle;

    public SendApkHandler(FileServerLifecycle<AndroidAppInfo> serverLifecycle) {
      super(SendApk.class);
      this.serverLifecycle = serverLifecycle;
    }

    @Override public void handleMessage(SendApk sendApkMessage, Sender<Message> messageSender) {
      new ShareAppsServerSocket(sendApkMessage.getServerPort(), sendApkMessage.getAndroidAppInfo(),
          serverLifecycle, 5000).startAsync();
      messageSender.send(new AckMessage(messageSender.getHost()));
      // TODO: 03-02-2017 neuro maybe a good ideia to stop the server somewhat :)
    }
  }

  private static class ReceiveApkHandler extends MessageHandler<ReceiveApk> {

    static AtomicInteger dir = new AtomicInteger('a');
    private final String root;
    private final StorageCapacity storageCapacity;
    private final FileClientLifecycle<AndroidAppInfo> fileClientLifecycle;

    public ReceiveApkHandler(String root, StorageCapacity storageCapacity,
        FileClientLifecycle<AndroidAppInfo> fileClientLifecycle) {
      super(ReceiveApk.class);
      this.root = root;
      this.storageCapacity = storageCapacity;
      this.fileClientLifecycle = fileClientLifecycle;
    }

    @Override public void handleMessage(ReceiveApk receiveApk, Sender<Message> messageSender) {
      AckMessage ackMessage = new AckMessage(messageSender.getHost());
      AndroidAppInfo androidAppInfo = receiveApk.getAndroidAppInfo();
      if (storageCapacity.hasCapacity(androidAppInfo.getFilesSize())) {
        ackMessage.setSuccess(true);

        messageSender.send(ackMessage);
        Host receiveApkServerHost = receiveApk.getServerHost();

        changeFilesRootDir(androidAppInfo.getFiles());

        new ShareAppsClientSocket(receiveApkServerHost.getIp(), receiveApkServerHost.getPort(),
            androidAppInfo.getFiles(), androidAppInfo, fileClientLifecycle).start();
      } else {
        messageSender.send(ackMessage);
      }
    }

    private void changeFilesRootDir(List<FileInfo> fileInfos) {
      for (FileInfo fileInfo : fileInfos) {
        fileInfo.setParentDirectory(root);
      }
    }
  }

  private static class ExitMessageHandler extends MessageHandler<ExitMessage> {

    private final AptoideMessageServerSocket aptoideMessageServerSocket;

    public ExitMessageHandler(AptoideMessageServerSocket aptoideMessageServerSocket) {
      super(ExitMessage.class);
      this.aptoideMessageServerSocket = aptoideMessageServerSocket;
    }

    @Override public void handleMessage(ExitMessage message, Sender<Message> messageSender) {
      aptoideMessageServerSocket.removeHost(message.getLocalHost());
    }
  }
}
