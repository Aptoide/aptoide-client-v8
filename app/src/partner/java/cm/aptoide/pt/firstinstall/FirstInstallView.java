package cm.aptoide.pt.firstinstall;

import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import java.util.List;
import rx.Observable;

/**
 * Created by diogoloureiro on 02/10/2017.
 *
 * First install view
 */

public interface FirstInstallView extends View {

  Observable<Void> installAllClick();

  void addFirstInstallDisplayables(List<Displayable> displayables, boolean finishLoading);

  void removeFragmentAnimation();
}
