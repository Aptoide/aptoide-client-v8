package cm.aptoide.pt.firstinstall;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
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
 */

public class FirstInstallFragment extends AptoideBaseFragment<BaseAdapter>
    implements FirstInstallView, BackButton {

  private Button installAllButton;
  private ImageView closeButton;
  private CrashReport crashReport;

  public static FirstInstallFragment newInstance() {
    Bundle args = new Bundle();
    FirstInstallFragment fragment = new FirstInstallFragment();
    fragment.setArguments(args);
    return fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    crashReport = CrashReport.getInstance();
  }

  @Override public int getContentViewId() {
    return R.layout.first_install;
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    installAllButton = (Button) view.findViewById(R.id.install_all_button);
    closeButton = (ImageView) view.findViewById(R.id.close_image_view);

    attachPresenter(
        new FirstInstallPresenter(this, crashReport, requestFactoryCdnPool, getContext(),
            ((PartnerApplication) getApplicationContext()).getBootConfig()
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

  @Override public Observable<Void> installAllClick() {
    return RxView.clicks(installAllButton);
  }

  @Override public Observable<Void> closeClick() {
    return RxView.clicks(closeButton);
  }

  @Override
  public void addFirstInstallDisplayables(List<Displayable> displayables, boolean finishLoading) {
    if (this.getView() != null) {
      addDisplayables(displayables, finishLoading);
    }
  }
}
