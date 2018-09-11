package cm.aptoide.pt.app.view;

/**
 * Created by D01 on 29/08/2018.
 */

class EditorialMedia {

  private final String type;
  private final String description;
  private final String url;

  public EditorialMedia(String type, String description, String url) {
    this.type = type;
    this.description = description;
    this.url = url;
  }

  public String getType() {
    return type;
  }

  public boolean hasType() {
    return type != null && !type.equals("");
  }

  public boolean isImage() {
    return hasType() && type.equals("image");
  }

  public boolean isVideo() {
    return hasType() && type.equals("video");
  }

  public String getDescription() {
    return description;
  }

  public boolean hasDescription() {
    return description != null && !description.equals("");
  }

  public String getUrl() {
    return url;
  }

  public boolean hasUrl() {
    return url != null && !url.equals("");
  }
}
