package cm.aptoide.pt.editorialList;

import java.util.Collections;
import java.util.List;

class EditorialListModel {

  private final Error error;
  private final boolean loading;
  private final List<CurationCard> curationCards;
  private final int offset;
  private final int total;

  public EditorialListModel(Error error) {
    this.error = error;
    loading = false;
    offset = 0;
    total = 0;
    curationCards = Collections.emptyList();
  }

  public EditorialListModel(boolean loading) {
    this.loading = loading;
    error = null;
    offset = 0;
    total = 0;
    curationCards = Collections.emptyList();
  }

  public EditorialListModel(List<CurationCard> curationCards, int offset, int total) {
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
