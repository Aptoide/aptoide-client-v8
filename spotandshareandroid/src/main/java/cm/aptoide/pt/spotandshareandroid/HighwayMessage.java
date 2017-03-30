package cm.aptoide.pt.spotandshareandroid;

/**
 * Created by filipegoncalves on 02-12-2016.
 */

public class HighwayMessage {

  private Status status;
  private String sender;
  private Type messageType;

  public HighwayMessage(String sender, Type type) {
    this.sender = sender;
    this.messageType = type;
    this.status = Status.HELLO;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public Type getMessageType() {
    return messageType;
  }

  public void setMessageType(Type messageType) {
    this.messageType = messageType;
  }

  public enum Status {

    HELLO,

    CHECKING_AVAILABLITY,//space

    AVAILABLE,

    UNAVAILABLE,

    SENDING_APP,

    RECEIVING_APP,

    SENDING_OBBS,

    RECEIVING_OBBS,

    WAITING_CONFIRMATION,

    CONFIRMED,

    ERROR
  }

  public enum Type {
    RECEIVE, SEND
  }
}
