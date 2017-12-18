package cm.aptoide.pt.dataprovider.ws.v7.post;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;

public final class CardPreviewResponse extends BaseV7Response {
  CardPreview data;

  public CardPreviewResponse() {
  }

  public CardPreview getData() {
    return this.data;
  }

  public void setData(CardPreview data) {
    this.data = data;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $data = this.getData();
    result = result * PRIME + ($data == null ? 43 : $data.hashCode());
    return result;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof CardPreviewResponse)) return false;
    final CardPreviewResponse other = (CardPreviewResponse) o;
    if (!other.canEqual((Object) this)) return false;
    final Object this$data = this.getData();
    final Object other$data = other.getData();
    if (this$data == null ? other$data != null : !this$data.equals(other$data)) return false;
    return true;
  }

  protected boolean canEqual(Object other) {
    return other instanceof CardPreviewResponse;
  }

  public String toString() {
    return "CardPreviewResponse(data=" + this.getData() + ")";
  }

  public static class CardPreview {
    private String type;
    private TitleAndThumbnail data;

    public CardPreview() {
    }

    public String getType() {
      return this.type;
    }

    public void setType(String type) {
      this.type = type;
    }

    public TitleAndThumbnail getData() {
      return this.data;
    }

    public void setData(TitleAndThumbnail data) {
      this.data = data;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $type = this.getType();
      result = result * PRIME + ($type == null ? 43 : $type.hashCode());
      final Object $data = this.getData();
      result = result * PRIME + ($data == null ? 43 : $data.hashCode());
      return result;
    }    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof CardPreview)) return false;
      final CardPreview other = (CardPreview) o;
      if (!other.canEqual((Object) this)) return false;
      final Object this$type = this.getType();
      final Object other$type = other.getType();
      if (this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
      final Object this$data = this.getData();
      final Object other$data = other.getData();
      if (this$data == null ? other$data != null : !this$data.equals(other$data)) return false;
      return true;
    }

    protected boolean canEqual(Object other) {
      return other instanceof CardPreview;
    }



    public String toString() {
      return "CardPreviewResponse.CardPreview(type="
          + this.getType()
          + ", data="
          + this.getData()
          + ")";
    }
  }

  public static class TitleAndThumbnail {
    private String title;
    private String thumbnail;

    public TitleAndThumbnail() {
    }

    public String getTitle() {
      return this.title;
    }

    public void setTitle(String title) {
      this.title = title;
    }

    public String getThumbnail() {
      return this.thumbnail;
    }

    public void setThumbnail(String thumbnail) {
      this.thumbnail = thumbnail;
    }

    protected boolean canEqual(Object other) {
      return other instanceof TitleAndThumbnail;
    }    public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof TitleAndThumbnail)) return false;
      final TitleAndThumbnail other = (TitleAndThumbnail) o;
      if (!other.canEqual((Object) this)) return false;
      final Object this$title = this.getTitle();
      final Object other$title = other.getTitle();
      if (this$title == null ? other$title != null : !this$title.equals(other$title)) return false;
      final Object this$thumbnail = this.getThumbnail();
      final Object other$thumbnail = other.getThumbnail();
      if (this$thumbnail == null ? other$thumbnail != null
          : !this$thumbnail.equals(other$thumbnail)) {
        return false;
      }
      return true;
    }

    public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $title = this.getTitle();
      result = result * PRIME + ($title == null ? 43 : $title.hashCode());
      final Object $thumbnail = this.getThumbnail();
      result = result * PRIME + ($thumbnail == null ? 43 : $thumbnail.hashCode());
      return result;
    }



    public String toString() {
      return "CardPreviewResponse.TitleAndThumbnail(title="
          + this.getTitle()
          + ", thumbnail="
          + this.getThumbnail()
          + ")";
    }
  }
}
