package cm.aptoide.pt.home;

import cm.aptoide.pt.presenter.View;
import java.util.List;

/**
 * Created by jdandrade on 07/03/2018.
 */

public interface HomeView extends View {
  void showHomeBundles(List<AppBundle> bundles);

  void scrollToTop();
}
