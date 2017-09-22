package cm.aptoide.pt.view.search;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import cm.aptoide.pt.dataprovider.model.v7.DataList;
import cm.aptoide.pt.dataprovider.model.v7.ListSearchApps;
import cm.aptoide.pt.view.search.result.SearchResultViewHolder;
import java.util.ArrayList;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultViewHolder> {

  private final ArrayList<SearchResultViewHolder> resultsForSearchFollowedStores;
  private final ArrayList<SearchResultViewHolder> resultsForSearchEverywhere;

  public SearchResultAdapter() {
    resultsForSearchFollowedStores = new ArrayList<>();
    resultsForSearchEverywhere = new ArrayList<>();
  }

  @Override public SearchResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    return null;
  }

  @Override public void onBindViewHolder(SearchResultViewHolder holder, int position) {

  }

  @Override public int getItemCount() {
    return 0;
  }

  public void showResultsForSearchFollowedStores() {

  }

  public void showResultsForSearchEverywhere() {

  }

  public void setResultForSearchFollowedStores(ListSearchApps data) {

  }

  public void setResultForSearchEverywhere(ListSearchApps data) {

  }

  private boolean hasMoreResults(ListSearchApps data) {
    DataList<ListSearchApps.SearchAppsApp> dataList = data.getDataList();
    return dataList.getList()
        .size() > 0 || data.getTotal() > data.getNextSize();
  }
}
