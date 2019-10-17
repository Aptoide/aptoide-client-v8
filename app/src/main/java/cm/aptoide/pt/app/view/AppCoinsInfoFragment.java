package cm.aptoide.pt.app.view;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import cm.aptoide.analytics.implementation.navigation.ScreenTagHistory;
import cm.aptoide.aptoideviews.video.YoutubePlayer;
import cm.aptoide.pt.R;
import cm.aptoide.pt.link.CustomTabsHelper;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.view.AppCoinsInfoPresenter;
import cm.aptoide.pt.view.BackButtonFragment;
import cm.aptoide.pt.view.NotBottomNavigationView;
import com.jakewharton.rxbinding.view.RxView;
import javax.inject.Inject;
import javax.inject.Named;
import rx.Observable;

/**
 * Created by D01 on 30/07/2018.
 */

public class AppCoinsInfoFragment extends BackButtonFragment
    implements AppCoinsInfoView, NotBottomNavigationView {

  @Inject AppCoinsInfoPresenter appCoinsInfoPresenter;
  @Inject @Named("aptoide-theme") String theme;
  private Toolbar toolbar;
  private View appCardView;
  private TextView appcMessageAppcoinsSection2a;
  private YoutubePlayer youtubePlayer;
  private Button installButton;
  private Button catappultDevButton;
  private int spannableColor;

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    getFragmentComponent(savedInstanceState).inject(this);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    toolbar = (Toolbar) view.findViewById(R.id.toolbar);
    spannableColor = StoreTheme.get(theme)
        .getPrimaryColor();
    appCardView = view.findViewById(R.id.app_cardview);
    installButton = (Button) view.findViewById(R.id.appview_install_button);
    catappultDevButton = (Button) view.findViewById(R.id.catappult_dev_button);
    appcMessageAppcoinsSection2a =
        (TextView) view.findViewById(R.id.appc_message_appcoins_section_2a);
    TextView appcMessageAppcoinsSection3 =
        (TextView) view.findViewById(R.id.appc_message_appcoins_section_3);
    TextView appcMessageAppcoinsSection4 =
        (TextView) view.findViewById(R.id.appc_message_appcoins_section_4);

    youtubePlayer = view.findViewById(R.id.youtube_player);

    setupTextView(R.drawable.ic_poa_coins, getString(R.string.appc_message_appcoins_section_3),
        appcMessageAppcoinsSection3);
    setupTextView(R.drawable.ic_poa_coins, getString(R.string.appc_message_appcoins_section_4),
        appcMessageAppcoinsSection4);
    ((TextView) appCardView.findViewById(R.id.app_title_textview)).setText(
        getString(R.string.appc_title_settings_appcoins_wallet));
    ((ImageView) appCardView.findViewById(R.id.app_icon_imageview)).setImageDrawable(
        ContextCompat.getDrawable(getContext(), R.drawable.appcoins_wallet_icon));

    setupWalletLink();
    setHasOptionsMenu(true);
    setupToolbar();
    youtubePlayer.loadVideo("j-Ejvmy5pUs", true);
    attachPresenter(appCoinsInfoPresenter);
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }

  @Override public void onDestroyView() {
    toolbar = null;
    appCardView = null;
    installButton = null;
    appcMessageAppcoinsSection2a = null;
    super.onDestroyView();
  }

  private void setupWalletLink() {
    final String formattedString =
        String.format(getString(R.string.appc_message_appcoins_section_3),
            getString(R.string.appc_title_settings_appcoins_wallet));
    SpannableString spannableString = new SpannableString(formattedString);
    appcMessageAppcoinsSection2a.setText(spannableString);
    appcMessageAppcoinsSection2a.setMovementMethod(LinkMovementMethod.getInstance());
  }

  private void setupTextView(int image, String text, TextView appcMessageAppcoinsSection) {
    final String spendGetAppcoinsLogo =
        String.format("<img width='24px' height='20px' src=\"%1$s\"/>", image);
    final String formatedText = String.format(text, spendGetAppcoinsLogo);
    appcMessageAppcoinsSection.setText(Html.fromHtml(formatedText, getImageGetter(), null));
  }

  private void setupToolbar() {
    toolbar.setTitle(R.string.appc_title_about_appcoins);

    final AppCompatActivity activity = (AppCompatActivity) getActivity();
    activity.setSupportActionBar(toolbar);
    ActionBar actionBar = activity.getSupportActionBar();
    if (actionBar != null) {
      actionBar.setDisplayHomeAsUpEnabled(true);
      actionBar.setTitle(toolbar.getTitle());
    }
  }

  @NonNull private Html.ImageGetter getImageGetter() {
    return source -> {
      Drawable drawable = null;
      try {
        drawable = getResources().getDrawable(Integer.parseInt(source));
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
      } catch (Resources.NotFoundException e) {
        Log.e("log_tag", "Image not found. Check the ID.", e);
      } catch (NumberFormatException e) {
        Log.e("log_tag", "Source string not a valid resource ID.", e);
      }
      return drawable;
    };
  }

  @Override public Observable<Void> cardViewClick() {
    return RxView.clicks(appCardView);
  }

  @Override public Observable<Void> catappultButtonClick() {
    return RxView.clicks(catappultDevButton);
  }

  @Override public Observable<Void> installButtonClick() {
    return RxView.clicks(installButton);
  }

  @Override public Observable<Void> appCoinsWalletLinkClick() {
    return RxView.clicks(appcMessageAppcoinsSection2a);
  }

  @Override public void openApp(String packageName) {
    AptoideUtils.SystemU.openApp(packageName, getContext().getPackageManager(), getContext());
  }

  @Override public void setButtonText(boolean isInstalled) {
    String installState = getResources().getString(R.string.appview_button_install);
    if (isInstalled) {
      installButton.setText(getResources().getString(R.string.appview_button_open));
    } else {
      installButton.setText(installState);
    }
  }

  @Override public void startCatappultDevWebView() {
    CustomTabsHelper.getInstance()
        .openInChromeCustomTab("https://catappult.io/", getContext(), theme);
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_appcoins_info, container, false);
  }
}
