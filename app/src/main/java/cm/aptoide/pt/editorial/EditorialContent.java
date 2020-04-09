package cm.aptoide.pt.editorial;

import java.util.List;

/**
 * Created by D01 on 31/08/2018.
 */

public class EditorialContent {

  private final String title;
  private final List<EditorialMedia> media;
  private final String message;
  private final String type;
  private final String actionTitle;
  private final String url;

  private final int position;

  private final EditorialAppModel editorialAppModel;

  public EditorialContent(EditorialContent editorialContent,
      EditorialDownloadModel editorialDownloadModel) {
    this.title = editorialContent.getTitle();
    this.media = editorialContent.getMedia();
    this.message = editorialContent.getMessage();
    this.type = editorialContent.getType();
    this.actionTitle = editorialContent.getActionTitle();
    this.url = editorialContent.getActionUrl();
    this.editorialAppModel =
        new EditorialAppModel(editorialContent.getEditorialAppModel(), editorialDownloadModel);
    this.position = editorialContent.getPosition();
  }

  public EditorialContent(String title, List<EditorialMedia> media, String message, String type,
      String actionTitle, String url, int position, EditorialAppModel editorialAppModel) {
    this.title = title;
    this.media = media;
    this.message = message;
    this.type = type;
    this.actionTitle = actionTitle;
    this.url = url;
    this.editorialAppModel = editorialAppModel;
    this.position = position;
  }

  public EditorialContent(String title, List<EditorialMedia> media, String message, String type,
      String actionTitle, String url, int position) {
    this.title = title;
    this.media = media;
    this.message = message;
    this.type = type;
    this.actionTitle = actionTitle;
    this.url = url;
    this.editorialAppModel = null;
    this.position = position;
  }

  public EditorialContent(String title, List<EditorialMedia> media, String message, String type,
      int position, EditorialAppModel editorialAppModel) {
    this.title = title;
    this.media = media;
    this.message = message;
    this.type = type;
    this.position = position;
    this.editorialAppModel = editorialAppModel;
    actionTitle = "";
    url = "";
  }

  public EditorialContent(String title, List<EditorialMedia> media, String message, String type,
      int position) {
    this.title = title;
    this.media = media;
    this.message = message;
    this.type = type;
    this.position = position;
    this.editorialAppModel = null;
    actionTitle = "";
    url = "";
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

  public boolean hasApp() {
    return editorialAppModel != null;
  }

  public EditorialAppModel getApp() {
    return editorialAppModel;
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

  public String getActionTitle() {
    return actionTitle;
  }

  public String getActionUrl() {
    return url;
  }

  public int getPosition() {
    return position;
  }

  public boolean hasAction() {
    return !actionTitle.equals("");
  }

  public EditorialAppModel getEditorialAppModel() {
    return editorialAppModel;
  }

  public boolean hasAnyMediaDescription() {
    for (EditorialMedia editorialMedia : media) {
      if (editorialMedia.hasDescription()) {
        return true;
      }
    }
    return false;
  }

  @Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof EditorialContent)) return false;

    EditorialContent content = (EditorialContent) o;

    if (position != content.position) return false;
    if (title != null ? !title.equals(content.title) : content.title != null) return false;
    if (media != null ? !media.equals(content.media) : content.media != null) return false;
    if (message != null ? !message.equals(content.message) : content.message != null) return false;
    if (type != null ? !type.equals(content.type) : content.type != null) return false;
    if (actionTitle != null ? !actionTitle.equals(content.actionTitle)
        : content.actionTitle != null) {
      return false;
    }
    if (url != null ? !url.equals(content.url) : content.url != null) return false;
    return editorialAppModel != null ? editorialAppModel.equals(content.editorialAppModel)
        : content.editorialAppModel == null;
  }

  @Override public int hashCode() {
    int result = title != null ? title.hashCode() : 0;
    result = 31 * result + (media != null ? media.hashCode() : 0);
    result = 31 * result + (message != null ? message.hashCode() : 0);
    result = 31 * result + (type != null ? type.hashCode() : 0);
    result = 31 * result + (actionTitle != null ? actionTitle.hashCode() : 0);
    result = 31 * result + (url != null ? url.hashCode() : 0);
    result = 31 * result + position;
    result = 31 * result + (editorialAppModel != null ? editorialAppModel.hashCode() : 0);
    return result;
  }
}
