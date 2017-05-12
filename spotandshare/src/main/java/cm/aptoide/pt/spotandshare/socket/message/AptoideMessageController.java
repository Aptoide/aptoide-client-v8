package cm.aptoide.pt.spotandshare.socket.message;

import cm.aptoide.pt.spotandshare.socket.Print;
import cm.aptoide.pt.spotandshare.socket.entities.Host;
import cm.aptoide.pt.spotandshare.socket.exception.ServerLeftException;
import cm.aptoide.pt.spotandshare.socket.interfaces.OnError;
import cm.aptoide.pt.spotandshare.socket.message.interfaces.Sender;
import cm.aptoide.pt.spotandshare.socket.message.messages.v1.AckMessage;
import cm.aptoide.pt.spotandshare.socket.message.messages.v1.ExitMessage;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import lombok.Getter;

/**
 * Created by neuro on 29-01-2017.
 */
public abstract class AptoideMessageController implements Sender<Message> {

  public static final long ACK_TIMEOUT = 5000;
  private static final String TAG = AptoideMessageController.class.getSimpleName();
  private final HashMap<Class, MessageHandler> messageHandlersMap;
  private OnError<IOException> onError;
  private ObjectOutputStream objectOutputStream;
  private ObjectInputStream objectInputStream;
  private LinkedBlockingQueue<AckMessage> ackMessages = new LinkedBlockingQueue<>();
  @Getter private Host host;
  @Getter private Host localhost;
  private Socket socket;
  @Getter private boolean connected;

  public AptoideMessageController(List<MessageHandler<? extends Message>> messageHandlers,
      OnError<IOException> onError) {
    this.messageHandlersMap = buildMessageHandlersMap(messageHandlers);
    this.onError = onError;
  }

  protected HashMap<Class, MessageHandler> buildMessageHandlersMap(
      List<MessageHandler<? extends Message>> messageHandlers) {

    HashMap<Class, MessageHandler> messageHandlersMap = new HashMap<>();
    for (MessageHandler handler : messageHandlers) {
      messageHandlersMap.put(handler.aClass, handler);
    }

    return messageHandlersMap;
  }

  public void onConnect(Socket socket) throws IOException {
    this.socket = socket;
    objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
    objectInputStream = new ObjectInputStream(socket.getInputStream());
    localhost = Host.fromLocalhost(socket);
    host = Host.from(socket);
    connected = true;
    startListening(objectInputStream);
  }

  private void startListening(ObjectInputStream objectInputStream) {
    try {
      while (true) {
        Object o = objectInputStream.readObject();
        Print.d(TAG, "startListening: " + Thread.currentThread()
            .getId() + ": Received input object. " + o.getClass()
            .getSimpleName());
        Message message = (Message) o;
        handle(message);
      }
    } catch (IOException e) {
      if (onError != null) {
        if (isServerLeft(e)) {
          onError.onError(new ServerLeftException(e));
        } else {
          onError.onError(e);
        }
      }
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
  }

  private void handle(Message message) {
    if (message instanceof AckMessage) {
      try {
        ackMessages.put((AckMessage) message);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    } else {
      if (canHandle(message)) {
        Print.d(TAG, "handle: Handling message " + message + ", " + message.getClass()
            .getSimpleName());
        messageHandlersMap.get(message.getClass())
            .handleMessage(message, this);
      } else {
        throw new IllegalArgumentException("Can't handle messages of type " + message.getClass()
            .getSimpleName());
      }
    }
  }

  private boolean isServerLeft(IOException e) {
    return e instanceof EOFException;
  }

  private boolean canHandle(Message message) {
    return messageHandlersMap.containsKey(message.getClass());
  }

  public void exit() {
    try {
      disable();
      sendWithAck(new ExitMessage(getLocalhost()));
      if (socket != null && !socket.isClosed()) {
        Print.d(TAG, "Closing socket " + socket);
        socket.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public void disable() {
    onError = null;
  }

  public synchronized boolean sendWithAck(Message message) throws InterruptedException {

    Print.d(TAG, "sendWithAck() called with: message = [" + message + "], " + message.getClass()
        .getSimpleName());

    if (!isConnected()) {
      Print.d(TAG, "sendWithAck: " + message.getClass()
          .getSimpleName() + " not connected!");
      return false;
    }

    // TODO: 02-02-2017 neuro no ack waiting lol
    AckMessage ackMessage = null;
    Print.d(TAG, "sendWithAck: " + Thread.currentThread()
        .getId() + ": Sending message with ack: " + message + ", " + message.getClass()
        .getSimpleName());
    try {
      objectOutputStream.writeObject(message);
      ackMessage = ackMessages.poll(ACK_TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      Print.d(TAG, "sendWithAck: " + Thread.currentThread()
          .getId() + ": Failed to receive ack!");
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

    Print.d(TAG, "sendWithAck: " + Thread.currentThread()
        .getId() + ": Received ack: " + ackMessage);

    return ackMessage != null && ackMessage.isSuccess();
  }

  @Override public synchronized void send(Message message) {
    Print.d(TAG, "send() called with: message = [" + message + "]");

    if (!isConnected()) {
      Print.d(TAG, "send: " + message.getClass()
          .getSimpleName() + " not connected!");
      return;
    }

    Print.d(TAG, "send: " + Thread.currentThread()
        .getId() + ": Sending message: " + message + ", " + message.getClass()
        .getSimpleName());
    try {
      if (objectOutputStream != null) {
        objectOutputStream.writeObject(message);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
