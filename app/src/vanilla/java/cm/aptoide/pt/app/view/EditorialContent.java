package cm.aptoide.pt.app.view;

import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import java.util.List;

/**
 * Created by D01 on 31/08/2018.
 */

class EditorialContent {
  private final String title;
  private final List<EditorialMedia> media;
  private final String message;
  private final String type;
  private final App app;

  public EditorialContent(String title, List<EditorialMedia> media, String message, String type,
      App app) {

    this.title = title;
    this.media = media;
    this.message = message;
    this.type = type;
    this.app = app;
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
    return app != null;
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

  public App getApp() {
    return app;
  }

  public String getAppName() {
    String name = null;
    if (app != null) {
      name = app.getName();
    }
    return name;
  }

  public String getIcon() {
    String icon = null;
    if (app != null) {
      icon = app.getIcon();
    }
    return icon;
  }

  public float getRating() {
    float rating = 0;
    if (app != null) {
      rating = app.getStats()
          .getRating()
          .getAvg();
    }
    return rating;
  }
}
