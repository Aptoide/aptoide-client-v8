package cm.aptoide.pt.editorial;

import java.util.Collections;
import java.util.List;

/**
 * Created by D01 on 29/08/2018.
 */

public class EditorialViewModel {

  private final List<EditorialContent> contentList;
  private final String title;
  private final String caption;
  private final String background;
  private final List<Integer> placeHolderPositions;
  private final List<EditorialContent> placeHolderContent;
  private final boolean shouldHaveAnimation;
  private final String cardId;
  private final String groupId;
  private final boolean loading;
  private final Error error;
  private final String captionColor;

  private final EditorialAppModel bottomCardAppModel;

  public EditorialViewModel(List<EditorialContent> editorialContentList, String title,
      String caption, String background, List<Integer> placeHolderPositions,
      List<EditorialContent> placeHolderContent, boolean shouldHaveAnimation, String cardId,
      String groupId, String captionColor) {
    contentList = editorialContentList;
    this.title = title;
    this.caption = caption;
    this.background = background;
    this.placeHolderPositions = placeHolderPositions;
    this.placeHolderContent = placeHolderContent;
    this.shouldHaveAnimation = shouldHaveAnimation;
    this.cardId = cardId;
    this.groupId = groupId;
    this.captionColor = captionColor;
    this.loading = false;
    this.error = null;
    this.bottomCardAppModel = null;
  }

  public EditorialViewModel(boolean loading) {
    this.loading = loading;
    title = "";
    caption = "";
    background = "";
    placeHolderPositions = Collections.emptyList();
    contentList = Collections.emptyList();
    placeHolderContent = Collections.emptyList();
    bottomCardAppModel = null;
    groupId = "";
    cardId = "";
    shouldHaveAnimation = false;
    error = null;
    captionColor = "";
  }

  public EditorialViewModel(Error error) {
    this.error = error;
    loading = false;
    contentList = Collections.emptyList();
    title = "";
    caption = "";
    background = "";
    placeHolderPositions = Collections.emptyList();
    placeHolderContent = Collections.emptyList();
    groupId = "";
    cardId = "";
    shouldHaveAnimation = false;
    captionColor = "";
    this.bottomCardAppModel = null;
  }

  public EditorialViewModel(List<EditorialContent> editorialContentList, String title,
      String caption, String background, List<Integer> placeHolderPositions,
      List<EditorialContent> placeHolderContent, boolean shouldHaveAnimation, String cardId,
      String groupId, String captionColor, EditorialAppModel bottomCardAppModel) {
    this.contentList = editorialContentList;
    this.title = title;
    this.caption = caption;
    this.background = background;
    this.placeHolderPositions = placeHolderPositions;
    this.placeHolderContent = placeHolderContent;
    this.bottomCardAppModel = bottomCardAppModel;
    this.shouldHaveAnimation = shouldHaveAnimation;
    this.cardId = cardId;
    this.groupId = groupId;
    this.captionColor = captionColor;
    error = null;
    loading = false;
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

  String getBackgroundImage() {
    return background;
  }

  boolean hasBackgroundImage() {
    return !background.equals("");
  }

  List<EditorialContent> getPlaceHolderContent() {
    return placeHolderContent;
  }

  boolean shouldHaveAnimation() {
    return shouldHaveAnimation;
  }

  public String getCardId() {
    return cardId;
  }

  public String getGroupId() {
    return groupId;
  }

  public String getCaptionColor() {
    return this.captionColor;
  }

  public EditorialAppModel getBottomCardAppModel() {
    return bottomCardAppModel;
  }

  public enum Error {
    NETWORK, GENERIC
  }
}
