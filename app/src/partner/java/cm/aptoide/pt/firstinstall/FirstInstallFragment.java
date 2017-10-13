package cm.aptoide.pt.firstinstall;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.PartnerApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.repository.RepositoryFactory;
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

  public static FirstInstallFragment newInstance() {
    Bundle args = new Bundle();
    FirstInstallFragment fragment = new FirstInstallFragment();
    fragment.setArguments(args);
    return fragment;
  }

  /**
   * setup toolbar name
   */
  @Override public void setupToolbar() {
    super.setupToolbar();
    getToolbar().setTitle(getString(R.string.essential_apps));
  }

  /**
   * setup first install menu with close button
   */
  @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    super.onCreateOptionsMenu(menu, inflater);
    menu.clear();
    inflater.inflate(R.menu.menu_firstinstall_fragment, menu);
  }

  /**
   * handle on menu item click
   */
  @Override public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.close_firstinstall) {
      removeFragmentAnimation();
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  /**
   * set option menu to true
   */
  @Override public void bindViews(View view) {
    super.bindViews(view);
    setHasOptionsMenu(true);
  }

  @Override public int getContentViewId() {
    return R.layout.first_install;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    firstInstallLayout = (RelativeLayout) view.findViewById(R.id.first_install_layout);
    installAllButton = (Button) view.findViewById(R.id.install_all_button);

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
                ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences())));
  }

  @Override public Observable<Void> installAllClick() {
    return RxView.clicks(installAllButton);
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
        getActivity().onBackPressed();
      }

      @Override public void onAnimationRepeat(Animation animation) {

      }
    });
    firstInstallLayout.startAnimation(animation);
  }
}
