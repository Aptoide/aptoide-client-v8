package cm.aptoide.pt.navigation;

import cm.aptoide.pt.home.AppBundle;
import cm.aptoide.pt.home.BottomHomeFragment;
import cm.aptoide.pt.home.Home;
import cm.aptoide.pt.home.HomePresenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.view.app.Application;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import rx.Single;
import rx.subjects.PublishSubject;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by jdandrade on 07/03/2018.
 */

public class HomePresenterTest {

  @Mock private BottomHomeFragment view;
  @Mock private Home home;

  private HomePresenter presenter;

  private PublishSubject<View.LifecycleEvent> lifecycleEvent;
  private List<AppBundle> bundles;

  @Before public void setupHomePresenter() {
    MockitoAnnotations.initMocks(this);

    lifecycleEvent = PublishSubject.create();
    presenter = new HomePresenter(view, home);
    bundles = new ArrayList<>();

    List<Application> applications = getAppsList();
    bundles.add(new AppBundle("Editors choice", applications, type));
    bundles.add(new AppBundle("Local Top Apps", applications, type));

    when(view.getLifecycle()).thenReturn(lifecycleEvent);
  }

  @Test public void onCreateShowHome() {
    //Given an initialised HomePresenter
    presenter.present();
    //When the user clicks the Home menu item
    //And loading of bundles are requested
    when(home.getHomeBundles()).thenReturn(Single.just(bundles));
    lifecycleEvent.onNext(View.LifecycleEvent.CREATE);
    //Then the home should be displayed
    verify(view).showHomeBundles(bundles);
    //Then the progress indicator should be hidden
  }

  private List<Application> getAppsList() {
    List<Application> tmp = new ArrayList<>();
    tmp.add(
        new Application("Aptoide", "http://via.placeholder.com/350x150", 0, 1000, "cm.aptoide.pt",
            300));
    tmp.add(new Application("Facebook", "http://via.placeholder.com/350x150", (float) 4.2, 1000,
        "katana.facebook.com", 30));
    return tmp;
  }
}
