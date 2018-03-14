package cm.aptoide.pt.home;

import cm.aptoide.pt.dataprovider.model.v7.Event;
import java.util.List;

/**
 * Created by jdandrade on 14/03/2018.
 */

class DummyBundle implements HomeBundle {
  @Override public String getTitle() {
    return null;
  }

  @Override public List<?> getContent() {
    return null;
  }

  @Override public BundleType getType() {
    return null;
  }

  @Override public Event getEvent() {
    return null;
  }

  @Override public String getTag() {
    return null;
  }
}
