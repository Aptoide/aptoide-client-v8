package cm.aptoide.pt.dataprovider.ws.v7;

import cm.aptoide.pt.dataprovider.model.v7.BaseV7EndlessDataListResponse;
import cm.aptoide.pt.dataprovider.ws.v7.home.Appearance;

public class EditorialListData extends BaseV7EndlessDataListResponse {

  public String id;
  public String type;
  public String title;
  public String caption;
  public String icon;
  public String views;
  public String date;
  public Appearance appearance;

  public EditorialListData() {

  }

  public String getId() {
    return id;
  }

  public String getType() {
    return type;
  }

  public String getTitle() {
    return title;
  }

  public String getCaption() {
    return caption;
  }

  public String getIcon() {
    return icon;
  }

  public String getViews() {
    return views;
  }

  public String getDate() {
    return date;
  }

  public Appearance getAppearance() {
    return appearance;
  }
}
