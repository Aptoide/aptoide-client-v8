package cm.aptoide.pt.app.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import cm.aptoide.pt.app.DownloadModel;
import java.text.DecimalFormat;
import java.util.List;

/**
 * Created by D01 on 28/08/2018.
 */

class EditorialItemsAdapter extends RecyclerView.Adapter<EditorialItemsViewHolder> {

  private final DecimalFormat oneDecimalFormat;
  private List<EditorialContent> editorialItemList;
  private EditorialItemsViewHolder appCardPlaceholderEditorialViewHolder;

  public EditorialItemsAdapter(List<EditorialContent> editorialItemList,
      DecimalFormat oneDecimalFormat) {
    this.editorialItemList = editorialItemList;
    this.oneDecimalFormat = oneDecimalFormat;
  }

  @Override public EditorialItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new EditorialItemsViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.editorial_item_layout, parent, false), oneDecimalFormat);
  }

  @Override
  public void onBindViewHolder(EditorialItemsViewHolder editorialItemsViewHolder, int position) {
    if (editorialItemList.get(position)
        .isPlaceHolderType()) {
      this.appCardPlaceholderEditorialViewHolder = editorialItemsViewHolder;
    }
    editorialItemsViewHolder.setVisibility(editorialItemList.get(position), position);
  }

  @Override public int getItemCount() {
    return editorialItemList.size();
  }

  public boolean isItemShown(float screenHeight, float screenWidth) {
    return appCardPlaceholderEditorialViewHolder.isVisible(screenHeight, screenWidth);
  }

  public View getPlaceHolder() {
    if (appCardPlaceholderEditorialViewHolder != null) {
      return appCardPlaceholderEditorialViewHolder.getPlaceHolder();
    }
    return null;
  }

  public void add(List<EditorialContent> editorialItemList) {
    this.editorialItemList.addAll(editorialItemList);
    notifyDataSetChanged();
  }

  public void setPlaceHolderInfo(String appName, String icon, String buttonText, float rating) {
    if (appCardPlaceholderEditorialViewHolder != null) {
      appCardPlaceholderEditorialViewHolder.setPlaceHolderInfo(appName, icon, buttonText, rating);
    }
  }

  public void setPlaceHolderDownloadInfo(DownloadModel downloadModel, String update, String install,
      String open) {
    if (appCardPlaceholderEditorialViewHolder != null) {
      appCardPlaceholderEditorialViewHolder.setPlaceHolderDownloadInfo(downloadModel, update,
          install, open);
    }
  }
}
