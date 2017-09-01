package cm.aptoide.pt.view.updates.installed;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.repository.RepositoryFactory;
import cm.aptoide.pt.view.account.AccountNavigator;
import cm.aptoide.pt.view.dialog.DialogUtils;
import cm.aptoide.pt.view.recycler.widget.Widget;
import cm.aptoide.pt.view.share.ShareAppHelper;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxrelay.PublishRelay;
import java.util.Locale;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

/**
 * Created by neuro on 17-05-2016.
 */
public class InstalledAppWidget extends Widget<InstalledAppDisplayable> {

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

    accountManager =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountManager();
    httpClient = ((AptoideApplication) getContext().getApplicationContext()).getDefaultClient();
    converterFactory = WebService.getDefaultConverter();

    this.bodyInterceptor =
        ((AptoideApplication) getContext().getApplicationContext()).getBaseBodyInterceptorV7Pool();

    final AccountNavigator accountNavigator =
        new AccountNavigator(getFragmentNavigator(), accountManager);
    this.accountNavigator = accountNavigator;
    dialogUtils = new DialogUtils(accountManager, accountNavigator, bodyInterceptor, httpClient,
        converterFactory, displayable.getInstalledRepository(),
        ((AptoideApplication) getContext().getApplicationContext()).getTokenInvalidator(),
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences(),
        getContext().getResources());
    shareAppHelper = new ShareAppHelper(
        RepositoryFactory.getInstalledRepository(getContext().getApplicationContext()),
        accountManager, accountNavigator, getContext(), displayable.getTimelineAnalytics(),
        PublishRelay.create(),
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences(),
        ((AptoideApplication) getContext().getApplicationContext()).isCreateStoreUserPrivacyEnabled());
    appName = pojo.getName();
    packageName = pojo.getPackageName();

    labelTextView.setText(pojo.getName());
    verNameTextView.setText(pojo.getVersionName());
    final FragmentActivity context = getContext();
    ImageLoader.with(context)
        .load(pojo.getIcon(), iconImageView);

    shareButtonLayout.setVisibility(View.VISIBLE);
    compositeSubscription.add(RxView.clicks(shareButtonLayout)
        .subscribe(__ -> shareAppHelper.shareApp(appName, packageName, pojo.getIcon(), ""),
            //// FIXME: 10-08-2017 pass origin atribute/string from udpates tab
            err -> CrashReport.getInstance()
                .log(err)));
  }
}
