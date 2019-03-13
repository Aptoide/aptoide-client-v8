package cm.aptoide.pt.editorialList;

import java.util.Collections;
import java.util.List;

class EditorialListViewModel {

  private final Error error;
  private final boolean loading;
  private final List<CurationCard> curationCards;
  private final int offset;
  private final int total;

  public EditorialListViewModel(Error error) {
    this.error = error;
    loading = false;
    offset = 0;
    total = 0;
    curationCards = Collections.emptyList();
  }

  public EditorialListViewModel(boolean loading) {
    this.loading = loading;
    error = null;
    offset = 0;
    total = 0;
    curationCards = Collections.emptyList();
  }

  public EditorialListViewModel(List<CurationCard> curationCards, int offset, int total) {
    this.curationCards = curationCards;
    this.offset = offset;
    this.total = total;
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

  public int getOffset() {
    return offset;
  }

  public int getTotal() {
    return total;
  }

  public enum Error {
    NETWORK, GENERIC
  }
}
