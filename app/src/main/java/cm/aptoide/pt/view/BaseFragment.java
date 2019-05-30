package cm.aptoide.pt.view;

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.FlavourFragmentModule;
import cm.aptoide.pt.bottomNavigation.BottomNavigationActivity;
import cm.aptoide.pt.logger.Logger;
import com.trello.rxlifecycle.components.support.RxFragment;
import java.lang.reflect.Field;

public abstract class BaseFragment extends RxFragment {

  private FragmentComponent fragmentComponent;
  private BottomNavigationActivity bottomNavigationActivity;

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

  @Override public void onAttach(Activity activity) {
    super.onAttach(activity);
    if (activity instanceof BottomNavigationActivity) {
      bottomNavigationActivity = ((BottomNavigationActivity) activity);
    }
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
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
    // WORKAROUND FOR NESTED FRAGMENTS DISAPPEARING DURING PARENT ANIMATION
    // See https://code.google.com/p/android/issues/detail?id=55228
    /*  find top most parent in my hierarchy that is currently removing */
    final Fragment removingParent = getRemovingParent(getParentFragment());
    /*  if its not an enter animation and removing parent was found, otherwise workaround not needed */
    if (!enter && removingParent != null) {
      /*  apply stub animation with same duration as the one that will be played for removing parent */
      Animation doNothingAnim = new AlphaAnimation(1, 1);
      doNothingAnim.setDuration(getRemovingParentAnimationDuration(removingParent, 300));
      return doNothingAnim;
    } else {
      return super.onCreateAnimation(transit, enter, nextAnim);
    }
  }

  private Fragment getRemovingParent(Fragment fragment) {
    /* if im null, then noone is removing */
    if (fragment == null) return null;
    /* get my parent */
    Fragment parent = fragment.getParentFragment();
    /* if my parent exists and is removing then continue checking higher in hierarchy */
    if (parent != null && parent.isRemoving()) return getRemovingParent(parent);
    /* if my parent doesnt exists or is not removing then check if I am */
    if (fragment.isRemoving()) return fragment;
    /* seems like none of my parents is removing, neither am I, so no one really is */
    return null;
  }
}
