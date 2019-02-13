package cm.aptoide.pt.editorialList;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import cm.aptoide.pt.R;
import cm.aptoide.pt.home.EditorialBundleViewHolder;
import cm.aptoide.pt.home.HomeEvent;
import java.util.List;
import rx.subjects.PublishSubject;

class EditorialListAdapter extends RecyclerView.Adapter<EditorialBundleViewHolder> {

  private final List<CurationCard> editorialListItems;
  private final PublishSubject<HomeEvent> uiEventsListener;

  public EditorialListAdapter(List<CurationCard> editorialListItems,
      PublishSubject<HomeEvent> uiEventsListener) {
    this.editorialListItems = editorialListItems;
    this.uiEventsListener = uiEventsListener;
  }

  @Override public EditorialBundleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return new EditorialBundleViewHolder(LayoutInflater.from(parent.getContext())
        .inflate(R.layout.editorial_action_item, parent, false), uiEventsListener);
  }

  @Override
  public void onBindViewHolder(EditorialBundleViewHolder editorialsViewHolder, int position) {
    editorialsViewHolder.setEditorialCard(editorialListItems.get(position), position);
  }

  @Override public int getItemCount() {
    return editorialListItems.size();
  }

  public void add(List<CurationCard> editorialItemList) {
    this.editorialListItems.addAll(editorialItemList);
    notifyDataSetChanged();
  }
}
