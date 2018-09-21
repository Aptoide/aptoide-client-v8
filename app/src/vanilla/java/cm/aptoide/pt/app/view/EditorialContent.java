package cm.aptoide.pt.app.view;

import java.util.List;

/**
 * Created by D01 on 31/08/2018.
 */

class EditorialContent {
  private final String title;
  private final List<EditorialMedia> media;
  private final String message;
  private final String type;
  private final String appName;
  private final String icon;
  private final float rating;

  public EditorialContent(String title, List<EditorialMedia> media, String message, String type,
      String appName, String icon, float rating) {

    this.title = title;
    this.media = media;
    this.message = message;
    this.type = type;
    this.appName = appName;
    this.icon = icon;
    this.rating = rating;
  }

  public String getMessage() {
    return message;
  }

  public boolean hasMessage() {
    return message != null && !message.equals("");
  }

  public String getType() {
    return type;
  }

  public boolean isPlaceHolderType() {
    return type != null && type.equals("app_placeholder");
  }

  public List<EditorialMedia> getMedia() {
    return media;
  }

  public boolean hasMedia() {
    return media != null && !media.isEmpty();
  }

  public boolean hasListOfMedia() {
    return hasMedia() && media.size() > 1;
  }

  public String getTitle() {
    return title;
  }

  public boolean hasTitle() {
    return title != null && !title.equals("");
  }

  public String getAppName() {
    return appName;
  }

  public String getIcon() {
    return icon;
  }

  public float getRating() {
    return rating;
  }
}
