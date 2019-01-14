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
  private final String name;
  private final String icon;
  private final float rating;
  private final String actionTitle;
  private final String actionUrl;

  public EditorialContent(String title, List<EditorialMedia> media, String message, String type,
      String name, String icon, float rating, String actionTitle, String actionUrl) {

    this.title = title;
    this.media = media;
    this.message = message;
    this.type = type;
    this.name = name;
    this.icon = icon;
    this.rating = rating;
    this.actionTitle = actionTitle;
    this.actionUrl = actionUrl;
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
    return name != null;
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

  public boolean hasAnyMediaDescription() {
    for (EditorialMedia editorialMedia : media) {
      if (editorialMedia.hasDescription()) {
        return true;
      }
    }
    return false;
  }

  public String getAppName() {
    return name;
  }

  public String getIcon() {
    return icon;
  }

  public float getRating() {
    return rating;
  }

  public boolean hasAction() {
    return actionTitle != null && !actionTitle.equals("");
  }

  public String getActionTitle() {
    return actionTitle;
  }

  public String getActionUrl() {
    return actionUrl;
  }
}
