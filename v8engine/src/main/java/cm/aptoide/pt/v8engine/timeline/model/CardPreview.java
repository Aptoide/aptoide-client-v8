package cm.aptoide.pt.v8engine.timeline.model;

public class CardPreview {
  private String type;
  private Data data;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Data getData() {
    return data;
  }

  public void setData(Data data) {
    this.data = data;
  }

  public static class Data{
    private String title;
    private String thumbnail;

    public String getTitle() {
      return title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getThumbnail() {
      return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
      this.thumbnail = thumbnail;
    }
  }
}
