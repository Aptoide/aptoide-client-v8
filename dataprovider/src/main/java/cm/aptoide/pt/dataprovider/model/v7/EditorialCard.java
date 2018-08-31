package cm.aptoide.pt.dataprovider.model.v7;

import cm.aptoide.pt.dataprovider.model.v7.listapp.App;
import java.util.List;

/**
 * Created by D01 on 29/08/2018.
 */

//1st level

public class EditorialCard extends BaseV7Response {

  private Data data;

  public EditorialCard() {

  }

  public Data getData() {
    return this.data;
  }

  public void setData(Data data) {
    this.data = data;
  }

  //2nd level
  public static class Data {
    private List<Content> content;
    private String type;
    private App app;
    private String background_image;

    public Data() {

    }

    public List<Content> getContent() {
      return content;
    }

    public void setContent(List<Content> content) {
      this.content = content;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public App getApp() {
      return app;
    }

    public void setApp(App app) {
      this.app = app;
    }

    public String getBackground_image() {
      return background_image;
    }

    public void setBackground_image(String background_image) {
      this.background_image = background_image;
    }

    //3rd level
  }

  public static class Content {
    private String title;
    private String message;
    private List<Media> media;
    private String type;

    public Content() {

    }

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getMessage() {
      return message;
    }

    public void setMessage(String message) {
      this.message = message;
    }

    public List<Media> getMedia() {
      return this.media;
    }

    public void setMedia(List<Media> media) {
      this.media = media;
    }

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }
  }

  //4th level
  public static class Media {
    private String type;
    private String description;
    private String url;

    public String getType() {
      return type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public String getDescription() {
      return description;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public String getUrl() {
      return url;
    }

    public void setUrl(String url) {
      this.url = url;
    }
  }
}
