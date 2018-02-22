package cm.aptoide.pt.updates.view.installed;

import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.account.view.AccountNavigator;
import cm.aptoide.pt.analytics.NavigationTracker;
import cm.aptoide.pt.analytics.analytics.AnalyticsManager;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.database.realm.Installed;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.navigator.ActivityResultNavigator;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.share.ShareAppHelper;
import cm.aptoide.pt.view.dialog.DialogUtils;
import cm.aptoide.pt.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;
import java.util.Locale;
import okhttp3.OkHttpClient;
import retrofit2.Converter;

/**
 * Created by neuro on 17-05-2016.
 */
public class InstalledAppWidget extends Widget<InstalledAppDisplayable> {

  private TextView labelTextView;
  private TextView verNameTextView;
  private ImageView iconImageView;

  public InstalledAppWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    labelTextView = (TextView) itemView.findViewById(R.id.name);
    iconImageView = (ImageView) itemView.findViewById(R.id.icon);
    verNameTextView = (TextView) itemView.findViewById(R.id.app_version);
  }

  @Override public void bindView(InstalledAppDisplayable displayable) {
    final Installed pojo = displayable.getPojo();

    labelTextView.setText(pojo.getName());
    verNameTextView.setText(pojo.getVersionName());
    final FragmentActivity context = getContext();
    ImageLoader.with(context)
        .load(pojo.getIcon(), iconImageView);
  }
}
