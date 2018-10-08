package cm.aptoide.pt.app.view;

/**
 * Created by D01 on 19/09/2018.
 */

class EditorialEvent {

  private final Type clickType;
  private final String url;

  public EditorialEvent(Type clickType, String url) {

    this.clickType = clickType;
    this.url = url;
  }

  public EditorialEvent(Type clickType) {

    this.clickType = clickType;
    this.url = "";
  }

  public Type getClickType() {
    return clickType;
  }

  public String getUrl() {
    return url;
  }

  public enum Type {
    BUTTON, APPCARD, CANCEL, PAUSE, RESUME, MEDIA
  }
}
