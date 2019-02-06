package cm.aptoide.pt.editorial;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import java.text.DecimalFormat;
import java.util.List;
import rx.subjects.PublishSubject;

/**
 * Created by D01 on 28/08/2018.
 */

class EditorialItemsAdapter extends RecyclerView.Adapter<EditorialItemsViewHolder> {

  private final DecimalFormat oneDecimalFormat;
  private final PublishSubject<EditorialDownloadEvent> downloadEventListener;
  private List<EditorialContent> editorialItemList;
  private boolean shouldHaveAnimation;
  private PublishSubject<EditorialEvent> uiEventListener;

  public EditorialItemsAdapter(List<EditorialContent> editorialItemList,
      DecimalFormat oneDecimalFormat, PublishSubject<EditorialEvent> uiEventListener,
      PublishSubject<EditorialDownloadEvent> downloadEventListener) {
    this.editorialItemList = editorialItemList;
    this.oneDecimalFormat = oneDecimalFormat;
    this.uiEventListener = uiEventListener;
    this.downloadEventListener = downloadEventListener;
    this.shouldHaveAnimation = true;
  }

  @Override public EditorialItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new EditorialItemsViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.editorial_item_layout, parent, false), oneDecimalFormat, uiEventListener,
        downloadEventListener);
  }

  @Override
  public void onBindViewHolder(EditorialItemsViewHolder editorialItemsViewHolder, int position) {
    editorialItemsViewHolder.setVisibility(editorialItemList.get(position), position,
        shouldHaveAnimation);
  }

  @Override public int getItemCount() {
    return editorialItemList.size();
  }

  public void add(List<EditorialContent> editorialItemList, boolean shouldHaveAnimation) {
    this.editorialItemList.addAll(editorialItemList);
    this.shouldHaveAnimation = shouldHaveAnimation;
    notifyDataSetChanged();
  }
}
