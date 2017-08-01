package cm.aptoide.pt.model.v7.base;

public class Info {

  private Status status;
  private Time time;

  public Info() {
  }

  public Info(Status status, Time time) {
    this.status = status;
    this.time = time;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public Time getTime() {
    return time;
  }

  public void setTime(Time time) {
    this.time = time;
  }

  public enum Status {
    OK, QUEUED, FAIL
  }
}
