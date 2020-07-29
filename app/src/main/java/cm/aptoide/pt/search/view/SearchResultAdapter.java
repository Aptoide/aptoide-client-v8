package cm.aptoide.pt.search.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.download.view.DownloadClick;
import cm.aptoide.pt.search.SearchResultDiffModel;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.search.model.SearchAdResultWrapper;
import cm.aptoide.pt.search.model.SearchAppResult;
import cm.aptoide.pt.search.model.SearchAppResultWrapper;
import cm.aptoide.pt.search.view.item.SearchLoadingViewHolder;
import cm.aptoide.pt.search.view.item.SearchResultAdViewHolder;
import cm.aptoide.pt.search.view.item.SearchResultItemView;
import cm.aptoide.pt.search.view.item.SearchResultViewHolder;
import com.jakewharton.rxrelay.PublishRelay;
import java.text.DecimalFormat;
import java.util.List;
import rx.subjects.PublishSubject;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultItemView> {

  private final PublishRelay<SearchAdResultWrapper> onAdClickRelay;
  private final PublishSubject<SearchAppResultWrapper> onItemViewClick;
  private final PublishSubject<DownloadClick> downloadClickPublishSubject;
  private final List<SearchAdResult> searchAdResults;
  private List<SearchAppResult> searchResults;
  private String query;
  private boolean adsLoaded = false;
  private boolean isLoadingMore = false;
  private CrashReport crashReport;
  private DecimalFormat oneDecimalFormatter;

  public SearchResultAdapter(PublishRelay<SearchAdResultWrapper> onAdClickRelay,
      PublishSubject<SearchAppResultWrapper> onItemViewClick,
      PublishSubject<DownloadClick> downloadClickPublishSubject,
      List<SearchAppResult> searchResults, List<SearchAdResult> searchAdResults,
      CrashReport crashReport, DecimalFormat decimalFormatter) {
    this.onAdClickRelay = onAdClickRelay;
    this.onItemViewClick = onItemViewClick;
    this.searchResults = searchResults;
    this.searchAdResults = searchAdResults;
    this.crashReport = crashReport;
    this.oneDecimalFormatter = decimalFormatter;
    this.downloadClickPublishSubject = downloadClickPublishSubject;
    //setHasStableIds(true);
  }

  @Override public SearchResultItemView onCreateViewHolder(ViewGroup parent, int viewType) {
    final Context context = parent.getContext();
    View view = LayoutInflater.from(context)
        .inflate(viewType, parent, false);

    switch (viewType) {
      case SearchResultViewHolder.LAYOUT: {
        return new SearchResultViewHolder(view, onItemViewClick, downloadClickPublishSubject,
            query);
      }

      case SearchResultAdViewHolder.LAYOUT: {
        return new SearchResultAdViewHolder(view, onAdClickRelay, oneDecimalFormatter);
      }

      default: {
        return new SearchLoadingViewHolder(view);
      }
    }
  }

  @SuppressWarnings("unchecked") @Override
  public void onBindViewHolder(SearchResultItemView holder, int position) {
    try {
      holder.setup(getItem(position));
    } catch (ClassCastException e) {
      crashReport.log(e);
    }
  }

  @Override public void onBindViewHolder(@NonNull SearchResultItemView holder, int position,
      @NonNull List<Object> payloads) {
    // Partial rebind for updating downloads
    if (holder instanceof SearchResultViewHolder && !payloads.isEmpty()) {
      ((SearchResultViewHolder) holder).setDownloadStatus((SearchAppResult) payloads.get(0));
    } else {
      super.onBindViewHolder(holder, position, payloads);
    }
  }

  @Override public int getItemViewType(int position) {
    if (!adsLoaded && position == 0) {
      return SearchLoadingViewHolder.LAYOUT;
    }

    final int totalItems = searchAdResults.size() + searchResults.size();
    if (isLoadingMore && position >= totalItems) {
      return SearchLoadingViewHolder.LAYOUT;
    }

    if (adsLoaded && position < searchAdResults.size()) {
      return SearchResultAdViewHolder.LAYOUT;
    }

    return SearchResultViewHolder.LAYOUT;
  }

  @Override public int getItemCount() {
    final int itemCount = searchAdResults.size() + searchResults.size();
    return isLoadingMore ? itemCount + 1 : itemCount;
  }

  @Override public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
    super.onDetachedFromRecyclerView(recyclerView);
    for (int i = 0; i < getItemCount(); i++) {
      try {
        ((SearchResultItemView) getItem(i)).prepareToRecycle();
      } catch (NullPointerException | ClassCastException e) {
      }
    }
  }

  private Object getItem(int position) {
    if (!adsLoaded && position == 0) {
      return null;
    }

    final int totalItems = searchAdResults.size() + searchResults.size();
    if (isLoadingMore && position >= totalItems) {
      return null;
    }

    if (adsLoaded && position < searchAdResults.size()) {
      return searchAdResults.get(position);
    }
    return searchResults.get(position - searchAdResults.size());
  }

  public void setResultForSearch(String query, SearchResultDiffModel searchResultDiffModel) {
    this.query = query;
    searchResults = searchResultDiffModel.getSearchResultsList();
    notifyDataSetChanged();
  }

  public void addResultForSearch(String query, SearchResultDiffModel searchResultDiffModel) {
    this.query = query;
    searchResults = searchResultDiffModel.getSearchResultsList();
    DiffUtil.DiffResult diffResult = searchResultDiffModel.getDiffResult();
    if (diffResult != null) {
      searchResultDiffModel.getDiffResult()
          .dispatchUpdatesTo(this);
    } else {
      notifyDataSetChanged();
    }
  }

  public void setResultForAd(SearchAdResult searchAd) {
    searchAdResults.add(searchAd);
    setAdsLoaded();
  }

  public void setAdsLoaded() {
    adsLoaded = true;
    notifyDataSetChanged();
  }

  public void setIsLoadingMore(boolean isLoadingMore) {
    if (this.isLoadingMore == isLoadingMore) return;
    this.isLoadingMore = isLoadingMore;
    if (isLoadingMore) {
      notifyItemInserted(getItemCount() - 1);
    } else {
      notifyItemRemoved(getItemCount() - 1);
    }
  }
}
