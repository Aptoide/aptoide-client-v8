package cm.aptoide.pt.model.v7.base;

import java.util.List;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created on 18/08/16.
 */
@EqualsAndHashCode(callSuper = true) @Data public class BaseV7EndlessListResponse<T>
    extends BaseV7EndlessResponse {

  private List<T> list;

  public BaseV7EndlessListResponse() {
    super(false);
  }

  @Override public int getTotal() {
    return list != null ? list.size() : 0;
  }

  @Override public int getNextSize() {
    return list != null ? NEXT_STEP : 0;
  }

  @Override public boolean hasData() {
    return list != null;
  }
}
