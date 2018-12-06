package cm.aptoide.pt.promotions;

import cm.aptoide.pt.presenter.View;
import java.util.List;

public interface PromotionsView extends View {

  void showPromotionApps(List<PromotionViewApp> appsList);
}
