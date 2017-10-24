package cm.aptoide.pt.firstinstall;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.PartnerApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.preferences.PartnersSecurePreferences;
import cm.aptoide.pt.repository.RepositoryFactory;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.view.BackButton;
import cm.aptoide.pt.view.fragment.AptoideBaseFragment;
import cm.aptoide.pt.view.recycler.BaseAdapter;
import cm.aptoide.pt.view.recycler.displayable.Displayable;
import com.jakewharton.rxbinding.view.RxView;
import java.util.List;
import rx.Observable;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by diogoloureiro on 02/10/2017.
 *
 * First install fragment
 */

public class FirstInstallFragment extends AptoideBaseFragment<BaseAdapter>
    implements FirstInstallView, BackButton {

  private Button installAllButton;
  private RelativeLayout firstInstallLayout;
  private RelativeLayout titleToolbar;
  private ImageView closeButton;

  public static FirstInstallFragment newInstance() {
    Bundle args = new Bundle();
    FirstInstallFragment fragment = new FirstInstallFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override protected boolean hasToolbar() {
    return false;
  }

  @Override public void bindViews(View view) {
    super.bindViews(view);
    firstInstallLayout = (RelativeLayout) view.findViewById(R.id.first_install_layout);
    installAllButton = (Button) view.findViewById(R.id.install_all_button);
    titleToolbar = (RelativeLayout) view.findViewById(R.id.first_install_toolbar);
    closeButton = (ImageView) view.findViewById(R.id.first_install_close_button);
  }

  @Override public void setupViews() {
    super.setupViews();
    titleToolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), StoreTheme.get(
        ((AptoideApplication) getActivity().getApplication()).getApplicationPreferences()
            .getDefaultThemeName())
        .getPrimaryColor()));
  }

  @Override public int getContentViewId() {
    return R.layout.first_install;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    Analytics.FirstInstall.popUp();
    handleOnBackKeyPressed();
    attachPresenter(
        new FirstInstallPresenter(this, CrashReport.getInstance(), requestFactoryCdnPool,
            getContext(), ((PartnerApplication) getApplicationContext()).getBootConfig()
            .getPartner()
            .getStore()
            .getName(), "",
            ((AptoideApplication) getContext().getApplicationContext()).getAdsRepository(),
            getContext().getResources(),
            (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE),
            RepositoryFactory.getAppRepository(getContext(),
                ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences())),
        savedInstanceState);
  }

  /**
   * overwrite on back key press to avoid the user of leaving the fragment with it
   */
  private void handleOnBackKeyPressed() {
    if (getView() != null) {
      getView().setFocusableInTouchMode(true);
      getView().requestFocus();
      getView().setOnKeyListener((v, keyCode, event) -> keyCode == KeyEvent.KEYCODE_BACK);
    }
  }

  @Override public Observable<Void> installAllClick() {
    return RxView.clicks(installAllButton);
  }

  @Override public Observable<Void> closeClick() {
    return RxView.clicks(closeButton);
  }

  /**
   * add first install displayables to the recyclerview.
   * enavles the install all button
   *
   * @param displayables list of displayables to add
   * @param finishLoading true to finish loading screen
   */
  @Override public void addFirstInstallDisplayables(List<Displayable> displayables,
      boolean finishLoading) {
    if (this.getView() != null) {
      addDisplayables(displayables, finishLoading);
      installAllButton.setVisibility(View.VISIBLE);
    }
  }

  /**
   * starts the removing fragment animation
   * once that animation finishes, removes the fragment
   *
   * take in mind, this should take in consideration the referrer extraction occurring
   */
  @Override public void removeFragmentAnimation() {
    Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.anim_out);
    animation.setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime));

    animation.setAnimationListener(new Animation.AnimationListener() {
      @Override public void onAnimationStart(Animation animation) {

      }

      @Override public void onAnimationEnd(Animation animation) {
        PartnersSecurePreferences.setFirstInstallFinished(true,
            ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences());
        getActivity().onBackPressed();
      }

      @Override public void onAnimationRepeat(Animation animation) {

      }
    });
    firstInstallLayout.startAnimation(animation);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }
}
