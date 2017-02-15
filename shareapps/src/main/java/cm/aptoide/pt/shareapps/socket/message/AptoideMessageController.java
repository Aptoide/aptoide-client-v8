package cm.aptoide.pt.shareapps.socket.message;

import cm.aptoide.pt.shareapps.socket.entities.Host;
import cm.aptoide.pt.shareapps.socket.message.interfaces.Sender;
import cm.aptoide.pt.shareapps.socket.message.messages.AckMessage;
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

  private final HashMap<Class, MessageHandler> messageHandlersMap;

  private ObjectOutputStream objectOutputStream;
  private ObjectInputStream objectInputStream;
  private LinkedBlockingQueue<AckMessage> ackMessages = new LinkedBlockingQueue<>();
  @Getter private Host host;

  public AptoideMessageController(List<MessageHandler<? extends Message>> messageHandlers) {
    this.messageHandlersMap = buildMessageHandlersMap(messageHandlers);
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
    objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
    objectInputStream = new ObjectInputStream(socket.getInputStream());
    host = Host.from(socket);

    startListening(objectInputStream);
  }

  private void startListening(ObjectInputStream objectInputStream) {
    try {
      while (true) {
        Object o = objectInputStream.readObject();
        System.out.println(
            Thread.currentThread().getId() + ": Received input object. " + o.getClass()
                .getSimpleName());
        Message message = (Message) o;
        handle(message);
      }
    } catch (IOException e) {
      e.printStackTrace();
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
        messageHandlersMap.get(message.getClass()).handleMessage(message, this);
      } else {
        throw new IllegalArgumentException(
            "Can't handle messages of type " + message.getClass().getSimpleName());
      }
    }
  }

  private boolean canHandle(Message message) {
    return messageHandlersMap.containsKey(message.getClass());
  }

  @Override public synchronized void send(Message message) {
    System.out.println(Thread.currentThread().getId() + ": Sending message: " + message);
    try {
      objectOutputStream.writeObject(message);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public synchronized boolean sendWithAck(Message message) throws InterruptedException {
    // TODO: 02-02-2017 neuro no ack waiting lol
    AckMessage ackMessage = null;
    System.out.println(Thread.currentThread().getId() + ": Sending message with ack: " + message);
    try {
      objectOutputStream.writeObject(message);
      ackMessage = ackMessages.poll(ACK_TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (IOException e) {
      e.printStackTrace();
    }

    System.out.println(Thread.currentThread().getId() + ": Received ack: " + ackMessage);

    return ackMessage != null && ackMessage.isSuccess();
  }
}
