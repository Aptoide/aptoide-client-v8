package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.model.v7.GetApp;
import cm.aptoide.pt.model.v7.GetAppMeta;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.V8Engine;
import cm.aptoide.pt.v8engine.fragment.implementations.AppViewFragment;
import cm.aptoide.pt.v8engine.repository.InstalledRepository;
import cm.aptoide.pt.v8engine.repository.RepositoryFactory;
import cm.aptoide.pt.v8engine.util.Translator;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.grid.OfficialAppDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;
import com.jakewharton.rxbinding.view.RxView;

public class OfficialAppWidget extends Widget<OfficialAppDisplayable> {

  private static final String TAG = OfficialAppWidget.class.getName();

  private ImageView appImage;
  private Button installButton;
  private TextView installMessage;
  private TextView appName;
  private RatingBar appRating;
  private View verticalSeparator;
  private TextView appDownloads;
  private TextView appVersion;
  private TextView appSize;

  public OfficialAppWidget(View itemView) {
    super(itemView);
  }

  @Override protected void assignViews(View itemView) {
    appImage = (ImageView) itemView.findViewById(R.id.app_image);
    installButton = (Button) itemView.findViewById(R.id.app_install_button);
    installMessage = (TextView) itemView.findViewById(R.id.install_message);
    appName = (TextView) itemView.findViewById(R.id.app_name);
    verticalSeparator = itemView.findViewById(R.id.vertical_separator);
    appRating = (RatingBar) itemView.findViewById(R.id.app_rating);
    appDownloads = (TextView) itemView.findViewById(R.id.app_downloads);
    appVersion = (TextView) itemView.findViewById(R.id.app_version);
    appSize = (TextView) itemView.findViewById(R.id.app_size);
  }

  @Override public void bindView(OfficialAppDisplayable displayable) {

    final FragmentActivity context = getContext();
    final Pair<String, GetApp> messageAndApp = displayable.getMessageGetApp();
    final boolean isAppInstalled = isAppInstalled(messageAndApp.second);

    int color;
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      color = context.getResources().getColor(R.color.default_color, context.getTheme());
    } else {
      color = context.getResources().getColor(R.color.default_color);
    }

    final GetAppMeta.App appData = messageAndApp.second.getNodes().getMeta().getData();
    final String appName = appData.getName();

    if (!TextUtils.isEmpty(messageAndApp.first)) {

      // get multi part message
      final String[] parts = Translator.translateToMultiple(messageAndApp.first);
      if (parts != null && parts.length == 4) {
        SpannableString middle =
            new SpannableString(String.format(isAppInstalled ? parts[3] : parts[2], appName));
        middle.setSpan(new ForegroundColorSpan(color), 0, middle.length(), Spanned.SPAN_MARK_MARK);

        SpannableStringBuilder text = new SpannableStringBuilder();
        text.append(parts[0]);
        text.append(middle);
        text.append(parts[1]);
        installMessage.setText(text);
      } else {
        installMessage.setText(messageAndApp.first);
      }
    } else {
      hideOfficialAppMessage();
    }

    appRating.setRating(appData.getStats().getRating().getAvg());

    this.appName.setText(appName);
    this.appDownloads.setText(String.format(context.getString(R.string.downloads_count),
        AptoideUtils.StringU.withSuffix(appData.getStats().getDownloads())));

    this.appVersion.setText(
        String.format(context.getString(R.string.version_number), appData.getFile().getVername()));

    this.appSize.setText(String.format(context.getString(R.string.app_size),
        AptoideUtils.StringU.formatBytes(appData.getFile().getFilesize(), false)));

    ImageLoader.with(context).load(appData.getIcon(), this.appImage);

    // check if app is installed. if it is, show open button

    // apply button background
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Drawable d = context.getDrawable(R.drawable.dialog_bg_2);
      d.setColorFilter(color, PorterDuff.Mode.SRC_IN);
      installButton.setBackground(d);
    } else {
      Drawable d = context.getResources().getDrawable(R.drawable.dialog_bg_2);
      d.setColorFilter(color, PorterDuff.Mode.SRC_IN);
      installButton.setBackgroundDrawable(d);
    }

    installButton.setText(context.getString(isAppInstalled ? R.string.open : R.string.install));

    compositeSubscription.add(RxView.clicks(installButton).subscribe(a -> {
      if (isAppInstalled) {
        AptoideUtils.SystemU.openApp(appData.getPackageName());
      } else {
        // show app view to install app
        Fragment appView = V8Engine.getFragmentProvider()
            .newAppViewFragment(appData.getPackageName(),
                AppViewFragment.OpenType.OPEN_AND_INSTALL);
        getFragmentNavigator().navigateTo(appView);
      }
    }, err -> {
      CrashReport.getInstance().log(err);
    }));
  }

  private boolean isAppInstalled(GetApp app) {
    InstalledRepository installedRepo = RepositoryFactory.getInstalledRepository();
    return installedRepo.contains(app.getNodes().getMeta().getData().getPackageName());
  }

  private void hideOfficialAppMessage() {
    installMessage.setVisibility(View.GONE);
    verticalSeparator.setVisibility(View.GONE);
  }
}
