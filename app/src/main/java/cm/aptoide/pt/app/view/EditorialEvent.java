package cm.aptoide.pt.app.view;

import java.util.List;

/**
 * Created by D01 on 19/09/2018.
 */

class EditorialEvent {

  private final Type clickType;
  private final int firstVisibleItemPosition;
  private final int lastVisibleItemPosition;
  private final int position;
  private final List<EditorialMedia> media;
  private final String url;

  public EditorialEvent(Type clickType, String url) {

    this.clickType = clickType;
    this.url = url;
    firstVisibleItemPosition = -1;
    lastVisibleItemPosition = -1;
    media = null;
    position = -1;
  }

  public EditorialEvent(Type clickType) {

    this.clickType = clickType;
    this.url = "";
    firstVisibleItemPosition = -1;
    lastVisibleItemPosition = -1;
    media = null;
    position = -1;
  }

  public EditorialEvent(Type clickType, int firstVisibleItemPosition, int lastVisibleItemPosition,
      int position, List<EditorialMedia> media) {

    this.clickType = clickType;
    this.firstVisibleItemPosition = firstVisibleItemPosition;
    this.lastVisibleItemPosition = lastVisibleItemPosition;
    this.position = position;
    this.media = media;
    this.url = "";
  }

  public Type getClickType() {
    return clickType;
  }

  public String getUrl() {
    return url;
  }

  public int getFirstVisiblePosition() {
    return firstVisibleItemPosition;
  }

  public int getPosition() {
    return position;
  }

  public int getLastVisibleItemPosition() {
    return lastVisibleItemPosition;
  }

  public List<EditorialMedia> getMedia() {
    return media;
  }

  public enum Type {
    ACTION, APPCARD, BUTTON, CANCEL, PAUSE, RESUME, MEDIA, MEDIA_LIST
  }
}
