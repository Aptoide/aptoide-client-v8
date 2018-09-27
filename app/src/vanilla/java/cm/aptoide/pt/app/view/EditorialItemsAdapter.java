package cm.aptoide.pt.app.view;

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
  private List<EditorialContent> editorialItemList;
  private PublishSubject<EditorialEvent> editorialMediaClicked;

  public EditorialItemsAdapter(List<EditorialContent> editorialItemList,
      DecimalFormat oneDecimalFormat, PublishSubject<EditorialEvent> editorialMediaClicked) {
    this.editorialItemList = editorialItemList;
    this.oneDecimalFormat = oneDecimalFormat;
    this.editorialMediaClicked = editorialMediaClicked;
  }

  @Override public EditorialItemsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new EditorialItemsViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.editorial_item_layout, parent, false), oneDecimalFormat,
        editorialMediaClicked);
  }

  @Override
  public void onBindViewHolder(EditorialItemsViewHolder editorialItemsViewHolder, int position) {
    editorialItemsViewHolder.setVisibility(editorialItemList.get(position), position);
  }

  @Override public int getItemCount() {
    return editorialItemList.size();
  }

  public void add(List<EditorialContent> editorialItemList) {
    this.editorialItemList.addAll(editorialItemList);
    notifyDataSetChanged();
  }
}
