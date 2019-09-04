package cm.aptoide.pt.app.view.googleplayservices;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.FlavourFragmentModule;
import cm.aptoide.pt.bottomNavigation.BottomNavigationActivity;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.presenter.Presenter;
import cm.aptoide.pt.presenter.View;
import cm.aptoide.pt.view.BaseActivity;
import cm.aptoide.pt.view.FragmentComponent;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.components.support.RxAppCompatDialogFragment;
import java.lang.reflect.Field;
import rx.Observable;

/**
 * TODO: This should follow the separation that is used for BaseDialogFragment/BaseDialogView.
 * TODO: Here it's merged for time constraints.
 *
 * TODO: A new style was created for the ghost button because of strange padding but I didn't
 * include
 * TODO: the click drawable. Should be fixed later if done properly.
 */
public abstract class BaseBottomSheetDialogFragment extends RxAppCompatDialogFragment
    implements View {
  private FragmentComponent fragmentComponent;
  private BottomNavigationActivity bottomNavigationActivity;

  public BaseBottomSheetDialogFragment() {
  }

  private static long getRemovingParentAnimationDuration(Fragment fragment, long defValue) {
    try {
      Field animInfoField = Fragment.class.getDeclaredField("mAnimationInfo");
      animInfoField.setAccessible(true);
      Object animationInfo = animInfoField.get(fragment);
      Field nextAnimField = animationInfo.getClass()
          .getDeclaredField("mNextAnim");
      nextAnimField.setAccessible(true);
      int nextAnimResource = nextAnimField.getInt(animationInfo);
      Animation nextAnim = AnimationUtils.loadAnimation(fragment.getActivity(), nextAnimResource);

      return (nextAnim == null) ? defValue : nextAnim.getDuration();
    } catch (NoSuchFieldException | IllegalAccessException | Resources.NotFoundException ex) {
      Logger.getInstance()
          .e("BASE FRAGMENT", "Unable to load next animation from parent.", ex);
      return defValue;
    }
  }

  public Dialog onCreateDialog(Bundle savedInstanceState) {
    return new BottomSheetDialog(this.getContext(), this.getTheme());
  }

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (activity instanceof BottomNavigationActivity) {
      bottomNavigationActivity = ((BottomNavigationActivity) activity);
    }
  }

  @Override public void onViewCreated(android.view.View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    if (bottomNavigationActivity != null) {
      bottomNavigationActivity.toggleBottomNavigation();
    }
  }

  @Override public void onDestroy() {
    fragmentComponent = null;
    super.onDestroy();
  }

  @Override public void onDetach() {
    bottomNavigationActivity = null;
    super.onDetach();
  }

  public FragmentComponent getFragmentComponent(Bundle savedInstanceState) {
    if (fragmentComponent == null) {
      AptoideApplication aptoideApplication =
          ((AptoideApplication) getContext().getApplicationContext());
      fragmentComponent = ((BaseActivity) getActivity()).getActivityComponent()
          .plus(aptoideApplication.getFragmentModule(this, savedInstanceState, getArguments(),
              aptoideApplication.isCreateStoreUserPrivacyEnabled(),
              (getActivity().getApplicationContext()).getPackageName()),
              new FlavourFragmentModule());
    }
    return fragmentComponent;
  }

  @Override public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
    // See https://code.google.com/p/android/issues/detail?id=55228
    final Fragment removingParent = getRemovingParent(getParentFragment());
    if (!enter && removingParent != null) {
      Animation doNothingAnim = new AlphaAnimation(1, 1);
      doNothingAnim.setDuration(getRemovingParentAnimationDuration(removingParent, 300));
      return doNothingAnim;
    } else {
      return super.onCreateAnimation(transit, enter, nextAnim);
    }
  }

  private Fragment getRemovingParent(Fragment fragment) {
    if (fragment == null) return null;
    Fragment parent = fragment.getParentFragment();
    if (parent != null && parent.isRemoving()) return getRemovingParent(parent);
    if (fragment.isRemoving()) return fragment;
    return null;
  }

  @NonNull @Override public final <T> LifecycleTransformer<T> bindUntilEvent(
      @NonNull cm.aptoide.pt.presenter.View.LifecycleEvent lifecycleEvent) {
    return RxLifecycle.bindUntilEvent(getLifecycleEvent(), lifecycleEvent);
  }

  @Override public Observable<cm.aptoide.pt.presenter.View.LifecycleEvent> getLifecycleEvent() {
    return lifecycle().flatMap(event -> convertToEvent(event));
  }

  @Override public void attachPresenter(Presenter presenter) {
    presenter.present();
  }

  @NonNull private Observable<cm.aptoide.pt.presenter.View.LifecycleEvent> convertToEvent(
      FragmentEvent event) {
    switch (event) {
      case ATTACH:
      case CREATE:
        return Observable.empty();
      case CREATE_VIEW:
        return Observable.just(cm.aptoide.pt.presenter.View.LifecycleEvent.CREATE);
      case START:
        return Observable.just(cm.aptoide.pt.presenter.View.LifecycleEvent.START);
      case RESUME:
        return Observable.just(cm.aptoide.pt.presenter.View.LifecycleEvent.RESUME);
      case PAUSE:
        return Observable.just(cm.aptoide.pt.presenter.View.LifecycleEvent.PAUSE);
      case STOP:
        return Observable.just(cm.aptoide.pt.presenter.View.LifecycleEvent.STOP);
      case DESTROY_VIEW:
        return Observable.just(cm.aptoide.pt.presenter.View.LifecycleEvent.DESTROY);
      case DETACH:
      case DESTROY:
        return Observable.empty();
      default:
        throw new IllegalStateException("Unrecognized event: " + event.name());
    }
  }
}
