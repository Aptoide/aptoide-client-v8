package cm.aptoide.pt.v8engine.view.updates.installed;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.analytics.Analytics;
import cm.aptoide.pt.v8engine.crashreports.CrashReport;
import cm.aptoide.pt.v8engine.networking.image.ImageLoader;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.spotandshare.SpotAndShareAnalytics;
import cm.aptoide.pt.v8engine.view.account.AccountNavigator;
import cm.aptoide.pt.v8engine.view.dialog.DialogUtils;
import cm.aptoide.pt.v8engine.view.recycler.widget.Displayables;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import cm.aptoide.pt.v8engine.view.share.ShareAppHelper;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.Locale;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

/**
 * Created by neuro on 17-05-2016.
 */
@Displayables({ InstalledAppDisplayable.class }) public class InstalledAppWidget
    extends Widget<InstalledAppDisplayable> {

  private static final Locale LOCALE = Locale.getDefault();
  private static final String TAG = InstalledAppWidget.class.getSimpleName();
  private AptoideAccountManager accountManager;
  private DialogUtils dialogUtils;

  private TextView labelTextView;
  private TextView verNameTextView;
  private ImageView iconImageView;
  private ViewGroup shareButtonLayout;

  private String appName;
  private String packageName;
  private AccountNavigator accountNavigator;
  private BodyInterceptor<BaseBody> bodyInterceptor;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;

  private ShareAppHelper shareAppHelper;

  public InstalledAppWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    labelTextView = (TextView) itemView.findViewById(R.id.name);
    iconImageView = (ImageView) itemView.findViewById(R.id.icon);
    verNameTextView = (TextView) itemView.findViewById(R.id.app_version);
    shareButtonLayout = (ViewGroup) itemView.findViewById(R.id.shareButtonLayout);
  }

  @Override public void bindView(InstalledAppDisplayable displayable) {
    Installed pojo = displayable.getPojo();

    accountManager = ((V8Engine) getContext().getApplicationContext()).getAccountManager();
    httpClient = ((V8Engine) getContext().getApplicationContext()).getDefaultClient();
    converterFactory = WebService.getDefaultConverter();

    this.bodyInterceptor =
        ((V8Engine) getContext().getApplicationContext()).getBaseBodyInterceptorV7();

    final AccountNavigator accountNavigator =
        new AccountNavigator(getFragmentNavigator(), accountManager, getActivityNavigator());
    this.accountNavigator = accountNavigator;
    dialogUtils = new DialogUtils(accountManager, accountNavigator, bodyInterceptor, httpClient,
        converterFactory, displayable.getInstalledRepository(),
        ((V8Engine) getContext().getApplicationContext()).getTokenInvalidator(),
        ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences(),
        getContext().getResources());
    shareAppHelper = new ShareAppHelper(RepositoryFactory.getInstalledRepository(getContext().getApplicationContext()), accountManager,
        accountNavigator, getContext(), new SpotAndShareAnalytics(Analytics.getInstance()),
        displayable.getTimelineAnalytics(), PublishRelay.create(),
        ((V8Engine) getContext().getApplicationContext()).getDefaultSharedPreferences());
    appName = pojo.getName();
    packageName = pojo.getPackageName();

    labelTextView.setText(pojo.getName());
    verNameTextView.setText(pojo.getVersionName());
    final FragmentActivity context = getContext();
    ImageLoader.with(context)
        .load(pojo.getIcon(), iconImageView);

    shareButtonLayout.setVisibility(View.VISIBLE);
    compositeSubscription.add(RxView.clicks(shareButtonLayout)
        .subscribe(__ -> shareAppHelper.shareApp(appName, packageName, pojo.getIcon(),
            SpotAndShareAnalytics.SPOT_AND_SHARE_START_CLICK_ORIGIN_UPDATES_TAB),
            err -> CrashReport.getInstance()
                .log(err)));
  }
}
