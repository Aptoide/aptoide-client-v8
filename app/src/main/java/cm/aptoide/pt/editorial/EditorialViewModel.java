package cm.aptoide.pt.editorial;

import cm.aptoide.pt.dataprovider.model.v7.Obb;
import java.util.Collections;
import java.util.List;

/**
 * Created by D01 on 29/08/2018.
 */

public class EditorialViewModel {

  private final List<EditorialContent> contentList;
  private final String caption;
  private final String background;
  private final List<Integer> placeHolderPositions;
  private final List<EditorialContent> placeHolderContent;
  private final String title;
  private final boolean loading;
  private final Error error;

  public EditorialViewModel(List<EditorialContent> editorialContentList, String title,
      String caption, String background, List<Integer> placeHolderPositions,
      List<EditorialContent> placeHolderContent) {
    contentList = editorialContentList;
    this.title = title;
    this.caption = caption;
    this.background = background;
    this.placeHolderPositions = placeHolderPositions;
    this.placeHolderContent = placeHolderContent;
    loading = false;
    error = null;
  }

  public EditorialViewModel(boolean loading) {
    this.loading = loading;
    error = null;
    contentList = Collections.emptyList();
    this.title = "";
    this.caption = "";
    this.background = "";
    this.placeHolderPositions = Collections.emptyList();
    placeHolderContent = Collections.emptyList();
  }

  public EditorialViewModel(Error error) {
    loading = false;
    this.error = error;
    contentList = Collections.emptyList();
    this.title = "";
    this.caption = "";
    this.background = "";
    this.placeHolderPositions = Collections.emptyList();
    placeHolderContent = Collections.emptyList();
  }

  boolean hasContent() {
    return contentList != null && !contentList.isEmpty();
  }

  public EditorialContent getContent(int position) {
    return contentList.get(position);
  }

  List<EditorialContent> getContentList() {
    return contentList;
  }

  public boolean isLoading() {
    return loading;
  }

  public Error getError() {
    return error;
  }

  public boolean hasError() {
    return error != null;
  }

  String getCaption() {
    return caption;
  }

  public String getTitle() {
    return title;
  }

  List<Integer> getPlaceHolderPositions() {
    return placeHolderPositions;
  }

  String getBottomCardAppName() {
    return getBottomCardContent().getAppName();
  }

  String getBottomCardAppIcon() {
    return getBottomCardContent().getIcon();
  }

  long getBottomCardAppId() {
    return getBottomCardContent().getId();
  }

  String getBottomCardPackageName() {
    return getBottomCardContent().getPackageName();
  }

  String getBottomCardMd5() {
    return getBottomCardContent().getMd5sum();
  }

  int getBottomCardVercode() {
    return getBottomCardContent().getVerCode();
  }

  String getBottomCardIcon() {
    return getBottomCardContent().getIcon();
  }

  String getBottomCardVername() {
    return getBottomCardContent().getVerName();
  }

  String getBottomCardPath() {
    return getBottomCardContent().getPath();
  }

  String getBottomCardPathAlt() {
    return getBottomCardContent().getPathAlt();
  }

  Obb getBottomCardObb() {
    return getBottomCardContent().getObb();
  }

  boolean shouldHaveAnimation() {
    if (placeHolderPositions != null) {
      return placeHolderPositions.size() == 1;
    }
    return false;
  }

  String getBackgroundImage() {
    return background;
  }

  boolean hasBackgroundImage() {
    return !background.equals("");
  }

  List<EditorialContent> getPlaceHolderContent() {
    return placeHolderContent;
  }

  private int getBottomCardPlaceHolderPosition() {
    if (placeHolderPositions != null && !placeHolderPositions.isEmpty()) {
      return placeHolderPositions.get(0);
    }
    return -1;
  }

  private EditorialContent getBottomCardContent() {
    return contentList.get(getBottomCardPlaceHolderPosition());
  }

  public enum Error {
    NETWORK, GENERIC;
  }
}
