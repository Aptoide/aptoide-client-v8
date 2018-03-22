package cm.aptoide.pt.store.view.my;

import cm.aptoide.pt.presenter.View;
import rx.Observable;

/**
 * Created by D01 on 14/03/18.
 */

public interface MyStoresView extends View {

  void scrollToTop();

  void setUserImage(String userAvatarUrl);

  Observable<Void> imageClick();

  void showAvatar();
}
