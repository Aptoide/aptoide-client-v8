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

  private static final int LOADING = R.layout.progress_item;
  private static final int EDITORIAL_CARD = R.layout.editorial_action_item;
  private final ProgressCard progressBundle;
  private final PublishSubject<HomeEvent> uiEventsListener;
  private List<CurationCard> editorialListItems;

  public EditorialListAdapter(List<CurationCard> editorialListItems, ProgressCard progressBundle,
      PublishSubject<HomeEvent> uiEventsListener) {
    this.editorialListItems = editorialListItems;
    this.progressBundle = progressBundle;
    this.uiEventsListener = uiEventsListener;
  }

  @Override public EditorialBundleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    if (viewType == EDITORIAL_CARD) {
      return new EditorialBundleViewHolder(LayoutInflater.from(parent.getContext())
          .inflate(EDITORIAL_CARD, parent, false), uiEventsListener);
    } else {
      return new LoadingViewHolder(LayoutInflater.from(parent.getContext())
          .inflate(LOADING, parent, false), uiEventsListener);
    }
  }

  @Override
  public void onBindViewHolder(EditorialBundleViewHolder editorialsViewHolder, int position) {
    if (!(editorialsViewHolder instanceof LoadingViewHolder)) {
      editorialsViewHolder.setEditorialCard(editorialListItems.get(position), position);
    }
  }

  @Override public int getItemViewType(int position) {
    if (editorialListItems.get(position) instanceof ProgressCard) {
      return LOADING;
    } else {
      return EDITORIAL_CARD;
    }
  }

  @Override public int getItemCount() {
    return editorialListItems.size();
  }

  public void add(List<CurationCard> editorialItemList) {
    this.editorialListItems.addAll(editorialItemList);
    notifyDataSetChanged();
  }

  public void addLoadMore() {
    if (getLoadingPosition() < 0) {
      editorialListItems.add(progressBundle);
      notifyItemInserted(editorialListItems.size() - 1);
    }
  }

  public void removeLoadMore() {
    int loadingPosition = getLoadingPosition();
    if (loadingPosition >= 0) {
      editorialListItems.remove(loadingPosition);
      notifyItemRemoved(loadingPosition);
    }
  }

  public synchronized int getLoadingPosition() {
    for (int i = editorialListItems.size() - 1; i >= 0; i--) {
      CurationCard curationCard = editorialListItems.get(i);
      if (curationCard instanceof ProgressCard) {
        return i;
      }
    }
    return -1;
  }

  public void update(List<CurationCard> curationCards) {
    this.editorialListItems = curationCards;
    notifyDataSetChanged();
  }

  public CurationCard getCard(int visibleItem) {
    return editorialListItems.get(visibleItem);
  }

  public void updateEditorialCard(CurationCard curationCard, String cardId) {
    for (int i = 0; i < editorialListItems.size(); i++) {
      if (editorialListItems.get(i)
          .getId()
          .equals(cardId)) {
        editorialListItems.set(i, curationCard);
        notifyItemChanged(i);
      }
    }
  }
}
