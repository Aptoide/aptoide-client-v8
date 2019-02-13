package cm.aptoide.pt.editorialList;

import java.util.Collections;
import java.util.List;

class EditorialListViewModel {

  private final Error error;
  private final boolean loading;
  private final List<CurationCard> curationCards;

  public EditorialListViewModel(Error error) {
    this.error = error;
    loading = false;
    curationCards = Collections.emptyList();
  }

  public EditorialListViewModel(boolean loading) {
    this.loading = loading;
    error = null;
    curationCards = Collections.emptyList();
  }

  public EditorialListViewModel(List<CurationCard> curationCards) {
    this.curationCards = curationCards;
    error = null;
    loading = false;
  }

  public Error getError() {
    return error;
  }

  public boolean isLoading() {
    return loading;
  }

  public boolean hasError() {
    return error != null;
  }

  public List<CurationCard> getCurationCards() {
    return curationCards;
  }

  public enum Error {
    NETWORK, GENERIC
  }
}
