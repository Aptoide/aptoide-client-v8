package cm.aptoide.pt.search.model;

import org.parceler.Parcel;

/**
 * Created by franciscocalado on 04/05/18.
 */

@Parcel public class SearchAppResultWrapper {

  private SearchAppResult searchAppResult;
  private int position;

  public SearchAppResultWrapper() {
  }

  public SearchAppResultWrapper(SearchAppResult result, int position) {
    searchAppResult = result;
    this.position = position;
  }

  public SearchAppResult getSearchAppResult() {
    return searchAppResult;
  }

  public int getPosition() {
    return position;
  }
}
