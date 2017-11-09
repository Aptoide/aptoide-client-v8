package cm.aptoide.pt.search.websocket2;

public final class SocketEvent {

  private final Status status;
  private final byte[] data;

  SocketEvent(Status status, byte[] data) {
    this.status = status;
    this.data = data;
  }

  SocketEvent(Status status) {
    this(status, null);
  }

  public Status getStatus() {
    return status;
  }

  public byte[] getData() {
    return data;
  }

  public boolean hasData() {
    return data != null;
  }

  public enum Status {
    OPEN, MESSAGE, CLOSING, CLOSED, FAILURE
  }
}
