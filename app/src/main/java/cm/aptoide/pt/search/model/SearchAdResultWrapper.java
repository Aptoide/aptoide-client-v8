package cm.aptoide.pt.search.model;

/**
 * Created by franciscocalado on 04/05/18.
 */

public class SearchAdResultWrapper {

  private SearchAdResult searchAdResult;
  private int position;

  public SearchAdResultWrapper(SearchAdResult searchAdResult, int position) {
    this.searchAdResult = searchAdResult;
    this.position = position;
  }

  public SearchAdResult getSearchAdResult() {
    return searchAdResult;
  }

  public int getPosition() {
    return position;
  }
}
